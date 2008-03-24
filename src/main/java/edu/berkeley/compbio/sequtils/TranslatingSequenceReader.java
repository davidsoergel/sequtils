/*
 * Copyright (c) 2008 Regents of the University of California
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
import java.util.Arrays;

/* $Id$ */

/**
 * @Author David Soergel
 * @Version 1.0
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

	public void setTranslationAlphabet(byte[] translationAlphabet)
		{
		if (!Arrays.equals(this.translationAlphabet, translationAlphabet))
			{
			this.translationAlphabet = translationAlphabet;
			translationValid = false;
			}
		}

	public int readTranslated() throws IOException, FilterException, NotEnoughSequenceException, TranslationException
		{
		read();
		if (!translationValid)
			{
			translate();
			}
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