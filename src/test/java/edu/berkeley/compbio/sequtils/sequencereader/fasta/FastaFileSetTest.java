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
