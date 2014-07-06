/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.stats.DiscreteDistribution1D;
import edu.berkeley.compbio.ml.cluster.AdditiveClusterable;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: Kcount.java 461 2009-06-23 03:00:32Z soergel $
 */

public abstract class Kcount<T extends Kcount> extends HierarchicalSpectrum<T>
		implements AdditiveClusterable<T>, SequenceSpectrum<T>, DiscreteDistribution1D
	{
// ------------------------------ FIELDS ------------------------------

	protected static final double UNKNOWN_SUM_OF_COUNTS = -1;

	protected static final int UNKNOWN_NUMBER_OF_SAMPLES = -1;
	protected int k;
	protected int numberOfBins;

	//protected int numberOfSamples = UNKNOWN_NUMBER_OF_SAMPLES;

	protected long originalSequenceLength = 0; // = UNKNOWN_LENGTH;
	protected long unknownCount = 0;

// --------------------- GETTER / SETTER METHODS ---------------------

	/**
	 * Returns the pattern length K that this Kcount handles (i.e. the number of symbols per word being counted)
	 *
	 * @return the pattern length
	 */
	public int getK()
		{
		return k;
		}

	/**
	 * Returns the number of bins, which is the number of possible patterns in this Kcount.  Typically this will the
	 * alphabet size to the power of K.  Assuming a straightforward implementation, this will equal getCounts().size().
	 *
	 * @return The number of bins
	 */
	public int getNumberOfBins()
		{
		return numberOfBins;
		}

	/**
	 * Returns the length of the sequence that was scanned to produce this spectrum.  This number may be greater than that
	 * given by {@link #getNumberOfSamples()} because every symbol is not necessarily counted as a sample, depending on the
	 * implementation.
	 *
	 * @return the length (type int) of this Kcount object.
	 * @see #addUnknown()
	 */
	public long getOriginalSequenceLength()
		{
		return originalSequenceLength;
		}

	public long getUnknownCount()
		{
		return unknownCount;
		}
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface AdditiveClusterable ---------------------

	/**
	 * updates this object by subtracting another one from it.
	 *
	 * @param object the object to subtract from this one
	 */
	public abstract void decrementBy(T object);

	/**
	 * updates this object by adding another one to it.
	 *
	 * @param object the object to add to this one
	 */
	public abstract void incrementBy(T object);

	/**
	 * Returns a new object representing the difference between this one and the given argument.
	 *
	 * @param object the object to be subtracted from this one
	 * @return the difference between this object and the argument
	 */
	public T minus(final T object)
		{
		final T result = clone();
		result.decrementBy(object);
		//result.getMetadata().setSequenceName("minus result");
		return result;
		}

	/**
	 * Returns a new object representing the sum of this one and the given argument.
	 *
	 * @param object the object to be added to this one
	 * @return the sum of this object and the argument
	 */
	public T plus(final T object)
		{
		final T result = clone();
		result.incrementBy(object);
		//result.getMetadata().setSequenceName("plus result");
		return result;
		}

// -------------------------- OTHER METHODS --------------------------

	/**
	 * Adds an "unknown" sample to this kcount, indicating that a character was consumed without incrementing any counter.
	 */
	public void addUnknown()
		{
		//metadata.length++;//incrementLength();
		originalSequenceLength++;
		unknownCount++;
		}

	public double unknownProportion()
		{
		return (double) unknownCount / (double) originalSequenceLength;
		}

	/**
	 * Returns the number of real samples on which this Kcount is based, not including pseudocounts.
	 *
	 * @return The number of samples
	 */
	/*	public int getNumberOfSamples()
		 {
		 return numberOfSamples;
		 }
 */

	/**
	 * Returns the the sum of the counts.  May be completely different from the number of samples due to smoothing or
	 * normalization.  Even for raw counts, this may be greater than the number of samples due to pseudocounts.  May differ
	 * on different levels of a Kcount due to lastwords.
	 */
	public abstract double getSumOfCounts();

	@Override
	public boolean hasParent()
		{
		return k > 0;
		}

	public abstract int idForSequence(byte[] seq);

	public abstract byte lastSymbolForId(int id);

	public abstract byte[] prefixForId(int id);

	public abstract int prefixId(int id);

	public abstract byte[] sequenceForId(int i);

	public abstract int suffixId(int id);
	}
