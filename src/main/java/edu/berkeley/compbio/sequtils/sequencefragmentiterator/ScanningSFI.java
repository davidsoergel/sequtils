/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import com.davidsoergel.runutils.Property;
import edu.berkeley.compbio.sequtils.sequencereader.SectionList;
import edu.berkeley.compbio.sequtils.strings.SequenceFragment;
import edu.berkeley.compbio.sequtils.strings.SequenceSpectrumScanner;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: ScanningSFI.java 1324 2010-03-08 22:44:49Z soergel $
 */

public abstract class ScanningSFI extends SectionListBasedSequenceFragmentIterator
	{
	// ------------------------------ FIELDS ------------------------------

	//** A null scanner would make sense only if we're never going to ask for a spectrum, but only want the raw or translated sequence
	//** this would be cleaner if we just make a RawSequenceScanner and TranslatedSequenceScanner and eliminate the direct-access methods

	@Property(helpmessage = "Which scanner to use to generate a Kcount from an input stream",
	          defaultvalue = "edu.berkeley.compbio.msensr.kcountscanner.ExactKcountScanner", isNullable = true)
	//,isPlugin = true)
	public SequenceSpectrumScanner spectrumScanner;

	public ScanningSFI()
		{
		super();
		}

	public ScanningSFI(SectionList sections)
		{
		super(sections);
		//theSectionList = sections;
		}

	public SequenceFragment getEmpty()
		{
		return new SequenceFragment(null, null, 0, null, 0, spectrumScanner);
		}
	}
