/*
 * Copyright (c) 2007-2013  David Soergel  <dev@davidsoergel.com>
 * Licensed under the Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */


package edu.berkeley.compbio.sequtils.sequencereader.fasta;

import com.davidsoergel.dsutils.math.MersenneTwisterFast;
import edu.berkeley.compbio.sequtils.NotEnoughSequenceException;
import edu.berkeley.compbio.sequtils.SequenceFragmentMetadata;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;


/**
 * @author <a href="mailto:dev@davidsoergel.com">David Soergel</a>
 * @version $Id: FastaParserTest.java 999 2009-06-23 03:00:18Z soergel $
 */

public class FastaParserTest {
    private static final Logger logger = Logger.getLogger(FastaParserTest.class);

    // ------------------------------ FIELDS ------------------------------

    String seq;
    FastaParser fp;
    FastaFileSet fs;
    File theDirectory;


    // -------------------------- OTHER METHODS --------------------------

    @Test
    public void currentPositionIsInHeaderWorksCorrectly()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException {
        // trick to test private method
        final Method checkCurrentPositionIsValidSequence =
                FastaParser.class.getDeclaredMethod("checkCurrentPositionIsValidSequence");
        checkCurrentPositionIsValidSequence.setAccessible(true);

        expectPositionValid(checkCurrentPositionIsValidSequence, 0, false);
        expectPositionValid(checkCurrentPositionIsValidSequence, 7, false);
        expectPositionValid(checkCurrentPositionIsValidSequence, 35, false);
        expectPositionValid(checkCurrentPositionIsValidSequence, 36, false);
        expectPositionValid(checkCurrentPositionIsValidSequence, 37, true);
        expectPositionValid(checkCurrentPositionIsValidSequence, 37, true);
        expectPositionValid(checkCurrentPositionIsValidSequence, 50, true);
        expectPositionValid(checkCurrentPositionIsValidSequence, 500, true);
        expectPositionValid(checkCurrentPositionIsValidSequence, 5000, false);
    }

    private void expectPositionValid(Method checkCurrentPositionIsValidSequence, int position, boolean validExpected)
            throws IOException {
        fp.seek(position);
        try {
            checkCurrentPositionIsValidSequence.invoke(fp);
        } catch (IllegalAccessException e) {
            logger.error("Error", e);
            assert false;
        } catch (InvocationTargetException e) {
            assert !validExpected;
            return;
        }
        assert validExpected;
    }

    @Test
    public void headersAreReadCorrectly() throws Exception {
        seq = fp.next().getSequenceName();
        assert seq.equals("testgenome.1.1");
    }

    @Test
    public void headersAreReadCorrectlyAcrossFiles() throws Exception {
        seq = fp.next().getSequenceName();
        assert seq.equals("testgenome.1.1");
        seq = fp.next().getSequenceName();
        assert seq.equals("testgenome.1.2");
        seq = fp.next().getSequenceName();
        assert seq.equals("testgenome.2.1");
        seq = fp.next().getSequenceName();
        assert seq.equals("testgenome.2.2");
    }

    @Test
    public void positionSeekWorksCorrectly() throws Exception {
        fp.seek(50);
        assertPosition50();
    }

    private void assertPosition50() throws Exception {
        byte b;
        b = fp.read();
        assert b == 'G';
        b = fp.read();
        assert b == 'G';
        b = fp.read();
        assert b == 'T';
        b = fp.read();
        assert b == 'G';
        b = fp.read();
        assert b == 'G';
    }

    @Test(expectedExceptions = NotEnoughSequenceException.class)
    public void readingEndOfSectionAtEOFProducesNotEnoughSequenceException()
            throws IOException, NotEnoughSequenceException {
        fp.next();
        fp.seek(fp.next());
        for (int i = 0; i < 2000; i++) {
            fp.read();
        }
    }

    @Test(expectedExceptions = NotEnoughSequenceException.class)
    public void readingEndOfSectionAtNextHeaderProducesNotEnoughSequenceException()
            throws IOException, NotEnoughSequenceException {
        fp.seek(fp.next());
        for (int i = 0; i < 2000; i++) {
            fp.read();
        }
    }

