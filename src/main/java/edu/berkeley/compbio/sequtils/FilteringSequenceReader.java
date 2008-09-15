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

import org.apache.commons.lang.NotImplementedException;

import java.io.IOException;


/**
 * @author <a href="mailto:dev.davidsoergel.com">David Soergel</a>
 * @version $Id$
 */

public class FilteringSequenceReader implements SequenceReader
	{
	// ------------------------------ FIELDS ------------------------------

	private SequenceReader base;
	private NucleotideFilter filter;


	// --------------------------- CONSTRUCTORS ---------------------------

	public FilteringSequenceReader(SequenceReader base, NucleotideFilter filter)
		{
		this.base = base;
		this.filter = filter;
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
	public int getTotalSequence()
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
	public void seek(SequenceFragmentMetadata section, int offset) throws IOException
		{
		base.seek(section, offset);
		}

	/**
	 * {@inheritDoc}
	 */
	public void setTranslationAlphabet(byte[] alphabet)
		{
		throw new NotImplementedException();
		}

	/**
	 * {@inheritDoc}
	 */
	public int readTranslated() throws IOException, FilterException, NotEnoughSequenceException, TranslationException
		{
		throw new NotImplementedException();
		}
	}
