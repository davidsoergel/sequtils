/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.runutils.Property;
import com.davidsoergel.runutils.PropertyConsumer;
import com.davidsoergel.stats.DistributionException;
import com.davidsoergel.stats.DistributionProcessor;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: RonPSTSmoother.java 442 2009-06-15 17:53:56Z soergel $
 */

@PropertyConsumer
public class RonPSTSmoother implements DistributionProcessor<RonPST>
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(RonPSTSmoother.class);

	@Property(helpmessage = "Smoothing factor (aka gammaMin)", defaultvalue = "0.1")
	public Double smoothFactor;


// --------------------------- CONSTRUCTORS ---------------------------

	public RonPSTSmoother()//String injectorId)//double smoothFactor)
		{
		//this.smoothFactor = smoothFactor;
		//ResultsCollectingProgramRun.getProps().injectProperties(injectorId, this);
		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DistributionProcessor ---------------------

	public void process(final RonPST ronPST)
		{
		try
			{
			// breadth first  (for no reason, just symmetry with the KneserNeyPSTSmoother where it is important)

			final List<RonPSTNode> nodesRemaining = new LinkedList<RonPSTNode>();
			nodesRemaining.add(ronPST);

			while (!nodesRemaining.isEmpty())
				{
				final RonPSTNode node = nodesRemaining.remove(0);
				node.getProbs().redistributeWithMinimum(smoothFactor);

				for (final RonPSTNode n : node.getUpstreamNodes())
					{
					if (n != null)
						{
						nodesRemaining.add(n);
						}
					}
				}
			}
		catch (DistributionException e)
			{
			logger.error("Error", e);
			throw new Error(e);
			}
		}
	}
