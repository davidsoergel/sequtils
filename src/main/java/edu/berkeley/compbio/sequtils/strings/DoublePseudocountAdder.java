/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.runutils.Property;
import com.davidsoergel.runutils.PropertyConsumer;
import com.davidsoergel.stats.DistributionProcessor;
import com.davidsoergel.stats.DoubleArrayContainer;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: DoublePseudocountAdder.java 442 2009-06-15 17:53:56Z soergel $
 */

@PropertyConsumer
public class DoublePseudocountAdder implements DistributionProcessor<DoubleArrayContainer>//extends DoubleArrayProcessor
	{
// ------------------------------ FIELDS ------------------------------

	@Property(helpmessage = "Pseudocounts to add to every bin", defaultvalue = "1e-6")
	public double uniformPseudoCount;


// --------------------------- CONSTRUCTORS ---------------------------

	public DoublePseudocountAdder()//String injectorId)//double smoothFactor)
		{
		//this.smoothFactor = smoothFactor;
		//	ResultsCollectingProgramRun.getProps().injectProperties(injectorId, this);
		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DistributionProcessor ---------------------

	public void process(final DoubleArrayContainer c)
		{
		if (uniformPseudoCount == 0)
			{
			return;
			}
		final double[] counts = c.getArray();

		for (int i = 0; i < counts.length; i++)
			{
			counts[i] += uniformPseudoCount;
			}
		}
	}
