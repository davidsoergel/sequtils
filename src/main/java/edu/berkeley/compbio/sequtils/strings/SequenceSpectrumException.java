/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.ChainedException;
import org.apache.log4j.Logger;

/**
 * Thrown when something involving a SequenceSpectrum goes wrong, such as when a requested spectrum cannot be computed.
 *
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: SequenceSpectrumException.java 442 2009-06-15 17:53:56Z soergel $
 */
public class SequenceSpectrumException extends ChainedException
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SequenceSpectrumException.class);


// --------------------------- CONSTRUCTORS ---------------------------

	public SequenceSpectrumException(final String s)
		{
		super(s);
		}

	public SequenceSpectrumException(final Exception e)
		{
		super(e);
		}

	public SequenceSpectrumException(final Exception e, final String s)
		{
		super(e, s);
		}
	}
