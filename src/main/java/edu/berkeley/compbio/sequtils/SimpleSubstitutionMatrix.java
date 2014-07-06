/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class SimpleSubstitutionMatrix implements AffineSubstitutionMatrix
	{
	private final float match;
	private final float mismatch;
	private final float gapOpen;
	private final float gapExtend;


	public SimpleSubstitutionMatrix(final float match, final float mismatch, final float gapOpen, final float gapExtend)
		{
		this.gapOpen = gapOpen;
		this.gapExtend = gapExtend;
		this.match = match;
		this.mismatch = mismatch;
		}

	public float score(final byte c, final byte d, boolean gapIsOpen)
		{
		final boolean gc = SequenceArrayUtils.isGap(c);
		final boolean gd = SequenceArrayUtils.isGap(d);
		if (gc && gd)
			{
			return 0;
			}
		if (gc || gd)
			{
			return gapIsOpen ? gapExtend : gapOpen;
			}
		if (c == d)
			{
			return match;
			}
		return mismatch;
		}
	}
