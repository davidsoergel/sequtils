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
