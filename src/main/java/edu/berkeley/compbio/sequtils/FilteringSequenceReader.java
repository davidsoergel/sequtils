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

import java.io.IOException;
import java.util.Arrays;

// PERF

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */

public class FilteringSequenceReader implements SequenceReader
	{
	// ------------------------------ FIELDS ------------------------------

	//private static final int DEFAULT_BUFFER_SIZE = 16384;
	private SequenceReader base;
	private SubstitutionFilter filter;


	// --------------------------- CONSTRUCTORS ---------------------------
	/*	public FilteringSequenceReader(SequenceReader base, NucleotideFilter filter)
	   {
	   this(base, filter, DEFAULT_BUFFER_SIZE);
	   }*/

	public FilteringSequenceReader(SequenceReader base, SubstitutionFilter filter)
		{
		this.base = base;
		this.filter = filter;

		//	initMainBuffer(buffersize);
		//	initTranslationBuffer(buffersize);
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface SequenceReader ---------------------


	/**
	 * {@inheritDoc}
	 */
	public void close()
		{
		base.close();
		}

	/**
	 * {@inheritDoc}
	 */
	public String getName()
		{
		return base.getName();
		}

	/**
	 * {@inheritDoc}
	 */
	public long getTotalSequence()
		{
		return base.getTotalSequence();
		}

	/**
	 * {@inheritDoc}
	 */
	public byte read() throws IOException, FilterException, NotEnoughSequenceException
		{
		return filter.filter(base.read());
		}

	/**
	 * {@inheritDoc}
	 */
	public int read(byte[] buffer, int length) throws IOException, FilterException, NotEnoughSequenceException
		{
		int valid = base.read(buffer, length);
		filter.filter(buffer, valid);
		return valid;
		}

	/**
	 * Returns the next nucleotide, after mapping the byte through a translation table associated with the reader.
	 * Typically used to map Roman characters representing nucleotides to the integers 0-3.
	 *
	 * @return The next nucleotide, using the translated alphabet, or EOF if the section or file has ended
	 * @see #setTranslationAlphabet(byte[] alphabet)
	 */
	public int readTranslated() throws IOException, FilterException, NotEnoughSequenceException, TranslationException
		{
		byte c = read();
		for (int i = 0; i < translationAlphabet.length; i++)
			{
			if (c == translationAlphabet[i])
				{
				return i;
				}
			}
		throw new TranslationException("Character not in translation alphabet: " + c);
		}

	/**
	 * resets the base reader.  Note that the filter will be applied anew on the next read, so the provided sequence may
	 * differ after a reset.
	 */
	public void reset()
		{
		base.reset();
		}

	/**
	 * {@inheritDoc}
	 */
	public void seek(SequenceFragmentMetadata section) throws IOException
		{
		base.seek(section);
		}

	/**
	 * {@inheritDoc}
	 */
	public void seek(SequenceFragmentMetadata section, long offset) throws IOException
		{
		base.seek(section, offset);
		}

	/**
	 * Set the character mapping to be used when reading nucleotides.
	 *
	 * @param alphabet A byte[] representing the byte translation table.  The mapping is specified in reverse: the desired
	 *                 translated value is the index of the input array, and the values in the array are the corresponding
	 *                 input characters to be mapped.  For example, the mapping 'A'->0, 'C'->1, 'G'->2, 'T'->3 is
	 *                 represented as new byte[]{'A','C','G','T'}.
	 * @see #readTranslated()
	 */
	public boolean setTranslationAlphabet(byte[] alphabet)
		{
		if (!Arrays.equals(this.translationAlphabet, alphabet))
			{
			this.translationAlphabet = alphabet;
			return true;
			}
		return false;
		}

	private byte[] translationAlphabet;
	}
