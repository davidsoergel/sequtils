/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
