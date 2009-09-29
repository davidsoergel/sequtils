package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.collections.OrderedPair;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class DynamicProgrammingPairwiseAligner
	{
	private static enum TracebackDirection
		{
			VERT, HORIZ, DIAG, STOP;

		private static boolean isGap(TracebackDirection z)
			{
			return z == VERT || z == HORIZ;
			}
		}

	private final AffineSubstitutionMatrix matrix;
//	private final int match;
//	private final int mismatch;
//	private final int gap;

	public static enum TracebackBegin
		{
			CORNER, // global
			RIGHT, // semi-global, include end of seqA
			BOTTOM, // semi-global, include end of seqB
			BOTTOMORRIGHT, // semi-global, include end of at least one sequence
			INTERNAL // local
		}

	public static enum TracebackEnd
		{
			CORNER, // global
			LEFT, // semi-global, include beginning of seqA
			TOP, // semi-global, include beginning of seqB
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
		return new DynamicProgrammingPairwiseAligner(new SimpleSubstitutionMatrix(5, -4, -10, .5f),
		                                             TracebackBegin.INTERNAL, TracebackEnd.INTERNAL);
		}

	public static DynamicProgrammingPairwiseAligner getNeedlemanWunsch()
		{
		// these are the default parameters from NCBI and EMBOSS
		// see http://emboss.sourceforge.net/apps/cvs/emboss/apps/needle.html
		// and ftp://ftp.ncbi.nih.gov/blast/matrices/NUC.4.2 and 4.4
		return new DynamicProgrammingPairwiseAligner(new SimpleSubstitutionMatrix(5, -4, -10, .5f),
		                                             TracebackBegin.CORNER, TracebackEnd.CORNER);
		}


	/*
	   It's easy to get mixed up about which sequence is on which axis, etc.  The conventions are as follows:



					seqA ->   (aIndex)
			 seqB
			   |
			   V


			  (bIndex)


			dp[aIndex][bIndex]


			 */


	public OrderedPair<byte[], byte[]> align(byte[] seqA, byte[] seqB)
		{
		// assume that the input sequences are already gap-free

		float[][] dp = new float[seqA.length][seqB.length];
		TracebackDirection[][] trace = new TracebackDirection[seqA.length][seqB.length];

		// initialize the top left corner

		dp[0][0] = 0;
		trace[0][0] = TracebackDirection.STOP;


		// initialize the top edge

		if (traceEnd == TracebackEnd.INTERNAL || traceEnd == TracebackEnd.TOP || traceEnd == TracebackEnd.TOPORLEFT)
			{
			// leading gaps in B, represented by a path along the top edge, are not penalized
			for (int aIndex = 1; aIndex < seqA.length; aIndex++)
				{
				dp[aIndex][0] = 0;
				trace[aIndex][0] = TracebackDirection.STOP;
				}
			}
		else
			{
			int x = 1;
			for (int aIndex = 1; aIndex < seqA.length; aIndex++)
				{
				dp[aIndex][0] = x;
				trace[aIndex][0] = TracebackDirection.HORIZ;
				x++;
				}
			}


		// initialize the left edge

		if (traceEnd == TracebackEnd.INTERNAL || traceEnd == TracebackEnd.LEFT || traceEnd == TracebackEnd.TOPORLEFT)
			{
			// leading gaps in A, represented by a path along the left edge, are not penalized
			for (int bIndex = 1; bIndex < seqA.length; bIndex++)
				{
				dp[0][bIndex] = 0;
				trace[0][bIndex] = TracebackDirection.STOP;
				}
			}
		else
			{
			int x = 1;
			for (int bIndex = 1; bIndex < seqA.length; bIndex++)
				{
				dp[0][bIndex] = x;
				trace[0][bIndex] = TracebackDirection.VERT;
				x++;
				}
			}


		// do DP in row-first order (doesn't really matter).

		// Don't bother keeping track of the best score; we'll just scan for it later, depending on the alignment type

		for (int aIndex = 0; aIndex < seqA.length; aIndex++)
			{
			for (int bIndex = 0; bIndex < seqB.length; bIndex++)
				{

				// compute possible scores for each path.  Note how we check whether the previes state was a gap, for the sake of affine penalties
				float horiz = dp[aIndex - 1][bIndex] + matrix
						.score(seqA[aIndex], seqB[bIndex], TracebackDirection.isGap(trace[aIndex - 1][bIndex]));

				float vert = dp[aIndex][bIndex - 1] + matrix
						.score(seqA[aIndex], seqB[bIndex], TracebackDirection.isGap(trace[aIndex][bIndex - 1]));

				float diag = dp[aIndex - 1][bIndex - 1] + matrix.score(seqA[aIndex], seqB[bIndex], false);

				// choose the maximum, storing the traceback info

				dp[aIndex][bIndex] = vert;
				trace[aIndex][bIndex] = TracebackDirection.VERT;

				if (horiz > dp[aIndex][bIndex])
					{
					dp[aIndex][bIndex] = horiz;
					trace[aIndex][bIndex] = TracebackDirection.HORIZ;
					}
				if (diag > dp[aIndex][bIndex])
					{
					dp[aIndex][bIndex] = diag;
					trace[aIndex][bIndex] = TracebackDirection.DIAG;
					}

				// if this is a local alignment and the score is negative, reset it to 0
				if (traceEnd == TracebackEnd.INTERNAL && dp[aIndex][bIndex] < 0)
					{
					dp[aIndex][bIndex] = 0;
					trace[aIndex][bIndex] = TracebackDirection.STOP;
					}
				}
			}

		// Decide where to start the traceback

		int aTrace = -1;  // induce IndexArrayOutOfBoundsException if this doesn't get set (should be impossible)
		int bTrace = -1;

		switch (traceBegin)
			{
			case CORNER:
				aTrace = seqA.length - 1;
				bTrace = seqB.length - 1;
				break;
			case BOTTOM:
				// scan the bottom edge for the best score
				bTrace = seqB.length - 1;
				aTrace = scanBottom(dp);
				break;
			case RIGHT:
				// scan the right edge for the best score
				aTrace = seqA.length - 1;
				bTrace = scanRight(dp);
				break;
			case BOTTOMORRIGHT:
				// scan the bottom and right edges for the best score

				aTrace = seqA.length - 1;
				bTrace = seqB.length - 1;

				int bestAIndex = scanBottom(dp);
				int bestBIndex = scanRight(dp);

				float bestBottomScore = dp[bestAIndex][bTrace];
				float bestRightScore = dp[aTrace][bestBIndex];

				if (bestBottomScore > bestRightScore)
					{
					aTrace = bestAIndex;
					}
				else
					{
					bTrace = bestBIndex;
					}

				break;
			case INTERNAL:
				// scan the entire array for the best score.
				// in the event of a tie, pick the one nearest the end on A; if still a tie, pick nearest the end on B

				bTrace = seqB.length - 1;

				float bestScore = 0;
				for (int aIndex = 0; aIndex < seqA.length; aIndex++)
					{
					for (int bIndex = 0; bIndex < seqB.length; bIndex++)
						{
						if (dp[aIndex][bIndex] > bestScore)
							{
							bestScore = dp[aIndex][bIndex];
							aTrace = aIndex;
							bTrace = bIndex;
							}
						}
					}

				break;
			}

		// now iTrav and jTrav point to the starting cell for the traceback.


		// Traceback.
		// We don't know yet how wide the alignment is, so allocate the maximum, and fill the arrays from the back

		final int maxWidth = seqA.length + seqB.length;
		byte[] alignedA = new byte[maxWidth];
		byte[] alignedB = new byte[maxWidth];

		int col = maxWidth - 1;

		boolean done = false;
		while (!done)
			{
			switch (trace[aTrace][bTrace])
				{
				case DIAG:
					alignedA[col] = seqA[aTrace];
					alignedB[col] = seqB[bTrace];
					aTrace--;
					bTrace--;
					break;
				case HORIZ:
					alignedA[col] = seqA[aTrace];
					alignedB[col] = SequenceArrayUtils.GAP_BYTE;
					aTrace--;
					break;
				case VERT:
					alignedA[col] = SequenceArrayUtils.GAP_BYTE;
					alignedB[col] = seqB[bTrace];
					bTrace--;
					break;
				case STOP:
					// where the traceback stops is determined by the states set up above according to the traceEnd setting
					// note we don't include the characters at this position
					done = true;
					break;
				}
			col--;
			}

		// now col points just before the beginning of the actual alignment, so we want to trim off the front

		int startPos = col + 1;
		int alignmentWidth = maxWidth - startPos;

		alignedA = SequenceArrayUtils.copySlice(alignedA, startPos, alignmentWidth);
		alignedB = SequenceArrayUtils.copySlice(alignedB, startPos, alignmentWidth);

		return new OrderedPair<byte[], byte[]>(alignedA, alignedB);
		}

	private int scanBottom(final float[][] dp)
		{
		int bestAIndex = -1;
		float bestBottomScore = 0;
		int cols = dp.length;
		int rows = dp[0].length;
		int lastRow = rows - 1;

		for (int aIndex = 0; aIndex < cols; aIndex++)
			{
			if (dp[aIndex][lastRow] > bestBottomScore)
				{
				bestBottomScore = dp[aIndex][lastRow];
				bestAIndex = aIndex;
				}
			}
		return bestAIndex;
		}

	private int scanRight(final float[][] dp)
		{
		int bestBIndex = -1;
		float bestRightScore = 0;
		int cols = dp.length;
		int rows = dp[0].length;
		int lastCol = cols - 1;

		for (int bIndex = 0; bIndex < rows; bIndex++)
			{
			if (dp[lastCol][bIndex] > bestRightScore)
				{
				bestRightScore = dp[lastCol][bIndex];
				bestBIndex = bIndex;
				}
			}
		return bestBIndex;
		}
	}
