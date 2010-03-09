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
