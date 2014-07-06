/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.sequencereader.fasta;

import com.davidsoergel.dsutils.CachedResourceReleaser;
import com.davidsoergel.dsutils.HasReleaseableResources;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Fasta File Set, useful in treating multiple fasta files as one
 *
 * @author David Tulga
 * @version $Id: FastaFileSet.java 1098 2009-07-19 01:41:49Z soergel $
 */
public class FastaFileSet implements HasReleaseableResources
	{
	// ------------------------------ FIELDS ------------------------------

	//private static final Logger logger = Logger.getLogger(FastaFileSet.class);
	private long totalSequence = 0;// how many nucleotides of sequence are in all files in the directory
	private Map<File, Long> fileStarts = new HashMap<File, Long>();
	// start position of each file if they were concatenated (inclusive)
	private Map<File, Long> fileEnds = new HashMap<File, Long>();
// end position of each file if they were concatenated (exclusive)

	private List<File> theFiles = new ArrayList<File>();
	// List rather than Set because order matters in keeping the start positions etc. straight
	private Map<File, RandomAccessFile> theRAFiles = new HashMap<File, RandomAccessFile>();

	private String name = null;
	private Integer taxid = null;
	private int genomeCount;

	private Lock lock = new ReentrantLock();

	// --------------------------- CONSTRUCTORS ---------------------------

	/**
	 * Initializes a file set from a path to a file or directory
	 *
	 * @param input Path to file or directory
	 * @throws IOException If any data errors occur
	 */
	/*	public FastaFileSet(String input) throws IOException
		 {
		 this(new File(input));
		 }
 */

	/**
	 * @param taxid
	 */
	public FastaFileSet(//String name,
	                    Integer taxid)
		{
		this.name = "" + taxid;
		this.taxid = taxid;
		CachedResourceReleaser.register(this);
		}

	public FastaFileSet(String input) throws IOException
		{
		this(new File(input));
		}

	/**
	 * Initializes a file set from a file or directory
	 *
	 * @param input File or directory to initialize
	 * @throws IOException If any data errors occur
	 */
	public FastaFileSet(File input) throws IOException
		{
		name = input.getName();

		if (input.isDirectory())
			{
			addDirectory(input);
			}
		else
			{
			addFile(input);
			}
		if (fileStarts.size() != fileEnds.size() || fileStarts.size() != theFiles.size())
			{
			throw new Error("Impossible");
			}
		}

	/**
	 * Initializes a file set from a file or directory
	 *
	 * @param inputs File or directory to initialize
	 * @throws IOException If any data errors occur
	 */
	public FastaFileSet(Collection<File> inputs) throws IOException
		{
		name = "";
		for (File input : inputs)
			{
			name = name + " + " + input.getName();

			if (input.isDirectory())
				{
				addDirectory(input);
				}
			else
				{
				addFile(input);
				}
			if (fileStarts.size() != fileEnds.size() || fileStarts.size() != theFiles.size())
				{
				throw new Error("Impossible");
				}
			}
		}

	public FastaFileSet(File file1, Integer id) throws IOException
		{
		this(file1);
		taxid = id;
		}

	public void addDirectory(File input) throws IOException
		{
		lock.lock();
		try
			{
			//Initialize the sequence provider with the correct input directory

			//maxAttempts = attempts;

			//if(logger.isDebugEnabled()) logger.debug("Initializing Species: " + input);

			File[] allFiles = input.listFiles();
			if (allFiles == null)
				{
				throw new IOException("Can't read directory: " + input);
				}

            Arrays.sort(allFiles);

			//name += "+" + input.getName();

			for (File file : allFiles)
				{
				if (!file.getName().contains("."))
					{
					continue;
					}
				String ext = file.getName().substring(file.getName().lastIndexOf('.'), file.getName().length())
						.toLowerCase();
				if (ext.equals(".fna") || ext.equals(".fasta"))
					{
					addFile(file);
					//theFiles.add(file);
					//if(logger.isDebugEnabled()) logger.debug("Found file: " + file + " " + lastPosition + " " + totalSequence);
					//lastPosition = totalSequence;
					}
				}
			if (fileStarts.size() != fileEnds.size() || fileStarts.size() != theFiles.size())
				{
				throw new Error("Impossible");
				}
			}
		finally
			{
			lock.unlock();
			}
		}

	public void addFile(File file)
		{
		lock.lock();
		try
			{
			if (!theFiles.contains(file))
				{
				fileStarts.put(file, totalSequence);
				totalSequence += file.length();// including the FASTA headers and newlines
				fileEnds.put(file, totalSequence);
				theFiles.add(file);
				//if(logger.isDebugEnabled()) logger.debug("Found file: " + file + " " + + totalSequence);
				}
			}
		finally
			{
			lock.unlock();
			}
		}

	@Deprecated
	public FastaFileSet(Collection<FastaFileSet> mergeSets, Integer taxid)
		{
		this.taxid = taxid;

		for (FastaFileSet mergeSet : mergeSets)
			{
			addAll(mergeSet);
			if (fileStarts.size() != fileEnds.size() || fileStarts.size() != theFiles.size())
				{
				throw new Error("Impossible");
				}
			if (name == null)
				{
				name = mergeSet.getName();
				}
			else
				{
				if (name.length() > 100 || mergeSet.getName().length() > 100)
					{
					name += " + many others";
					}
				else
					{
					name = name + " + " + mergeSet.getName();
					}
				}
			}
		if (fileStarts.size() != fileEnds.size() || fileStarts.size() != theFiles.size())
			{
			throw new Error("Impossible");
			}
		}

	public void addAll(FastaFileSet otherset)
		{
		lock.lock();
		try
			{
			for (File f : otherset.theFiles)
				{
				addFile(f);
				}
			if (fileStarts.size() != fileEnds.size() || fileStarts.size() != theFiles.size())
				{
				throw new Error("Impossible");
				}
			}
		finally
			{
			lock.unlock();
			}
		}

	// --------------------- GETTER / SETTER METHODS ---------------------

	/**
	 * Returns the name of the FastaFileSet
	 *
	 * @return The name
	 */
	public String getName()
		{
		return name;
		}

	public Integer getTaxid()
		{
		return taxid;
		}

	/**
	 * Returns the total amount of sequence present in all files
	 *
	 * @return The total amount of sequence
	 */
	public long getTotalSequence()
		{
		lock.lock();
		try
			{
			// REVIEW Fix this so it doesn't count FASTA Headers and newlines
			return totalSequence;
			}

		finally

			{
			lock.unlock();
			}
		}

	public long getAverageGenomeSize()
		{
		return totalSequence / genomeCount;
		}

	// ------------------------ CANONICAL METHODS ------------------------

/*	@Override
	protected void finalize() throws Throwable
		{
		close();
		super.finalize();
		}
*/

	/**
	 * Closes all the open filehandles
	 */
	public void releaseCachedResources()
		{
		if (lock.tryLock())  // if someone else is using the resource, don't mess with it (produces deadlock anyway)
			{
			try
				{
				//if(logger.isDebugEnabled()) logger.debug("Closing Fileset.");
				for (File file : theFiles)
					{
					RandomAccessFile r = theRAFiles.get(file);
					if (r != null)
						{
						try
							{
							r.close();
							theRAFiles.remove(file);
							}
						catch (IOException e)
							{
							// Already Closed
							}
						}
					}
				}
			finally

				{
				lock.unlock();
				}
			}
		}

	public void close()
		{
		releaseCachedResources();
		}

	/**
	 * Returns the name of the FastaFileSet
	 *
	 * @return The name
	 */
	@Override
	public String toString()
		{
		lock.lock();
		try
			{
			return name + " (" + genomeCount + " genome" + (genomeCount == 1 ? "" : "s") + ")";
			}
		finally

			{
			lock.unlock();
			}
		}

	// -------------------------- OTHER METHODS --------------------------

	/**
	 * Reads a section from the files
	 *
	 * @param buf      Buffer to read the data in
	 * @param position Offset in the set to start reading (inclusive)
	 * @param length   Length to read
	 * @return The number of bytes actually read
	 * @throws IOException                If any data errors occur
	 * @throws NotEnoughSequenceException If there is no more sequence before the end of the set
	 */

	public int readAt(byte[] buf, long position, int length) throws NotEnoughSequenceException, IOException
		{
		lock.lock();
		try
			{
			if (theFiles.size() == 0)
				{
				throw new NotEnoughSequenceException("Fileset contains no files!");
				}
			if (position < 0 || position >= fileEnds.get(theFiles.get(theFiles.size() - 1)))
				{
				throw new NotEnoughSequenceException("Offset " + position + " outside of available sequence range!");
				}
			int charactersRead = 0;
			for (File file : theFiles)
				{
				if (fileStarts.get(file) <= position && position < fileEnds.get(file))
					{
					//if(logger.isDebugEnabled()) logger.debug("Read at Start: " + (position-fileStarts.get(file)) + " Length: " + length);
					RandomAccessFile raf = getRandomAccessFile(file);
					raf.seek(position - fileStarts.get(file));

					int r = raf.read(buf, charactersRead, length);

					charactersRead += r;
					position += r;
					length -= r;
					}
				}
			if (charactersRead == 0)
				{
				throw new NotEnoughSequenceException("No characters read");
				}
			return charactersRead;
			}
		finally

			{
			lock.unlock();
			}
		}

	private RandomAccessFile getRandomAccessFile(File f) throws FileNotFoundException
		{
		lock.lock();
		try
			{
			try
				{
				RandomAccessFile tmp = theRAFiles.get(f);
				if (tmp == null) // || !tmp.getFD().valid())
					{
					tmp = new RandomAccessFile(f, "r");
					theRAFiles.put(f, tmp);
					}
				return tmp;
				}
			catch (FileNotFoundException e)
				{
				// avoid synchronization on this for the release call
				CachedResourceReleaser.release();


				// try again, but really throw the exception if it occurs this time
				RandomAccessFile tmp = new RandomAccessFile(f, "r");
				theRAFiles.put(f, tmp);
				return tmp;
				}
			}
		finally

			{
			lock.unlock();
			}
		}

	public void incrementGenomeCount()
		{
		lock.lock();
		try
			{
			genomeCount++;
			}

		finally

			{
			lock.unlock();
			}
		}

	public int getGenomeCount()
		{
		lock.lock();
		try
			{
			return genomeCount;
			}
		finally

			{
			lock.unlock();
			}
		}
	}
