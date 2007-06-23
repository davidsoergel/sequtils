package edu.berkeley.compbio.sequtils;


/**
 * @author lorax
 * @version 1.0
 */
public class ByteArraySequenceReader implements SequenceReader
	{
	//private static Logger logger = Logger.getLogger(ByteArraySequenceReader.class);

	byte[] theBytes;
	int pos = 0;

	public ByteArraySequenceReader(byte[] b)
		{
		theBytes = b;
		}

	public ByteArraySequenceReader(String s)
		{
		this(s.getBytes());
		}

	public byte read() throws NotEnoughSequenceException// Read one character from the buffer
		{
		try
			{
			return theBytes[pos++];
			}
		catch (IndexOutOfBoundsException e)
			{
			throw new NotEnoughSequenceException(e);
			}
		}

	/*
	 public void seek(int position) throws IOException // Seek to a particular position
		 {
		 pos = position;
		 }
 */
	public int getTotalSequence()
		{
		return theBytes.length;
		}

	/*	public void checkCurrentPositionIsValidSequence() throws NotEnoughSequenceException
		 {
		 // Always valid for a byte array
		 }

	 public SectionFragmentMetadata nextSection() throws IOException
		 {
		 return new SectionFragmentMetadata(null, "Byte Array Sequence", 0, theBytes.length);
		 }
 */
	public String getName()
		{
		return "Byte Array Sequence";
		}

	public int toSectionStart()
		{
		return pos = 0;
		}

	public void close()
		{
		// Do Nothing
		}
	}
