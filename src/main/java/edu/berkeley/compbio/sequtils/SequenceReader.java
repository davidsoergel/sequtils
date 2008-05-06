/*
 * Copyright (c) 2001-2008 David Soergel
 * 418 Richmond St., El Cerrito, CA  94530
 * dev@davidsoergel.com
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the names of any contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



package edu.berkeley.compbio.sequtils;


import java.io.IOException;

/**
 * Provides an interface for a buffered sequence reader, to return nucleotides and process parsers or mutation
 * transparently.
 *
 * @author David Tulga
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
	int getTotalSequence();

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

	int readTranslated() throws IOException, FilterException, NotEnoughSequenceException, TranslationException;

	/**
	 * reset the stream to the beginning.
	 */
	void reset();


	void seek(SequenceFragmentMetadata section) throws IOException;

	void seek(SequenceFragmentMetadata section, int offset) throws IOException;

	void setTranslationAlphabet(byte[] alphabet);
	}
