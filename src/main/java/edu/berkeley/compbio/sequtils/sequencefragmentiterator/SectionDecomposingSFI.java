/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import com.davidsoergel.stats.DistributionProcessorException;
import edu.berkeley.compbio.sequtils.FilterException;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceFragmentMetadata;
import edu.berkeley.compbio.sequtils.SequenceRuntimeException;
import edu.berkeley.compbio.sequtils.sequencereader.SectionList;
import edu.berkeley.compbio.sequtils.strings.SequenceFragment;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Provides a SequenceFragmentIterator that reads section lists
 *
 * @author David Soergel
 * @version $Id: SectionDecomposingSFI.java 1324 2010-03-08 22:44:49Z soergel $
 */
public abstract class SectionDecomposingSFI extends ScanningSFI
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SectionDecomposingSFI.class);

//	protected SectionList sectionList;

	protected SequenceFragmentMetadata currentSection;
	protected int currentSectionPosition = 0;
	//protected boolean cached = false;

	//private int position = 0;
	//private SequenceFragment kcache;
	//private int totalSequence;
	private int charactersRead = 0;


	// --------------------------- CONSTRUCTORS ---------------------------


	public SectionDecomposingSFI(SectionList sections) throws IOException
		{
		super(sections);
		//MsensrRun.getProps().injectProperties(injectorId, this);

		//sectionList = sections;//, (int) (inlength * 1.1)
		sectionList.reset();
		try
			{
			currentSection = sectionList.next();//new SectionFragment(theSectionList, theSectionList.next());
			}
		catch (NotEnoughSequenceException e)
			{
			logger.error("Error", e);
			logger.error("No sections available for " + sectionList.getName());
			currentSection = null;
			}
		//totalSequence = theSectionList.getTotalSequence();
		//this.scanner = scanner; //
		//scanner = new ExactKcountScanner();
		//position = currentSection.getMetadata().getStartPosition();
		}

	// --------------------- GETTER / SETTER METHODS ---------------------

	/*	public void toSectionStart() throws IOException
		 {
		 // Move to currentSection start
		 //position = theSectionList.toSectionStart();
		 theSectionList.seek(currentSection.getMetadata(), 0);

		 cached = false;
		 }
 */

	public long getCharactersRead()
		{
		return charactersRead;
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface Iterator ---------------------


	@NotNull
	public synchronized SequenceFragment next() throws NoSuchElementException
		{

		try
			{
			SequenceFragment kcache = getNextWindow();
			charactersRead += kcache.getLength();
			return kcache;
			// note cached is still false
			}
		catch (IOException e)
			{
			if (logger.isDebugEnabled())
				{
				logger.debug("IOException when trying to read next window.", e);
				}
			throw new SequenceRuntimeException("IOException when trying to read next window.");
			}
		catch (FilterException e)
			{
			if (logger.isDebugEnabled())
				{
				logger.debug("FilterException when trying to read next window.", e);
				}
			throw new SequenceRuntimeException("FilterException when trying to read next window.");
			}
		catch (DistributionProcessorException e)
			{
			if (logger.isDebugEnabled())
				{
				logger.debug("DistributionProcessorException when trying to read next window.", e);
				}
			throw new SequenceRuntimeException("DistributionProcessorException when trying to read next window.");
			}
		catch (NotEnoughSequenceException e)
			{
			if (logger.isDebugEnabled())
				{
				logger.debug("NotEnoughSequenceException when trying to read next window.", e);
				}
			throw new NoSuchElementException("NotEnoughSequenceException when trying to read next window.");
			}
		}

	// -------------------------- OTHER METHODS --------------------------

	public void close()
		{
		sectionList.close();
		}

	public abstract int estimatedTotalSamples();

	/*	public void setMetaToSpeciesName()
		 {
		 meta = theSectionList.getName();
		 currentSection.getMetadata().setSequenceName(meta);
		 }
 */

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

	@NotNull
	protected abstract SequenceFragment getNextWindow()
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException;

	@NotNull
	protected abstract SequenceFragment getNextWindow(int theWidth)
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException;

	//private int cacheCharactersRead;
	//private String meta = null;
	/*	public WindowedSequenceFragmentIterator(FastaFileSet fileset)
			 throws IOException //, int inlength, int instep) throws IOException
		 {
		 this(new FastaParser(fileset));
		 }
 */


	/*	public void seek(int pos) throws IOException
		 {
		 position = pos;
		 theSectionList.seek(position);
		 cached = false;
		 }
 */
	}
