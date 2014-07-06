/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;


import java.io.IOException;

/**
 * Provides an interface for a buffered sequence reader, to return nucleotides and process parsers or mutation
 * transparently.
 *
 * @author David Tulga
 * @version $Id$
 */
public interface SequenceReader
	{
	// -------------------------- OTHER METHODS --------------------------

	/**
	 * Seeks to the beginning of the current section
	 *
	 * @return The new position
	 */
	//	public int toSectionStart(SectionFragmentMetadata section) throws IOException;
	/**
	 * Closes the reader, and all sub-readers
	 */
	void close();

	/**
	 * Checks to make sure the seek position is in sequence, not a header
	 *
	 * @throws edu.berkeley.compbio.msensr.NotEnoughSequenceException
	 *          This exception is thrown when the current seek position isn't valid, such as in a header.
	 */
	//public void checkCurrentPositionIsValidSequence() throws NotEnoughSequenceException;

	/**
	 * Traverses to the next section and returns metadata corresponding to it
	 *
	 * @return Metadata about the next section
	 */
	//public SectionFragmentMetadata nextSection() throws IOException;

	/**
	 * Returns the species name of this reader
	 *
	 * @return A String representing the name of the sequence
	 */
	String getName();

	/**
	 * Seek to a particular position
	 */
	//public void seek(SectionFragmentMetadata section, int sectionPosition)
	//		throws IOException; // Seek to a particular position

	/**
	 * Seek to a particular position
	 */
	//	public void seek(SectionFragmentMetadata section)
	//			throws IOException; // Seek to a particular position

	/**
	 * Returns the total amount of sequence present in this reader
	 *
	 * @return The total amount of sequence
	 */
	long getTotalSequence();

	/**
	 * Returns the next nucleotide
	 *
	 * @return The next nucleotide, or EOF if the section or file has ended
	 */
	byte read() throws IOException, FilterException, NotEnoughSequenceException;// Read one character from the buffer

	/**
	 * Reads nucleotides into a byte array
	 *
	 * @param buffer a byte[] to read into.
	 * @param length how many nucleotides to read (must be less than or equal to the size of the given buffer)
	 * @return The number of nucleotides read
	 */
	int read(byte[] buffer, int length)
			throws IOException, FilterException, NotEnoughSequenceException;// Read one character from the buffer

	/**
	 * Returns the next nucleotide, after mapping the byte through a translation table associated with the reader.
	 * Typically used to map Roman characters representing nucleotides to the integers 0-3.
	 *
	 * @return The next nucleotide, using the translated alphabet, or EOF if the section or file has ended
	 * @see #setTranslationAlphabet(byte[] alphabet)
	 */
	int readTranslated() throws IOException, FilterException, NotEnoughSequenceException, TranslationException;

	/**
	 * reset the stream to the beginning.
	 */
	void reset();

	/**
	 * Set the location of the reader to that specified by the given SequenceFragmentMetadata, so that the next read()
	 * operation will return the first character of the specified SequenceFragment.
	 *
	 * @param section
	 * @throws IOException
	 */
	void seek(SequenceFragmentMetadata section) throws IOException;

	/**
	 * Set the location of the reader to an offset beyond the location specified by the given SequenceFragmentMetadata, so
	 * that the next read() operation will return the character at that offset of the specified SequenceFragment.
	 *
	 * @param section
	 * @throws IOException
	 */
	void seek(SequenceFragmentMetadata section, long offset) throws IOException;

	/**
	 * Set the character mapping to be used when reading nucleotides.
	 *
	 * @param alphabet A byte[] representing the byte translation table.  The mapping is specified in reverse: the desired
	 *                 translated value is the index of the input array, and the values in the array are the corresponding
	 *                 input characters to be mapped.  For example, the mapping 'A'->0, 'C'->1, 'G'->2, 'T'->3 is
	 *                 represented as new byte[]{'A','C','G','T'}.
	 * @see #readTranslated()
	 */
	boolean setTranslationAlphabet(byte[] alphabet);


	/**
	 * Frees up memory, filehandles, etc. from caches that can be re-filled if necessary
	 */
//	void releaseCachedResources();
	}
