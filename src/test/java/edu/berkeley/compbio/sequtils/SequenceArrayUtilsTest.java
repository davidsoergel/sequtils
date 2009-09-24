package edu.berkeley.compbio.sequtils;

import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class SequenceArrayUtilsTest
	{
	byte[] b = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
	byte[] bg = new byte[]{0, 1, 2, 3, '-', 5, '-', '-', 8, 9, 10, 11, 12};

	@Test
	public void testCopySlice()
		{
		byte[] r = SequenceArrayUtils.copySlice(b, 3, 4);

		assert Arrays.equals(r, new byte[]{3, 4, 5, 6});
		}

	@Test
	public void testCopySliceNoGaps() throws NotEnoughSequenceException
		{
		byte[] r = SequenceArrayUtils.copySliceNoGaps(bg, 3, 4);

		assert Arrays.equals(r, new byte[]{3, '-', 5, '-', '-', 8, 9});
		}

	@Test
	public void testCopySliceNoGapsReverse() throws NotEnoughSequenceException
		{
		byte[] r = SequenceArrayUtils.copySliceNoGapsReverse(bg, 10, 4);

		assert Arrays.equals(r, new byte[]{3, '-', 5, '-', '-', 8, 9});
		}
	}
