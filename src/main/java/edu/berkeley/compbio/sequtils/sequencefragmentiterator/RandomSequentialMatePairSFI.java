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
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Provides a SequenceFragmentIterator that reads random mate-pairs
 *
 * @author David Tulga
 * @version $Id: RandomSequentialMatePairSFI.java 1324 2010-03-08 22:44:49Z soergel $
 */
public class RandomSequentialMatePairSFI extends ScanningSFI
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(RandomSequentialMatePairSFI.class);

	//private SectionSequenceFragmentIterator sectionIterator;
	//private int totalSequence;
	private int maxAttempts = 20;

	private int matePairsRead;
	private int maxCount = Integer.MAX_VALUE;

	private int charactersRead = 0;


	// --------------------------- CONSTRUCTORS ---------------------------

	public RandomSequentialMatePairSFI(SectionList input) throws IOException
		{
		this(input, Integer.MAX_VALUE);
		}

	//private KcountScanner scanner;


	public RandomSequentialMatePairSFI(SectionList input, int maxCount) throws IOException
		{
		super(input);
		//sectionIterator = new SectionSequenceFragmentIterator(input);
		//totalSequence = sectionIterator.getTotalSequence();


		this.maxCount = maxCount;
		//this.scanner = scanner; //
		//scanner = new ExactKcountScanner();

		//loadNext();  // can't do this here because the spectrumScanner hasn't been injected yet
		}


	// --------------------- GETTER / SETTER METHODS ---------------------

	/*
   private static NucleotideKcount joinPotentialMatePair(NucleotideKcount k1, NucleotideKcount k2)
	   {
	   String id1 = k1.getMetadata().getSequenceName();
	   String id2 = k2.getMetadata().getSequenceName();
	   logger.debug("Joining Mate Pairs: " + id1 + " and " + id2);
	   //assert id1.substring(0,id1.length()-2).equals(id2.substring(0,id2.length()-2));
	   //assert id1.substring(id1.length()-1,id1.length()).equals(id2.substring(id2.length()-1,id2.length()));
	   //assert id1.substring(id1.length()-2,id1.length()-1).equals("b");
	   //assert id2.substring(id2.length()-2,id2.length()-1).equals("g");
	   if (id1.substring(0, id1.length() - 2).equals(id2.substring(0, id2.length() - 2))
			   && id1.substring(id1.length() - 1, id1.length()).equals(id2.substring(id2.length() - 1, id2.length()))
			   && id1.substring(id1.length() - 2, id1.length() - 1).equals("b") && id2
			   .substring(id2.length() - 2, id2.length() - 1).equals("g"))
		   {
		   NucleotideKcount result = k1.plus(k2);
		   result.getMetadata().setSequenceName(id1.substring(0,id1.length()-3));

		   return result;
		   }
	   else
		   {
		   return null;
		   }
	   }*/


	@Override
	public long getCharactersRead()
		{
		return charactersRead;
		//return sectionIterator.getCharactersRead();
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface Iterator ---------------------


	@NotNull
	public synchronized SequenceFragment next() throws NoSuchElementException
		{

		int attempts = 0;
		if (matePairsRead >= maxCount) //hasNext())
			{
			throw new NoSuchElementException();
			}
		while (attempts++ < maxAttempts)
			{
			try
				{
				SequenceFragment result = null;
				SequenceFragmentMetadata sectionA = sectionList.randomSectionUniform();
				sectionList.seek(sectionA);
				SequenceFragmentMetadata sectionB = sectionList.next();

				/* here we could just keep reading forwards until we get a pair, as in
				SequentialMatePairSequenceFragmentIterator; but then we'd be biased towards pairs
				that follow non-pairs.  So, we just advance one step if necessary,
				and then try a completely different position if that fails.
				*/

				if (SequentialMatePairSFI.isMatePair(sectionA, sectionB))
					{
					sectionList.seek(sectionA);
					SequenceFragment k1 = new SequenceFragment(sectionA.getParentMetadata(), sectionA.getSequenceName(),
					                                           sectionA.getStartPosition(), sectionList,
					                                           SequenceFragment.UNKNOWN_LENGTH, spectrumScanner);
					//Kcount k1 = scanner.scanSequence(theSectionList, Integer.MAX_VALUE);
					k1.checkAvailable();
					sectionList.seek(sectionB);
					//Kcount k2 = scanner.scanSequence(theSectionList, Integer.MAX_VALUE);
					SequenceFragment k2 = new SequenceFragment(sectionB.getParentMetadata(), sectionB.getSequenceName(),
					                                           sectionB.getStartPosition(), sectionList,
					                                           SequenceFragment.UNKNOWN_LENGTH, spectrumScanner);
					k2.checkAvailable();
					matePairsRead++;
					result = SequentialMatePairSFI.joinMatePair(k1, k2);
					}
				else
					{
					SequenceFragmentMetadata sectionC = sectionList.next();

					if (SequentialMatePairSFI.isMatePair(sectionB, sectionC))
						{
						sectionList.seek(sectionB);
						SequenceFragment k2 =
								new SequenceFragment(sectionB.getParentMetadata(), sectionB.getSequenceName(),
								                     sectionB.getStartPosition(), sectionList,
								                     SequenceFragment.UNKNOWN_LENGTH, spectrumScanner);
						k2.checkAvailable();
						sectionList.seek(sectionC);
						SequenceFragment k3 =
								new SequenceFragment(sectionC.getParentMetadata(), sectionC.getSequenceName(),
								                     sectionC.getStartPosition(), sectionList,
								                     SequenceFragment.UNKNOWN_LENGTH, spectrumScanner);
						k3.checkAvailable();
						matePairsRead++;
						result = SequentialMatePairSFI.joinMatePair(k2, k3);
						}
					}

				if (result != null)
					{

					//applyFragmentLabeller(result);

					return result;
					}
				}
			catch (IOException e)
				{
				// try again
				}
			catch (NotEnoughSequenceException e)
				{
				// we must have picked the very last section.  No problem, just try again
				}
			}
		throw new NoSuchElementException("Failed to find a SequenceFragment after " + maxAttempts + "attempts.");
		}

	// -------------------------- OTHER METHODS --------------------------

	@Override
	public void close()
		{
		sectionList.close();
		}

	/*
	 public RandomSequentialMatePairSequenceFragmentIterator(BufferedSequenceReader input, int maxCount)
			 throws IOException
		 {
		 sectionIterator = new WindowedSequenceFragmentIterator(input);
		 totalSequence = sectionIterator.getTotalSequence();
		 this.maxCount = maxCount;
		 }

	 public RandomSequentialMatePairSequenceFragmentIterator(BufferedSequenceReader input) throws IOException
		 {
		 this(input, Integer.MAX_VALUE);
		 }
 */

	@Override
	public int estimatedTotalSamples()
		{
		return Integer.MAX_VALUE;
		}

	@Override
	public long getTotalSequence()
		{
		return sectionList.getTotalSequence();
		}

	/*	@Override
	 public void releaseCachedResources()
		 {
		 theSectionList.releaseCachedResources();
		 }
 */

	public synchronized void reset()
		{
		charactersRead = 0;
		matePairsRead = 0;
		}
	}
