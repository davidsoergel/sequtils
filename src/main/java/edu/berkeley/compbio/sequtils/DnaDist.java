package edu.berkeley.compbio.sequtils;

import com.davidsoergel.stats.DissimilarityMeasure;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class DnaDist implements DissimilarityMeasure<byte[]>
	{
	//** cleaner to describe this as affine gaps?  bah too complicated for this purpose

	public enum GapMode
		{
			NOGAPS, EACHGAP, ONEGAP
		}

	public final GapMode gapmode;

	//	private static final byte GAP_BYTE = SequenceArrayUtils.GAP_BYTE;  // is there some advantage to having this local?

	public DnaDist(final GapMode gapmode)
		{
		this.gapmode = gapmode;
		}

	public double distanceFromTo(final byte[] a, final byte[] b)
		{
		assert a.length == b.length;

		if (gapmode == GapMode.ONEGAP)
			{
			return distanceFromToOneGap(a, b); // separate this out for efficiency
			}

		final int len = a.length;

		int match = 0;
		int mismatch = 0;
		int gapA = 0;
		int gapB = 0;

		int gapBoth = 0;

		for (int i = 0; i < len; i++)
			{
			if (SequenceArrayUtils.isGap(a[i]))// == GAP_BYTE)
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
		double denominator;
		if (gapmode == GapMode.NOGAPS)
			{
			denominator = (double) (match + mismatch);
			return (double) match / denominator;
			}
		else // if (gapmode == GapMode.EACHGAP)
			{
			denominator = (double) (match + mismatch + gapA + gapB);
			}

		if (Double.isNaN(denominator) || Double.isInfinite(denominator))
			{
			return 1.0;
			}
		return 1.0 - ((double) match / denominator);
		}

	private double distanceFromToOneGap(final byte[] a, final byte[] b)
		{
		int len = a.length;

		int match = 0;
		int mismatch = 0;
		int gapA = 0;
		int gapB = 0;
		int gapBlocksA = 0;
		int gapBlocksB = 0;

		boolean aWasGap = false;
		boolean bWasGap = false;

		int gapBoth = 0;

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

		final double denominator = (double) (match + mismatch + gapBlocksA + gapBlocksB);

		if (Double.isNaN(denominator) || Double.isInfinite(denominator))
			{
			return 1.0;
			}
		return 1.0 - ((double) match / denominator);
		}
	}
