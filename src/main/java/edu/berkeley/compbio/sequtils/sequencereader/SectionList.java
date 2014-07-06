/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
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
