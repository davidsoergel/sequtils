/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;

import java.io.IOException;


/**
 * @version 1.0
 */
public class ByteArraySequenceReader extends TranslatingSequenceReader
	{
	// ------------------------------ FIELDS ------------------------------

	//private static final Logger logger = Logger.getLogger(ByteArraySequenceReader.class);
	//byte[] theBytes;
	//int pos = 0;


	// --------------------------- CONSTRUCTORS ---------------------------

	public ByteArraySequenceReader(byte[] b)
		{
		buf = b;
		initTranslationBuffer(b.length);
		}

	public ByteArraySequenceReader(String s)
		{
		this(s.getBytes());
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface SequenceReader ---------------------


	/**
	 * {@inheritDoc}
	 */
	public void close()
		{
		// Do Nothing
		}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	/*
		 public void seek(int position) throws IOException // Seek to a particular position
			 {
			 pos = position;
			 }
	 */
	public long getTotalSequence()
		{
		return buf.length;
		}

	/**
	 * {@inheritDoc}
	 */
	public byte read() throws NotEnoughSequenceException// Read one character from the buffer
		{
		try
			{
			return buf[bufPosition++];
			}
		catch (IndexOutOfBoundsException e)
			{
			throw new NotEnoughSequenceException(e);
			}
		}

	/**
	 * {@inheritDoc}
	 */
	public int read(byte[] buffer, int length)
			throws IOException, FilterException, NotEnoughSequenceException// Read one character from the buffer
		{
		length = Math.min(length, buf.length);
		System.arraycopy(buf, bufPosition, buffer, 0, length);
		return length;
		}

	/*public int toSectionStart()
		{
		return pos = 0;
		}*/


	/**
	 * {@inheritDoc}
	 */
	public void reset()
		{
		bufPosition = 0;
		}

	/**
	 * {@inheritDoc}
	 */
	public void seek(SequenceFragmentMetadata section) throws IOException
		{
		bufPosition = 0;
		}

	/**
	 * {@inheritDoc}
	 */
	public void seek(SequenceFragmentMetadata section, long offset) throws IOException
		{
		if (offset > Integer.MAX_VALUE)
			{
			throw new IOException("Arrays can't be bigger than Integer.MAX_VALUE");
			}
		bufPosition = (int) offset;
		}
	}
