/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.GenericFactoryException;
import com.davidsoergel.stats.DistributionProcessorException;
import edu.berkeley.compbio.sequtils.FilterException;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SubstitutionFilter;

import java.io.IOException;

/**
 * Provides an interface for classes that can scan sequences to count the occurrences of substrings
 *
 * @author <a href="mailto:dev.davidsoergel.com">David Soergel</a>
 * @version $Id: SequenceSpectrumScanner.java 477 2009-07-18 00:46:23Z soergel $
 */
public interface SequenceSpectrumScanner
	{
// -------------------------- OTHER METHODS --------------------------

	/**
	 * Returns an empty SequenceSpectrum of the appropriate type.  This spectrum is "empty" in the sense that it is not
	 * based on any sequence, but it may nonetheless represent some prior, e.g. uniform.
	 *
	 * @return an empty SequenceSpectrum of the appropriate type.
	 */
	SequenceSpectrum getEmpty();

	SubstitutionFilter getNucleotideFilter();
	/*	Kcount scanSequence(SequenceReader in, int desiredlength, List<byte[]> firstWords) //, int firstWordLength)
				throws IOException, FilterException, NotEnoughSequenceException;*/

	/**
	 * Check whether the reader associated with this scanner is able to provide the sequence specified by the provided
	 * SequenceFragment.  Essentially, this seeks the reader to the start of the fragment and attempts to read symbols up
	 * to the length of the fragment.  If the method returns without throwing a NotEnoughSequenceException, then the
	 * requested fragment is available.
	 *
	 * @param fragment the SequenceFragment
	 * @throws IOException                when an input/output error occurs on the reader
	 * @throws FilterException            when the scanner is filtering the sequence while reading it, but the filter
	 *                                    throws an exception
	 * @throws NotEnoughSequenceException when the reader cannot supply the desired amound of sequence
	 */
//	void checkSequenceAvailable(SequenceFragment fragment)//SequenceReader theReader, int desiredlength)
//			throws IOException, FilterException, NotEnoughSequenceException;

	/**
	 * Scans a sequence to count pattern frequencies.
	 *
	 * @param fragment the SequenceFragment providing the sequence to be scanned
	 * @return a SequenceSpectrum containing the counts of all patterns being scanned for
	 * @throws java.io.IOException when an input/output error occurs on the reader
	 * @throws edu.berkeley.compbio.sequtils.FilterException
	 *                             when the scanner is filtering the sequence while reading it, but the filter throws an
	 *                             exception
	 * @throws edu.berkeley.compbio.sequtils.NotEnoughSequenceException
	 *                             when the reader cannot supply the desired amound of sequence (some scanners may not
	 *                             throw this exception, but instead simply return a Kcount based on the short sequence)
	 */
	SequenceSpectrum scanSequence(SequenceFragment fragment)//SequenceReader sequenceReader, int desiredLength)//
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException,
			       GenericFactoryException;

	/**
	 * Scans a sequence to count pattern frequencies, considering only words that follow the given prefix.  Primarily used
	 * for multi-pass scanning, to achieve a large effective word length within reasonable memory.
	 *
	 * @param fragment the SequenceFragment providing the sequence to be scanned
	 * @param prefix   the word that must precede each pattern in order for it to be counted
	 * @return a SequenceSpectrum based on words immediately following the prefix
	 * @throws IOException
	 * @throws edu.berkeley.compbio.sequtils.FilterException
	 *
	 * @throws NotEnoughSequenceException
	 */
	SequenceSpectrum scanSequence(SequenceFragment fragment, //SequenceReader resetReader, int desiredLength,
	                              byte[] prefix)
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException,
			       GenericFactoryException;
	}
