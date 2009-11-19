/*
 * Copyright (c) 2001-2008 David Soergel
 * 418 Richmond St., El Cerrito, CA  94530
 * dev@davidsoergel.com
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the names of any contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.DSArrayUtils;
import com.davidsoergel.dsutils.LabellableImpl;
import com.davidsoergel.dsutils.collections.MutableWeightedSet;
import com.davidsoergel.dsutils.collections.WeightedSet;
import com.davidsoergel.runutils.Property;
import com.davidsoergel.runutils.PropertyConsumer;
import com.davidsoergel.stats.DistributionException;
import com.davidsoergel.stats.DistributionProcessor;
import com.davidsoergel.stats.DistributionProcessorException;
import com.davidsoergel.stats.Multinomial;
import com.davidsoergel.stats.MutableDistribution;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * A probabilistic suffix tree, as described in
 * <p/>
 * D. Ron, Y. Singer and N. Tishby. The power of amnesia: learning probabilistic automata with variable memory length.
 * Machine Learning, 25:117-149, 1996.  http://citeseer.ist.psu.edu/article/ron96power.html
 *
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: RonPST.java 519 2009-10-08 07:01:28Z soergel $
 * @Author David Soergel (soergel@compbio.berkeley.edu)
 */
@PropertyConsumer
public class RonPST extends RonPSTNode
		implements SequenceSpectrum<RonPST>, MutableDistribution//implements SequenceSpectrumTranslator
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(RonPST.class);

	@Property(helpmessage = "A distribution processor to run on this PST, typically used for smoothing",
	          defaultvalue = "edu.berkeley.compbio.ml.strings.RonPSTSmoother", isNullable = true)
	//isPlugin = true,
	public DistributionProcessor<RonPST> completionProcessor;
	//private double eta0, eta1, eta2, gammaMin, L;

	//private double pMin, alpha, pRatioMinMax, gammaMin;
	//private int l_max;
	/*
	 Pmin = (1.-eta1) * eta0
	 alpha = eta2
	 gammaMin
	 pRatioMinMax = p_ratio = (1 + (3 * eta2))
 */

	private String label;

//	private final MutableWeightedSet<String> weightedLabels = new ConcurrentHashWeightedSet<String>();

	private LabellableImpl<String> labels = new LabellableImpl<String>();

	public void doneLabelling()
		{
		labels.doneLabelling();
		}

	@NotNull
	public WeightedSet<String> getImmutableWeightedLabels()
		{
		return labels.getImmutableWeightedLabels();
		}

	@NotNull
	public MutableWeightedSet<String> getMutableWeightedLabels()
		{
		return labels.getMutableWeightedLabels();
		}

	public int getItemCount()
		{
		return labels.getItemCount();
		}

	private long originalSequenceLength;

	// diagnostics
	private int total = 0;
	private int leaves = 0;
	//private int maxdepth = 0;
	private double avgdepth = 0;


