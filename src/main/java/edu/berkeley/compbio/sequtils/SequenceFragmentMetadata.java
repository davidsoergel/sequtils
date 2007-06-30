package edu.berkeley.compbio.sequtils;

/**
 * Provides a container for information about a particular sequence.  Sequence fragments are related hierarchically by
 * containment; a largerfragment containing this one is called its "parent".  For instance, a given FASTA file bay
 * contain many different sequences.  In this case a SequenceFragmentMetadata representing the file as a whole is the
 * parent of the SequenceFragmentMetadatas representing each individual sequence.
 *
 * @author David Soergel
 */
public class SequenceFragmentMetadata
	{
	public static final int UNKNOWN_LENGTH = -1;

	protected SequenceFragmentMetadata parentMetadata = null;

	protected String sequenceName = null;
	protected int startPosition = 0;

	// this is public so we can increment it directly (better performance?)
	public int length = 0;

	/**
	 * Constructs a new SequenceFragmentMetadata by specifying its coordinates with respect to a containing parent
	 * sequence.
	 *
	 * @param parent        the SequenceFragmentMetadata representing a larger sequence in which this one is contained
	 * @param sequenceName  a String identifier for this sequence
	 * @param startPosition the index in the parent sequence of the first symbol in this sequence
	 * @param length        the length of this sequence
	 */
	public SequenceFragmentMetadata(SequenceFragmentMetadata parent, String sequenceName, int startPosition, int length)
		{
		this.parentMetadata = parent;
		this.sequenceName = sequenceName;
		this.startPosition = startPosition;
		this.length = length;
		}

	/**
	 * Constructs a new SequenceFragmentMetadata by specifying its start position with respect to a containing parent
	 * sequence, but with an unknown length.  (The length will presumably be determined and set later, as needed.)
	 *
	 * @param parent        the SequenceFragmentMetadata representing a larger sequence in which this one is contained
	 * @param sequenceName  a String identifier for this sequence
	 * @param startPosition the index in the parent sequence of the first symbol in this sequence
	 */
	public SequenceFragmentMetadata(SequenceFragmentMetadata parent, String sequenceName, int startPosition)
		{
		this(parent, sequenceName, startPosition, UNKNOWN_LENGTH);
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
	 * Returns a String identifying this sequnce
	 *
	 * @return a String identifying this sequnce
	 */
	public String getSequenceName()
		{
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
	public String toString()
		{
		return sequenceName + "(" + startPosition + ":" + length + ")";
		}

	/*
   public SequenceFragmentMetadata clone()
	   {
	   return new SequenceFragmentMetadata(parentMetadata, sequenceName, startPosition, length);
	   }*/
	}
