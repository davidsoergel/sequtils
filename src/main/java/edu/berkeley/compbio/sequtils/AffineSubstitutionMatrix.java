package edu.berkeley.compbio.sequtils;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public interface AffineSubstitutionMatrix
	{
	float score(final byte c, final byte d, boolean gapIsOpen);
	}
