/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;

import org.apache.log4j.Logger;

/**
 * @version 1.0
 */
public class FilterException extends SequenceException
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SequenceException.class);


	// --------------------------- CONSTRUCTORS ---------------------------

	public FilterException(String s)
		{
		super(s);
		}

	public FilterException(Exception e)
		{
		super(e);
		}

	public FilterException(Exception e, String s)
		{
		super(e, s);
		}
	}
