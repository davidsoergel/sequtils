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
 * @version $Id: RonPSASmoother.java 442 2009-06-15 17:53:56Z soergel $
 */

@PropertyConsumer
public class RonPSASmoother implements DistributionProcessor<RonPSA>
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(RonPSASmoother.class);

	@Property(helpmessage = "Smoothing factor (aka gammaMin)", defaultvalue = "0.01")
	public Double smoothFactor;


// --------------------------- CONSTRUCTORS ---------------------------

	public RonPSASmoother()//String injectorId)//double smoothFactor)
		{
		//this.smoothFactor = smoothFactor;
		//ResultsCollectingProgramRun.getProps().injectProperties(injectorId, this);
		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DistributionProcessor ---------------------

	public void process(final RonPSA ronPSA)
		{
		try
			{
			// breadth first  (for no reason, just symmetry with the KneserNeyPSTSmoother where it is important)

			final List<MarkovTreeNode> nodesRemaining = new LinkedList<MarkovTreeNode>();
			nodesRemaining.add(ronPSA);

			while (!nodesRemaining.isEmpty())
				{
				final MarkovTreeNode node = nodesRemaining.remove(0);
				node.getProbs().redistributeWithMinimum(smoothFactor);

				for (final MarkovTreeNode n : node.getChildren())
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
