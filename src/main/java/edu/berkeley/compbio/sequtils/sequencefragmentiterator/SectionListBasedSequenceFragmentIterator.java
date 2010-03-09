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
