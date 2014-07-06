/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.LabellableImpl;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Provides a container for information about a particular sequence.  Sequence fragments are related hierarchically by
 * containment; a larger fragment containing this one is called its "parent".  For instance, a given FASTA file may
 * contain many different sequences.  In this case a SequenceFragmentMetadata representing the file as a whole is the
 * parent of the SequenceFragmentMetadatas representing each individual sequence.
 * <p/>
 * While a SequenceFragmentMetadata may consist of unconnected sequences (e.g., chromosomes), it assumes these are
 * contiguous, and in a consistent order, for the sake of sequence coordinates.
 *
 * @author David Soergel
 * @version $Id$
 */
public class SequenceFragmentMetadata<C extends SequenceFragmentMetadata> extends LabellableImpl<String>
		implements Comparable<C>, Serializable //, Clusterable<SequenceFragmentMetadata> //, WeightedLabelCarrier
	{
	// ------------------------------ FIELDS ------------------------------

	public static final long UNKNOWN_LENGTH = Long.MAX_VALUE;

	// this is public so we can increment it directly (better performance?)  // no more

	/**
	 * The total number of characters represented by this fragment, including "N" characters or whatever.  May have the
	 * value UNKNOWN_LENGTH if the fragment has not yet been scanned.
	 */
	protected long length = 0;

	protected SequenceFragmentMetadata parentMetadata = null;

	protected String sequenceName = null;
	protected long startPosition = 0;
	private Integer taxid;

	// --------------------------- CONSTRUCTORS ---------------------------

	/**
	 * Constructs a new SequenceFragmentMetadata by specifying its start position with respect to a containing parent
	 * sequence, but with an unknown length.  (The length will presumably be determined and set later, as needed.)
	 *
	 * @param parent        the SequenceFragmentMetadata representing a larger sequence in which this one is contained
	 * @param sequenceName  a String identifier for this sequence
	 * @param startPosition the index in the parent sequence of the first symbol in this sequence
	 */
	public SequenceFragmentMetadata(SequenceFragmentMetadata parent, String sequenceName, Integer taxid,
	                                int startPosition)
		{
		this(parent, sequenceName, taxid, startPosition, UNKNOWN_LENGTH);
		}

	/**
	 * Constructs a new SequenceFragmentMetadata by specifying its coordinates with respect to a containing parent
	 * sequence.
	 *
	 * @param parent        the SequenceFragmentMetadata representing a larger sequence in which this one is contained
	 * @param sequenceName  a String identifier for this sequence
	 * @param startPosition the index in the parent sequence of the first symbol in this sequence
	 * @param length        the length of this sequence
	 */
	public SequenceFragmentMetadata(SequenceFragmentMetadata parent, String sequenceName, Integer taxid,
	                                long startPosition, long length)
		{
		this.parentMetadata = parent;
		this.sequenceName = sequenceName;
		this.taxid = taxid;
		this.startPosition = startPosition;
		this.length = length;
		}

	// --------------------- GETTER / SETTER METHODS ---------------------

	/**
	 * Returns the length of this sequence
	 *
	 * @return the length of this sequence
	 */
	public long getLength()
		{
		return length;
		}

	/**
	 * Sets the length of this sequence
	 *
	 * @param length the length of this sequence
	 */
	public synchronized void setLength(int length)
		{
		this.length = length;
		}

	/**
	 * Returns the SequenceFragmentMetadata representing the parent sequence, a longer sequence that contains this one.
	 *
	 * @return the SequenceFragmentMetadata representing the parent sequence, a longer sequence that contains this one.
	 */
	public SequenceFragmentMetadata getParentMetadata()
		{
		return parentMetadata;
		}

	/**
	 * Sets the SequenceFragmentMetadata representing the parent sequence, a longer sequence that contains this one.
	 *
	 * @param parentMetadata a SequenceFragmentMetadata representing the parent sequence, a longer sequence that contains
	 *                       this one.
	 */
	public void setParentMetadata(SequenceFragmentMetadata parentMetadata)
		{
		this.parentMetadata = parentMetadata;
		}

	/**
	 * Returns a String identifying this sequence.  If this sequence does not have its own unique name, give the name of
	 * the parent sequence with the start position and length.
	 *
	 * @return a String identifying this sequence
	 */
	@NotNull
	public String getSequenceName()
		{
		if (sequenceName == null)
			{
			if (parentMetadata != null)
				{
				// this will never change so we may as well store it

				// if this fragment includes the full length of the parent, don't mess with the name
				if (startPosition == 0 && length == parentMetadata.getLength())
					{
					sequenceName = parentMetadata.getSequenceName();
					}
				else
					{
					sequenceName = parentMetadata.getSequenceName() + " (" + startPosition + ":" + length + ")";
					}
				}
			else
				{
				sequenceName = "Sequence of Unknown Identity";
				}
			}

		return sequenceName;
		}

	/**
	 * Sets a String identifying this sequence
	 *
	 * @param sequenceName a String identifying this sequnce
	 */
	public void setSequenceName(String sequenceName)
		{
		this.sequenceName = sequenceName;
		}

	/**
	 * Returns the start position of this sequence, a 0-based index with respect to the parent sequence.
	 *
	 * @return the start position of this sequence, a 0-based index with respect to the parent sequence.
	 */
	public long getStartPosition()
		{
		return startPosition;
		}

	public long getStartPositionFromRoot()
		{
		if (parentMetadata != null)
			{
			return startPosition + parentMetadata.getStartPositionFromRoot();
			}
		return startPosition;
		}

	/**
	 * Sets the start position of this sequence, a 0-based index with respect to the parent sequence.
	 *
	 * @param startPosition the start position of this sequence, a 0-based index with respect to the parent sequence.
	 */
	public void setStartPosition(long startPosition)
		{
		this.startPosition = startPosition;
		}

	/*
   public SequenceFragmentMetadata clone()
	   {
	   return new SequenceFragmentMetadata(parentMetadata, sequenceName, startPosition, length);
	   }*/

	/**
	 * Returns the NCBI taxid of the taxon to which this sequence belongs
	 *
	 * @return
	 */
	public Integer getTaxid()
		{
		return taxid;
		}

	// ------------------------ CANONICAL METHODS ------------------------

	// use direct field access instead
	/*	public void incrementLength()
		 {
		 length++;
		 }
 */

	/**
	 * Clears this Metadata to allow re-use
	 */
	/*public void clear()
		{
		//correctSpecies = null;
		//filename = null;
		sequenceName = null;
		startPosition = 0;
		length = 0;
		}*/
	/**
	 *
	 */
	@Override
	public String toString()
		{
		return getSequenceName();
		}

	// -------------------------- OTHER METHODS --------------------------

	/**
	 * Returns a String identifying the largest sequence to which this fragment belongs (i.e., the root af the parent
	 * hierarchy)
	 *
	 * @return a String identifying the largest sequence to which this fragment belongs
	 */
	@NotNull
	public String getRootSequenceName()
		{
		return getRootMetadata().getSequenceName();
		}

	/**
	 * Returns a String identifying the largest sequence to which this fragment belongs (i.e., the root af the parent
	 * hierarchy)
	 *
	 * @return a String identifying the largest sequence to which this fragment belongs
	 */
	@NotNull
	public SequenceFragmentMetadata getRootMetadata()
		{
		if (parentMetadata != null)
			{
			return parentMetadata.getRootMetadata();
			}
		return this;
		}

	/*	public String getExclusiveLabel()
		 {
		 return getRootSequenceName();
		 }
 */


	/**
	 * Tells whether this fragment overlaps with another.
	 *
	 * @param other
	 * @return true if the sequence fragments overlap, false otherwise.
	 * @throws SequenceException
	 */
	public boolean overlaps(@NotNull SequenceFragmentMetadata other) throws SequenceException
		{
		try
			{
			if (!getRootMetadata().equalValue(other.getRootMetadata()))// && other.parentMetadata != null (redundant)
				{
				return false;
				}
			}
		catch (NullPointerException e)
			{
			throw new SequenceException("Can't determine overlap without parent metadata");
			}

		// OK, both parents exist and are equal
		long thisStart = getStartPositionFromRoot();
		long otherStart = other.getStartPositionFromRoot();

		if (thisStart > otherStart)
			{
			if (other.getLength() == UNKNOWN_LENGTH)
				{
				throw new SequenceException("Can't determine overlap for unknown length");
				}
			if (otherStart + other.length > startPosition)
				{
				return true;
				}
			return false;
			}

		else if (otherStart > thisStart)
			{
			if (getLength() == UNKNOWN_LENGTH)
				{
				throw new SequenceException("Can't determine overlap for unknown length");
				}
			if (thisStart + length > otherStart)
				{
				return true;
				}
			return false;
			}

		else
			// startPosition == other.startPosition
			{
			return true;
			}
		}

	/**
	 * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive integer
	 * as this object is less than, equal to, or greater than the specified object.
	 * <p/>
	 * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) == -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and
	 * <tt>y</tt>.  (This implies that <tt>x.compareTo(y)</tt> must throw an exception iff <tt>y.compareTo(x)</tt> throws
	 * an exception.)
	 * <p/>
	 * <p>The implementor must also ensure that the relation is transitive: <tt>(x.compareTo(y)&gt;0 &amp;&amp;
	 * y.compareTo(z)&gt;0)</tt> implies <tt>x.compareTo(z)&gt;0</tt>.
	 * <p/>
	 * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt> implies that <tt>sgn(x.compareTo(z)) ==
	 * sgn(y.compareTo(z))</tt>, for all <tt>z</tt>.
	 * <p/>
	 * <p>It is strongly recommended, but <i>not</i> strictly required that <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.
	 * Generally speaking, any class that implements the <tt>Comparable</tt> interface and violates this condition should
	 * clearly indicate this fact.  The recommended language is "Note: this class has a natural ordering that is
	 * inconsistent with equals."
	 * <p/>
	 * <p>In the foregoing description, the notation <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
	 * <i>signum</i> function, which is defined to return one of <tt>-1</tt>, <tt>0</tt>, or <tt>1</tt> according to
	 * whether the value of <i>expression</i> is negative, zero or positive.
	 *
	 * @param o the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the
	 *         specified object.
	 * @throws ClassCastException if the specified object's type prevents it from being compared to this object.
	 */
	public int compareTo(C o)
		{
		SequenceFragmentMetadata osfm = ((SequenceFragmentMetadata) o);
		int result = getRootSequenceName().compareTo(osfm.getRootSequenceName());
		if (result == 0)
			{
			long diff = getStartPositionFromRoot() - osfm.getStartPositionFromRoot();
			result = diff < 0 ? -1 : diff > 0 ? 1 : 0;
			}
		return result;
		}

	public boolean equalValue(SequenceFragmentMetadata that)// o)
		{
		if (this == that)
			{
			return true;
			}
		if (that == null || getClass() != that.getClass())
			{
			return false;
			}

		//SequenceFragmentMetadata that = (SequenceFragmentMetadata) o;

		if (length != that.length)
			{
			return false;
			}
		if (startPosition != that.startPosition)
			{
			return false;
			}
		if (parentMetadata != null ? !parentMetadata.equals(that.parentMetadata) : that.parentMetadata != null)
			{
			return false;
			}
		if (sequenceName != null ? !sequenceName.equals(that.sequenceName) : that.sequenceName != null)
			{
			return false;
			}
		if (taxid != null ? !taxid.equals(that.taxid) : that.taxid != null)
			{
			return false;
			}

		return true;
		}

	// can't use hashCode with non-final variables.  Therefore, don't implement equals() either; just add a separate method for content equality

	/*
	public int hashCode()
		{
		int result;
		result = length;
		result = 31 * result + (parentMetadata != null ? parentMetadata.hashCode() : 0);
		result = 31 * result + (sequenceName != null ? sequenceName.hashCode() : 0);
		result = 31 * result + startPosition;
		result = 31 * result + (taxid != null ? taxid.hashCode() : 0);
		result = 31 * result + (weightedLabels != null ? weightedLabels.hashCode() : 0);
		return result;
		}*/
	}
