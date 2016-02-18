package gr.demokritos.biographs.indexing.inverted;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.biographs.*;
import java.io.File;

/**
 * Unit test for simple App.
 */
public class InvertedTest
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public InvertedTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( InvertedTest.class );
    }
	
	static InvertedIndex nclData;

	/**
	 * Test that the {@link TrieDatabase} class works properly.
	 */
	public void testCreateTrieIndex() {
		String nclIndex = "/3061_consistent_nucleosomes.fa";

		nclData = new InvertedIndex();

		BioGraph[] nclBgs = null;
		try {
			// build database index 
			File resNCL = new File(getClass().getResource(nclIndex).toURI());

			nclData.buildIndex(resNCL);
			nclBgs = BioGraph.fastaFileToGraphs(resNCL);
			assertTrue(true); // succeed
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false); // fail
		}
		/* make sure the graphs were read properly */
		assertNotNull(nclBgs);
		for (BioGraph b: nclBgs) {
			assertNotNull(nclData.getMatches(b));
			assertTrue(nclData.getMatches(b).size() > 0);
		}
	}
}
