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

import com.davidsoergel.stats.DistributionProcessorException;
import edu.berkeley.compbio.sequtils.FilterException;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.sequencereader.SectionList;
import edu.berkeley.compbio.sequtils.strings.SequenceFragment;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Provides a SequenceFragmentIterator that reads sections.
 *
 * @author David Tulga
 * @version $Id: RandomSectionSFI.java 1324 2010-03-08 22:44:49Z soergel $
 */
public class RandomSectionSFI extends SectionDecomposingSFI
	{
	// ------------------------------ FIELDS ------------------------------

	private boolean nextSectionTrigger = false;


	// --------------------------- CONSTRUCTORS ---------------------------

	//private static final Logger logger = Logger.getLogger(SectionSequenceFragmentIterator.class);
	/*	public SectionSequenceFragmentIterator(FastaFileSet fileset) throws IOException
		 {
		 super(fileset);
		 }

	 public SectionSequenceFragmentIterator(BufferedSequenceReader bsr) throws IOException
		 {
		 super(bsr);
		 }
 */

	public RandomSectionSFI(SectionList list) throws IOException
		{
		super(list);
		nextSectionTrigger = false;
		}

	// -------------------------- OTHER METHODS --------------------------

	@Override
	protected synchronized SequenceFragment getNextWindow()
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException
		{
		SequenceFragment result;
		if (nextSectionTrigger)
			{
			currentSection = sectionList.randomSectionUniform();//next();
			currentSectionPosition = 0;
			}
		else
			{
			nextSectionTrigger = true;
			}
		sectionList.seek(currentSection, currentSectionPosition);
		result = new SequenceFragment(currentSection, null, currentSectionPosition, sectionList,
		                              SequenceFragment.UNKNOWN_LENGTH, spectrumScanner);// SectionFragment, maxLength
		//result.checkAvailable();
		return result;
		}

	@NotNull
	protected synchronized SequenceFragment getNextWindow(int theWidth)
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException
		{
		throw new NotImplementedException("A SectionSFI returns entire sections; can't request a length");
		}

	/**
	 * Note this is partly redundant with RepeatingAggregatingSFI
	 */

/*	public synchronized SequenceFragment totalFragment() throws IOException
		{
		reset();
		SequenceFragment result = next();
		try
			{
			while (true)
				{
				result.incrementBy(next());
				}
			}
		catch (NoSuchElementException e)
			{
			// OK, ran out of sequence
			}
		reset();
		return result;
		}


	public synchronized void reset() //throws IOException
		{
		sectionList.reset();
		//	cached = false;
		nextSectionTrigger = false;
		}*/

	//private int avglength;
	@Override
	public int estimatedTotalSamples()
		{
		// we have no idea at all, but have to provide some number that'll be used to initialize ArrayLists and such
		return 10;
		//		return theSectionList.getTotalSequence() / avglength;
		}

	public void init()
		{

		}
	}
