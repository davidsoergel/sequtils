package edu.berkeley.compbio.sequtils;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: soergel
 * Date: Sep 12, 2006
 * Time: 6:23:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class FilteringSequenceReader implements SequenceReader
	{
	private SequenceReader base;
	private NucleotideFilter filter;

	public FilteringSequenceReader(SequenceReader base, NucleotideFilter filter)
		{
		this.base = base;
		this.filter = filter;
		}

	/**
	 * Returns the total amount of sequence present in this reader
	 *
	 * @return The total amount of sequence
	 */
	public int getTotalSequence()
		{
		return base.getTotalSequence();
		}

	/**
	 * Returns the species name of this reader4
	 *
	 * @return A String representing the name of the sequence
	 */
	public String getName()
		{
		return base.getName();
		}

	/**
	 * Closes the reader, and all sub-readers
	 */
	public void close()
		{
		base.close();
		}

	public byte read() throws IOException, FilterException, NotEnoughSequenceException
		{
		return filter.filter(base.read());
		}
	}
