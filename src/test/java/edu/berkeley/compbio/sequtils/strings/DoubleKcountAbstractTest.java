/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.ContractTest;
import com.davidsoergel.dsutils.ContractTestAwareContractTest;
import com.davidsoergel.dsutils.TestInstanceFactory;

import java.util.Queue;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class DoubleKcountAbstractTest<T extends DoubleKcount>
		extends ContractTestAwareContractTest<Kcount>//implements TestInstanceFactory<Kcount>
	{
// ------------------------------ FIELDS ------------------------------

	protected final TestInstanceFactory<T> tif;


// --------------------------- CONSTRUCTORS ---------------------------

	public DoubleKcountAbstractTest(final TestInstanceFactory<T> tif)
		{
		this.tif = tif;
		}

// -------------------------- OTHER METHODS --------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContractTestsToQueue(final Queue<ContractTest> theContractTests)
		{
		theContractTests.add(new KcountAbstractTest<T>(tif));
		}
	}
