package edu.berkeley.compbio.sequtils;

import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * @version 1.0
 */
//@PropertyConsumer
public class SequenceArrayUtils
	{
	private static final Logger logger = Logger.getLogger(SequenceArrayUtils.class);
	//public static final char GAP_CHAR = '-';
	public static final byte GAP_BYTE = (byte) '-';
	public static final byte UNKNOWN_BYTE = (byte) 'N';

	//@Property(helpmessage = "Characters representing gaps", defaultvalue = ".- ")
	public static final String gapChars = ".- ";


	public SequenceArrayUtils()
		{

		}


	public static void rotate(byte array[], int from, int to) throws SequenceArrayException
		{
		if (from == to)
			{
			return;
			}
		int j = 0;
		byte temp = array[from];


		try
			{
			if (from < to)
				{
				for (j = from; j < to; j++)
					{
					array[j] = array[j + 1];
					}
				}
			else
				{
				for (j = from; j > to; j--)
					{
					array[j] = array[j - 1];
					}
				}
			array[to] = temp;
			}
		catch (ArrayIndexOutOfBoundsException e)
			{
			logger.error("" + from + " " + to + " " + j + " " + Arrays.toString(array));
			logger.error("Error", e);
			throw new SequenceArrayException(e);
			}
		}

	private static ThreadLocal<byte[]> temp_tl = new ThreadLocal<byte[]>();// char[];

	/**
	 * Rotates an array slice in place within the larger array.  Moves elements from the "from" end to the "to" end; so if
	 * from < to, the overall rotation is to the left.
	 *
	 * @param array a char array
	 * @param from  the 0-based array index of the first column to move (inclusive)
	 * @param to    the 0-based array index of the last column to move (inclusive)
	 * @param size  the number of positions to rotate the slice.  Can be negative to indicate rotation to the right.
	 */
	public static void rotate(byte array[], int from, int to, int size) throws SequenceArrayException
		{
		if (from == to || size == 0)
			{
			return;
			}
		if (size < 0)
			{
			size = -size;
			int tmp = to;
			to = from;
			from = tmp;
			}
		int j = 0;
		byte[] temp = temp_tl.get();
		if (temp == null || temp.length < array.length)
			{
			logger.debug("Rotate: allocating new temp array of size " + array.length * 2);
			temp = (new byte[array.length * 2]);
			temp_tl.set(temp);
			}
		//logger.debug("Rotating: " + seq + " " + array[seq].length + " " + from + " " + to + " " + size);

		// from < to and to < from are of course the same operation (swapping two sections of sequence)
		// we just distinguish them for efficiency, so that we put the smaller of the two moving
		// regions in the temp array.  In our case, we assume that the "size" part is the smaller one.
		try
			{
			if (from < to)
				{
				for (j = 0; j < size; j++)
					{
					temp[j] = array[from + j];
					}
				for (j = from; j <= (to - size); j++)
					{
					//logger.debug("" + seq + " " + j + " " + size + " " + (j+size));
					array[j] = array[j + size];
					}
				for (j = 0; j < size; j++)
					{
					array[to + 1 - size + j] = temp[j];
					}
				}
			else
				{
				for (j = 0; j < size; j++)
					{
					temp[j] = array[from - size + 1 + j];
					}
				for (j = from; j >= (to + size); j--)
					{
					array[j] = array[j - size];
					}
				for (j = 0; j < size; j++)
					{
					array[to + j] = temp[j];
					}
				}
			}
		catch (ArrayIndexOutOfBoundsException e)
			{
			logger.error("" + from + " " + to + " " + j + " " + Arrays.toString(array));
			logger.error("Error", e);
			throw new SequenceArrayException(e);
			}
		}

	public static void rotateUpdatingGaps(int array[], int from, int to)
		{
		if (from == to)
			{
			return;
			}
		int j = 0;
		//int temp = array[seq][from];

		try
			{
			if (from < to)
				{
				for (j = from; j < to; j++)
					{
					array[j] = array[j + 1];
					}
				}
			else
				{
				for (j = from; j > to; j--)
					{
					array[j] = array[j - 1];
					}
				array[to] = (to == 0 ? 0 : array[to - 1]);
				}
			}
		catch (ArrayIndexOutOfBoundsException e)
			{
			logger.error("" + from + " " + to + " " + j);
			logger.error("Error", e);
			throw new SequenceArrayException(e);
			}
		}

	public static void rotateUpdatingGaps(int array[], int from, int to, int size)
		{
		if (from == to || size == 0)
			{
			return;
			}
		if (size < 0)
			{
			size = -size;
			int tmp = to;
			to = from;
			from = tmp;
			}
		int j = 0;

		try
			{
			if (from < to)
				{
				for (j = from; j <= (to - size); j++)
					{
					array[j] = array[j + size];
					}
				for (j = (to + 1 - size); j <= to; j++)
					{
					array[j] = array[to - size];
					}
				}
			else
				{
				for (j = from; j >= (to + size); j--)
					{
					array[j] = array[j - size];
					}
				int fillvalue = ((to == 0) ? 0 : array[to - 1]);
				for (j = to; j <= to + size - 1; j++)
					{
					array[j] = fillvalue;
					}
				}
			}
		catch (ArrayIndexOutOfBoundsException e)
			{
			logger.error("" + from + " " + to + " " + j);
			logger.error("Error", e);
			throw new SequenceArrayException(e);
			}
		}

	public static int columnToSeqIndex(byte[] x, int col)//, String gapChars)
		{
		int pos, count = -1;
		for (pos = 0; pos <= col; pos++)
			{
			//logger.debug("Testing column: " + col + " " + x[col] + " " + count);
			if (gapChars.indexOf(x[pos]) == -1)
				{
				count++;
				}
			}
		//if (x[col] == '-') return -count;
		return count;
		}

	/**
	 * Return the column (0-based) in which the n'th (0-based) non-gap character appears.
	 *
	 * @param x
	 * @param idx
	 */
	public static int seqIndexToColumn(byte[] x, int idx)//, String gapChars)
		{
		//if(idx == numChars(x)+1) { return x.length - 1; }

		if (idx < 0)
			{
			//logger.error("Sought column for negative index");
			//logger.error("Index = " + idx);
			return -1;
			}
		int col, count = -1;
		int lastNonGapCol = -1;
		for (col = 0; col < x.length; col++)
			{
			if (gapChars.indexOf(x[col]) == -1)// if this column is not a gap
				{
				count++;
				lastNonGapCol = col;
				}
			if (count == idx)
				{
				return col;
				}
			}

		// if the requested non-gap index is just beyond the end, return the next column after the last residue
		if (idx == count + 1)
			{
			return lastNonGapCol + 1;
			}

		//logger.debug("Could not find column: " + idx + " " + numChars(x) + " " + x.length + " " + col + " " + count);
		//logger.debug(new String(x));

		// the requested non-gap index exceeds the number of available characters by a lot; invalid
		logger.debug(
				"Requested non-gap index exceeds number of available characters by more than one: " + idx + " " + count
				+ " " + x.length);
		throw new SequenceArrayException("Requested sequence index out of range");
		}

	//private static String gapChars;
	//public static void setGapChars(String s) { gapChars = s; }

	public static int numGaps(byte[] x)//, String gapChars)
		{
		int count = 0;
		for (byte c : x)
			{
			//logger.debug("Testing column: " + col + " " + x[col] + " " + count);
			if (gapChars.indexOf(c) != -1)
				{
				count++;
				}
			}
		return count;
		}

	public static int numGaps(char[] x)//, String gapChars)
		{
		int count = 0;
		for (char c : x)
			{
			//logger.debug("Testing column: " + col + " " + x[col] + " " + count);
			if (gapChars.indexOf(c) != -1)
				{
				count++;
				}
			}
		return count;
		}

	public static int numNonGaps(byte[] x)//, String gapChars)
		{
		int count = 0;
		for (byte c : x)
			{
			//logger.debug("Testing column: " + col + " " + x[col] + " " + count);
			if (gapChars.indexOf(c) == -1)
				{
				count++;
				}
			}
		return count;
		}

	/*
	public static boolean isGap(char x)//, String gapChars)
		{
		return gapChars.indexOf(x) != -1;
		}
*/

	public static boolean isGap(byte x)//, String gapChars)
		{
		return gapChars.indexOf((char) x) != -1;
		}

	public static int numGapClusters(byte[] x)//, String gapChars)
		{
		try
			{
			int count = 0;
			for (int col = 0; col < x.length; col++)
				{
				//logger.debug("Testing column: " + col + " " + x[col] + " " + count);
				if (gapChars.indexOf(x[col]) != -1 && (col == 0 || gapChars.indexOf(x[col - 1]) == -1))
					{
					count++;
					}
				}
			return count;
			}
		catch (ArrayIndexOutOfBoundsException e)
			{
			logger.error("Error", e);
			throw new SequenceArrayException(e);
			}
		}

	public static int numChars(byte[] x)//, String gapChars)
		{
		return x.length - numGaps(x);
		}
/*
	public static int numChars(char[] x)//, String gapChars)
		{
		return x.length - numGaps(x);
		}
*/

	public static int startPositionOfGapCluster(byte[] x, int cluster)//, String gapChars)
			throws SequenceArrayException
		{
		int count = 0;
		for (int col = 0; col < x.length; col++)
			{
			//logger.debug("Testing column: " + col + " " + x[col] + " " + count);
			if (gapChars.indexOf(x[col]) != -1 && (col == 0 || gapChars.indexOf(x[col - 1]) == -1))
				{
				if (count == cluster)
					{
					return col;
					}
				count++;
				}
			}
		throw new SequenceArrayException(
				"Requested cluster " + cluster + " out of range; " + numGapClusters(x) + " available.");
		}

	/**
	 * Strip all gaps, left-justifying the sequences.  Updates both the character array and the index array.
	 */
	/*public static void stripGaps(char[][] x, int[][] idx)//, String gapChars)
		{
		for (int i = 0; i < x.length; i++)
			{
			for (int j = 0; j < x[i].length; j++)
				{
				int maxPossibleGaps = x[i].length - j;
				while (gapChars.indexOf(x[i][j]) != -1 && maxPossibleGaps > 0)
					{
					//logger.debug("Removing gap at " + i + ", " + j);
					rotate(x[i], j, x[i].length - 1);
					if (idx != null)
						{
						rotateUpdatingGaps(idx[i], j, idx[i].length - 1);
						}
					maxPossibleGaps--;
					}
				}
			}
		}*/
	public static void stripGaps(byte[][] x)//, String gapChars)
		{
		for (int i = 0; i < x.length; i++)
			{
			for (int j = 0; j < x[i].length; j++)
				{
				int maxPossibleGaps = x[i].length - j;
				while (gapChars.indexOf(x[i][j]) != -1 && maxPossibleGaps > 0)
					{
					//logger.debug("Removing gap at " + i + ", " + j);
					rotate(x[i], j, x[i].length - 1);

					maxPossibleGaps--;
					}
				}
			}
		}

	public static void replaceGaps(byte[] seq1)//, String gapChars)
		{
		for (int i = 0; i < seq1.length; i++)
			{
			if (isGap(seq1[i]))
				{
				seq1[i] = GAP_BYTE;
				}
			}
		}


	public static boolean isLeftJustified(byte[] seq)
		{
		byte last = 'A';// not a gap
		for (int i = 0; i < seq.length; i++)
			{
			byte c = seq[i];
			if (last == GAP_BYTE && c != GAP_BYTE)
				{
				return false;
				}
			last = c;
			}
		return true;
		}


	/**
	 * Tell whether position pos1 in sequence seq2 is aligned to pos2 in seq2 in the given char array.
	 *
	 * @param x
	 * @param seq1
	 * @param pos1
	 * @param seq2
	 * @param pos2
	 * @return true if the positions are aligned, false otherwise
	 */
	public static boolean isAligned(byte[][] x, int seq1, int pos1, int seq2, int pos2)
		{
		boolean result = (seqIndexToColumn(x[seq1], pos1) == seqIndexToColumn(x[seq2], pos2));
		if (isGap(x[seq1][pos1]) || isGap(x[seq2][pos2]))
			{
			result = false;
			}

		return result;
		}

	public static byte[] copySlice(final byte[] x, final int pos, final int width)
		{
		byte[] result = new byte[width];
		System.arraycopy(x, pos, result, 0, width);
		return result;
		}

	/**
	 * copy a slice starting at the given position of whatever width is needed to obtain the requested number of non-gap
	 * characters
	 *
	 * @param x
	 * @param pos
	 * @param nonGapsDesired
	 * @return
	 */
	public static byte[] copySliceUpToNNonGaps(final byte[] x, final int pos, final int nonGapsDesired)
			throws NotEnoughSequenceException
		{
		int trav = pos;
		int nonGapsFound = 0;

		while (trav < x.length)
			{

			if (!isGap(x[trav]))
				{
				nonGapsFound++;
				}

			trav++;

			if (nonGapsFound == nonGapsDesired)
				{
				break;
				}
			}

		// now trav points to the first character that we don't want
		// thus, we want the sequence starting at pos of length trav - pos

		if (nonGapsFound < nonGapsDesired)
			{
			throw new NotEnoughSequenceException("There are not enough non-gap characters after the given position");
			}

		return copySlice(x, pos, trav - pos);
		}

	/**
	 * copy a slice ending at the given position (exclusive!) of whatever width is needed to obtain the requested number of
	 * non-gap characters
	 *
	 * @param x
	 * @param pos
	 * @param nonGapsDesired
	 * @return
	 */
	public static byte[] copySliceUpToNNonGapsReverse(final byte[] x, final int pos, final int nonGapsDesired)
			throws NotEnoughSequenceException
		{
		int trav = pos - 1;
		int nonGapsFound = 0;

		while (trav >= 0)
			{

			if (!isGap(x[trav]))
				{
				nonGapsFound++;
				}


			if (nonGapsFound == nonGapsDesired)
				{
				break;
				}

			trav--;
			}

		// now trav points to the first character that we do want
		// thus, we want the sequence starting at trav of length pos - trav

		if (nonGapsFound < nonGapsDesired)
			{
			throw new NotEnoughSequenceException("There are not enough non-gap characters after the given position");
			}

		return copySlice(x, trav, pos - trav);
		}

	public static byte[] applyMask(boolean[] mask, byte[] x, byte maskByte)
		{
		final int len = x.length;
		assert mask.length == len;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			{
			result[i] = mask[i] ? x[i] : maskByte;
			}
		return result;
		}

	public static boolean startsWithGaps(final byte[] aFrag, final int startWindow)
		{
		for (int i = 0; i < startWindow; i++)
			{
			if (!isGap(aFrag[i]))
				{
				return false;
				}
			}
		return true;
		}

	public static boolean endsWithGaps(final byte[] aFrag, final int endWindow)
		{
		for (int i = 0; i < endWindow; i++)
			{
			if (!isGap(aFrag[aFrag.length - i]))
				{
				return false;
				}
			}
		return true;
		}

	public static byte[] padEndWithGaps(final byte[] aFrag, final int desiredLength)
		{
		if (aFrag.length >= desiredLength)
			{
			return aFrag;
			}
		byte[] result = new byte[desiredLength];
		System.arraycopy(aFrag, 0, result, 0, aFrag.length);
		Arrays.fill(result, aFrag.length, desiredLength, GAP_BYTE);
		return result;
		}

	public static byte[] padStartWithGaps(final byte[] aFrag, final int desiredLength)
		{
		if (aFrag.length >= desiredLength)
			{
			return aFrag;
			}
		byte[] result = new byte[desiredLength];
		int startPos = desiredLength - aFrag.length;
		System.arraycopy(aFrag, 0, result, startPos, aFrag.length);
		Arrays.fill(result, 0, startPos, GAP_BYTE);
		return result;
		}
	}
