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

import java.io.IOException;

/**
 * Provides an interface for classes that can scan sequences to count the occurrences of substrings
 *
 * @author David Soergel
 * @version $Id
 */
public interface KcountScanner extends SequenceSpectrumScanner
	{
// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface SequenceSpectrumScanner ---------------------

	/**
	 * {@inheritDoc}
	 */
	Kcount scanSequence(SequenceFragment fragment)//SequenceReader sequenceReader, int desiredLength)//,
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException,
			       GenericFactoryException;

	/*	Kcount scanSequence(SequenceReader in, int desiredlength, List<byte[]> firstWords) //, int firstWordLength)
				throws IOException, FilterException, NotEnoughSequenceException;*/

	/*	void checkSequenceAvailable(SequenceReader theReader, int desiredlength)
				throws IOException, FilterException, NotEnoughSequenceException;*/

	/**
	 * {@inheritDoc}
	 */
	Kcount scanSequence(SequenceFragment fragment, byte[] prefix)//SequenceReader resetReader, int desiredLength
			throws IOException, FilterException, NotEnoughSequenceException, DistributionProcessorException,
			       GenericFactoryException;
	}
