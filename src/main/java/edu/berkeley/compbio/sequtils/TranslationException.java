/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.ChainedException;
import org.apache.log4j.Logger;

/**
 * @version 1.0
 */
public class TranslationException extends ChainedException
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SequenceException.class);


	// --------------------------- CONSTRUCTORS ---------------------------

	public TranslationException(String s)
		{
		super(s);
		}

	public TranslationException(Exception e)
		{
		super(e);
		}

	public TranslationException(Exception e, String s)
		{
		super(e, s);
		}
	}
