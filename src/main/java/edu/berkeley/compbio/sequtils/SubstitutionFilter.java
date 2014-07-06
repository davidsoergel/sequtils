/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;


/**
 * A filter that mutates nucleotides one at a time (i.e., without taking context into account).
 *
 * @author David Soergel
 * @version $Id$
 */
public abstract interface SubstitutionFilter
	{
	// -------------------------- OTHER METHODS --------------------------

	//private static final Logger logger = Logger.getLogger(NucleotideFilter.class);

	/**
	 * Mutate the provided nucleotide and return it.  Note that many implementations may actually change the nucleotide
	 * only rarely, so the returned byte will frequently be the same as the argument.
	 *
	 * @param b the byte representing the nucleotide character (simply cast from char)
	 * @return a byte containing the filtered nucleotide
	 * @throws FilterException
	 */
	byte filter(byte b) throws FilterException;

	/**
	 * Mutate each nucleotide in the provided byte buffer, in place.
	 *
	 * @param buffer a byte[] containing the nucleotide sequence to be mutated
	 * @throws FilterException
	 */
	void filter(byte[] buffer) throws FilterException;

	/**
	 * Mutate the first n nucleotides in the provided byte buffer, in place.  Useful in cases where only the initial part
	 * of the buffer is valid.
	 *
	 * @param buffer a byte[] containing the nucleotide sequence to be mutated
	 * @param length the number of symbols to mutate
	 * @throws FilterException
	 */
	void filter(byte[] buffer, int length) throws FilterException;
	}
