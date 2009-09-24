package edu.berkeley.compbio.sequtils;

import com.davidsoergel.stats.DissimilarityMeasure;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class DnaDist implements DissimilarityMeasure<byte[]>
	{
	private double gapOpenPenalty;
	private double gapExtendsPenalty;


	public DnaDist(final double gapOpenPenalty, final double gapExtendsPenalty)
		{
		this.gapExtendsPenalty = gapExtendsPenalty;
		this.gapOpenPenalty = gapOpenPenalty;
		}

	public double distanceFromTo(final byte[] a, final byte[] b)
		{
		assert a.length == b.length;

		final int len = a.length;

		int match = 0;
		int mismatch = 0;
		int gapA = 0;
		int gapB = 0;

		int gapBlocksA = 0;
		int gapBlocksB = 0;

		boolean aWasGap = false;
		boolean bWasGap = false;

		int gapBoth = 0;

		Set<Byte> matches = new HashSet<Byte>();

		for (int i = 0; i < len; i++)
			{
			if (SequenceArrayUtils.isGap(a[i]))
				{
				if (SequenceArrayUtils.isGap(b[i]))
					{
					gapBoth++;
					// does not count towards A or B gap blocks, just ignore entirely
					}
				else
					{
					gapA++;
					if (!aWasGap)
						{
						gapBlocksA++;
						}
					aWasGap = true;
					bWasGap = false;
					}
				}
			else if (SequenceArrayUtils.isGap(b[i]))
				{
				gapB++;
				if (!bWasGap)
					{
					gapBlocksB++;
					}
				aWasGap = false;
				bWasGap = true;
				}
			else if (a[i] == b[i])
					{
					matches.add(a[i]);
					match++;
					aWasGap = false;
					bWasGap = false;
					}
				else
					{
					mismatch++;
					aWasGap = false;
					bWasGap = false;
					}
			}


		assert match + mismatch + gapA + gapB + gapBoth == len;


		final int gapOpens = gapBlocksA + gapBlocksB;
		final int totalAligned = match + mismatch;
		final int totalGaps = gapA + gapB;
		final int gapExtends = totalGaps - gapOpens;

		// in the "affine" model here, we just add the gaps to the alignment as weighted mismatches, extending both the numerator and the denominator
		final double gapsAsMismatches = gapOpens * gapOpenPenalty + gapExtends * gapExtendsPenalty;

		final double numerator = mismatch + gapsAsMismatches;
		final double denominator = totalAligned + gapsAsMismatches;

		final double mismatchFrequency = numerator / denominator;

		final double jukesCantorDistance = -3. / 4. * Math.log(1 - (4. / 3.) * mismatchFrequency);

		if (Double.isNaN(jukesCantorDistance) || Double.isInfinite(jukesCantorDistance))
			{
			return UNKNOWN_DISTANCE;
			//throw new NotEnoughSequenceException("No overlapping sequence; can't compute a distance");
			}

		return jukesCantorDistance;
		}
	}
