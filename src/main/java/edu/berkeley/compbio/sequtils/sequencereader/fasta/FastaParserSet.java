/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.sequencereader.fasta;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * Creates a HashSet of FastaParsers
 *
 * @author David Soergel
 * @version $Id: FastaParserSet.java 999 2009-06-23 03:00:18Z soergel $
 */
public class FastaParserSet extends HashSet<FastaParser>
	{
	// --------------------------- CONSTRUCTORS ---------------------------

	public FastaParserSet(Collection<File> theFiles) throws IOException
		{
		super();
		for (File speciesDirectory : theFiles)
			{
			add(new FastaParser(new FastaFileSet(speciesDirectory), true));
			}
		}

	public FastaParserSet(File parentDirectory) throws IOException
		{
		super();
		for (File speciesDirectory : parentDirectory.listFiles())
			{
			add(new FastaParser(new FastaFileSet(speciesDirectory), true));
			}
		}

	public FastaParserSet(File parentDirectory, int buffersize) throws IOException
		{
		super();
		for (File speciesDirectory : parentDirectory.listFiles())
			{
			add(new FastaParser(new FastaFileSet(speciesDirectory), buffersize, true));
			}
		}
	}
