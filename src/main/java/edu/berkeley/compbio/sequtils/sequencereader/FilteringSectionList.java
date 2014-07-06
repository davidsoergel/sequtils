/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
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
