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
import com.davidsoergel.stats.Multinomial;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: KneserNeyPSASmoother.java 442 2009-06-15 17:53:56Z soergel $
 */

@PropertyConsumer
public class KneserNeyPSASmoother implements DistributionProcessor<RonPSA>
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(KneserNeyPSASmoother.class);

	@Property(helpmessage = "Smoothing factor", defaultvalue = "0.1")
	public Double smoothFactor;
	private double smoothFactorTimesFour;// = smoothFactor * 4;


// --------------------------- CONSTRUCTORS ---------------------------

	public KneserNeyPSASmoother()//String injectorId)//double smoothFactor)
		{
		//this.smoothFactor = smoothFactor;
		//ResultsCollectingProgramRun.getProps().injectProperties(injectorId, this);
		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DistributionProcessor ---------------------

	public void process(final RonPSA ronPSA)
		{
		// mix the root with the uniform distribution
		final Multinomial<Byte> uniform = new Multinomial<Byte>();
		try
			{
			for (final byte b : ronPSA.getAlphabet())
				{
				uniform.put(b, 1);
				}
			uniform.normalize();

			ronPSA.getProbs().mixIn(uniform, smoothFactorTimesFour);

			// do the rest of the tree, breadth first

			final List<RonPSANode> nodesRemaining = new LinkedList<RonPSANode>();
			for (final MarkovTreeNode n : ronPSA.getChildren())
				{
				if (n != null)
					{
					nodesRemaining.add((RonPSANode) n);
					}
				}

			while (!nodesRemaining.isEmpty())
				{
				final RonPSANode node = nodesRemaining.remove(0);
				smooth(node);//, ronPST);
				//	nodesRemaining.addAll(node.getChildren());//.values());
				for (final MarkovTreeNode n : node.getChildren())
					{
					if (n != null)
						{
						nodesRemaining.add((RonPSANode) n);
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

// -------------------------- OTHER METHODS --------------------------

	public void init()
		{
		smoothFactorTimesFour = smoothFactor * 4;
		}

	private void smooth(final RonPSANode node) throws DistributionException
		{
		node.getProbs().mixIn(node.getBackoffPrior().getProbs(), smoothFactorTimesFour);

		//node.getProbs().mixIn(ronPST.getBackoffPrior(node.getIdBytes()).getProbs(), smoothFactorTimesFour);
		}
	}
