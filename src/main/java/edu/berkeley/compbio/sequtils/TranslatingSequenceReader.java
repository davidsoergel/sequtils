/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils;

import java.io.IOException;
import java.util.Arrays;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */

public abstract class TranslatingSequenceReader implements SequenceReader
	{

	boolean translationValid = false;
	private byte[] translationAlphabet;

	private int[] translatedBuf;

	protected byte[] buf;
	protected int bufPosition = 0;

	public void initTranslationBuffer(int buffersize)
		{
		translatedBuf = new int[buffersize];
		}


	public void initMainBuffer(int buffersize)
		{
		buf = new byte[buffersize];
		}

	/**
	 * {@inheritDoc}
	 */
	public boolean setTranslationAlphabet(byte[] translationAlphabet)
		{
		if (!Arrays.equals(this.translationAlphabet, translationAlphabet))
			{
			this.translationAlphabet = translationAlphabet;
			translationValid = false;
			return true;
			}
		return false;
		}

	/**
	 * {@inheritDoc}
	 */
	public int readTranslated() throws IOException, FilterException, NotEnoughSequenceException, TranslationException
		{
		// make sure the buffer is valid
		read();

		// then translate it if needed
		if (!translationValid)
			{
			translate();
			}
		// ** count on bufPosition being incremented by the read call above... yucky
		int result = translatedBuf[bufPosition - 1];
		if (result == -1)
			{
			throw new TranslationException("Symbol not in alphabet: " + buf[bufPosition - 1]);
			}
		return result;
		}


	private void translate()
		{
		for (int j = 0; j < buf.length; j++)
			{
			translatedBuf[j] = -1;// default assumption: symbol not in alphabet

			for (int i = 0; i < translationAlphabet.length; i++)
				{
				if (buf[j] == translationAlphabet[i])
					{
					translatedBuf[j] = i;
					break;
					}
				}
			}
		translationValid = true;
		}

	protected void setTranslationInvalid()
		{
		translationValid = false;
		}
	}
