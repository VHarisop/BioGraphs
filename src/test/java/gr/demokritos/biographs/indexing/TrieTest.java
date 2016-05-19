package gr.demokritos.biographs.indexing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.io.BioInput;
import gr.demokritos.biographs.indexing.databases.TrieDatabase;

import java.io.File;

/**
 * Unit test for simple App.
 */
public class TrieTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TrieTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TrieTest.class );
    }
	
	// similarity databases to be shared amongst tests
	static TrieDatabase nfrData;
	static TrieDatabase nclData;

	/**
	 * Test that the {@link TrieDatabase} class works properly.
	 */
	public void testCreateTrieIndex() {
		String nclIndex = "/synth.fa";
		nclData = new TrieDatabase() {
			@Override
			protected String getGraphCode(BioGraph bGraph) {
				return bGraph.getCanonicalCode();
			}
		};

		BioGraph[] nclBgs = null;
		try {
			// build database index 
			File resNCL = new File(getClass().getResource(nclIndex).toURI());

			nclData.buildIndex(resNCL);
			nclBgs = BioInput.fastaFileToGraphs(resNCL);
			assertTrue(true); // succeed
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false); // fail
		}
		/* make sure the graphs were read properly */
		assertNotNull(nclBgs);

		/* make sure that retrieval works for graphs that exist in the trie */
		for (BioGraph b: nclBgs) {
			assertNotNull(nclData.getNodes(b));
			assertTrue(nclData.getNodes(b).size() > 0);
		}
	}
}
