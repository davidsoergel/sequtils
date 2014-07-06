/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.sequencereader.fasta;

import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: FastaFileSetTest.java 652 2008-09-23 02:20:00Z soergel $
 */

public class FastaFileSetTest {
    // ------------------------------ FIELDS ------------------------------

    private static final Logger logger = Logger.getLogger(FastaFileSetTest.class);

    FastaFileSet fs;
    File theDirectory;
    byte[] buf = new byte[20];


    // -------------------------- OTHER METHODS --------------------------

    @Test
    public void attemptToReadPastEndJustReadsLess() throws IOException, NotEnoughSequenceException {
        int charsRead = fs.readAt(buf, 3336, 20);
        logger.debug("Read at end read: " + charsRead);

        assert (charsRead == 10);
    }

    @Test
    public void readAtCrossesFileBoundaries() throws IOException, NotEnoughSequenceException {
        byte[] buf2 = new byte[10];
        fs.readAt(buf2, 1706, 10);
        String got = new String(buf2);
        String expected = "TTCT\n>test";
        logger.debug("Read crossing file boundaries: " + got);
        assert (got.equals(expected));
    }

    @Test(expectedExceptions = NotEnoughSequenceException.class)
    public void readAtPositionPastEndThrowsException() throws IOException, NotEnoughSequenceException {
        fs.readAt(buf, 4000, 10);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        //	Map<String, Object> props = new HashMap<String, Object>();

        //props.setProperty("msensr.windowscan.writeshortwindows", "1");
        //props.setProperty("msensr.ktrie.smoothfactor", "2");
        //props.setProperty("msensr.ktrie.smoothtype", "sampleSizeBased");
        //	HierarchicalTypedPropertyNode n = PropertyConsumerClassParser.parseRootContextClass(StubSequenceFragmentIterator.class);
        //	MapToHierarchicalTypedPropertyNodeAdapter.mergeInto(n, props);
        //	stubSequenceFragmentIteratorFactory = new PropertyConsumerFactory<StubSequenceFragmentIterator>(n);

        URL url = ClassLoader.getSystemResource("Test_Genome");
        theDirectory = new File(url.getPath());
        logger.info("Loading test genome: " + theDirectory.getAbsolutePath() + " " + theDirectory.exists());
        //theDirectory = new File("src/test/data/Test_Genome/");
        fs = new FastaFileSet(theDirectory);

        logger.info("Got FastaFileSet: " + fs.getTotalSequence());
    }
}
