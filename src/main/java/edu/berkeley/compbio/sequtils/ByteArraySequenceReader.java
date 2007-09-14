/* $Id$ */

/*
 * Copyright (c) 2007 Regents of the University of California
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
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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
 * @author lorax
 * @version 1.0
 */
public class ByteArraySequenceReader implements SequenceReader
	{
	// ------------------------------ FIELDS ------------------------------

	//private static Logger logger = Logger.getLogger(ByteArraySequenceReader.class);
	byte[] theBytes;
	int pos = 0;


	// --------------------------- CONSTRUCTORS ---------------------------

	public ByteArraySequenceReader(byte[] b)
		{
		theBytes = b;
		}

	public ByteArraySequenceReader(String s)
		{
		this(s.getBytes());
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface SequenceReader ---------------------

	public byte read() throws NotEnoughSequenceException// Read one character from the buffer
		{
		try
			{
			return theBytes[pos++];
			}
		catch (IndexOutOfBoundsException e)
			{
			throw new NotEnoughSequenceException(e);
			}
		}

	/**
	 * Reads nucleotides into a byte array
	 *
	 * @param buffer a byte[] to read into.
	 * @param length how many nucleotides to read (must be less than or equal to the size of the given buffer)
	 * @return The number of nucleotides read
	 */
	public int read(byte[] buffer, int length)
			throws IOException, FilterException, NotEnoughSequenceException// Read one character from the buffer
		{
		length = Math.min(length, theBytes.length);
		System.arraycopy(theBytes, pos, buffer, 0, length);
		return length;
		}

	/*
	 public void seek(int position) throws IOException // Seek to a particular position
		 {
		 pos = position;
		 }
 */
	public int getTotalSequence()
		{
		return theBytes.length;
		}

	/*	public void checkCurrentPositionIsValidSequence() throws NotEnoughSequenceException
		 {
		 // Always valid for a byte array
		 }

	 public SectionFragmentMetadata nextSection() throws IOException
		 {
		 return new SectionFragmentMetadata(null, "Byte Array Sequence", 0, theBytes.length);
		 }
 */
	public String getName()
		{
		return "Byte Array Sequence";
		}

	public void close()
		{
		// Do Nothing
		}

	// -------------------------- OTHER METHODS --------------------------

	/*public int toSectionStart()
		{
		return pos = 0;
		}*/


	public void reset()
		{
		pos = 0;
		}

	public void seek(SequenceFragmentMetadata section) throws IOException
		{
		pos = 0;
		}

	public void seek(SequenceFragmentMetadata section, int offset) throws IOException
		{
		pos = offset;
		}
	}