    @Test(expectedExceptions = NotEnoughSequenceException.class)
    public void requestingTooManySectionsProducesNotEnoughSequenceException()
            throws IOException, NotEnoughSequenceException {
        fp.next();
        fp.next();
        fp.next();
        fp.next();
        fp.next();
    }

    @Test
    public void resetWorksCorrectly() throws Exception {
        fp.seek(50);
        assertPosition50();
        fp.reset();
        seq = fp.next().getSequenceName();
        //System.out.println(seq);
        assert seq.equals("testgenome.1.1");
        assertSection1p1();
    }

    private void assertSection1p1() throws Exception {
        byte b;
        b = fp.read();
        assert b == 'G';
        b = fp.read();
        assert b == 'C';
        b = fp.read();
        assert b == 'A';
        b = fp.read();
        assert b == 'A';
        b = fp.read();
        assert b == 'C';
    }

    @Test
    public void resetWorksCorrectlyAcrossFiles() throws Exception {
        fp.next();
        fp.seek(fp.next());
        fp.seek(fp.next());
        assertSection2p1();
        fp.reset();
        seq = fp.next().getSequenceName();
        //System.out.println(seq);
        assert seq.equals("testgenome.1.1");
    }

    private void assertSection2p1() throws Exception {
        byte b;
        b = fp.read();
        assert b == 'C';
        b = fp.read();
        assert b == 'T';
        b = fp.read();
        assert b == 'G';
        b = fp.read();
        assert b == 'G';
        b = fp.read();
        assert b == 'T';
    }

    @Test
    public void sectionSeekWorksCorrectlyAcrossFiles() throws Exception {
        SequenceFragmentMetadata section1p1 = fp.next();
        fp.next();
        SequenceFragmentMetadata section2p1 = fp.next();
        fp.seek(section2p1);
        assertSection2p1();
        fp.seek(section1p1);
        assertSection1p1();
    }

    @Test
    public void sequenceIsProvidedCorrectlyAcrossFiles() throws Exception {
        fp.seek(fp.next());
        assertSection1p1();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        MersenneTwisterFast.init();


        //	Map<String, Object> props = new HashMap<String, Object>();
        //props.setProperty("msensr.windowscan.writeshortwindows", "1");
        //props.setProperty("msensr.ktrie.smoothfactor", "2");
        //props.setProperty("msensr.ktrie.smoothtype", "sampleSizeBased");

        //	HierarchicalTypedPropertyNode n = PropertyConsumerClassParser.parseRootContextClass(StubSequenceFragmentIterator.class);
        //	MapToHierarchicalTypedPropertyNodeAdapter.mergeInto(n, props);
        //	stubSequenceFragmentIteratorFactory = new PropertyConsumerFactory<StubSequenceFragmentIterator>(n);

        URL url = ClassLoader.getSystemResource("Test_Genome");
        theDirectory = new File(url.getPath());
        //theDirectory = new File("src/test/data/Test_Genome/");
        fs = new FastaFileSet(theDirectory);
        logger.info("Loading test genome: " + theDirectory.getAbsolutePath() + " " + theDirectory.exists());

        fp = new FastaParser(fs, true);
        logger.info("Got FastaParser: " + fp.getTotalSequence());
    }

    /*
    @Test
    public void checkValidWorksCorrectly() throws Exception
        {

        FastaParser fp;

        FastaFileSet fs;

        File theDirectory = new File("src/test/data/Test_Genome/");

        fs = new FastaFileSet(theDirectory);

        fp = new FastaParser(fs);

        fp.seek(0);
        //System.out.println("Start...");
        try
            {
            fp.checkCurrentPositionIsValidSequence();
            assert false;
            }
        catch (NotEnoughSequenceException e)
            {
            // Good
            }

        fp.seek(3);
        try
            {
            fp.checkCurrentPositionIsValidSequence();
            assert false;
            }
        catch (NotEnoughSequenceException e)
            {
            // Good
            }

        fp.seek(100);
        fp.checkCurrentPositionIsValidSequence();
        }*/
}
