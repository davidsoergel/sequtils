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
