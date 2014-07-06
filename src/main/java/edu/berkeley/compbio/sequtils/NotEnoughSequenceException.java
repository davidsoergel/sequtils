/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;

//import org.apache.log4j.Logger;

/**
 * Used when there is insufficient sequence in a reader for the next operation, such as the end of section encountered,
 * or being inside a header.
 *
 * @author David Tulga
 * @version $Id$
 */
public class NotEnoughSequenceException extends SequenceException
	{
	// --------------------------- CONSTRUCTORS ---------------------------

	//private static final Logger logger = Logger.getLogger(NotEnoughSequenceException.class);
	/*
	 public NotEnoughSequenceException()
		 {
		 super();
		 }

 */

	public NotEnoughSequenceException(String s)
		{
		super(s);
		}

	public NotEnoughSequenceException(Exception e)
		{
		super(e);
		}

	public NotEnoughSequenceException(Exception e, String s)
		{
		super(e, s);
		}
	}
