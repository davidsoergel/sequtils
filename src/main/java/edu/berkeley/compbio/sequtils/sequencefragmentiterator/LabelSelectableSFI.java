package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import edu.berkeley.compbio.sequtils.strings.SequenceFragment;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public interface LabelSelectableSFI extends LabelProvidingSFI
	{

	SequenceFragment nextRequiringLabel(String requestLabel);
	}
