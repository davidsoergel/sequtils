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
		OrderedPair<byte[], byte[]> realigned = realign(new OrderedPair<byte[], byte[]>(a, b), aligner);
		if (realigned == null)
			{
			return Double.NaN;
			}
		return dnaDist.distanceFromTo(realigned.getKey1(), realigned.getKey2());
		}

	public static OrderedPair<byte[], byte[]> realign(OrderedPair<byte[], byte[]> pair,
	                                                  final DynamicProgrammingPairwiseAligner aligner)
		{
		// make gap-free copies
		byte[] aGapFree = SequenceArrayUtils.copyNoGaps(pair.getKey1());
		byte[] bGapFree = SequenceArrayUtils.copyNoGaps(pair.getKey2());

		if (aGapFree.length == 0 || bGapFree.length == 0)
			{
			return null; //Double.NaN;
			}

		// align them

		OrderedPair<byte[], byte[]> aligned = aligner.align(aGapFree, bGapFree);

		byte[] realignedA = aligned.getKey1();
		byte[] realignedB = aligned.getKey2();
		return new OrderedPair<byte[], byte[]>(realignedA, realignedB);
		}
	}
