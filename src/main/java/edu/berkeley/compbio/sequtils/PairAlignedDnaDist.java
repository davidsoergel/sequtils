/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.collections.OrderedPair;
import com.davidsoergel.stats.DissimilarityMeasure;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class PairAlignedDnaDist implements DissimilarityMeasure<byte[]> //, DissimilarityMeasure<SequenceFragment>
	{
	private final DnaDist dnaDist;
	private final DynamicProgrammingPairwiseAligner aligner;

	public PairAlignedDnaDist(final DnaDist dnaDist, final DynamicProgrammingPairwiseAligner aligner)
		{
		this.dnaDist = dnaDist;
		this.aligner = aligner;
		}

	public double distanceFromTo(final byte[] a, final byte[] b)
		{
		OrderedPair<byte[], byte[]> realigned = aligner.realign(new OrderedPair<byte[], byte[]>(a, b));
		if (realigned == null)
			{
			return Double.NaN;
			}
		return dnaDist.distanceFromTo(realigned.getKey1(), realigned.getKey2());
		}


	@Override
	public String toString()
		{
		String shortname = getClass().getName();
		shortname = shortname.substring(shortname.lastIndexOf(".") + 1);
		return shortname //+ " " + aligner 
		       + " " + dnaDist;
		}
	}
