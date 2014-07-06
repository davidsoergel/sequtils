/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.math.MathUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: RonPSTNodeTest.java 442 2009-06-15 17:53:56Z soergel $
 */

public class RonPSTNodeTest
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(RonPSTNodeTest.class);
	RonPSTNode theNode;


// -------------------------- OTHER METHODS --------------------------

	@Test
	public void addDirectlyUpstreamNodeWorks() throws SequenceSpectrumException
		{
		theNode = new RonPSTNode(new byte[0], new byte[]{'a', 'b', 'c', 'd'});
		assert theNode.getUpstreamNode((byte) 'a') == null;
		theNode.addUpstreamNode((byte) 'a');
		assert theNode.getUpstreamNode((byte) 'a') != null;
		}

	@Test
	public void addUpstreamNodeChainWorks() throws SequenceSpectrumException
		{
		theNode = new RonPSTNode(new byte[0], new byte[]{'a', 'b', 'c', 'd'});
		assert theNode.getUpstreamNode((byte) 'd') == null;
		theNode.addUpstreamNode(new byte[]{'a', 'b', 'c', 'd'});
		assert theNode.getUpstreamNode((byte) 'd').getUpstreamNode((byte) 'c').getUpstreamNode((byte) 'b')
				.getUpstreamNode((byte) 'a') != null;
		}

	@Test
	public void copyProbsFromWorks()
		{
		try
			{
			theNode.copyProbsFrom(new RonPSTTest.StubSequenceSpectrum());
			for (final RonPSTNode trav : theNode.getAllUpstreamNodes())
				{
				assert trav.getProbs().size() == 0 || (trav.getProbs().size() == 4 && trav.getProbs()
						.isAlreadyNormalized());
				}
			}
		catch (Exception e)
			{
			logger.error("Error", e);
			assert false;
			}
		}

	@Test
	public void countUpstreamNodesWorks()
		{
		assert theNode.countUpstreamNodes() == 3;
		}

	@Test
	public void getAllUpstreamNodesWorks()
		{
		assert theNode.getAllUpstreamNodes().size() == 11;
		}

	@BeforeMethod
	public void setUp()
		{
		MathUtils.initApproximateLog(-12, 12, 3, 100000);
		try
			{
			buildSimplePST();
			}
		catch (SequenceSpectrumException e)
			{
			logger.error("Error", e);
			}
		}

	private void buildSimplePST() throws SequenceSpectrumException
		{
		theNode = new RonPSTNode(new byte[0], new byte[]{'a', 'b', 'c', 'd'});
		theNode.addUpstreamNode((byte) 'a');
		theNode.addUpstreamNode((byte) 'b');
		theNode.addUpstreamNode((byte) 'c');

		RonPSTNode trav = theNode.getUpstreamNode((byte) 'a');
		trav.addUpstreamNode((byte) 'b');
		trav.addUpstreamNode((byte) 'c');

		trav = theNode.getUpstreamNode((byte) 'b');
		trav.addUpstreamNode((byte) 'c');
		trav.addUpstreamNode((byte) 'd');

		trav = theNode.getUpstreamNode((byte) 'c');
		trav.addUpstreamNode((byte) 'b');
		trav.addUpstreamNode((byte) 'c');
		trav.addUpstreamNode((byte) 'd');
		}

	@Test
	public void updateLogProbsRecursiveWorks() throws SequenceSpectrumException
		{
		theNode.copyProbsFrom(new RonPSTTest.StubSequenceSpectrum());
		theNode.updateLogProbsRecursive();
		assert MathUtils.equalWithinFPError(theNode.getUpstreamNode((byte) 'a').getLogProbs()[0], -2.3025850929940455);
		}

	@Test
	public void updateLogProbsWorks()
		{
		theNode.copyProbsFrom(new RonPSTTest.StubSequenceSpectrum());
		theNode.updateLogProbs();
		assert MathUtils.equalWithinFPError(theNode.getLogProbs()[0], -1.6094379124341003);
		}
	}
