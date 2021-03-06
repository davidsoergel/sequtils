/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import edu.berkeley.compbio.sequtils.sequencereader.SectionList;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public abstract class SectionListBasedSequenceFragmentIterator extends AbstractSequenceFragmentIterator
	{

	protected SectionList sectionList;

	public SectionListBasedSequenceFragmentIterator(SectionList sections)
		{
		super();
		this.sectionList = sections;
		}

	public SectionListBasedSequenceFragmentIterator()
		{
		super();
		}
	}
