package edu.berkeley.compbio.sequtils;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class SimpleSubstitutionMatrix extends LogOddsSubstitutionMatrix
	{
	private final int match;
	private final int mismatch;
	private final int gap;


	public SimpleSubstitutionMatrix(final int match, final int mismatch, final int gap)
		{
		this.gap = gap;
		this.match = match;
		this.mismatch = mismatch;
		}

	@Override
	public int score(final byte c, final byte d)
		{
		final boolean gc = SequenceArrayUtils.isGap(c);
		final boolean gd = SequenceArrayUtils.isGap(d);
		if (gc && gd)
			{
			return 0;
			}
		if (gc || gd)
			{
			return gap;
			}
		if (c == d)
			{
			return match;
			}
		return mismatch;
		}
	}
