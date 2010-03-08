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


package edu.berkeley.compbio.sequtils.sequencereader.fasta;

import com.davidsoergel.dsutils.CacheManager;
import com.davidsoergel.dsutils.DSArrayUtils;
import com.davidsoergel.dsutils.math.MersenneTwisterFast;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceArrayUtils;
import edu.berkeley.compbio.sequtils.SequenceError;
import edu.berkeley.compbio.sequtils.SequenceException;
import edu.berkeley.compbio.sequtils.SequenceFragmentMetadata;
import edu.berkeley.compbio.sequtils.SequenceReader;
import edu.berkeley.compbio.sequtils.TranslatingSequenceReader;
import edu.berkeley.compbio.sequtils.sequencereader.SectionList;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Fasta File Parser
 *
 * @author David Tulga
 * @version $Id: FastaParser.java 1246 2009-10-09 23:29:26Z soergel $
 */
public class FastaParser extends TranslatingSequenceReader implements SectionList
	{
	// ------------------------------ FIELDS ------------------------------

	private static final Logger logger = Logger.getLogger(FastaParser.class);

	private static final int DEFAULT_BUFFER_SIZE = 16384;

	private static final int upstreamBufferSize = 256;
	private final int buffersize;// = 16384;
	//private final int peekbuffersize = 16;
	private final int maxidlen = 256;

	private final int MAX_RANDOM_ATTEMPTS = 50;

	private FastaFileSet fileset;
	private long totalsequence;

	final boolean skipGaps;


	private int validCharsInBuffer = 0;
	private long filePosition = 0;
	private final byte EOF = -1;
	private byte[] idStr;

	private final byte[] upstreamBuffer = new byte[upstreamBufferSize + 1];

	private SequenceFragmentMetadata rootMetadata;

	private TreeMap<Long, SequenceFragmentMetadata> offsetToSectionIndex;
	private SequenceFragmentMetadata[] allSectionsInOffsetOrder;
	private HashMap<String, SequenceFragmentMetadata> idToSection;


	// --------------------------- CONSTRUCTORS ---------------------------

	/**
	 * Initializes a parser from a fileset
	 *
	 * @param fs The FastaFileSet
	 */
	public FastaParser(FastaFileSet fs, boolean skipGaps)
		{
		this(fs, DEFAULT_BUFFER_SIZE, skipGaps);
		}

	/**
	 * Initializes a parser from a fileset and with a specific buffer size
	 *
	 * @param fs      The FastaFileSet
	 * @param bufsize The buffer size
	 */
	public FastaParser(FastaFileSet fs, int bufsize, boolean skipGaps)
		{
		this.skipGaps = skipGaps;
		buffersize = bufsize;//Math.max(bufsize, buffersize);
		fileset = fs;
		totalsequence = fs.getTotalSequence();
		rootMetadata = new SequenceFragmentMetadata(null, getName(), null, 0, totalsequence);

		initMainBuffer(buffersize);
		initTranslationBuffer(buffersize);

		idStr = new byte[maxidlen];
		}

	/*	public void releaseCachedResources()
		 {
		 fileset.releaseCachedResources();
		 }
 */

	public String getName()
		{
		return fileset.getName();
		}

	// ------------------------ CANONICAL METHODS ------------------------

/*	@Override
	protected void finalize() throws Throwable
		{
		close();
		super.finalize();
		}
*/
	/*	private SequenceFragmentMetadata findParentSection(SequenceFragmentMetadata childLocation) throws IOException
		 {
		 //if(logger.isDebugEnabled()) logger.debug("To Section Start...");
		 int temp;
		 int temp2;
		 if (bufPosition >= validCharsInBuffer)
			 {
			 validCharsInBuffer = fileset.read(buf, filePosition, peekbuffersize);
			 filePosition += validCharsInBuffer;
			 bufPosition = 0;
			 }
		 if (buf[bufPosition] != '>')
			 {
			 filePosition = filePosition - validCharsInBuffer + bufPosition;
			 temp = 1;
			 while (temp > 0)
				 {
				 //System.out.print("*"+filePosition+"*");
				 int temppos = filePosition;
				 filePosition = Math.max(filePosition - updist, 0);
				 temp = fileset.read(upstreamBuffer, filePosition, Math.min(updist, temppos));
				 //System.out.print(":"+filePosition+":");
				 //System.out.print("..." + new String(upstreamBuffer) + "...");
				 if (temp != Math.min(updist, temppos) && filePosition > 0)
					 {
					 temp2 = fileset.read(upstreamBuffer, filePosition + temp, updist - temp);
					 //System.out.print(".2." + new String(upstreamBuffer) + ".2.");
					 //if(temp != updist - temp)
					 //	{
					 //    throw new IOException("Too close to beginning of file!");
					 //	}
					 temp = temp2;
					 }
				 int j = temp;
				 filePosition += temp;
				 while (j > 0)
					 {
					 j--;
					 filePosition--;
					 if (upstreamBuffer[j] == '>')
						 {
						 validCharsInBuffer = 0;
						 bufPosition = 0;
						 //System.out.print("*"+filePosition+"*");
						 return filePosition;
						 }
					 }
				 }
			 }
		 return filePosition;
		 }
 */

	public void close()
		{
		//if(logger.isDebugEnabled()) logger.debug("Closing Parser.");
		fileset.close();
		}

	@Override
	public String toString()
		{
		return fileset.toString();
		}

	// ------------------------ INTERFACE METHODS ------------------------


	// --------------------- Interface SectionList ---------------------


	@NotNull
	public SequenceFragmentMetadata randomSectionFragment() throws NotEnoughSequenceException, IOException
		{
		int i = 0;
		int position;
		while (i++ < MAX_RANDOM_ATTEMPTS)
			{
			try
				{
				position = (int) (totalsequence * MersenneTwisterFast.random());
				seek(position);
				checkCurrentPositionIsValidSequence();
				}
			/*	catch (IOException e)
			   {
			   continue;
			   }*/
			catch (NotEnoughSequenceException e)
				{
				logger.trace("trace", e);
				continue;
				}

			return new SequenceFragmentMetadata(rootMetadata, "Random sequence from " + getName(), getTaxid(),
			                                    position);
			}
		throw new NotEnoughSequenceException(
				"Could not find valid sequence after " + MAX_RANDOM_ATTEMPTS + " random attempts");
		}

	/**
	 * Return a section, randomly chosen nucleotidewise (i.e., weighted by section length).
	 *
	 * @return The section
	 */
	public SequenceFragmentMetadata randomSectionLengthWeighted() throws IOException
		{
		SequenceFragmentMetadata result;
		try
			{
			result = randomSectionFragment();
			result = findParentSection(result);
			}
		/*catch (IOException e)
			{
			logger.error("Error", e);
			throw new MsensrError(e);
			}*/
		catch (NotEnoughSequenceException e)
			{
			logger.error("Error", e);
			throw new SequenceError(e);
			}
		return result;
		}

	public SequenceFragmentMetadata randomSectionUniform() throws IOException
		{
		if (offsetToSectionIndex == null)
			{
			generateIndex();
			//if(sectionIndex.isEmpty()) { throw new IOException("Section Index is empty!"); }
			}
		double rand = MersenneTwisterFast.random();
		int index = (int) Math.floor(rand * allSectionsInOffsetOrder.length);
		return allSectionsInOffsetOrder[index];
		}

	int[] indexesShuffled;
	int shuffledIndex;

	public SequenceFragmentMetadata nextShuffled() throws IOException, NotEnoughSequenceException
		{
		if (offsetToSectionIndex == null)
			{
			generateIndex();
			//if(sectionIndex.isEmpty()) { throw new IOException("Section Index is empty!"); }
			}
		if (indexesShuffled == null)
			{
			indexesShuffled = DSArrayUtils.createIncrementingIntArray(allSectionsInOffsetOrder.length, 0, 1);
			shuffledIndex = -1;
			}
		shuffledIndex++;
		if (shuffledIndex >= allSectionsInOffsetOrder.length)
			{
			throw new NotEnoughSequenceException("");
			}
		return allSectionsInOffsetOrder[shuffledIndex];
		}

	// --------------------- Interface SequenceReader ---------------------

	public long getTotalSequence()
		{
		return fileset.getTotalSequence();
		}

	public byte read() throws IOException, NotEnoughSequenceException
		{
		byte t;
		while (true)
			{
			if (bufPosition >= validCharsInBuffer)
				{
				try
					{
					setTranslationInvalid();
					validCharsInBuffer = fileset.readAt(buf, filePosition, buffersize);
					filePosition += validCharsInBuffer;
					bufPosition = 0;
					}
				catch (IOException e)
					{
					validCharsInBuffer = 1;
					bufPosition = 0;
					buf[bufPosition] = EOF;
					throw e;
					}
				}
			t = buf[bufPosition];
			if (t == EOF || t == '>')
				{
				// Section end
				//if(logger.isDebugEnabled()) logger.debug("Reader End of Section.");

				//note we don't advance the position, so we continue to return EOF on subsequent calls
				//return EOF;
				throw new NotEnoughSequenceException("End of sequence");
				}
			else if (t == '\n' || t == '\r' || (skipGaps && SequenceArrayUtils.gapChars.indexOf(t)
			                                                != -1)) //t == ' ' || t == '-')
				{
				// Skip newlines and gaps
				bufPosition++;
				}
			else
				{
				bufPosition++;
				//if(logger.isDebugEnabled()) logger.debug("Read: " + (char) t);
				return t;
				}
			}
		}

	public int read(byte[] buffer, int length)
		{
		int ok = 0;
		try
			{
			for (int i = 0; i < length; i++)
				{
				buffer[i] = read();
				ok++;
				}
			}
		catch (IOException e)
			{
			}
		catch (NotEnoughSequenceException e)
			{
			}
		return ok;
		}


	public void seek(SequenceFragmentMetadata location) //throws IOException

		{
		seek(location, 0);
		}

	// -------------------------- OTHER METHODS --------------------------

	private void checkCurrentPositionIsValidSequence() throws NotEnoughSequenceException, IOException
		{
		//if(logger.isDebugEnabled()) logger.debug("Checking if valid...");
		//	try
		//		{
		//we are testing backwards from fileposition _inclusive_, so we need to read fileTrav+1 characters.

		long fileTrav = filePosition - validCharsInBuffer + bufPosition;
		//System.out.println(fileTrav);
		int upstreamCharsAvailable = fileset.readAt(upstreamBuffer, Math.max(fileTrav - upstreamBufferSize, 0),
		                                            (int) Math.min(fileTrav + 1, upstreamBufferSize));

		if (upstreamCharsAvailable != Math.min(upstreamBufferSize, fileTrav + 1))
			{
			// this should never happen
			throw new SequenceError("Didn't get as much upstream sequence as we wanted");
			}

		// TODO: what's this for?
		/*			if (upstreamCharsAvailable != Math.min(upstreamBufferSize, fileTrav) + 1
							   && filePosition - upstreamBufferSize > 0)
						   {
						   upstreamCharsAvailable = fileset.read(upstreamBuffer,
																 fileTrav - upstreamBufferSize + upstreamCharsAvailable,
																 upstreamBufferSize - upstreamCharsAvailable + 1);
						   //System.out.print(".2." + new String(upstreamBuffer) + ".2.");
						   //if(temp != updist - temp)
						   //	{
						   //    throw new IOException("Too close to beginning of file!");
						   //	}
						   }

		   */
		//if(fileset.read(upstreamBuffer, Math.max(filePosition - updist, 0), updist) != updist)
		//	{
		//    throw new NotEnoughSequenceException("Too close to beginning of file!");
		//	}
		//System.out.println(":");
		//System.out.println(temp);
		int j = upstreamCharsAvailable - 1;// the array is zero-based, so j is now the max valid position
		while (j >= 0)
			{
			//System.out.print((char) upstreamBuffer[j]);
			if (upstreamBuffer[j] == '>')
				{
				//if(logger.isDebugEnabled()) logger.debug("Inside of header!");
				throw new NotEnoughSequenceException("Inside of header!");
				}
			if (upstreamBuffer[j] == '\n' || upstreamBuffer[j] == '\r')
				{
				//if(logger.isDebugEnabled()) logger.debug("Valid!");
				return;
				}
			j--;
			}
		//if(logger.isDebugEnabled()) logger.debug("Lines longer than " + updist + " bytes.");
		throw new NotEnoughSequenceException("Lines longer than " + upstreamBufferSize + " bytes.");
		/*		}
	   catch (IOException e)
		   {
		   //if(logger.isDebugEnabled()) logger.debug("IOException!",e);
		   throw new NotEnoughSequenceException(e);
		   }*/
		}

	/**
	 * Return the metadata of the section containing the given section (i.e., the nearest upstream header).
	 *
	 * @param childLocation Section to find the parent of
	 * @return The section's metadata
	 * @throws IOException If any data errors occur
	 */
	private SequenceFragmentMetadata findParentSection(SequenceFragmentMetadata childLocation) throws IOException
		{
		if (offsetToSectionIndex == null)
			{
			generateIndex();
			//if(sectionIndex.isEmpty()) { throw new IOException("Section Index is empty!"); }
			}
		SortedMap<Long, SequenceFragmentMetadata> head =
				offsetToSectionIndex.headMap(childLocation.getStartPosition() + 1);
		return head.get(head.lastKey());
		}

	private void generateIndex() throws IOException
		{
		offsetToSectionIndex =
				(TreeMap<Long, SequenceFragmentMetadata>) CacheManager.get(this, getName() + ".offsetToSection");
		idToSection = (HashMap<String, SequenceFragmentMetadata>) CacheManager.get(this, getName() + ".idToSection");

		if (offsetToSectionIndex == null)
			{
			long currentLocation = filePosition - validCharsInBuffer + bufPosition;
			reset();
			offsetToSectionIndex = new TreeMap<Long, SequenceFragmentMetadata>();
			idToSection = new HashMap<String, SequenceFragmentMetadata>();

			SequenceFragmentMetadata section;
			try
				{
				while (true)
					{
					section = next();
					offsetToSectionIndex.put(section.getStartPosition(), section);
					idToSection.put(section.getSequenceName(), section);
					}
				}
			/*catch (IOException e)
			{
			logger.error("Error", e);
			}*/
			catch (NotEnoughSequenceException e)
				{
				// exhausted sections
				//logger.error("Error", e);
				}

			seek(currentLocation);

			CacheManager.put(this, getName() + ".offsetToSection", offsetToSectionIndex);
			CacheManager.put(this, getName() + ".idToSection", idToSection);
			}
		SequenceFragmentMetadata[] a = new SequenceFragmentMetadata[offsetToSectionIndex.size()];
		allSectionsInOffsetOrder = offsetToSectionIndex.values().toArray(a);// hope the items stay sorted...
		}

	public void reset()
		{
		//try
		//	{
		seek(0);
		//	}
		//catch (IOException e)
		//	{
		//	logger.error("Error", e);
		//	throw new MsensrError(e);
		//	}
		}

	/**
	 * Returns the underlying fileset
	 *
	 * @return The fileset
	 */
	/*	public FastaFileSet getFileSet()
		 {
		 return fileset;
		 }
 */

	/**
	 * Returns the next section
	 *
	 * @return The metadata of the next section
	 * @throws IOException
	 * @throws NotEnoughSequenceException
	 */
	@NotNull
	public SequenceFragmentMetadata next() throws NotEnoughSequenceException, IOException
		{
		logger.trace("Next Section...");

		SequenceFragmentMetadata section =
				new SequenceFragmentMetadata(rootMetadata, readNextSectionHeader(), getTaxid(), 0);
		//section.setSequenceName(readNextSectionHeader());
		section.setStartPosition(filePosition - validCharsInBuffer + bufPosition);
		return section;
		}

	public SequenceFragmentMetadata getRootMetadata()
		{
		return rootMetadata;
		}

	/**
	 * Returns a SequenceFragmentMetadata encompassing all sequence available to this parser, inheriting from the parent
	 * metadata of the same range for lame reasons
	 *
	 * @return The metadata of the next section
	 * @throws IOException
	 * @throws NotEnoughSequenceException
	 */
	/*	public SequenceFragmentMetadata getAggregateSequenceFragmentMetadata()
	   {
	   if (logger.isDebugEnabled())
		   {
		   logger.debug("Getting aggregate metadata...");
		   }
	   SequenceFragmentMetadata section = new SequenceFragmentMetadata(rootMetadata, getName(), getTaxid(), 0);
	   //section.setSequenceName(readNextSectionHeader());
	   //section.setStartPosition(0);
	   return section;
	   }*/
	public void setFragmentLength(SequenceFragmentMetadata frag) throws IOException
		{
		seek(frag);
		int length = 0;
		try
			{
			while (true)
				{
				read();
				length++;
				}
			}
		catch (NotEnoughSequenceException e)
			{
			//done
			}
		frag.setLength(length);
		}

	protected void seek(long newRawPosition) //throws IOException
		{
		//if(logger.isDebugEnabled()) logger.debug("Seek to: " + newRawPosition);
		long oldRawPosition = filePosition - validCharsInBuffer + bufPosition;
		//System.out.println("Pos: "+filePosition+"");
		//System.out.println("I: "+bufPosition+"");
		//System.out.println("New newRawPosition: "+newRawPosition+"");
		//System.out.println("Current newRawPosition: "+oldRawPosition+"");
		//if (newRawPosition != oldRawPosition)
		//	{
		//int distance = newRawPosition - oldRawPosition;
		int newBufPosition = (int) (bufPosition + (newRawPosition - oldRawPosition));
		if (newBufPosition >= 0 && newBufPosition < validCharsInBuffer)
			{
			bufPosition = newBufPosition;
			//filePosition = newRawPosition;
			//System.out.println("Rewinding.");
			}
		else
			{
			filePosition = newRawPosition;
			//validCharsInBuffer = fileset.read(buf, filePosition, buffersize);
			//filePosition += validCharsInBuffer;
			validCharsInBuffer = 0;
			bufPosition = 0;
			//System.out.println("Resetting.");
			}
		//	}
		//System.out.println("Pos: "+filePosition+"");
		//System.out.println("I: "+bufPosition+"");
		//System.out.println("Done.");
		//System.out.println("");
		//System.out.print("*"+filePosition+"*");
		}

	/**
	 * Returns a SequenceReader at the given section
	 *
	 * @param section The section to seek to
	 * @return This, set to seek to the given section
	 * @throws IOException If a file I/O problem is encountered
	 */
	public SequenceReader getReader(SequenceFragmentMetadata section) throws IOException
		{
		return getReader(section, 0);
		}

	/**
	 * Returns a SequenceReader at the given section
	 *
	 * @param section         The section to seek to
	 * @param sectionPosition The position within the section to seek to
	 * @return This, set to seek to the given section
	 * @throws IOException If a file I/O problem is encountered
	 */
	public SequenceReader getReader(SequenceFragmentMetadata section, int sectionPosition) throws IOException
		{
		seek(section, sectionPosition);
		return this;
		//return null;  //To change body of implemented methods use File | Settings | File Templates.
		}

	public void seek(SequenceFragmentMetadata location, long offset) //throws IOException
		{
		long newRawPosition = offset;
		if (location == null)
			{
			throw new SequenceError("Can't seek to null location");
			}
		while (location != null)
			{
			newRawPosition += location.getStartPosition();
			location = location.getParentMetadata();
			}
		seek(newRawPosition);
		}

	public Integer getTaxid()
		{
		return fileset.getTaxid();
		}

	/**
	 * Read forward from the current position until a new section header is encountered.  Return it, leaving the traversal
	 * pointer at the beginning of the sequence following the header.
	 *
	 * @return
	 * @throws IOException
	 * @throws NotEnoughSequenceException
	 */
	private String readNextSectionHeader() throws NotEnoughSequenceException, IOException
		{
		//if(logger.isDebugEnabled()) logger.debug("Reading header...");
		boolean readingId = false;
		boolean finishedReadingId = false;
		//boolean done = false;
		byte t;
		int idLen = 0;
		while (true)
			{
			if (bufPosition >= validCharsInBuffer)
				{
				setTranslationInvalid();
				validCharsInBuffer = fileset.readAt(buf, filePosition, buffersize);
				filePosition += validCharsInBuffer;
				bufPosition = 0;
				}
			t = buf[bufPosition++];
			if (t == '>')
				{
				// Start reading id
				readingId = true;
				//done = false;
				//bufPosition++;
				}
			else if (t == EOF || ((readingId || finishedReadingId) && (t == '\n' || t == '\r')))
				{
				if (idLen == 0)
					{
					throw new NotEnoughSequenceException("End of Files, no more sections");
					}
				return new String(idStr, 0, idLen);
				}
			/*if (t == EOF || t == '>' || t == '\n')
				{
				// Section end
				if(t == '>')
					{
					// Start reading id
					readId = true;
					done = false;
					}
				if(done || t == '\n' || t == EOF)
					{
					return new String(idStr, 0, idLen);
					}
				bufPosition++;
				}*/
			else if (t == ' ')
					{
					if (readingId)
						{
						readingId = false;
						finishedReadingId = true;
						//done = true;
						}
					//bufPosition++;
					}
				else
					{
					//bufPosition++;
					if (readingId)
						{
						idStr[idLen] = t;
						idLen++;
						}
					}
			}
		}

	@NotNull
	public synchronized SequenceFragmentMetadata getSequenceFragmentMetadata(final String id)
			throws SequenceException, IOException
		{
		if (idToSection == null)
			{
			generateIndex();
			//if(sectionIndex.isEmpty()) { throw new IOException("Section Index is empty!"); }
			}
		final SequenceFragmentMetadata result = idToSection.get(id);
		if (result == null)
			{
			throw new SequenceException("Sequence fragment " + id + " not found");
			}
		return result;
		}
	}
