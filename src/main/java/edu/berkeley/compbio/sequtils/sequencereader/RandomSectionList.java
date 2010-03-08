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


package edu.berkeley.compbio.sequtils.sequencereader;

import com.davidsoergel.dsutils.math.MersenneTwisterFast;
import edu.berkeley.compbio.sequtils.FilterException;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceFragmentMetadata;
import edu.berkeley.compbio.sequtils.TranslationException;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Provides randomly generated sequence implementing SequenceReader
 *
 * @author David Tulga
 * @version $Id: RandomSectionList.java 1255 2009-10-12 07:56:41Z soergel $
 */
public class RandomSectionList implements SectionList
	{
	// ------------------------------ FIELDS ------------------------------

	//static MersenneTwisterFast mtf = new MersenneTwisterFast();


	// --------------------------- CONSTRUCTORS ---------------------------

	public RandomSectionList()
		{
		// Do Nothing
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface SectionList ---------------------

	public SequenceFragmentMetadata next()//throws IOException, NotEnoughSequenceException
		{
		return new SequenceFragmentMetadata(null, randomName(), null, MersenneTwisterFast.randomInt(Integer.MAX_VALUE));
		}

	public SequenceFragmentMetadata nextShuffled()//throws IOException, NotEnoughSequenceException
		{
		return new SequenceFragmentMetadata(null, randomName(), null, MersenneTwisterFast.randomInt(Integer.MAX_VALUE));
		}

	@NotNull
	public SequenceFragmentMetadata randomSectionFragment() throws NotEnoughSequenceException
		{
		return new SequenceFragmentMetadata(null, randomName(), null, MersenneTwisterFast.randomInt(Integer.MAX_VALUE));
		}

	public SequenceFragmentMetadata randomSectionLengthWeighted() throws NotEnoughSequenceException
		{
		return new SequenceFragmentMetadata(null, randomName(), null, MersenneTwisterFast.randomInt(Integer.MAX_VALUE));
		}

	public SequenceFragmentMetadata randomSectionUniform() throws IOException
		{
		return new SequenceFragmentMetadata(null, randomName(), null, MersenneTwisterFast.randomInt(Integer.MAX_VALUE));
		}

/*	public void releaseCachedResources()
		{
		}
*/
	// --------------------- Interface SequenceReader ---------------------

	public void close()
		{
		// Do Nothing
		}

	public String getName()
		{
		return "Random Sequence";
		}

	public void reset()
		{
		//Do Nothing
		}

	public long getTotalSequence()
		{
		return Long.MAX_VALUE;
		}

	public byte read()//throws NotEnoughSequenceException
		{
		switch (MersenneTwisterFast.randomInt(4))
			{
			case 0:
				return 'A';
			case 1:
				return 'T';
			case 2:
				return 'G';
			default:
				return 'C';
			}
		}

	public int read(byte[] buffer, int length)
		{
		for (int i = 0; i < length; i++)
			{
			buffer[i] = read();
			}

		return length;
		}

	public int readTranslated() throws IOException, FilterException, NotEnoughSequenceException, TranslationException
		{
		throw new NotImplementedException();
		}

	public void seek(SequenceFragmentMetadata section) throws IOException
		{
		//Do Nothing
		}

	public void seek(SequenceFragmentMetadata section, long offset) throws IOException
		{
		//Do Nothing
		}

	public boolean setTranslationAlphabet(byte[] alphabet)
		{
		throw new NotImplementedException();
		}

	// -------------------------- OTHER METHODS --------------------------

	private String randomName()
		{
		return "Random Sequence " + MersenneTwisterFast.randomInt(Integer.MAX_VALUE);
		}

	public byte[] read(int len)
		{
		byte[] seq = new byte[len];
		for (int i = 0; i < len; i++)
			{
			switch (MersenneTwisterFast.randomInt(4))
				{
				case 0:
					seq[i] = 'A';
					break;
				case 1:
					seq[i] = 'T';
					break;
				case 2:
					seq[i] = 'G';
					break;
				default:
					seq[i] = 'C';
				}
			}
		return seq;
		}


	public SequenceFragmentMetadata getRootMetadata()
		{
		return next();
		}
	}
