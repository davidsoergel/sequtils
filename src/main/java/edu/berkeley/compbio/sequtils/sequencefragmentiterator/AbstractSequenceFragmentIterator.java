/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import com.davidsoergel.trees.dhtpn.SerializableDoubleHierarchicalTypedProperties;
import edu.berkeley.compbio.sequtils.strings.SequenceFragment;
import org.apache.log4j.Logger;

/**
 * Provides an interface for an Iterator of Kcounts
 *
 * @author David Tulga
 * @author David Soergel
 * @version $Id: AbstractSequenceFragmentIterator.java 1290 2009-11-19 09:48:23Z soergel $
 */
public abstract class AbstractSequenceFragmentIterator
		implements SequenceFragmentIterator// extends Kcount> //<? implements Kcount>>
	{
	private static final Logger logger = Logger.getLogger(AbstractSequenceFragmentIterator.class);

	/*
	@Property(helpmessage = "A provider of classification labels to use",
	          defaultvalue = "", isNullable = true)
	//public LabelChooser<String> labelChooser;
	public FragmentLabeller fragmentLabeller;
	*/

//	@Property(inherited = true)
//	public FragmentLabeller leaveOneOutFragmentLabeller;

	private int genomeCount;

	public AbstractSequenceFragmentIterator()
		{
		}

/*	public Iterator<SequenceFragment> iterator()
		{
		return this;
		}*/

	// ------------------------ CANONICAL METHODS ------------------------

	//private static final Logger logger = Logger.getLogger(SequenceFragmentIterator.class);
	/*
	 protected SequenceFragmentIterator(String injectorId)
		 {
		 ResultsCollectingProgramRun.getProps().injectProperties(injectorId, this);
		 }
 */
	// ------------------------ CANONICAL METHODS ------------------------

	/**
	 * {@inheritDoc}
	 */


	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface Iterator ---------------------

	/**
	 * Unsupported, as all iteration is online
	 */
	public void remove()
		{
		throw new UnsupportedOperationException();
		}

	//public int compareTo(SequenceFragmentIterator b)
	//		{
	//		// alpha sort
	//		return toString().compareTo(b.toString());
	//		}


	/**
	 * Frees up memory, filehandles, etc. from caches that can be re-filled if necessary
	 */
	//	public abstract void releaseCachedResources();
	public void setResults(SerializableDoubleHierarchicalTypedProperties<?> results)
		{
		}

	public void runPhaseInit()
		{
		}


/*	public long getAverageGenomeSize()
		{
		return getTotalSequence() / genomeCount;
		}

	public void setGenomeCount(int genomeCount)
		{
		this.genomeCount = genomeCount;
		}

	public int getGenomeCount()
		{
		return genomeCount;
		}
*/

/*
  protected ThreadPoolExecutor executor = null;

  public void setSubtaskThreadPool(final ThreadPoolExecutor ex)
	  {
	  this.executor = ex;
	  }*/

	/*public Set<SequenceFragment> getComprehensiveExampleSet()
		{
		logger.warn("Requested comprehensive example set on an SFI that doesn't provide it: " + this);
		return null;
		}*/

	public SequenceFragment nextFullyLabelled()
		{
		SequenceFragment s = next();
		s.doneLabelling();
		return s;
		}
	}
