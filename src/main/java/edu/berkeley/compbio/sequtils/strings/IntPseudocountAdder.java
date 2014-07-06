/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.runutils.Property;
import com.davidsoergel.runutils.PropertyConsumer;
import com.davidsoergel.stats.DistributionProcessor;
import com.davidsoergel.stats.IntArrayContainer;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: IntPseudocountAdder.java 442 2009-06-15 17:53:56Z soergel $
 */

@PropertyConsumer
public class IntPseudocountAdder implements DistributionProcessor<IntArrayContainer>//extends DoubleArrayProcessor
	{
// ------------------------------ FIELDS ------------------------------

	@Property(helpmessage = "Pseudocounts to add to every bin", defaultvalue = "0")
	public int uniformPseudoCount;


// --------------------------- CONSTRUCTORS ---------------------------

	public IntPseudocountAdder()//String injectorId)//double smoothFactor)
		{
		//this.smoothFactor = smoothFactor;
		//ResultsCollectingProgramRun.getProps().injectProperties(injectorId, this);
		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DistributionProcessor ---------------------

	public void process(final IntArrayContainer c)
		{
		final int[] counts = c.getArray();

		for (int i = 0; i < counts.length; i++)
			{
			counts[i] += uniformPseudoCount;
			}
		}
	}
