package edu.berkeley.compbio.sequtils;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public interface IndelFilter
	{
	/**
	 * return the number of nucleotides to add or remove at a given nucletodie position.  0 == leave the sequnce unchanged
	 *
	 * @return
	 */
	int numChars();

	byte generate();
	}
