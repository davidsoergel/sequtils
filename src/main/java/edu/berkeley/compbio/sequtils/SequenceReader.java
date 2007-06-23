package edu.berkeley.compbio.sequtils;


import java.io.IOException;

/**
 * Provides an interface for a buffered sequence reader, to return nucleotides and process parsers or mutation transparently.
 *
 * @author David Tulga
 */
public interface SequenceReader
	{
	/**
	 * Returns the next nucleotide
	 *
	 * @return The next nucleotide, or EOF if the section or file has ended
	 */
	public byte read()
			throws IOException, FilterException, NotEnoughSequenceException;// Read one character from the buffer

	/**
	 * Seek to a particular position
	 */
	//public void seek(SectionFragmentMetadata section, int sectionPosition)
	//		throws IOException; // Seek to a particular position

	/**
	 * Seek to a particular position
	 */
	//	public void seek(SectionFragmentMetadata section)
	//			throws IOException; // Seek to a particular position

	/**
	 * Returns the total amount of sequence present in this reader
	 *
	 * @return The total amount of sequence
	 */
	public int getTotalSequence();

	/**
	 * Checks to make sure the seek position is in sequence, not a header
	 *
	 * @throws edu.berkeley.compbio.msensr.NotEnoughSequenceException
	 *          This exception is thrown when the current seek position isn't valid, such as in a header.
	 */
	//public void checkCurrentPositionIsValidSequence() throws NotEnoughSequenceException;

	/**
	 * Traverses to the next section and returns metadata corresponding to it
	 *
	 * @return Metadata about the next section
	 */
	//public SectionFragmentMetadata nextSection() throws IOException;

	/**
	 * Returns the species name of this reader
	 *
	 * @return A String representing the name of the sequence
	 */
	public String getName();

	/**
	 * Seeks to the beginning of the current section
	 *
	 * @return The new position
	 */
	//	public int toSectionStart(SectionFragmentMetadata section) throws IOException;

	/**
	 * Closes the reader, and all sub-readers
	 */
	public void close();
	}
