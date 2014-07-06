/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.ContractTest;
import com.davidsoergel.dsutils.ContractTestAware;
import com.davidsoergel.dsutils.TestInstanceFactory;
import com.davidsoergel.dsutils.math.MathUtils;
import com.davidsoergel.stats.DistributionException;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.util.Queue;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: MarkovTreeNodeTest.java 442 2009-06-15 17:53:56Z soergel $
 */

public class MarkovTreeNodeTest extends ContractTestAware<MarkovTreeNode>
		implements TestInstanceFactory<SequenceSpectrum>
	{
// ------------------------------ FIELDS ------------------------------

	private final byte[] alphabet = new byte[]{'a', 'b', 'c', 'd'};


// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface TestInstanceFactory ---------------------

	/**
	 * {@inheritDoc}
	 */
	public MarkovTreeNode createInstance() throws Exception
		{
		final SequenceSpectrum ss = createMockSimpleSpectrum();
		final MarkovTreeNode n = createSimpleMarkovTree();
		n.copyProbsFromSpectrumRecursively(ss);
		return n;
		}

// -------------------------- OTHER METHODS --------------------------

	@Test
	public void addChildSequenceWorks() throws SequenceSpectrumException
		{
		final MarkovTreeNode n = createComplexMarkovTree();
		assert n.getChild((byte) 'd') == null;
		n.add(new byte[]{'d', 'a', 'a', 'b'});
		assert n.getChild((byte) 'd') != null;
		assert n.getChild((byte) 'd').getChild((byte) 'a') != null;
		assert n.getChild((byte) 'd').getChild((byte) 'a').getChild((byte) 'a') != null;
		assert n.getChild((byte) 'd').getChild((byte) 'a').getChild((byte) 'a').getChild((byte) 'b') != null;
		assert n.getChild((byte) 'd').getChild((byte) 'a').getChild((byte) 'a').getChild((byte) 'b').getMaxDepth() == 1;
		assert n.getChild((byte) 'd').getMaxDepth() == 4;
		}

	private MarkovTreeNode createComplexMarkovTree() throws SequenceSpectrumException
		{
		final MarkovTreeNode node = new MarkovTreeNode(new byte[0], alphabet);
		node.add(new byte[]{'b', 'c', 'b'});
		node.add(new byte[]{'b', 'a', 'b'});
		node.add(new byte[]{'a', 'a', 'b'});
		return node;
		}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addContractTestsToQueue(final Queue<ContractTest> theContractTests)
		{
		theContractTests.add(new SequenceSpectrumInterfaceTest(this));
		}

	@Test
	public void addOneChildWorks() throws SequenceSpectrumException
		{
		final MarkovTreeNode n = createComplexMarkovTree();
		assert n.getChild((byte) 'd') == null;
		n.addChild((byte) 'd');
		assert n.getChild((byte) 'd') != null;
		assert n.getChild((byte) 'd').getMaxDepth() == 1;
		}

	@Test
	public void completeAndCopyProbsFromWorks() throws SequenceSpectrumException, DistributionException
		{
		final SequenceSpectrum ss = createMockSimpleSpectrum();

		final MarkovTreeNode n = createSimpleMarkovTree();
		n.copyProbsFromSpectrumRecursively(ss);

		assert n.getChild((byte) 'd') == null;// node d has no children, so it has no reason to exist
		assert n.get(new byte[]{'d'}) == null;
		assert n.conditionalProbability((byte) 'd') == 0.4;// but there is still a transition probability
		assert n.get(new byte[]{'b'}) != null;
		assert n.conditionalProbability((byte) 'd', new byte[]{'b'}) == 0.36;
		assert n.conditionalProbability((byte) 'd', new byte[]{'a'})
		       == 0.0;// this one was not specified, but shouldn't throw an exception-- that's the "complete" part
		}

	public static SequenceSpectrum createMockSimpleSpectrum() throws SequenceSpectrumException
		{
		final SequenceSpectrum ss = createMock(SequenceSpectrum.class);

		expect(ss.conditionalProbability(eq((byte) 'a'), aryEq(new byte[0]))).andReturn(.1);
		expect(ss.conditionalProbability(eq((byte) 'b'), aryEq(new byte[0]))).andReturn(.2);
		expect(ss.conditionalProbability(eq((byte) 'c'), aryEq(new byte[0]))).andReturn(.3);
		expect(ss.conditionalProbability(eq((byte) 'd'), aryEq(new byte[0]))).andReturn(.4);

		expect(ss.conditionalProbability(eq((byte) 'a'), aryEq(new byte[]{'a'}))).andReturn(.1);
		expect(ss.conditionalProbability(eq((byte) 'b'), aryEq(new byte[]{'a'}))).andReturn(.2);
		expect(ss.conditionalProbability(eq((byte) 'c'), aryEq(new byte[]{'a'}))).andReturn(.3);
		//expect(ss.conditionalProbability(eq((byte) 'd'), aryEq(new byte[]{'a'}))).andReturn(.4);
		expect(ss.conditionalProbability(eq((byte) 'd'), aryEq(new byte[]{'a'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();

		expect(ss.conditionalProbability(eq((byte) 'a'), aryEq(new byte[]{'b'}))).andReturn(.11);
		expect(ss.conditionalProbability(eq((byte) 'b'), aryEq(new byte[]{'b'}))).andReturn(.22);
		expect(ss.conditionalProbability(eq((byte) 'c'), aryEq(new byte[]{'b'}))).andReturn(.31);
		expect(ss.conditionalProbability(eq((byte) 'd'), aryEq(new byte[]{'b'}))).andReturn(.36);

		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'a', 'a'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'a', 'b'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'a', 'c'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'a', 'd'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();

		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'b', 'a'}))).andReturn(.25).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'b', 'b'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'b', 'c'}))).andReturn(.25).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'b', 'd'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();

		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'c', 'a'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'c', 'b'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'c', 'c'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'c', 'd'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();

		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'d', 'a'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'d', 'b'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'d', 'c'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();
		expect(ss.conditionalProbability(anyByte(), aryEq(new byte[]{'d', 'd'})))
				.andThrow(new SequenceSpectrumException("Unknown probability")).anyTimes();


		/*
		  expect(ss.conditionalProbability(eq((byte) 'a'), aryEq(new byte[]{
						  'b',
						  'a'
				  }))).andReturn(.1);
				  expect(ss.conditionalProbability(eq((byte) 'b'), aryEq(new byte[]{
						  'b',
						  'a'
				  }))).andReturn(.2);
				  expect(ss.conditionalProbability(eq((byte) 'c'), aryEq(new byte[]{
						  'b',
						  'a'
				  }))).andReturn(.3);
				  expect(ss.conditionalProbability(eq((byte) 'd'), aryEq(new byte[]{
						  'b',
						  'a'
				  }))).andReturn(.4);


				  expect(ss.conditionalProbability(eq((byte) 'a'), aryEq(new byte[]{
						  'b',
						  'c'
				  }))).andReturn(.1);
				  expect(ss.conditionalProbability(eq((byte) 'b'), aryEq(new byte[]{
						  'b',
						  'c'
				  }))).andReturn(.2);
				  expect(ss.conditionalProbability(eq((byte) 'c'), aryEq(new byte[]{
						  'b',
						  'c'
				  }))).andReturn(.3);
				  expect(ss.conditionalProbability(eq((byte) 'd'), aryEq(new byte[]{
						  'b',
						  'c'
				  }))).andReturn(.4);
		  */

		replay(ss);
		return ss;
		}

	private MarkovTreeNode createSimpleMarkovTree() throws SequenceSpectrumException
		{
		final MarkovTreeNode node = new MarkovTreeNode(new byte[0], alphabet);
		node.add(new byte[]{'b', 'c'});
		node.add(new byte[]{'b', 'a'});
		node.add(new byte[]{'a'});
		return node;
		}

	@Test(expectedExceptions = {SequenceSpectrumException.class})
	public void conditionalProbabilityThrowsExceptionOnOverlySpecificProbabilityRequest()
			throws SequenceSpectrumException, DistributionException
		{
		final SequenceSpectrum ss = createMockSimpleSpectrum();

		final MarkovTreeNode n = createSimpleMarkovTree();
		n.copyProbsFromSpectrumRecursively(ss);

		n.conditionalProbability((byte) 'd', new byte[]{'b', 'a', 'd'});
		}

	@Test
	public void emptyClonesHaveEqualValue() throws SequenceSpectrumException
		{
		final MarkovTreeNode n = createComplexMarkovTree();
		assert n.clone().equalValue(n);
		}

	@Factory
	public Object[] instantiateAllContractTests()
		{
		return super.instantiateAllContractTestsWithName(MarkovTreeNode.class.getCanonicalName());
		}

	@Test
	public void maxDepthWorks() throws SequenceSpectrumException
		{
		final MarkovTreeNode n = createComplexMarkovTree();
		assert n.getChild((byte) 'd') == null;
		n.addChild((byte) 'd');
		assert n.getChild((byte) 'd') != null;
		assert n.getChild((byte) 'd').getMaxDepth() == 1;
		}

	@Test
	public void populatedClonesHaveEqualValue() throws SequenceSpectrumException, DistributionException
		{
		final SequenceSpectrum ss = createMockSimpleSpectrum();

		final MarkovTreeNode n = createSimpleMarkovTree();
		n.copyProbsFromSpectrumRecursively(ss);

		assert n.clone().equalValue(n);
		}

	@Test
	public void totalProbabilitiesAreCorrect() throws SequenceSpectrumException, DistributionException
		{
		final SequenceSpectrum ss = createMockSimpleSpectrum();
		final MarkovTreeNode n = createSimpleMarkovTree();
		n.copyProbsFromSpectrumRecursively(ss);

		assert MathUtils.equalWithinFPError(n.totalProbability(new byte[]{'b'}), 0.2);
		assert MathUtils.equalWithinFPError(n.totalProbability(new byte[]{'b', 'a'}), 0.022);
		assert MathUtils.equalWithinFPError(n.totalProbability(new byte[]{'b', 'a'}), 0.022);
		assert MathUtils.equalWithinFPError(n.totalProbability(new byte[]{'b', 'c'}), 0.062);
		}

	@Test(expectedExceptions = {SequenceSpectrumException.class})
	public void totalProbabilityThrowsExceptionOnOverlySpecificProbabilityRequest()
			throws SequenceSpectrumException, DistributionException
		{
		final SequenceSpectrum ss = createMockSimpleSpectrum();

		final MarkovTreeNode n = createSimpleMarkovTree();
		n.copyProbsFromSpectrumRecursively(ss);

		n.totalProbability(new byte[]{'b', 'a', 'd', 'd'});
		}

	@Test
	public void variousProbabilitiesAreCorrectAndConsistent() throws SequenceSpectrumException, DistributionException
		{
		final SequenceSpectrum ss = createMockSimpleSpectrum();

		final MarkovTreeNode n = createSimpleMarkovTree();
		n.copyProbsFromSpectrumRecursively(ss);

		assert n.conditionalProbability((byte) 'a') == 0.1;
		assert n.conditionalProbability((byte) 'b') == 0.2;
		assert n.conditionalProbability((byte) 'c') == 0.3;
		assert n.conditionalProbability((byte) 'd') == 0.4;

		assert n.conditionalProbability((byte) 'a', new byte[]{'b'}) == 0.11;
		assert n.conditionalProbability((byte) 'b', new byte[]{'b'}) == 0.22;
		assert n.conditionalProbability((byte) 'c', new byte[]{'b'}) == 0.31;
		assert n.conditionalProbability((byte) 'd', new byte[]{'b'}) == 0.36;

		assert n.getChild((byte) 'b').conditionalProbability((byte) 'a') == 0.11;
		assert n.getChild((byte) 'b').conditionalProbability((byte) 'b') == 0.22;
		assert n.getChild((byte) 'b').conditionalProbability((byte) 'c') == 0.31;
		assert n.getChild((byte) 'b').conditionalProbability((byte) 'd') == 0.36;

		assert n.conditionalsFrom(new byte[]{'b'}).get((byte) 'a') == 0.11;
		assert n.conditionalsFrom(new byte[]{'b'}).get((byte) 'b') == 0.22;
		assert n.conditionalsFrom(new byte[]{'b'}).get((byte) 'c') == 0.31;
		assert n.conditionalsFrom(new byte[]{'b'}).get((byte) 'd') == 0.36;
		}
	}
