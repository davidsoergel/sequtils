package edu.berkeley.compbio.sequtils;

import org.apache.log4j.Logger;

/**
 * @author lorax
 * @version 1.0
 */
public class FilterException extends SequenceException
	{
	private static Logger logger = Logger.getLogger(SequenceException.class);

	public FilterException(String s)
		{
		super(s);
		}

	public FilterException(Exception e, String s)
		{
		super(e, s);
		}

	public FilterException(Exception e)
		{
		super(e);
		}
	}
