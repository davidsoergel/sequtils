/*
 * Copyright (c) 2001-2008 David Soergel
 * 418 Richmond St., El Cerrito, CA  94530
 * dev@davidsoergel.com
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the author nor the names of any contributors may
 *       be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.berkeley.compbio.sequtils.strings;

import com.davidsoergel.dsutils.math.MathUtils;
import com.davidsoergel.stats.DistributionException;
import com.davidsoergel.stats.Multinomial;
import org.apache.log4j.Logger;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: SimplexVectorTargetAndProportion.java 442 2009-06-15 17:53:56Z soergel $
 */

public class SimplexVectorTargetAndProportion<T>// implements SequenceSpectrum<RelativeMarkovTreeNode>
	{
// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(SimplexVectorTargetAndProportion.class);
	Multinomial<T> target = new Multinomial<T>();
	double mixingProportion;


// --------------------------- CONSTRUCTORS ---------------------------

	//	protected MarkovTreeNode backoffParent;

	/**
	 * Constructs a new RelativeMarkovTreeNode by comparing one MarkovTreeNode with another and representing the difference
	 * as a vector on the simplex.  A PSA can be constructed from these where each node is represented the difference
	 * between the multinomial at the node and its (backoff) prior expectation.
	 * <p/>
	 * //@param currentNode   a MarkovTreeNode containing a multinomial //@param backoffParent the MarkovTreeNode
	 * containing a multinomial to which the currentNode should be compared; //                    typically the backoff
	 * prior in our case.
	 */
	public SimplexVectorTargetAndProportion(final Multinomial<T> fromDist,
	                                        final Multinomial<T> toDist)//MarkovTreeNode currentNode, MarkovTreeNode backoffParent)
		{
		//	this.backoffParent = backoffParent;
		//	Multinomial<Byte> childProbs = currentNode.getProbs();
		//	Multinomial<Byte> parentProbs = backoffParent.getProbs();

		//byte zeroSymbol = 0;
		mixingProportion = 0;
		//double maxSymbol = 0;
		try
			{
			// see which symbol probability would hit zero first if we keep going in the same direction
			for (final T b : toDist.getElements())
				{
				final double alpha = 1 - (toDist.get(b) / fromDist.get(b));

				if (mixingProportion < alpha && alpha <= 1)
					{
					mixingProportion = alpha;
					//	zeroSymbol = b;
					//maxSymbol = b;
					}
				}

			// then find the target probabilities
			if (mixingProportion == 0)
				{
				// distributions are identical
				target = null;
				}
			else
				{
				for (final T b : toDist.getElements())
					{
					/*	if (b == zeroSymbol)
					   {

					   targetVal = 0;
					   }
				   else
					   {*/
					double targetVal =
							(1 / mixingProportion) * toDist.get(b) + (1 - (1 / mixingProportion)) * fromDist.get(b);
					//	}
					// avoid infinitesimal negative values due to numerical imprecision
					if (MathUtils.equalWithinFPError(targetVal, 0))
						{
						targetVal = 0;
						}
					target.put(b, targetVal);
					}
				if (!target.isAlreadyNormalized())
					{
					throw new DistributionException("Failed to compute conditional bias target distribution correctly");
					}
				}
			}
		catch (DistributionException e)
			{
			logger.error("Error", e);
			//	throw new Error(e);
			}
		}

// --------------------- GETTER / SETTER METHODS ---------------------

	public double getMixingProportion()
		{
		return mixingProportion;
		}

	public Multinomial<T> getTarget()
		{
		return target;
		}
	}
