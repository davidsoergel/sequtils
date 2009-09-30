package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.collections.OrderedPair;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class DynamicProgrammingPairwiseAligner
	{
	private static final Logger logger = Logger.getLogger(DynamicProgrammingPairwiseAligner.class);

	private static enum TracebackDirection
		{
			VERT, HORIZ, DIAG, STOP;
		}

	private final AffineSubstitutionMatrix matrix;
//	private final int match;
//	private final int mismatch;
//	private final int gap;

	public static enum TracebackBegin
		{
			CORNER, // global
			RIGHT, // semi-global, include end of seqH
			BOTTOM, // semi-global, include end of seqV
			BOTTOMORRIGHT, // semi-global, include end of at least one sequence
			INTERNAL // local
		}

	public static enum TracebackEnd
		{
			CORNER, // global
			LEFT, // semi-global, include beginning of seqH
			TOP, // semi-global, include beginning of seqV
			TOPORLEFT, // semi-global, include beginning of at least one sequence
			INTERNAL // local
		}

	/*
	private final boolean ignoreLeadingGapsA;
	private final boolean ignoreTrailingGapsA;
	private final boolean ignoreLeadingGapsB;
	private final boolean ignoreTrailingGapsB;

	private final boolean ignoreLeadingPoorAlignment;
	private final boolean ignoreTrailingPoorAlignment;
*/

	private final TracebackBegin traceBegin;
	private final TracebackEnd traceEnd;

	/*public DynamicProgrammingPairwiseAligner(final int match, final int mismatch, final int gap,
	                                         final TracebackBegin traceBegin, final TracebackEnd traceEnd)
		{
		this.gap = gap;
		this.match = match;
		this.mismatch = mismatch;
		this.traceBegin = traceBegin;
		this.traceEnd = traceEnd;
		}*/

	public DynamicProgrammingPairwiseAligner(final AffineSubstitutionMatrix matrix, final TracebackBegin traceBegin,
	                                         final TracebackEnd traceEnd)
		{
		this.matrix = matrix;
		this.traceBegin = traceBegin;
		this.traceEnd = traceEnd;
		}

	public static DynamicProgrammingPairwiseAligner getSmithWaterman()
		{
		return new DynamicProgrammingPairwiseAligner(new SimpleSubstitutionMatrix(5, -4, -10, -0.5f),
		                                             TracebackBegin.INTERNAL, TracebackEnd.INTERNAL);
		}

	public static DynamicProgrammingPairwiseAligner getNeedlemanWunsch()
		{
		// these are the default parameters from NCBI and EMBOSS
		// see http://emboss.sourceforge.net/apps/cvs/emboss/apps/needle.html
		// and ftp://ftp.ncbi.nih.gov/blast/matrices/NUC.4.2 and 4.4
		return new DynamicProgrammingPairwiseAligner(new SimpleSubstitutionMatrix(5, -4, -10, -0.5f),
		                                             TracebackBegin.CORNER, TracebackEnd.CORNER);
		}


	/*
	   It's easy to get mixed up about which sequence is on which axis, etc.  The conventions are as follows:



					seqH ->   (hIndex)
			 seqV
			   |
			   V


			  (vIndex)


			dp[hIndex][vIndex]


			 */


	public OrderedPair<byte[], byte[]> align(byte[] seqH, byte[] seqV)
		{
		// assume that the input sequences are already gap-free

		float[][] dp = new float[seqH.length + 1][seqV.length + 1];
		TracebackDirection[][] trace = new TracebackDirection[seqH.length + 1][seqV.length + 1];

		// initialize the top left corner

		dp[0][0] = 0;
		trace[0][0] = TracebackDirection.STOP;


		// initialize the top edge

		if (traceEnd == TracebackEnd.INTERNAL || traceEnd == TracebackEnd.TOP || traceEnd == TracebackEnd.TOPORLEFT)
			{
			// leading gaps in B, represented by a path along the top edge, are not penalized
			for (int hIndex = 0; hIndex < seqH.length; hIndex++)
				{
				int dpHIndex = hIndex + 1;
				dp[dpHIndex][0] = 0;
				trace[dpHIndex][0] = TracebackDirection.STOP;
				}
			}
		else
			{
			// penalize leading gaps in B (consuming characters from A);

			// the first cell incurs a gap-open penalty
			float x = matrix.score(seqH[0], SequenceArrayUtils.GAP_BYTE, false);
			dp[1][0] = x;
			trace[1][0] = TracebackDirection.HORIZ;

			for (int hIndex = 1; hIndex < seqH.length; hIndex++)
				{
				int dpHIndex = hIndex + 1;
				x += matrix.score(seqH[hIndex], SequenceArrayUtils.GAP_BYTE, true);
				dp[dpHIndex][0] = x;
				trace[dpHIndex][0] = TracebackDirection.HORIZ;
				}
			}


		// initialize the left edge

		if (traceEnd == TracebackEnd.INTERNAL || traceEnd == TracebackEnd.LEFT || traceEnd == TracebackEnd.TOPORLEFT)
			{
			// leading gaps in A, represented by a path along the left edge, are not penalized
			for (int vIndex = 0; vIndex < seqV.length; vIndex++)
				{
				int dpVIndex = vIndex + 1;
				dp[0][dpVIndex] = 0;
				trace[0][dpVIndex] = TracebackDirection.STOP;
				}
			}
		else
			{
			// penalize leading gaps in H (consuming characters from V);

			// the first cell incurs a gap-open penalty
			float x = matrix.score(seqV[0], SequenceArrayUtils.GAP_BYTE, false);
			dp[0][1] = x;
			trace[0][1] = TracebackDirection.VERT;

			for (int vIndex = 1; vIndex < seqV.length; vIndex++)
				{
				int dpVIndex = vIndex + 1;
				x += matrix.score(seqV[vIndex], SequenceArrayUtils.GAP_BYTE, true);
				dp[0][dpVIndex] = x;
				trace[0][dpVIndex] = TracebackDirection.VERT;
				}
			}


		// do DP in row-first order (doesn't really matter).

		// Don't bother keeping track of the best score; we'll just scan for it later, depending on the alignment type
		// note the dp indexes are 1 ahead of the sequence indexes...very confusing.
		// That is: the dp indexes indicate how many characters of the sequence have been consumed already at this cell.
		// Thus, the index in the sequence of the character that was just consumed is 1 less than that.

		// we'll do the iteration in terms of the sequences, then add 1 to get the dp cell

		for (int hIndex = 0; hIndex < seqH.length; hIndex++)
			{
			int dpHIndex = hIndex + 1;
			for (int vIndex = 0; vIndex < seqV.length; vIndex++)
				{
				int dpVIndex = vIndex + 1;

				// compute possible scores for each path.
				// Note how we check whether the previous state was a gap in the same sequence, for the sake of affine penalties.

				float horiz = dp[dpHIndex - 1][dpVIndex] + matrix.score(seqH[hIndex], SequenceArrayUtils.GAP_BYTE,
				                                                        trace[dpHIndex - 1][dpVIndex]
				                                                        == TracebackDirection.HORIZ);

				float vert = dp[dpHIndex][dpVIndex - 1] + matrix.score(SequenceArrayUtils.GAP_BYTE, seqV[vIndex],
				                                                       trace[dpHIndex][dpVIndex - 1]
				                                                       == TracebackDirection.VERT);

				float diag = dp[dpHIndex - 1][dpVIndex - 1] + matrix.score(seqH[hIndex], seqV[vIndex], false);

				// choose the maximum, storing the traceback info.
				// prefer the diagonal transition in case of a tie; second prefer the horizontal transition (gap in B)

				dp[dpHIndex][dpVIndex] = diag;
				trace[dpHIndex][dpVIndex] = TracebackDirection.DIAG;

				if (horiz > dp[dpHIndex][dpVIndex])
					{
					dp[dpHIndex][dpVIndex] = horiz;
					trace[dpHIndex][dpVIndex] = TracebackDirection.HORIZ;
					}
				if (vert > dp[dpHIndex][dpVIndex])
					{
					dp[dpHIndex][dpVIndex] = vert;
					trace[dpHIndex][dpVIndex] = TracebackDirection.VERT;
					}

				// if this is a local alignment and the score is negative, reset it to 0
				if (traceEnd == TracebackEnd.INTERNAL && dp[dpHIndex][dpVIndex] < 0)
					{
					dp[dpHIndex][dpVIndex] = 0;
					trace[dpHIndex][dpVIndex] = TracebackDirection.STOP;
					}
				}
			}

		// Decide where to start the traceback, in dp coordinates

		int aTrace = -1;  // induce ArrayIndexOutOfBoundsException if this doesn't get set (should be impossible)
		int bTrace = -1;

		switch (traceBegin)
			{
			case CORNER:
				aTrace = seqH.length;
				bTrace = seqV.length;
				break;
			case BOTTOM:
				// scan the bottom edge for the best score
				bTrace = seqV.length;
				aTrace = scanBottom(dp);
				break;
			case RIGHT:
				// scan the right edge for the best score
				aTrace = seqH.length;
				bTrace = scanRight(dp);
				break;
			case BOTTOMORRIGHT:
				// scan the bottom and right edges for the best score

				aTrace = seqH.length;
				bTrace = seqV.length;

				int besthIndex = scanBottom(dp);
				int bestvIndex = scanRight(dp);

				float bestBottomScore = dp[besthIndex][bTrace];
				float bestRightScore = dp[aTrace][bestvIndex];

				if (bestBottomScore > bestRightScore)
					{
					aTrace = besthIndex;
					}
				else
					{
					bTrace = bestvIndex;
					}

				break;
			case INTERNAL:
				// scan the entire array for the best score.
				// in the event of a tie, pick the one nearest the end on A; if still a tie, pick nearest the end on B

				bTrace = seqV.length - 1;

				float bestScore = Float.MIN_VALUE;
				for (int hIndex = 0; hIndex < seqH.length; hIndex++)
					{
					for (int vIndex = 0; vIndex < seqV.length; vIndex++)
						{
						if (dp[hIndex][vIndex] > bestScore)
							{
							bestScore = dp[hIndex][vIndex];
							aTrace = hIndex;
							bTrace = vIndex;
							}
						}
					}

				break;
			}

		// now iTrav and jTrav point to the starting cell for the traceback.


		// Traceback.
		// We don't know yet how wide the alignment is, so allocate the maximum, and fill the arrays from the back

		final int maxWidth = seqH.length + seqV.length;
		byte[] alignedA = new byte[maxWidth];
		byte[] alignedB = new byte[maxWidth];

		int col = maxWidth - 1;

		boolean done = false;
		while (!done)
			{
			switch (trace[aTrace][bTrace])
				{
				case DIAG:
					alignedA[col] = seqH[aTrace - 1];
					alignedB[col] = seqV[bTrace - 1];
					aTrace--;
					bTrace--;
					break;
				case HORIZ:
					alignedA[col] = seqH[aTrace - 1];
					alignedB[col] = SequenceArrayUtils.GAP_BYTE;
					aTrace--;
					break;
				case VERT:
					alignedA[col] = SequenceArrayUtils.GAP_BYTE;
					alignedB[col] = seqV[bTrace - 1];
					bTrace--;
					break;
				case STOP:
					// where the traceback stops is determined by the states set up above according to the traceEnd setting
					//alignedA[col] = seqH[aTrace - 1];
					//alignedB[col] = seqV[bTrace - 1];
					done = true;
					break;
				}
			col--;
			}

		// now col points two before the beginning of the actual alignment, so we want to trim off the front

		int startPos = col + 2;
		int alignmentWidth = maxWidth - startPos;

		alignedA = SequenceArrayUtils.copySlice(alignedA, startPos, alignmentWidth);
		alignedB = SequenceArrayUtils.copySlice(alignedB, startPos, alignmentWidth);

		//logger.error(new String(alignedA));
		//logger.error(new String(alignedB));

		return new OrderedPair<byte[], byte[]>(alignedA, alignedB);
		}

	private int scanBottom(final float[][] dp)
		{
		int besthIndex = -1;
		float bestBottomScore = Float.MIN_VALUE;
		int cols = dp.length;
		int rows = dp[0].length;
		int lastRow = rows - 1;

		for (int hIndex = 0; hIndex < cols; hIndex++)
			{
			if (dp[hIndex][lastRow] > bestBottomScore)
				{
				bestBottomScore = dp[hIndex][lastRow];
				besthIndex = hIndex;
				}
			}
		return besthIndex;
		}

	private int scanRight(final float[][] dp)
		{
		int bestvIndex = -1;
		float bestRightScore = Float.MIN_VALUE;
		int cols = dp.length;
		int rows = dp[0].length;
		int lastCol = cols - 1;

		for (int vIndex = 0; vIndex < rows; vIndex++)
			{
			if (dp[lastCol][vIndex] > bestRightScore)
				{
				bestRightScore = dp[lastCol][vIndex];
				bestvIndex = vIndex;
				}
			}
		return bestvIndex;
		}
	}
