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
