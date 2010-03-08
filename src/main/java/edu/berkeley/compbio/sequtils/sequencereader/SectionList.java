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

import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceFragmentMetadata;
import edu.berkeley.compbio.sequtils.SequenceReader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * A list of sequence fragments, represented only by their metadata.
 *
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: SectionList.java 1246 2009-10-09 23:29:26Z soergel $
 */
public interface SectionList extends SequenceReader//implements List<Section>
	{
	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface SequenceReader ---------------------

	/*		{
		 seek(section.getStartPosition() + sectionPosition);
		 }
 */
	//	protected abstract void seek(int newRawPosition) throws IOException;

	//public void close();

	/**
	 * {@inheritDoc}
	 */
	void reset();


	// -------------------------- OTHER METHODS --------------------------

	//private static final Logger logger = Logger.getLogger(SectionList.class);
	//public int getTotalSequence();

	//public String getName();

	/**
	 * Iterates through the list of sections in order.
	 *
	 * @return the next SequenceFragmentMetadata
	 */
	@NotNull
	SequenceFragmentMetadata next() throws IOException, NotEnoughSequenceException;

	/**
	 * Iterates through the list of sections in random order, providing each section exactly once
	 *
	 * @return
	 * @throws IOException
	 * @throws NotEnoughSequenceException
	 */
	@NotNull
	SequenceFragmentMetadata nextShuffled() throws IOException, NotEnoughSequenceException;

	//public boolean hasNext();


	/**
	 * Get a random section fragment (really just the start position, with no length specified), selected uniformly
	 * (nucleotidewise) from all available sequence.  Note this should not first select a random Section, since sections of
	 * different lengths may thereby produce biased results.
	 */
	@NotNull
	SequenceFragmentMetadata randomSectionFragment() throws NotEnoughSequenceException, IOException;

	/**
	 * Get a random section from the list, selected uniformly from the complete sections in the list, weighted by their
	 * lengths.  Effectively, choose a randomSectionFragment(), i.e. a random position nucleotidewise, and then return the
	 * Section in which that position is found.
	 */
	SequenceFragmentMetadata randomSectionLengthWeighted() throws NotEnoughSequenceException, IOException;

	/**
	 * Get a random section from the list, selected uniformly from the complete sections in the list, ignoring their
	 * lengths.
	 */
	SequenceFragmentMetadata randomSectionUniform() throws IOException;

	//	BufferedSequenceReader theReader;

	//public byte read();


	SequenceFragmentMetadata getRootMetadata();
	}
