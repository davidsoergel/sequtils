package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.collections.OrderedPair;
import com.davidsoergel.stats.DissimilarityMeasure;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class PairAlignedDnaDist implements DissimilarityMeasure<byte[]>
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
		// make gap-free copies
		byte[] aGapFree = SequenceArrayUtils.copyNoGaps(a);
		byte[] bGapFree = SequenceArrayUtils.copyNoGaps(b);

		if (aGapFree.length == 0 || bGapFree.length == 0)
			{
			return Double.NaN;
			}

		// align them

		OrderedPair<byte[], byte[]> aligned = aligner.align(aGapFree, bGapFree);

		byte[] realignedA = aligned.getKey1();
		byte[] realignedB = aligned.getKey2();

		return dnaDist.distanceFromTo(realignedA, realignedB);
		}
	}
