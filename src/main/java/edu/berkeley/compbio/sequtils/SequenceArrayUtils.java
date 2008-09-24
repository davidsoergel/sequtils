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
	public static final char GAP_CHAR = '-';

	//@Property(helpmessage = "Characters representing gaps", defaultvalue = ".- ")
	public static final String gapChars = ".- ";


	public SequenceArrayUtils()
		{

		}

	public static void rotate(char array[], int from, int to) throws SequenceArrayException
		{
		if (from == to)
			{
			return;
			}
		int j = 0;
		char temp = array[from];


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
			e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
			logger.error("" + from + " " + to + " " + j + " " + Arrays.toString(array));
			logger.error(e);
			throw new SequenceArrayException(e);
			}
		}

	private static ThreadLocal<char[]> temp_tl = new ThreadLocal<char[]>();// char[];

	/**
	 * Rotates an array slice in place within the larger array.  Moves elements from the "from" end to the "to" end; so if
	 * from < to, the overall rotation is to the left.
	 *
	 * @param array a char array
	 * @param from  the 0-based array index of the first column to move (inclusive)
	 * @param to    the 0-based array index of the last column to move (inclusive)
	 * @param size  the number of positions to rotate the slice.  Can be negative to indicate rotation to the right.
	 */
	public static void rotate(char array[], int from, int to, int size) throws SequenceArrayException
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
		char[] temp = temp_tl.get();
		if (temp == null || temp.length < array.length)
			{
			logger.info("Rotate: allocating new temp array of size " + array.length * 2);
			temp = (new char[array.length * 2]);
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
			e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
			logger.error("" + from + " " + to + " " + j + " " + Arrays.toString(array));
			logger.error(e);
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
			e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
			logger.error("" + from + " " + to + " " + j);
			logger.error(e);
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
			e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
			logger.error("" + from + " " + to + " " + j);
			logger.error(e);
			throw new SequenceArrayException(e);
			}
		}

	public static int columnToSeqIndex(char[] x, int col)//, String gapChars)
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
	public static int seqIndexToColumn(char[] x, int idx)//, String gapChars)
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
		logger.debug("Requested non-gap index exceeds number of available characters by more than one: " + idx + " "
				+ count + " " + x.length);
		throw new SequenceArrayException("Requested sequence index out of range");
		}

	//private static String gapChars;
	//public static void setGapChars(String s) { gapChars = s; }

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

	public static boolean isGap(char x)//, String gapChars)
		{
		return gapChars.indexOf(x) != -1;
		}

	public static int numGapClusters(char[] x)//, String gapChars)
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
			e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
			logger.error(e);
			throw new SequenceArrayException(e);
			}
		}

	public static int numChars(char[] x)//, String gapChars)
		{
		return x.length - numGaps(x);
		}

	public static int startPositionOfGapCluster(char[] x, int cluster)//, String gapChars)
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
	public static void stripGaps(char[][] x)//, String gapChars)
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

	public static void replaceGaps(char[] seq1)//, String gapChars)
		{
		for (int i = 0; i < seq1.length; i++)
			{
			if (isGap(seq1[i]))
				{
				seq1[i] = GAP_CHAR;
				}
			}
		}


	public static boolean isLeftJustified(char[] seq)
		{
		char last = 'A';// not a gap
		for (int i = 0; i < seq.length; i++)
			{
			char c = seq[i];
			if (last == GAP_CHAR && c != GAP_CHAR)
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
	public static boolean isAligned(char[][] x, int seq1, int pos1, int seq2, int pos2)
		{
		boolean result = (seqIndexToColumn(x[seq1], pos1) == seqIndexToColumn(x[seq2], pos2));
		if (isGap(x[seq1][pos1]) || isGap(x[seq2][pos2]))
			{
			result = false;
			}

		return result;
		}
	}
