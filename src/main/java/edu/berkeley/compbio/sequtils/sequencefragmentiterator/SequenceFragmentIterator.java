package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import com.davidsoergel.trees.dhtpn.SerializableDoubleHierarchicalTypedProperties;
import edu.berkeley.compbio.ml.cluster.ClusterableIterator;
import edu.berkeley.compbio.sequtils.strings.SequenceFragment;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public interface SequenceFragmentIterator extends ClusterableIterator<SequenceFragment>
	{
	/*	@Override
	 protected void finalize() throws Throwable
		 {
		 close();
		 super.finalize();
		 }
 */

	/**
	 * Closes the SequenceFragmentIterator and all sub-readers
	 */
	void close();

	void remove();

	/**
	 * provides the next item from the iterator without actually consuming it
	 *
	 * @return
	 */
	//public abstract SequenceFragment peek();


	// -------------------------- OTHER METHODS --------------------------
	int estimatedTotalSamples();

	/**
	 * Returns the total number of nucleotides read
	 *
	 * @return The total number of nucleotides read
	 */
	long getCharactersRead();

	/**
	 * Returns the total amount of sequence available
	 *
	 * @return The total amount of sequence
	 */
	long getTotalSequence();

	//	public abstract void releaseCachedResources();

	void setResults(SerializableDoubleHierarchicalTypedProperties<?> results);

	void runPhaseInit();

	//long getAverageGenomeSize();

//	void setGenomeCount(int genomeCount);

//	int getGenomeCount();
	}
