/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.DSArrayUtils;
import com.davidsoergel.stats.DistributionProcessor;
import com.davidsoergel.stats.DistributionProcessorException;
import edu.berkeley.compbio.sequtils.FilterException;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceReader;
import edu.berkeley.compbio.sequtils.TranslationException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: RonPSA.java 461 2009-06-23 03:00:32Z soergel $
 */

public class RonPSA extends RonPSANode
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(RonPSA.class);


// --------------------------- CONSTRUCTORS ---------------------------

	public RonPSA()
		{

		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface SequenceSpectrum ---------------------

	//	@NotNull
	//	public MarkovTreeNode nextNode(byte sigma)
	//		{
	//		return children.get(sigma);
	/*		MarkovTreeNode result = children.get(sigma);
					if (result == null)
						{
						result = backlinkChildren.get(sigma);
						}
					return result;*/
	//		}


	/**
	 * Computes the total log probability of generating the given sequence fragment under the model.  This differs from
	 * {@link #totalProbability(byte[])} in that the sequence fragment is not given explicitly but only as metadata.  Thus
	 * its probability may be computed from summary statistics that are already available in the given SequenceFragment
	 * rather than from the raw sequence.  Also, because these probabilities are typically very small, the result is
	 * returned in log space (indeed implementations will likely compute them in log space).
	 * <p/>
	 * Note this computes the probability only in the forward direction (i.e., it does not take a reverse complement
	 * also).
	 *
	 * @param sequenceFragment the SequenceFragment whose probability is to be computed
	 * @return the natural logarithm of the conditional probability (a double value between 0 and 1, inclusive)
	 */
	@Override
	public double fragmentLogProbability(final SequenceFragment sequenceFragment, final boolean perSample)
			throws SequenceSpectrumException
		{
		synchronized (sequenceFragment.getReaderForSynchronizing())
			{
			// simply follow the MarkovTreeNode as a state machine, using backlinks
			final SequenceReader in;
			try
				{
				in = sequenceFragment.getResetReader();
				}
			catch (NotEnoughSequenceException e)
				{
				throw new SequenceSpectrumRuntimeException(e);
				}
			in.setTranslationAlphabet(getAlphabet());
			double logprob = 0;
			RonPSANode currentNode = this;
			long count = 0;
			int samples = 0;
			final long desiredLength = sequenceFragment.getDesiredLength();
			while (count < desiredLength)
				{
				try
					{
					final int c = in.readTranslated();
					final double logConditionalProbability = currentNode.logConditionalProbabilityByAlphabetIndex(c);

					/*	logger.debug(
				  "Conditional at " + new String(currentNode.getIdBytes()) + " " + (char) getAlphabet()[c] + " = "
						  + currentNode.conditionalProbabilityByAlphabetIndex(c));
						  */
					logprob += logConditionalProbability;
					samples++;
					currentNode = currentNode.nextNodeByAlphabetIndex(c);
					}
				catch (NotEnoughSequenceException e)
					{
					logger.error("Error", e);
					throw new SequenceSpectrumException(e);
					}
				catch (TranslationException e)
					{
					// probably a bad input character
					logger.debug(" at " + in, e);

					// ignore it, but reset the state machine
					currentNode = this;
					}
				catch (IOException e)
					{
					logger.error("Error", e);
					throw new SequenceSpectrumException(e);
					}
				catch (FilterException e)
					{
					logger.error("Error", e);
					throw new SequenceSpectrumException(e);
					}
				count++;
				}


			if (perSample)
				{
				// we have ln(product(p) == sum(ln(p)).
				// The geometric mean is exp(sum(ln(p))/n), so to get ln(geometric mean) we need only divide by n.
				logprob /= samples;
				}


			return logprob;
			}
		}

// -------------------------- OTHER METHODS --------------------------

	public void learn(final double branchAbsoluteMin, final double branchConditionalMin, final double pRatioMinMax,
	                  final int l_max, final SequenceSpectrum fromSpectrum,
	                  final DistributionProcessor<RonPSA> completionProcessor) throws DistributionProcessorException
		{
		final RonPST pst = new RonPST(branchAbsoluteMin, branchConditionalMin, pRatioMinMax, l_max, fromSpectrum);
		convertFrom(pst);
		if (completionProcessor != null)
			{
			completionProcessor.process(this);
			}
		diagnostics();
		}

	/*	public RonPSA(byte[] alphabet)
	   {
	   super(new byte[0], alphabet);
	   }*/

	/**
	 * Convert a PST into a PSA as described in Ron et al Appendix B.  Assumes we're starting from an empty node.
	 *
	 * @param pst
	 */
	private void convertFrom(final RonPST pst)
		{
		setAlphabet(pst.getAlphabet());
		setId(DSArrayUtils.EMPTY_BYTE_ARRAY);
		copyProbsFromSpectrumRecursively(pst);
		buildPSARecursivelyFromPSTNode(pst, pst);
		updateLogProbsRecursive();
		setOriginalSequenceLength(pst.getOriginalSequenceLength());
		setUnknownCount(pst.getUnknownCount());

		// at this point we have a pure tree with a full set of probabilities at every node

		setBacklinks();
		}

	private void buildPSARecursivelyFromPSTNode(final RonPSTNode pstNode, final SequenceSpectrum spectrum)
		{
		getOrAddNodeAndIntermediates(pstNode.getIdBytes(), spectrum);

		for (final RonPSTNode pstUpstream : pstNode.getUpstreamNodes())
			{
			if (pstUpstream != null)
				{
				buildPSARecursivelyFromPSTNode(pstUpstream, spectrum);
				}
			}
		}

	private RonPSANode getOrAddNodeAndIntermediates(final byte[] id, final SequenceSpectrum spectrum)
		{
		//RonPSANode psaNode = new RonPSANode(id, alphabet);
		RonPSANode result = getDescendant(id);
		if (result == null)
			{
			final RonPSANode parent = getOrAddNodeAndIntermediates(DSArrayUtils.prefix(id, id.length - 1), spectrum);
			result = parent.addChild(id[id.length - 1]);
			result.copyProbsFromSpectrumRecursively(spectrum);
			}
		return result;
		}

	private List<RonPSANode> setBacklinks()
		{
		final List<RonPSANode> result = new LinkedList();
		final Queue<RonPSANode> breadthFirstQueue = new LinkedList<RonPSANode>();
		breadthFirstQueue.add(this);
		while (!breadthFirstQueue.isEmpty())
			{
			final RonPSANode next = breadthFirstQueue.remove();
			next.setBacklinksUsingRoot(this, breadthFirstQueue);
			result.add(next);
			}
		return result;
		}
	}
