/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package edu.berkeley.compbio.sequtils.strings;

import java.util.List;


/**
 * Interface for objects that are able to provide a list of the "first words" associated with a sequence spectrum.  The
 * purpose of this is that we may compute a chain of spectra, e.g., computing conditional k-mer probabilities from
 * absolute probabilities, which are in turn based on counts.  A spectrum based on conditional probabilities does not
 * fully describe the distribution unless the starting points are also specified.  A spectrum may be based on multiple
 * independent sequences (either becaue there are multiple inputs, or because there are unknown symbols in the
 * sequence).  Thus, a complete specification of the spectrum requires a list containing the initial word of each
 * segment.  This list can be propagated around, or not, depending on whether it is relevant, independent of the rest of
 * the model (e.g., the conditional probabilities).
 *
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: FirstWordProvider.java 406 2009-05-13 20:53:36Z soergel $
 */

public interface FirstWordProvider
	{
// -------------------------- OTHER METHODS --------------------------

	/**
	 * Returns a list of all the initial words of length k that are associated with this object.  A single sequence has
	 * only one initial word, of course, but this object may represent a set of sequences, or a sequence with internal
	 * interruptions such as unknown symbols, and so may contain a number of initial words.
	 *
	 * @param k the width of the words to provide.
	 * @return the List<byte[]>
	 */
	List<byte[]> getFirstWords(int k);
	}
