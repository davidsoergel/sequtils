package edu.berkeley.compbio.sequtils;

import org.apache.log4j.Logger;
import com.davidsoergel.dsutils.ChainedException;

/**
 * @author lorax
 * @version 1.0
 */
public class SequenceException extends ChainedException
	{
	private static Logger logger = Logger.getLogger(SequenceException.class);

	public SequenceException(String s)
		{
		super(s);
		}

	public SequenceException(Exception e, String s)
		{
		super(e, s);
		}

	public SequenceException(Exception e)
		{
		super(e);
		}
	}
