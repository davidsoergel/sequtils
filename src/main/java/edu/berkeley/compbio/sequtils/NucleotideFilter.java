package edu.berkeley.compbio.sequtils;


/**
 * Provides an Interface for a Mutating Nucleotide Filter
 *
 * @author David Soergel
 */
public abstract interface NucleotideFilter
	{
	//private static Logger logger = Logger.getLogger(NucleotideFilter.class);

	public byte filter(byte b) throws FilterException;
	}
