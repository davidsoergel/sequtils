/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
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
