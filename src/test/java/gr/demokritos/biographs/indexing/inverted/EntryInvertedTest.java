package gr.demokritos.biographs.indexing.inverted;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.io.BioInput;
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;
import gr.demokritos.biographs.indexing.structs.GraphIndexEntry;

import java.io.File;
import java.util.Set;

/**
 * Unit test for simple App.
 */
public class EntryInvertedTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EntryInvertedTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EntryInvertedTest.class );
    }
	
	/**
	 * Test that the {@link EntryInvertedIndex} class works properly.
	 */
	public void testEntryInvertedIndex() {
		String nclIndex = "/synth.fa";

		EntryInvertedIndex nclData = new EntryInvertedIndex();
		BioGraph[] nclBgs = null;
		try {
			// build database index 
			File resNCL = new File(getClass().getResource(nclIndex).toURI());
			nclData.build(resNCL, GraphType.DNA);
			nclBgs = BioInput.fastaFileToGraphs(resNCL);
			assertTrue(true); // succeed
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false); // fail
		}
		/* make sure the graphs were read properly and that all queries for
		 * already existing graphs return a result. */
		assertNotNull(nclBgs);
		for (BioGraph b: nclBgs) {
			Set<GraphIndexEntry> matches = nclData.getMatches(b);
			assertNotNull(matches);
			assertTrue(matches.size() > 0);
		}
	}
}
