package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import java.util.Set;

/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id$
 */
public interface LabelProvidingSFI extends SequenceFragmentIterator
	{
	//void runPhaseInit();

	Set<String> getMutuallyExclusiveLabels();

//	void close();

//	SequenceFragment next();

	//void rejectLastSample();

	//void supportRequiringLocalLabels();

	//void rejectLabel(String label);

	/**
	 * Implementations may override this in order to attempt to find SequenceFragments that have the requested label.  This
	 * is not guaranteed, though, and the default implementation just calls the regular next().
	 *
	 * @param label
	 * @return
	 */
//	public SequenceFragment nextPreferringLabel(String label)
//		{
//		return next();
//		}

	/**
	 * Implementations may override this in order to attempt to find SequenceFragments that have the requested label.  This
	 * is not guaranteed, though, and the default implementation just calls the regular next().
	 *
	 * @param label
	 * @return
	 */
/*	public SequenceFragment nextRequiringLabel(String label)
		{
		throw new NotImplementedException();
		}
*/
	/**
	 * Implementations may override this in order to attempt to find SequenceFragments that have the requested label.  This
	 * is not guaranteed, though, and the default implementation just calls the regular next().
	 *
	 * @param taxId
	 * @return
	 */
	// SequenceFragment nextAtApproximateDistance(Integer taxId, double minDistance, double maxDistance)
	//		throws NoSuchNodeException;
	/*	{
		throw new NotImplementedException();
		}
*/
/*	public void supportRequiringLabels(FragmentLabeller fragmentLabeller)
		{
		logger.warn("Requested label-requiring support on an SFI that doesn't provide it: " + this);
		}
*/

	/**
	 * Inform the iterator that we didn't like the SF it gave us for some reason.  Important for LabelBalancingSFI.
	 */
	// void rejectLastSample();
	/*	{
		// do nothing
		}
*/
/*	public FragmentLabeller getFragmentLabeller()
		{
		return fragmentLabeller;
		}*/

	// void rejectLastLabel();
/*		{
		// do nothing
		}
*/
	// void rejectLabel(String label);
/*		{
		// do nothing
		}
*/
	}
