package edu.berkeley.compbio.sequtils;

import com.davidsoergel.stats.DistributionException;
import com.davidsoergel.stats.MultinomialDistribution;
import org.apache.log4j.Logger;

/**
 * A matrix of state-to-state probabilities, in probability units.
 *
 * @author lorax
 * @version 1.0
 * @see LogOddsSubstitutionMatrix
 */
public class TransitionMatrix
	{
	private static Logger logger = Logger.getLogger(TransitionMatrix.class);

	private int states;
	private MultinomialDistribution[] transitions;

	public TransitionMatrix(double[][] probs) throws DistributionException

		{
		states = probs.length;
		transitions = new MultinomialDistribution[states];
		for (int i = 0; i < states; i++)
			{
			if (probs[i].length != states)
				{
				throw new DistributionException("Transition matrix must be square");
				}
			transitions[i] = new MultinomialDistribution(probs[i]);
			}
		}

	public int sampleTransition(int fromState) throws DistributionException
		{
		return transitions[fromState].sample();
		}
	}
