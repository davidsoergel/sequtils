/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.sequencefragmentiterator;

import com.davidsoergel.dsutils.GenericFactory;
import com.davidsoergel.dsutils.GenericFactoryException;
import edu.berkeley.compbio.sequtils.ByteArraySequenceReader;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceException;
import edu.berkeley.compbio.sequtils.strings.SequenceFragment;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @version 1.0
 */
public class StubSFI extends ScanningSFI
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(StubSFI.class);

	boolean initialized = false;
	List<ByteArraySequenceReader> sections;// = new LinkedList<SectionFragment>();

	private int charactersRead = 0;
	//List<SequenceFragmentMetadata> metadatas;

	//private Iterator<SequenceReader> it;
	private int pos = 0;

	private GenericFactory<ByteArraySequenceReader> byteArraySequenceReaderFactory;


	// --------------------------- CONSTRUCTORS ---------------------------

	//KcountScanner theScanner;

	public StubSFI() throws SequenceException,
	                        GenericFactoryException//Initialize the sequence provider with the correct input file or directory
		{
		super();
		initialized = true;
		sections = new LinkedList<ByteArraySequenceReader>();
		//metadatas = new LinkedList<SequenceFragmentMetadata>();
		//spectrumScanner = new ExactKcountScanner();

		sections.add(new ByteArraySequenceReader("ATGGTGCGAGCTAGACCAGATTTAGACAAGAACTTGGCCTCGTAAGCTAT"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq1", 0));

		sections.add(new ByteArraySequenceReader("ATCGTGCGGGCTAGACAAGATATAGACCAGAACTCGGCCTCGTAAGCTAT"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq2", 0)); // just a few mutations

		sections.add(new ByteArraySequenceReader("CGTTATGCCGATAGACGTACAGATCGGTGTCACGTGCCGTCAACAGCACG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq3", 0));
		sections.add(new ByteArraySequenceReader("CGTAATCCCGACAGATGTACAGATCGGTCTCACGTCCCGTCCACATCACG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq4", 0)); // just a few mutations

		sections.add(new ByteArraySequenceReader("GCGTGAGTCCCCGGTTTGTCCAAACAGATTTGTTGGAAACCCAAGGTTGG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq5", 0));
		sections.add(new ByteArraySequenceReader("GCCTGACTCCCTGGTTCGTCCAAACTGATTTGATGGAAACCTAAGGTTGG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq6", 0)); // just a few mutations

		sections.add(new ByteArraySequenceReader("TCGCGAATCGACTGCCGATCGACTACATCGGCAATACCGCATCGACATCG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq7", 0)); // just a few mutations
		sections.add(new ByteArraySequenceReader("CTCGCCTCGCTCCCGCTCGCCCTCGCTCCGCTCCGCTCGACGCTCCGCTC"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq8", 0)); // just a few mutations

		sections.add(new ByteArraySequenceReader("ATACTAATCATATATTCCTATCAGATCATAATTCTATTATCAATGATTAT"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq9", 0)); // just a few mutations
		sections.add(new ByteArraySequenceReader("ACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTACGTAC"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq10", 0)); // just a few mutations

		sections.add(new ByteArraySequenceReader("CGTTATACCGATAGACGTACAAATCGGTGTCACGTGCCGTCAACAGCACG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq11", 0));
		sections.add(new ByteArraySequenceReader("CGTAATACCGACAGATGTACATATCGGTCTCACGTCCCGTCCACATCACG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq12", 0)); // just a few mutations

		sections.add(new ByteArraySequenceReader("CGTTATTCCGATAGACGTACATATCGGTGTCACGTGCCGTCAACAGCACG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq13", 0));
		sections.add(new ByteArraySequenceReader("CGTAATTCCGACAGATGTACAAATCGGTCTCACGTCCCGTCCACATCACG"));
		//metadatas.add(new SequenceFragmentMetadata(null, "Seq14", 0)); // just a few mutations

		//it = sections.iterator();
		//return this;
		}

	// --------------------- GETTER / SETTER METHODS ---------------------

	public long getCharactersRead()
		{
		return charactersRead;
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface Iterator ---------------------

	public boolean hasNext()
		{
		return pos < 14;
		}

	public SequenceFragment peek()
		{
		throw new NotImplementedException();
		}

	@NotNull
	public SequenceFragment next()//Supplies the next read
		{
		//assert length == 50;  // test methods should request this
		assert initialized;
		try
			{
			if (pos >= 14)
				{
				throw new NoSuchElementException("No more sequences");
				}
			SequenceFragment result =
					new SequenceFragment(null, "Seq " + pos, 0, sections.get(pos), 50, spectrumScanner);
			result.checkAvailable();
			//			Kcount result = theScanner.scanSequence(sections.get(pos), 50);
			//			result.getMetadata().setSequenceName("Seq " + pos);
			charactersRead += result.getLength();
			pos++;
			result.doneLabelling();
			return result;
			}
		catch (NotEnoughSequenceException e)
			{
			logger.error("Error", e);
			assert false;
			}
		//	assert false;
		throw new NoSuchElementException();
		}

	// -------------------------- OTHER METHODS --------------------------

	public void close()
		{
		// Do Nothing
		}

	public int estimatedTotalSamples()
		{
		throw new NotImplementedException();
		}

	public long getTotalSequence()
		{
		return "ATGGTGCGAGCTAGACCAGATTTAGACAAGAACTTGGCCTCGTAAGCTAT".length() * 14;
		}

	/*@Override
	public void releaseCachedResources()
		{
		}
*/

	public void reset()
		{
		pos = 0;
		for (ByteArraySequenceReader r : sections)
			{
			r.reset();
			}
		/*try
			{
			init();
			}
		catch (BMsensrExceptione)
			{
			logger.error("Error", e);
			assert false;
			}*/
		}
	}
