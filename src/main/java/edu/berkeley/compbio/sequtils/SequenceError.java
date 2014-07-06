/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.ChainedError;
import org.apache.log4j.Logger;

/**
 * @version 1.0
 */
public class SequenceError extends ChainedError
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SequenceError.class);


	// --------------------------- CONSTRUCTORS ---------------------------

	public SequenceError(String s)
		{
		super(s);
		}

	public SequenceError(Exception e)
		{
		super(e);
		}

	public SequenceError(Exception e, String s)
		{
		super(e, s);
		}
	}
