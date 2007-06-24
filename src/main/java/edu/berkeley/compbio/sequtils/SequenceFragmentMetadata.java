package edu.berkeley.compbio.sequtils;

/**
 * Provides a container for information about a particular sequence.
 *
 * @author David Soergel
 */
public class SequenceFragmentMetadata
	{
	//private static Logger logger = Logger.getLogger(SequenceFragmentMetadata.class);

	//private randomKcountProvider correctSpecies;
	//private String filename;
	//private File sourceFile;
	public static final int UNKNOWN_LENGTH = -1;

	protected SequenceFragmentMetadata parentMetadata = null;

	protected String sequenceName = null;
	protected int startPosition = 0;

	// this is public so we can increment it directly (better performance?)
	public int length = 0;

	public SequenceFragmentMetadata(SequenceFragmentMetadata parent, String sequenceName, int startPosition, int length)
		{
		this.parentMetadata = parent;
		this.sequenceName = sequenceName;
		this.startPosition = startPosition;
		this.length = length;
		}

	public SequenceFragmentMetadata(SequenceFragmentMetadata parent, String sequenceName, int startPosition)
		{
		this.parentMetadata = parent;
		this.sequenceName = sequenceName;
		this.startPosition = startPosition;
		this.length = UNKNOWN_LENGTH;
		}

	/*	public SequenceFragmentMetadata(String sequenceName)
		 {
		 this(sequenceName, 0, 0);
		 }

	 public SequenceFragmentMetadata()
		 {
		 this(null, 0, 0);
		 }
 */
	/*	public randomKcountProvider getCorrectSpecies()
	   {
	   return correctSpecies;
	   }

   public void setCorrectSpecies(randomKcountProvider correctSpecies)
	   {
	   this.correctSpecies = correctSpecies;
	   }

   public String getFilename()
	   {
	   return filename;
	   }

   public void setFilename(String filename)
	   {
	   this.filename = filename;
	   }*/

	public SequenceFragmentMetadata getParentMetadata()
		{
		return parentMetadata;
		}

	public void setParentMetadata(SequenceFragmentMetadata parentMetadata)
		{
		this.parentMetadata = parentMetadata;
		}

	public String getSequenceName()
		{
		return sequenceName;
		}

	public void setSequenceName(String sequenceName)
		{
		this.sequenceName = sequenceName;
		}

	public int getStartPosition()
		{
		return startPosition;
		}

	public void setStartPosition(int startPosition)
		{
		this.startPosition = startPosition;
		}

	public int getLength()
		{
		return length;
		}

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
	public void clear()// allow pooling
		{
		//correctSpecies = null;
		//filename = null;
		sequenceName = null;
		startPosition = 0;
		length = 0;
		}

	public String toString()
		{
		return sequenceName + "(" + startPosition + ":" + length + ")";
		}

	public SequenceFragmentMetadata clone()
		{
		return new SequenceFragmentMetadata(parentMetadata, sequenceName, startPosition, length);
		}
	}
