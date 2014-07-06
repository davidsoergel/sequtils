/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils;

import com.davidsoergel.stats.DissimilarityMeasure;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class GapFrequency implements DissimilarityMeasure<byte[]>
	{


	public double distanceFromTo(final byte[] a, final byte[] b)
		{
		assert a.length == b.length;

		final int len = a.length;

		int match = 0;
		int mismatch = 0;
		int gapA = 0;
		int gapB = 0;

		int gapBoth = 0;

		for (int i = 0; i < len; i++)
			{
			if (SequenceArrayUtils.isGap(a[i]))
				{
				if (SequenceArrayUtils.isGap(b[i]))
					{
					gapBoth++;
					}
				else
					{
					gapA++;
					}
				}
			else if (SequenceArrayUtils.isGap(b[i]))
				{
				gapB++;
				}
			else if (a[i] == b[i])
					{
					match++;
					}
				else
					{
					mismatch++;
					}
			}


		assert match + mismatch + gapA + gapB + gapBoth == len;

		int totalAligned = match + mismatch;
		int totalGaps = gapA + gapB;

		double denominator = (totalAligned + totalGaps);

		if (denominator == 0)
			{
			return 1.0;
			}

		return totalGaps / denominator;
		}
	}
