package edu.berkeley.compbio.sequtils;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class SequenceArrayUtilsTest
	{
	private static final Logger logger = Logger.getLogger(SequenceArrayUtilsTest.class);

	byte[] b = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
	byte[] bg = new byte[]{0, 1, 2, 3, '-', 5, '-', '-', 8, 9, 10, 11, 12};

	@Test
	public void testReverseComplement()
		{
		byte[] rc = SequenceArrayUtils.reverseComplement("A-CGT-RS--".getBytes());
		assert Arrays.equals(rc, "--WY-ACG-T".getBytes());
		}

	@Test
	public void testIupacPattern()
		{
		Pattern rc = SequenceArrayUtils.iupacPattern("ACGTRS");
		logger.error(rc.pattern());
		assert rc.pattern().equals("[.-]*A[.-]*C[.-]*G[.-]*T[.-]*[AGR][.-]*[CGS]");
		}

	@Test
	public void testStartsWithIupac()
		{
		assert SequenceArrayUtils.startsWithIUPAC("ACGTGC".getBytes(), "ACGTRS".getBytes());
		assert SequenceArrayUtils.startsWithIUPAC("A--C-G--TGC--".getBytes(), "ACGTRS".getBytes());


		assert !SequenceArrayUtils.startsWithIUPAC("-AAGTGC".getBytes(), "ACGTRS".getBytes());
		assert !SequenceArrayUtils.startsWithIUPAC("-A--A-G--TGC--".getBytes(), "ACGTRS".getBytes());

		assert !SequenceArrayUtils.startsWithIUPAC("-ACGTGC".getBytes(), "ACGTRS".getBytes());
		assert !SequenceArrayUtils.startsWithIUPAC("-A--C-G--TGC--".getBytes(), "ACGTRS".getBytes());
		}

	@Test(expectedExceptions = SequenceArrayException.class)
	public void startsWithIupacAcceptsNoGaps()
		{
		assert SequenceArrayUtils.startsWithIUPAC("ACGTGC".getBytes(), "AC-GTRS".getBytes());
		}

	@Test
	public void testCopySlice()
		{
		byte[] r = SequenceArrayUtils.copySlice(b, 3, 4);

		assert Arrays.equals(r, new byte[]{3, 4, 5, 6});
		}

	@Test
	public void testCopySliceUpToNNonGaps() throws NotEnoughSequenceException
		{
		byte[] r = SequenceArrayUtils.copySliceUpToNNonGaps(bg, 3, 4);

		assert Arrays.equals(r, new byte[]{3, '-', 5, '-', '-', 8, 9});
		}

	/*	@Test
	 public void testCopySliceUpToNNonGapsReverse() throws NotEnoughSequenceException
		 {
		 byte[] r = SequenceArrayUtils.copySliceUpToNNonGapsReverse(bg, 10, 4);

		 assert Arrays.equals(r, new byte[]{3, '-', 5, '-', '-', 8, 9});
		 }
 */
	@Test
	public void testCopyNoGaps() throws NotEnoughSequenceException
		{
		byte[] r = SequenceArrayUtils.copyNoGaps(bg);

		assert Arrays.equals(r, new byte[]{0, 1, 2, 3, 5, 8, 9, 10, 11, 12});

		r = SequenceArrayUtils.copyNoGaps(gf);

		assert Arrays.equals(r, b);
		}

	byte[] g = new byte[]{'-', '-', '-', '-', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
	byte[] f = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, '-', '-', '-', '-'};
	byte[] gf = new byte[]{'-', '-', '-', '-', 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, '-', '-', '-', '-'};

	@Test
	public void testStartsWithGaps()
		{
		assert SequenceArrayUtils.startsWithGaps(g, 2);
		assert SequenceArrayUtils.startsWithGaps(g, 4);
		assert !SequenceArrayUtils.startsWithGaps(g, 5);
		assert !SequenceArrayUtils.startsWithGaps(g, 7);
		}

	@Test
	public void testEndsWithGaps()
		{
		assert SequenceArrayUtils.endsWithGaps(f, 2);
		assert SequenceArrayUtils.endsWithGaps(f, 4);
		assert !SequenceArrayUtils.endsWithGaps(f, 5);
		assert !SequenceArrayUtils.endsWithGaps(f, 7);
		}

	@Test
	public void testPadEndWithGaps()
		{
		byte[] r = SequenceArrayUtils.padEndWithGaps(g, 21);
		assert Arrays.equals(r, gf);
		}

	@Test
	public void testPadStartWithGaps()
		{
		byte[] r = SequenceArrayUtils.padStartWithGaps(f, 21);
		assert Arrays.equals(r, gf);
		}
	}
