/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
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
