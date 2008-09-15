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


package edu.berkeley.compbio.sequtils;


/**
 * A filter that mutates nucleotides one at a time (i.e., without taking context into account).
 *
 * @author David Soergel
 * @version $Id$
 */
public abstract interface NucleotideFilter
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