// --------------------------- CONSTRUCTORS ---------------------------

	public RonPST()//String injectorId)
		{
		//ResultsCollectingProgramRun.getProps().injectProperties(injectorId, this);
		}

	/*
	     * @param alpha                the "headroom" over the smoothing factor gammaMin that determines whether a branch is to
		 *                             be pruned or not.  a branch must have a conditional probability of at least (1 + alpha)
		 *                             * gammaMin in order to be retained.
		 *  * @param gammaMin             the minimum conditional probability of a symbol that we are interested in.  In the Ron /
		 *                             Bejerano formulation, this is used as a smoothing factor; no conditional probability may
		 *                             be less than this parameter after smoothing.  Even in the absence of smoothing, this
		 *                             parameter contributes to the determination of whether a branch is to be pruned or not,
		 *                             since if the empirical probability of a branch before smoothing is less than the
		 *                             smoothing factor, it's considered to be noise and should be pruned.

	 */
	/**
	 * Constructs a new Probabilistic Suffix Tree according to the Learn-PST algorithm (Ron et al 1996 p. 13), based on an
	 * existing sequence spectrum (typically, the set of all word counts; or naively just the sequence itself, if we don't
	 * mind re-scanning it a whole bunch of times as they seem to suggest).
	 *
	 * @param branchAbsoluteMin    the minimum total probability of a string that should be taken seriously.  Branches with
	 *                             probabilities less than this will be pruned.  Ron et al. call this pMin.
	 * @param branchConditionalMin the minimum conditional probability of a symbol that we are interested in.  Ron et al.
	 *                             express this as (1 + alpha) * gammaMin.  Here we just provide it directly since the
	 *                             smoothing process (and hence gammaMin) are broken out.
	 * @param pRatioMinMax         The ratio threshold for considering the probability of a symbol to have changed compared
	 *                             to the back-off prior.  That is, either the symbol probability must be a factor of
	 *                             pRatioMinMax greater than its back-off prior value, or vice versa, in order for the
	 *                             probability to be taken seriously.  Note that values less than 1 would cause all symbol
	 *                             probabilities to pass the test, so those aren't very useful.
	 * @param l_max                the maximum depth of the tree (that is, the maximum memory length)
	 * @param prob                 the SequenceSpectrum providing the symbol conditional probabilities from which the PST
	 *                             will be learned
	 * @throws SequenceSpectrumException if something goes wrong with the given spectrum
	 */


	public RonPST(final double branchAbsoluteMin, final double branchConditionalMin, final double pRatioMinMax,
	              final int l_max, final SequenceSpectrum prob)
		//throws SequenceSpectrumException//DistributionException,
		{
		this();
		learn(branchAbsoluteMin, branchConditionalMin, pRatioMinMax, l_max, prob);
		originalSequenceLength = prob.getOriginalSequenceLength();
		}

	/*	public RonPST(double pMin, double alpha, double pRatioMinMax, double gammaMin, int l_max, SequenceSpectrum prob)
		   //throws SequenceSpectrumException//DistributionException,
		   {
		   this();
		   learn(pMin, alpha, pRatioMinMax, gammaMin, l_max, prob);
		   }*/

	private void learn(final double branchAbsoluteMin, final double branchConditionalMin, final double pRatioMinMax,
	                   final int l_max, final SequenceSpectrum fromSpectrum)
		{
		setId(DSArrayUtils.EMPTY_BYTE_ARRAY);
		setAlphabet(fromSpectrum.getAlphabet());

		/*
		  this.pMin = pMin;
		  this.alpha = alpha;
		  this.pRatioMinMax = pRatioMinMax;
		  this.gammaMin = gammaMin;
		  this.l_max = l_max;
  */


		// First Phase

		// Step 1

		final Set<byte[]> remainingSequences = new HashSet<byte[]>();

		for (final byte c : fromSpectrum.getAlphabet())
			{
			final byte[] s = new byte[]{c};
			try
				{
				if (fromSpectrum.totalProbability(s) >= branchAbsoluteMin)//pMin)
					{
					remainingSequences.add(s);
					}
				}
			catch (SequenceSpectrumException e)
				{
				logger.warn("Unknown probability: " + new String(s));
				logger.warn("warn", e);
				// too bad, the requested probability is not known
				}
			}

		// Step 2

		while (!remainingSequences.isEmpty())
			{
			final byte[] s = remainingSequences.iterator().next();

			// A)
			remainingSequences.remove(s);

			// B)
			for (final byte sigma : fromSpectrum.getAlphabet())
				{
				try
					{
					final double conditional = fromSpectrum.conditionalProbability(sigma, s);
					final double suffixConditional =
							fromSpectrum.conditionalProbability(sigma, DSArrayUtils.suffix(s, 1));

					final double probRatio = conditional / suffixConditional;
					//	logger.debug("" + conditional + " / " + suffixConditional + " = " + probRatio);
					if ((conditional >= branchConditionalMin)// (1. + alpha) * gammaMin)
					    &&
					    // for some reason Ron et al only want to test this one way, but Bejerano, Kermorvant, etc.
					    // do it both ways, and that makes more sense anyway
					    ((probRatio >= pRatioMinMax) || (probRatio <= (1. / pRatioMinMax))))
						{
						//	logger.debug("" + conditional + " / " + suffixConditional + " = " + probRatio);
						addAllSuffixes(s);
						break;
						}
					}
				catch (SequenceSpectrumException e)
					{
					logger.warn("Unknown probability: " + new String(s));
					// too bad, the requested probability is not known
					}
				}

			// C)

			// note we consider any string with probability greater than branchAbsoluteMin, regardless of whether or not the most recent character is distributed differently
			// from the backoff.  The reason for this is that we may discover an anomalous distribution at the 8th character in a case where 4-7 were uninformative.
			if (s.length < l_max)
				{
				for (final byte sigma2 : fromSpectrum.getAlphabet())
					{
					final byte[] s2 = DSArrayUtils.prepend(sigma2, s);
					try
						{
						if (fromSpectrum.totalProbability(s2) >= branchAbsoluteMin)//pMin)
							{
							remainingSequences.add(s2);
							}
						}
					catch (SequenceSpectrumException e)
						{
						// too bad, the requested probability is not known
						}
					}
				}
			}


		// Second Phase

		// Step 1 (unnecessary)
		// Step 2: add missing nodes to the tree

		//complete();
		//logger.trace("1");
		copyProbsFrom(fromSpectrum);
		//logger.trace("2");


		// now we have the right tree structure, with the empirical probabilities assigned.

		// Ron et al split the steps up differently, in that they build the tree structure first, and
		// then simultaneously assign the probabilities and smooth them.  We prefer to do the
		// smoothing as a distinct step.


		// Step 3 (smoothing)

		// REVIEW Smoothing framework
		// in this framework, smoothing is accomplished simply by constructing a new
		// SequenceSpectrum of the appropriate type, i.e. KneserNeySmoothedSpectrum

		//new KneserNeyPSTSmoother().smooth(this);
		/*	for (MarkovTreeNode n : thePST)
		   {
		   n.setGamma();
		   }*/

		//return root;
		//List<MarkovTreeNode> breadthFirstList = setBacklinks();


		if (completionProcessor != null)
			{
			try
				{
				completionProcessor.process(this);
				}
			catch (DistributionProcessorException e)
				{
				logger.error("Error", e);
				throw new SequenceSpectrumRuntimeException(e);
				}
			}

		updateLogProbsRecursive();

		diagnostics();
		}

	/**
	 * Adds each suffix of the given string to the tree.  Note that each suffix is started fresh from the root.  (A call to
	 * the regular add() makes all suffixes present further down the tree anyway, which is to say, conditional on the
	 * prefix up to that point.  Here, we want nodes for each suffix without such conditioning.)
	 *
	 * @param s the byte[]
	 */
	private void addAllSuffixes(byte[] s) throws SequenceSpectrumException
		{
		while (s.length > 0)
			{
			addUpstreamNode(s);
			s = DSArrayUtils.suffix(s, 1);
			}
		}

	private void diagnostics()
		{
		for (final RonPSTNode node : getAllUpstreamNodes())
			{
			total++;
			if (node.isLeaf())
				{
				leaves++;
				final int depth = node.getIdBytes().length;//length();
				avgdepth += depth;
				//maxdepth = Math.max(maxdepth, depth);
				}
			}
		//maxdepth += 1;
		avgdepth /= leaves;
		avgdepth += 1;
		//	logger.info("Learned Ron PST using params " + branchAbsoluteMin + " " + branchConditionalMin + " " + pRatioMinMax
		//			+ " " + l_max);
		logger.debug("Learned Ron PST with " + total + " nodes, " + leaves + " leaves, avg depth " + avgdepth
		             + ", max depth " + getMaxDepth());
		if (logger.isTraceEnabled())
			{
			logger.trace("\n" + toLongString());
			}
		}

	public String toLongString()
		{
		final StringBuffer sb = new StringBuffer();
		final Formatter formatter = new Formatter(sb, Locale.US);
		appendString(formatter, "");
		return sb.toString();
		}

