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
import com.davidsoergel.dsutils.DSArrayUtils;
import com.davidsoergel.dsutils.TestInstanceFactory;
import com.davidsoergel.dsutils.math.MathUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: SequenceSpectrumInterfaceTest.java 442 2009-06-15 17:53:56Z soergel $
 */

public class SequenceSpectrumInterfaceTest extends AtomicContractTest
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SequenceSpectrumInterfaceTest.class);
	//public abstract SequenceSpectrum createInstance() throws Exception;

	private final TestInstanceFactory<SequenceSpectrum> tif;


// --------------------------- CONSTRUCTORS ---------------------------

	public SequenceSpectrumInterfaceTest(final TestInstanceFactory<SequenceSpectrum> tif)
		{
		this.tif = tif;
		}

// -------------------------- OTHER METHODS --------------------------

	@Test
	public void getRandomReturnsAlphabetSymbols() throws Exception
		{
		final SequenceSpectrum ss = tif.createInstance();
		final byte[] alphabet = ss.getAlphabet();
		for (int count = 0; count < 100; count++)
			{
			final byte b = ss.sample(new byte[0]);
			assert DSArrayUtils.contains(alphabet, b);
			}
		}

	@Test
	public void variousProbabilitiesAreConsistent() throws Exception
		{
		final SequenceSpectrum ss = tif.createInstance();
		int multipliedConditionals = 0;
		for (int count = 0; count < 100; count++)
			{
			final byte b = ss.sample(new byte[0]);
			final byte c = ss.sample(new byte[]{b});
			assert ss.conditionalProbability(b, new byte[0]) == ss.conditionalsFrom(new byte[0]).get(b);

			double d1;

			try
				{
				d1 = ss.conditionalProbability(c, new byte[]{b});
				}
			catch (SequenceSpectrumException e)
				{
				d1 = -1;// just a marker
				}
			catch (SequenceSpectrumRuntimeException e)
				{
				d1 = -1;// just a marker
				}
			double d2;
			try
				{
				d2 = ss.conditionalsFrom(new byte[]{b}).get(c);
				}
			catch (SequenceSpectrumException e)
				{
				d2 = -1;// just a marker
				}
			catch (SequenceSpectrumRuntimeException e)
				{
				d2 = -1;// just a marker
				}
			assert d1 == d2;

			try
				{
				final double total = ss.totalProbability(new byte[]{b, c});
				final double cond1 = ss.conditionalProbability(b, new byte[0]);
				final double cond2 = ss.conditionalProbability(c, new byte[]{b});
				final double mult = cond1 * cond2;
				if (!MathUtils.equalWithinFPError(total, mult))
					{
					logger.error(String.valueOf(cond1) + "  *  " + cond2);
					logger.error("Total prob = " + total + "; Multiplied conditionals = " + mult);
					}
				assert MathUtils.equalWithinFPError(total, mult);
				multipliedConditionals++;
				}
			catch (SequenceSpectrumException e)
				{
				// no point in doing this test if the given spectrum doesn't have the required resolution
				// but hey, that's why we do it 100 times.
				}
			}
		assert multipliedConditionals > 5;// still we want to be sure it worked at all
		}
	}
