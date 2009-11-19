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


package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.AtomicContractTest;
import com.davidsoergel.dsutils.TestInstanceFactory;
import com.davidsoergel.dsutils.math.MersenneTwisterFast;
import org.testng.annotations.Test;

import java.util.Arrays;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: KcountAbstractTest.java 461 2009-06-23 03:00:32Z soergel $
 */

public class KcountAbstractTest<T extends Kcount> extends AtomicContractTest
	{
// ------------------------------ FIELDS ------------------------------

	//public abstract Kcount createInstance() throws Exception;
	private final TestInstanceFactory<T> tif;


// --------------------------- CONSTRUCTORS ---------------------------

	public KcountAbstractTest(final TestInstanceFactory<T> tif)
		{
		this.tif = tif;
		}

// -------------------------- OTHER METHODS --------------------------

	@Test
	public void addUnknownIncrementsLength() throws Exception
		{
		final Kcount kc = tif.createInstance();
		final long l = kc.getOriginalSequenceLength();
		kc.addUnknown();
		assert kc.getOriginalSequenceLength() == l + 1;
		}

	@Test
	public void idForSequenceIdAndSequenceForIdAreInverses() throws Exception
		{
		final Kcount kc = tif.createInstance();
		final int id1 = kc.idForSequence(new byte[]{'a', 'c', 'g', 't'});
		final byte[] a1 = kc.sequenceForId(id1);
		for (int rep = 0; rep < 10; rep++)
			{
			final byte[] seq = new byte[kc.getK()];
			for (int i = 0; i < kc.getK(); i++)
				{
				final int r = MersenneTwisterFast.randomInt(4);
				seq[i] = kc.getAlphabet()[r];//kc.sample(new byte[0]);
				}
			final int id = kc.idForSequence(seq);
			final byte[] a = kc.sequenceForId(id);
			assert Arrays.equals(a, seq);
			}
		}
	}