// --------------------- GETTER / SETTER METHODS ---------------------

	/**
	 * {@inheritDoc}
	 */
	public byte[] getAlphabet()
		{
		return alphabet;
		}

	public long getOriginalSequenceLength()
		{
		return originalSequenceLength;
		}


	public void setLabel(final String label)
		{
		this.label = label;
		}

// ------------------------ CANONICAL METHODS ------------------------

	/**
	 * Clone this object.  Should behave like {@link Object#clone()} except that it returns an appropriate type and so
	 * requires no cast.  Also, we insist that this method be implemented in inheriting classes, so it does not throw
	 * CloneNotSupportedException.
	 *
	 * @return a clone of this instance.
	 * @see Object#clone
	 * @see Cloneable
	 */
	@Override
	public RonPST clone()
		{
		throw new NotImplementedException();
		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface AdditiveClusterable ---------------------


	public void decrementBy(final RonPST object)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public void decrementByWeighted(final RonPST object, final double weight)
		{
		throw new NotImplementedException();
		}

	public void incrementBy(final RonPST object)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public void incrementByWeighted(final RonPST object, final double weight)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public RonPST minus(final RonPST object)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public void multiplyBy(final double v)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public RonPST plus(final RonPST object)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public RonPST times(final double v)
		{
		throw new NotImplementedException();
		}

// --------------------- Interface Clusterable ---------------------

	public boolean equalValue(final RonPST other)
		{
		throw new NotImplementedException();
		}

// --------------------- Interface SequenceSpectrum ---------------------


	/**
	 * Computes the conditional probability of generating a symbol given a prefix under the model, backing off to shorter
	 * prefixes as needed if the given prefix is not explicitly represented.
	 *
	 * @param sigma  a byte specifying the symbol whose probability is to be computed
	 * @param prefix a byte array providing the conditioning prefix
	 * @return the conditional probability, a double value between 0 and 1, inclusive
	 * @see #fragmentLogProbability(SequenceFragment, boolean)
	 */
	public double conditionalProbability(final byte sigma, final byte[] prefix) throws SequenceSpectrumException
		{
		//return getLongestSuffix(ArrayUtils.append(prefix, sigma)).conditionalProbability(sigma);
		try
			{
			return getLongestSuffix(prefix).getProbs().get(sigma);
			}
		catch (DistributionException e)
			{
			logger.error("Error", e);
			throw new SequenceSpectrumException(e);
			}
		}

	/**
	 * Computes the conditional probability distribution of symbols given a prefix under the model, backing off to shorter
	 * prefixes as needed if the given prefix is not explicitly represented.
	 *
	 * @param prefix a byte array providing the conditioning prefix
	 * @return the Multinomial conditional distribution of symbols following the given prefix
	 */
	public Multinomial<Byte> conditionalsFrom(final byte[] prefix) throws SequenceSpectrumException
		{
		return getLongestSuffix(prefix).getProbs();
		}

	/**
	 * Computes the total log probability of generating the given sequence fragment under the model.  This differs from
	 * {@link #totalProbability(byte[])} in that the sequence fragment is not given explicitly but only as metadata.  Thus
	 * its probability may be computed from summary statistics that are already available in the given SequenceFragment
	 * rather than from the raw sequence.  Also, because these probabilities are typically very small, the result is
	 * returned in log space (indeed implementations will likely compute them in log space).
	 *
	 * @param sequenceFragment the SequenceFragment whose probability is to be computed
	 * @return the natural logarithm of the conditional probability (a double value between 0 and 1, inclusive)
	 */
	public double fragmentLogProbability(final SequenceFragment sequenceFragment, final boolean perSample)
			throws SequenceSpectrumException
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	/*	public int getNumberOfSamples()
		 {
		 throw new NotImplementedException();
		 }
*/
	/*
	public void runBeginTrainingProcessor() throws DistributionProcessorException
		{
		}


	public void runFinishTrainingProcessor() throws DistributionProcessorException
		{
		}
*/
	/**
	 * {@inheritDoc}
	 */
	public byte sample(final byte[] prefix) throws SequenceSpectrumException
		{
		try
			{
			return conditionalsFrom(prefix).sample();
			}
		catch (DistributionException e)
			{
			logger.error("Error", e);
			throw new SequenceSpectrumException(e);
			}
		}

	/**
	 * {@inheritDoc}
	 */
	public byte[] sample(final int length) throws SequenceSpectrumException
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public void setIgnoreEdges(final boolean b)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public void setImmutable()
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public boolean spectrumEquals(final SequenceSpectrum spectrum)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public double totalProbability(final byte[] s) throws SequenceSpectrumException
		{
		double result = 1.;
		// PERF inefficient due to allocating a separate byte[] for each prefix

		for (int i = 0; i < s.length; i++)
			{
			result *= conditionalProbability(s[i], DSArrayUtils.prefix(s, i));
			}

		return result;
		}

// -------------------------- OTHER METHODS --------------------------

	/*
   public int getMaxDepth()
	   {
	   if(maxdepth == 0)
		   {
		   maxdepth = super.getMaxDepth();
		   }
	   return maxdepth;
	   }*/

	public double getAvgDepth()
		{
		return avgdepth;
		}

	/**
	 * {@inheritDoc}
	 */
	public String getExclusiveLabel()
		{
		return label;
		}

	/*
	 public RonPSTNode getBackoffPrior(byte[] id) throws SequenceSpectrumException
		 {
		 return getLongestSuffix(ArrayUtils.suffix(id, 1));
		 }
 */

	private RonPSTNode getLongestSuffix(final byte[] bytes) throws SequenceSpectrumException
		{
		RonPSTNode currentNode = this;
		for (int i = bytes.length - 1; i >= 0; i--)
			{
			final RonPSTNode nextNode = currentNode.getUpstreamNode(bytes[i]);
			if (nextNode == null)
				{
				return currentNode;
				}
			currentNode = nextNode;
			}
		return currentNode;
		}

	public int getNumLeaves()
		{
		return leaves;
		}

	public int getNumNodes()
		{
		return total;
		}
	}
