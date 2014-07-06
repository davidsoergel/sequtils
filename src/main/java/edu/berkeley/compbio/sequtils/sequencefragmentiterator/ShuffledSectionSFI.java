/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
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
 * @version $Id: ShuffledSectionSFI.java 1324 2010-03-08 22:44:49Z soergel $
 */
public class ShuffledSectionSFI extends SectionDecomposingSFI
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

	public ShuffledSectionSFI(SectionList list) throws IOException
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
			currentSection = sectionList.nextShuffled();//next();
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
