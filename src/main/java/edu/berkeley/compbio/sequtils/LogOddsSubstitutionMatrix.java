/**
 * Created by IntelliJ IDEA.
 * User: lorax
 * Date: Apr 29, 2004
 * Time: 6:42:51 PM
 * To change this template use File | Settings | File Templates.
 */

package edu.berkeley.compbio.sequtils;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 * A substitution matrix with integer scores, such as a PAM or BLOSUM matrix.
 */
public class LogOddsSubstitutionMatrix
	{

	private static Logger logger = Logger.getLogger(LogOddsSubstitutionMatrix.class);

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
			//  logger.error(e);
			//  e.printStackTrace();
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

			logger.debug("input1 = " + input);

			while (input.ttype != StreamTokenizer.TT_EOL)
				{
				buffer.append(input.sval.charAt(0));

				input.nextToken();

				logger.debug("input2 = " + input);
				}
			residues = buffer.toString();
			numResidues = residues.length();

			logger.debug("residues = " + residues + "\nnumResidues = " + numResidues);

			// Create appropriately-sized matrix

			SubstitutionMatrix = new int[numResidues][numResidues];
			buffer.delete(0, numResidues);


			while (input.ttype == StreamTokenizer.TT_EOL)
				{
				input.nextToken();
				}

			logger.debug("input3 = " + input);

			// Read in substitution matrix values

			for (i = 0; i < numResidues; i++)
				{
				buffer.append(input.sval.charAt(0));

				logger.debug("input4 = " + input);

				input.nextToken();


				for (j = 0; j < numResidues; j++)
					{
					// scale everything down!  (only relative probs matter)  // nonsense
					SubstitutionMatrix[i][j] = (int) input.nval;// - 10;
					input.nextToken();

					logger.debug("input5 = " + input);
					}
				input.nextToken();
				}

			// Store names of residues for looking up values

			residues = buffer.toString();
			residues = residues.replace('*', '-');
			}
		catch (IOException e)
			{
			e.printStackTrace();
			logger.debug(e);
			}
		}


	public int score(char c, char d)
		{
		int x, y;
		//logger.debug("Substitution score : " + c + ", " + d);
		x = residues.indexOf(c);
		y = residues.indexOf(d);
		//logger.debug("Substitution score : " + c + ", " + d + " = " + SubstitutionMatrix[x][y]);
		return SubstitutionMatrix[x][y];
		}

	public int scoreAgainstGap(char c)
		{
		return score(c, '-');
		}
	}

