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


package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceFragmentMetadata;
import edu.berkeley.compbio.sequtils.sequencereader.SectionList;
import edu.berkeley.compbio.sequtils.strings.SequenceFragment;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Provides a SequenceFragmentIterator that reads sequential mate-pairs
 *
 * @author David Tulga
 * @version $Id: SequentialMatePairSFI.java 1324 2010-03-08 22:44:49Z soergel $
 */
public class SequentialMatePairSFI extends ScanningSFI
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SequentialMatePairSFI.class);

	//private SectionList sectionList;

	//private KcountScanner scanner;

	private long charactersRead = 0;

//	private SequenceFragment theNextKcount = null;


	// -------------------------- STATIC METHODS --------------------------

	public static boolean isMatePair(SequenceFragmentMetadata k1, SequenceFragmentMetadata k2)
		{
		if (k1 == null || k2 == null)
			{
			return false;
			}

		String id1 = k1.getSequenceName();
		String id2 = k2.getSequenceName();
		//System.out.println(id1);
		//System.out.println(id2);
		//assert id1.substring(0,id1.length()-2).equals(id2.substring(0,id2.length()-2));
		//assert id1.substring(id1.length()-1,id1.length()).equals(id2.substring(id2.length()-1,id2.length()));
		//assert id1.substring(id1.length()-2,id1.length()-1).equals("b");
		//assert id2.substring(id2.length()-2,id2.length()-1).equals("g");

		String base1 = id1.substring(0, id1.length() - 3);
		String base2 = id2.substring(0, id2.length() - 3);

		char sep1 = id1.charAt(id1.length() - 3);
		char sep2 = id2.charAt(id2.length() - 3);

		char end1 = id1.charAt(id1.length() - 2);
		char end2 = id2.charAt(id2.length() - 2);

		char cloneNumber1 = id1.charAt(id1.length() - 1);
		char cloneNumber2 = id2.charAt(id2.length() - 1);

		if (sep1 != '.')
			{
			logger.warn("Unknown id format (not mate pair): " + id1);
			return false;
			}
		if (sep2 != '.')
			{
			logger.warn("Unknown id format (not mate pair): " + id2);
			return false;
			}

		if (base1.equals(base2) && cloneNumber1 == cloneNumber2 && ((end1 == 'b' && end2 == 'g')
		                                                            || (end2 == 'b' && end1 == 'g')
		                                                            || (end1 == 'x' && end2 == 'y') || (end2 == 'x'
		                                                                                                && end1
		                                                                                                   == 'y')))
			{
			return true;
			}
		else
			{
			//logger.warn("Not mate pairs: " + id1 + " " + id2);
			return false;
			}
		}

	@NotNull
	public static SequenceFragment joinMatePair(SequenceFragment k1, SequenceFragment k2)
		{
		// redundant, but so what, it's fast
		String id1 = k1.getSequenceName();
		String base1 = id1.substring(0, id1.length() - 3);
		char sep1 = id1.charAt(id1.length() - 3);
		char cloneNumber1 = id1.charAt(id1.length() - 1);

		SequenceFragment result = k1.plus(k2);
		result.setSequenceName(base1 + sep1 + cloneNumber1);
		// note the length is already set

		return result;
		}

	// --------------------------- CONSTRUCTORS ---------------------------

	public SequentialMatePairSFI(SectionList input) throws IOException
		{
		super(input);
		//sectionList = input;//new SectionSequenceFragmentIterator(input);
		//this.scanner = scanner;
		//scanner = new ExactKcountScanner();
		}

	// --------------------- GETTER / SETTER METHODS ---------------------

	@Override
	public long getCharactersRead()
		{
		return charactersRead;
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface Iterator ---------------------


	public SequenceFragment next()
		{
		// return theSectionList.hasNext(); // doesn't guarantee that the next NucleotideKcount is not null

		// we don't use a SectionSequenceFragmentIterator here, because that way we'd unnecessarily count
		// every section whether or not it ends up in a mate pair

		try
			{

			SequenceFragmentMetadata s1;
			SequenceFragmentMetadata s2 = sectionList.next();
			SequenceFragment result = null;
			while (true) //(s2 != null)
				{
				s1 = s2;
				s2 = sectionList.next();
				if (isMatePair(s1, s2))
					{
					sectionList.seek(s1);
					SequenceFragment k1 =
							new SequenceFragment(s1.getParentMetadata(), s1.getSequenceName(), s1.getStartPosition(),
							                     sectionList, SequenceFragment.UNKNOWN_LENGTH, spectrumScanner);
					k1.checkAvailable();
					//Kcount k1 = scanner.scanSequence(theSectionList, Integer.MAX_VALUE);

					sectionList.seek(s2);
					//Kcount k2 = scanner.scanSequence(theSectionList, Integer.MAX_VALUE);
					SequenceFragment k2 =
							new SequenceFragment(s1.getParentMetadata(), s1.getSequenceName(), s1.getStartPosition(),
							                     sectionList, SequenceFragment.UNKNOWN_LENGTH, spectrumScanner);
					k2.checkAvailable();
					result = joinMatePair(k1, k2);
					break;
					}
				//theNextKcount = joinPotentialMatePair(s1, s2);
				}
			charactersRead += result.getLength();
			return result;
			}
		catch (IOException e)
			{
			logger.error("Error", e);
			throw new NoSuchElementException();
			}
		catch (NotEnoughSequenceException e)
			{
			// no problem, end of sequence
			throw new NoSuchElementException();
			}
		}

	/*
	public SequenceFragment next()
		{
		if (!hasNext())
			{
			throw new NoSuchElementException();
			}
		SequenceFragment result = theNextKcount;
		theNextKcount = null;
		charactersRead += result.getLength();
		return result;
		}
		*/

	// -------------------------- OTHER METHODS --------------------------

	@Override
	public void close()
		{
		sectionList.close();
		}

	@Override
	public int estimatedTotalSamples()
		{
		throw new NotImplementedException();
		}

	@Override
	public long getTotalSequence()
		{
		return sectionList.getTotalSequence();
		}

	/*@Override
	public void releaseCachedResources()
		{
		theSectionList.releaseCachedResources();
		}
*/

	public void reset() //throws IOException
		{
		sectionList.reset();
		}
	}
