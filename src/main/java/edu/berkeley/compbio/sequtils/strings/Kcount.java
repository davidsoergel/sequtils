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
