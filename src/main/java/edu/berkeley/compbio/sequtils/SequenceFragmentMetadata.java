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


package edu.berkeley.compbio.sequtils;

import com.davidsoergel.dsutils.collections.HashWeightedSet;
import com.davidsoergel.dsutils.collections.WeightedSet;

/**
 * Provides a container for information about a particular sequence.  Sequence fragments are related hierarchically by
 * containment; a larger fragment containing this one is called its "parent".  For instance, a given FASTA file may
 * contain many different sequences.  In this case a SequenceFragmentMetadata representing the file as a whole is the
 * parent of the SequenceFragmentMetadatas representing each individual sequence.
 *
 * @author David Soergel
 * @version $Id$
 */
public class SequenceFragmentMetadata
	{
	// ------------------------------ FIELDS ------------------------------

	public static final int UNKNOWN_LENGTH = -1;

	// this is public so we can increment it directly (better performance?)
	public int length = 0;

	protected SequenceFragmentMetadata parentMetadata = null;

	protected String sequenceName = null;
	protected int startPosition = 0;
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
	                                int startPosition, int length)
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
	public int getLength()
		{
		return length;
		}

	/**
	 * Sets the length of this sequence
	 *
	 * @param length the length of this sequence
	 */
	public void setLength(int length)
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
	public String getSequenceName()
		{
		if (sequenceName == null)
			{
			if (parentMetadata != null)
				{
				// this will never change so we may as well store it
				sequenceName = parentMetadata.getSequenceName() + " (" + startPosition + ":" + length + ")";
				}
			else
				{
				sequenceName = "Sequence of Unknown Identity";
				}
			}

		return sequenceName;
		}

	/**
	 * Sets a String identifying this sequnce
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
	public int getStartPosition()
		{
		return startPosition;
		}

	/**
	 * Sets the start position of this sequence, a 0-based index with respect to the parent sequence.
	 *
	 * @param startPosition the start position of this sequence, a 0-based index with respect to the parent sequence.
	 */
	public void setStartPosition(int startPosition)
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
	public String getRootSequenceName()
		{
		if (parentMetadata != null)
			{
			return parentMetadata.getRootSequenceName();
			}
		return getSequenceName();
		}

	public String getLabel()
		{
		return getRootSequenceName();
		}

	private WeightedSet<String> weightedLabels = new HashWeightedSet<String>();

	public WeightedSet<String> getWeightedLabels()
		{
		return weightedLabels;
		}

	/**
	 * Tells whether this fragment overlaps with another.
	 *
	 * @param other
	 * @return true if the sequence fragments overlap, false otherwise.
	 * @throws SequenceException
	 */
	public boolean overlaps(SequenceFragmentMetadata other) throws SequenceException
		{
		try
			{
			if (!parentMetadata.equals(other.parentMetadata))// && other.parentMetadata != null (redundant)
				{
				return false;
				}
			}
		catch (NullPointerException e)
			{
			throw new SequenceException("Can't determine overlap without parent metadata");
			}

		// OK, both parents exist and are equal

		if (startPosition > other.startPosition)
			{
			if (other.length == UNKNOWN_LENGTH)
				{
				throw new SequenceException("Can't determine overlap for unknown length");
				}
			if (other.startPosition + other.length > startPosition)
				{
				return true;
				}
			return false;
			}

		else if (other.startPosition > startPosition)
			{
			if (length == UNKNOWN_LENGTH)
				{
				throw new SequenceException("Can't determine overlap for unknown length");
				}
			if (startPosition + length > other.startPosition)
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

	public boolean equals(Object o)
		{
		if (this == o)
			{
			return true;
			}
		if (o == null || getClass() != o.getClass())
			{
			return false;
			}

		SequenceFragmentMetadata that = (SequenceFragmentMetadata) o;

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
		}
	}
