package edu.berkeley.compbio.sequtils;

//import org.apache.log4j.Logger;

/**
 * Used when there is insufficient sequence in a reader for the next operation, such as the end of section encountered, or being inside a header.
 *
 * @author David Tulga
 */
public class NotEnoughSequenceException extends SequenceException
	{
	//private static Logger logger = Logger.getLogger(NotEnoughSequenceException.class);

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

	public NotEnoughSequenceException(Exception e, String s)
		{
		super(e, s);
		}

	public NotEnoughSequenceException(Exception e)
		{
		super(e);
		}
	}
