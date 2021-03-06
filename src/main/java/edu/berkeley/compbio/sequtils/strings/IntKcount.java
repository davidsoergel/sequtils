/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.stats.IntArrayContainer;

import java.util.Arrays;

/**
 * Represents the counts of a set of given patterns appearing in a string.  Typically the patterns counted are simply
 * all subsequences up to a length K.  How the symbols are interpreted and counted is up to the implementation and the
 * associated KcountScanner, so it's easy for instance to make optimized scanners for certain kinds of sequences, or to
 * use compressed alphabets, and so forth.
 * <p/>
 * Kcounts may be related through a "parent" link.  The interpretation depends on the implementation, but a typical
 * usage would be for the "parent" Kcount to contain less-specific information, i.e. fewer counts of more general
 * patterns, perhaps generated by aggregating the specific counts from the child.
 * <p/>
 * ("Parent" may not be the best name for this... perhaps "generalized" or some such?)
 *
 * @author David Soergel
 * @version $Id: IntKcount.java 442 2009-06-15 17:53:56Z soergel $
 */
public abstract class IntKcount<T extends IntKcount> extends Kcount<T> implements IntArrayContainer
	{
// ------------------------------ FIELDS ------------------------------

	protected int[] counts;


// --------------------------- CONSTRUCTORS ---------------------------

	/**
	 * Creates a new Kcount instance.
	 */
	public IntKcount()
		{
		}

// ------------------------ CANONICAL METHODS ------------------------

	/**
	 * Clone this object.  Should behave like {@link Object#clone()} except that it returns an appropriate type and so
	 * requires no cast.  Also, we insist that is method be implemented in inheriting classes, so it does not throw
	 * CloneNotSupportedException.
	 *
	 * @return a clone of this instance.
	 * @see Object#clone
	 * @see java.lang.Cloneable
	 */
	@Override
	public abstract T clone();


// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Clusterable ---------------------

	/**
	 * Test whether the given pattern counts are equal to those stored here.  Differs from equals() in that implementations
	 * of this interface may contain additional state which make them not strictly equal; here we're only interested in
	 * whether they're equal as far as this interface is concerned.
	 *
	 * @param other The Kcount to compare against
	 * @return True if they are equivalent, false otherwise
	 */
	public boolean equalValue(final T other)
		{
		return Arrays.equals(counts, other.counts);
		}

// --------------------- Interface IntArrayContainer ---------------------

	/**
	 * Returns an array of the counts.  The mapping of patterns to array indices is implementation-dependent.  A typical
	 * implementation will order all possible patterns up to length K in lexical order according to the symbol alphabet.
	 *
	 * @return The array of counts
	 */
	public int[] getArray()
		{
		return counts;
		}

// --------------------- Interface SequenceSpectrum ---------------------

	/*		{
	   if (numberOfBins == 1)
		   {
		   return counts[0];
		   }
	   return getParent().getNumberOfSamples() - getLastWords().size();// due to last word
	   }*/

	//	public abstract List<Integer> getLastWords();

	/**
	 * Test whether the given sequence statistics are equivalent to this one.  Differs from equals() in that
	 * implementations of this interface may contain additional state which make them not strictly equal; here we're only
	 * interested in whether they're equal as far as this interface is concerned.  Equivalent to {@link
	 * #equalValue(IntKcount)} in this case.
	 *
	 * @param spectrum the SequenceSpectrum
	 * @return the boolean
	 */
	public boolean spectrumEquals(final SequenceSpectrum spectrum)
		{
		return equalValue((T) spectrum);
		}
	}
