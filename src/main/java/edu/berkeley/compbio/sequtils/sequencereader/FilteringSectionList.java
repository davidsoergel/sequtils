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

import edu.berkeley.compbio.sequtils.FilteringSequenceReader;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceFragmentMetadata;
import edu.berkeley.compbio.sequtils.SubstitutionFilter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: FilteringSectionList.java 1246 2009-10-09 23:29:26Z soergel $
 */

public class FilteringSectionList extends FilteringSequenceReader implements SectionList
	{
	// ------------------------------ FIELDS ------------------------------

	private SectionList base;


	// --------------------------- CONSTRUCTORS ---------------------------

	/*
	private SequenceReader baseReader;
	private SequenceReader filteredReader;
	*/
	//private NucleotideFilter filter;

	public FilteringSectionList(SectionList base, SubstitutionFilter filter)
		{
		super(base, filter);
		this.base = base;
		/*		this.base = base;
		  baseReader = base.getReader();
		  //this.filter = filter;
		  filteredReader = new FilteringSequenceReader(baseReader, filter);
  */
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface SectionList ---------------------

	/*	public int getTotalSequence()
		 {
		 return base.getTotalSequence();
		 }

	 public String getName()
		 {
		 return base.getName();
		 }
 */

	public SequenceFragmentMetadata next() throws IOException, NotEnoughSequenceException
		{
		return base.next();
		}

	@NotNull
	public SequenceFragmentMetadata randomSectionFragment() throws NotEnoughSequenceException, IOException
		{
		return base.randomSectionFragment();
		}

	public SequenceFragmentMetadata randomSectionLengthWeighted() throws NotEnoughSequenceException, IOException
		{
		return base.randomSectionLengthWeighted();
		}

	public SequenceFragmentMetadata randomSectionUniform() throws IOException
		{
		return base.randomSectionUniform();
		}

	public SequenceFragmentMetadata nextShuffled() throws NotEnoughSequenceException, IOException
		{
		return base.nextShuffled();
		}

/*	public void releaseCachedResources()
		{
		base.releaseCachedResources();
		}
*/
	// --------------------- Interface SequenceReader ---------------------


	/*
	 public void close()
		 {
		 base.close();
		 }
 */

	@Override
	public void reset()
		{
		base.reset();
		}

	/*	public SequenceReader getReader()
		 {
		 return filteredReader;
		 }
 */

	@Override
	public void seek(SequenceFragmentMetadata section) throws IOException
		{
		base.seek(section);
		}

	@Override
	public void seek(SequenceFragmentMetadata section, long offset) throws IOException
		{
		base.seek(section, offset);
		}

	public SequenceFragmentMetadata getRootMetadata()
		{
		return base.getRootMetadata();
		}
	}
