package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.collections.OrderedPair;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public class DynamicProgrammingPairwiseAlignerTest
	{
	private static final Logger logger = Logger.getLogger(DynamicProgrammingPairwiseAlignerTest.class);

	@Test
	public void testNeedlemanWunsch()
		{
		DynamicProgrammingPairwiseAligner nw = DynamicProgrammingPairwiseAligner.getNeedlemanWunsch();
		//	DynamicProgrammingPairwiseAligner nw = new DynamicProgrammingPairwiseAligner(new SimpleSubstitutionMatrix(1, 0, 0, 0),
		//	                                                                             DynamicProgrammingPairwiseAligner.TracebackBegin.CORNER, DynamicProgrammingPairwiseAligner.TracebackEnd.CORNER);
		OrderedPair<byte[], byte[]> result = nw.align("GAATTCAGTTA".getBytes(), "GGATCGA".getBytes());
		final String a = new String(result.getKey1());
		logger.warn(a);
		final String b = new String(result.getKey2());
		logger.warn(b);
		//assert a.equals("")
		}
	}
