/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.GenericFactory;
import com.davidsoergel.dsutils.GenericFactoryAware;
import com.davidsoergel.stats.DistributionProcessorException;
import com.davidsoergel.stats.Multinomial;
import edu.berkeley.compbio.ml.cluster.AdditiveClusterable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a statistical model of a sequence of bytes.  The generic type tells what other kinds of SequenceSpectrum
 * classes this one is compatible with under addition; frequently this is just the implementation class itself, but it
 * may also be a superclass or interface that is compatible under addition via {@link AdditiveClusterable}.  The fact
 * that this class extends AdditiveClusterable expresses the requirement that there be a sensible (i.e., commutative and
 * associative) "addition" operation on these models, given by {@link #plus(edu.berkeley.compbio.ml.cluster.AdditiveClusterable)}.
 *
 * @author David Soergel
 * @version $Id
 */
public interface SequenceSpectrum<T extends SequenceSpectrum>
		extends AdditiveClusterable<T>, Cloneable, GenericFactoryAware
	{
// ------------------------ CANONICAL METHODS ------------------------

	/**
	 * {@inheritDoc}
	 */
	T clone();


// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface GenericFactoryAware ---------------------

	/**
	 * {@inheritDoc}
	 */
	@NotNull
	GenericFactory getFactory();


// -------------------------- OTHER METHODS --------------------------

	/**
	 * Computes the conditional probability of generating a symbol given a prefix under the model.
	 *
	 * @param sigma  a byte specifying the symbol whose probability is to be computed
	 * @param prefix a byte array providing the conditioning prefix
	 * @return the conditional probability, a double value between 0 and 1, inclusive
	 * @throws SequenceSpectrumException when anything goes wrong
	 * @see #fragmentLogProbability(SequenceFragment)
	 */
	double conditionalProbability(byte sigma, byte[] prefix)
			throws SequenceSpectrumException;// throws SequenceSpectrumException;


	/**
	 * Computes the conditional probability distribution of symbols given a prefix under the model.
	 *
	 * @param prefix a byte array providing the conditioning prefix
	 * @return the Multinomial conditional distribution of symbols following the given prefix
	 * @throws SequenceSpectrumException when anything goes wrong
	 */
	Multinomial<Byte> conditionalsFrom(byte[] prefix)
			throws SequenceSpectrumException;// throws SequenceSpectrumException;

	/**
	 * Computes the total log probability of generating the given sequence fragment under the model.  This differs from
	 * {@link #totalProbability(byte[])} in that the sequence fragment is not given explicitly but only as metadata.  Thus
	 * its probability may be computed from summary statistics that are already available in the given SequenceFragment
	 * rather than from the raw sequence.  Also, because these probabilities are typically very small, the result is
	 * returned in log space (indeed implementations will likely compute them in log space).
	 *
	 * @param sequenceFragment the SequenceFragment whose probability is to be computed
	 * @param perSample        normalize the probability by the number of samples found in the SequenceFragment, i.e., take
	 *                         the geometric mean
	 * @return the natural logarithm of the conditional probability (a double value between 0 and 1, inclusive)
	 */
	double fragmentLogProbability(SequenceFragment sequenceFragment, boolean perSample)
			throws SequenceSpectrumException;

	/**
	 * Returns the alphabet of this SequenceSpectrum object.
	 *
	 * @return the alphabet (type byte[]) of this SequenceSpectrum object.
	 */
	byte[] getAlphabet();

	/**
	 * Returns the maximum length of substrings considered in computing this statistical model of the sequence.  Our
	 * implicit assumption is that the sequences being modeled have some correlation length, and thus that statistical
	 * models of them can be built from substrings up to that length.  Thus, this method tells the maximum correlation
	 * length provided by the model.  A manifestation of this is that conditional probabilities of symbols given a prefix
	 * will cease to change as the prefix is lengthened (to the left) past this length.
	 *
	 * @return the maximum correlation length considered in the model.
	 */
	int getMaxDepth();

	/**
	 * Returns the length of the sequence that was scanned to produce this spectrum.  This number may be greater than that
	 * given by {@link #getNumberOfSamples()} because every symbol is not necessarily counted as a sample, depending on the
	 * implementation.
	 *
	 * @return the length (type int) of this Kcount object.
	 * @see #addUnknown()
	 */
	long getOriginalSequenceLength();

	/**
	 * Returns the number of characters from the original sequence that were not counted because they were unknown
	 *
	 * @return
	 */
	long getUnknownCount();

	/**
	 * Returns the number of real samples on which this spectrum is based, not including pseudocounts or any other smoothing whatnot.
	 *
	 * @return The number of samples
	 */

	//	int getNumberOfSamples();  // turns out we don't need this

	//void addPseudocounts();


	// REVIEW really the lifecycle of a SequenceSpectrum should be managed more carefully, i.e. as a Builder

	/**
	 * A SequenceSpectrum object that is being learned from data may have a DistributionProcessor associated with it that
	 * should be run before the training begins, such as a pseudocountadder; if so, this method executes the processor.
	 *
	 * @throws DistributionProcessorException when the processor fails for any reason
	 */
//	void runBeginTrainingProcessor() throws DistributionProcessorException;

	/**
	 * A SequenceSpectrum object that is being learned from data may have a DistributionProcessor associated with it that
	 * should be run after the training is finished, such as a smoothing processor; if so, this method executes the
	 * processor.
	 *
	 * @throws DistributionProcessorException when the processor fails for any reason
	 */
//	void runFinishTrainingProcessor() throws DistributionProcessorException;

	/**
	 * Chooses a random symbol according to the conditional probabilities of symbols following the given prefix.  Shortcut
	 * equivalent to conditionalsFrom(prefix).sample().byteValue()
	 *
	 * @param prefix a byte array providing the conditioning prefix
	 * @return the chosen symbol
	 */
	byte sample(byte[] prefix) throws SequenceSpectrumException;

	/**
	 * Chooses a random string according to the conditional probabilities of symbols.
	 *
	 * @param length the length of the desired random string
	 * @return a byte[] of the desired length sampled from this distribution
	 */
	byte[] sample(int length) throws SequenceSpectrumException;

	//void runCompletionProcessor() throws DistributionProcessorException;

	/**
	 * Specify whether or not this sequence spectrum attempts to account for edge effects, i.e. the first and last words of
	 * each sequence.  Accounting for these properly introduces a good deal of complexity and requires extra time, so
	 * considering spectra to describe biases is infinitely long sequn
	 *
	 * @param b the ignoreEdges
	 * @see FirstWordProvider
	 */
	void setIgnoreEdges(boolean b);


	/**
	 * Declare that this spectrum should not change anymore.  In practice this is not enforced; this is used only to free
	 * up memory by forgetting data that would be needed to recompute the spectrum if it were to change.
	 */
	void setImmutable();

	/**
	 * Test whether the given sequence statistics are equivalent to this one.  Differs from equals() in that
	 * implementations of this interface may contain additional state which make them not strictly equal; here we're only
	 * interested in whether they're equal as far as this interface is concerned.
	 * <p/>
	 * Naive implementations will simply test for exact equality; more sophisticated implementations ought to use a more
	 * rigorous idea of "statistically equivalent", though in that case we'll probabably need to provide more parameters,
	 * such as a p-value threshold to use.  Note that the spectra know the number of samples used to generate them, so at
	 * least that's covered.
	 *
	 * @param spectrum the SequenceSpectrum to compare
	 * @return True if the spectra are equivalent, false otherwise
	 */
	boolean spectrumEquals(SequenceSpectrum spectrum);

	/**
	 * Computes the probability of generating the given sequence under the model.
	 *
	 * @param s a byte array
	 * @return the probability, a double value between 0 and 1, inclusive
	 */
	double totalProbability(byte[] s) throws SequenceSpectrumException;// throws SequenceSpectrumException;


//	public void releaseCachedResources();
	}
