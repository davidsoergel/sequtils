package edu.berkeley.compbio.sequtils;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * A substitution matrix with integer scores (in log probability units), such as a PAM or BLOSUM matrix.
 *
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: LogOddsSubstitutionMatrix.java 317 2009-07-19 22:39:31Z soergel $
 */
public class LogOddsSubstitutionMatrix
	{

	private static final Logger logger = Logger.getLogger(LogOddsSubstitutionMatrix.class);

	int[][] SubstitutionMatrix;
	String residues;

	public String filename;

	public void init() throws FileNotFoundException
		{
		Reader r;
		try
			{
			r = new InputStreamReader(getClass().getResourceAsStream(filename));
			}
		catch (Exception e)
			{
			//  logger.error("Error", e);
			r = new FileReader(filename);
			}
		readFromReader(r);
		}

	/*	public SubstitutionMatrix(Reader matrix)
		 {
		 readFromReader(matrix);
		 }
 */
	private void readFromReader(Reader matrix)
		{
		StreamTokenizer input;
		StringBuffer buffer = new StringBuffer();
		int i, j, numResidues;
		input = new StreamTokenizer(matrix);
		try
			{
			// Read in residue names

			input.commentChar('#');
			input.wordChars('*', '*');
			input.eolIsSignificant(true);
			input.nextToken();

			while (input.ttype == StreamTokenizer.TT_EOL)
				{
				input.nextToken();
				}

			logger.trace("input1 = " + input);

			while (input.ttype != StreamTokenizer.TT_EOL)
				{
				buffer.append(input.sval.charAt(0));

				input.nextToken();

				logger.trace("input2 = " + input);
				}
			residues = buffer.toString();
			numResidues = residues.length();

			logger.trace("residues = " + residues + "\nnumResidues = " + numResidues);

			// Create appropriately-sized matrix

			SubstitutionMatrix = new int[numResidues][numResidues];
			buffer.delete(0, numResidues);


			while (input.ttype == StreamTokenizer.TT_EOL)
				{
				input.nextToken();
				}

			logger.trace("input3 = " + input);

			// Read in substitution matrix values

			for (i = 0; i < numResidues; i++)
				{
				buffer.append(input.sval.charAt(0));

				logger.trace("input4 = " + input);

				input.nextToken();


				for (j = 0; j < numResidues; j++)
					{
					// scale everything down!  (only relative probs matter)  // nonsense
					SubstitutionMatrix[i][j] = (int) input.nval;// - 10;
					input.nextToken();

					logger.trace("input5 = " + input);
					}
				input.nextToken();
				}

			// Store names of residues for looking up values

			residues = buffer.toString();
			residues = residues.replace('*', '-');
			}
		catch (IOException e)
			{
			logger.error("Error", e);
			}
		}


	public int score(byte c, byte d)
		{
		int x, y;
		//logger.debug("Substitution score : " + c + ", " + d);
		x = residues.indexOf(c);
		y = residues.indexOf(d);
		//logger.debug("Substitution score : " + c + ", " + d + " = " + SubstitutionMatrix[x][y]);
		return SubstitutionMatrix[x][y];
		}

	public int scoreAgainstGap(byte c)
		{
		return score(c, (byte) '-');
		}
	}

