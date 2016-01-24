package gr.demokritos.biographs.indexing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.biographs.*;

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
		String nfrIndex = "/1099_consistent_NFR.fa";
		String nclIndex = "/3061_consistent_nucleosomes.fa";

		nfrData = new TrieDatabase() {
			@Override
			protected String getGraphCode(BioGraph bGraph) {
				return bGraph.getCanonicalCode();
			}
		};

		nclData = new TrieDatabase() {
			@Override
			protected String getGraphCode(BioGraph bGraph) {
				return bGraph.getCanonicalCode();
			}
		};

		BioGraph[] nfrBgs = null, nclBgs = null;
		try {
			// build database index 
			File resNFR = new File(getClass().getResource(nfrIndex).toURI());
			File resNCL = new File(getClass().getResource(nclIndex).toURI());

			nfrData.buildIndex(resNFR);
			nclData.buildIndex(resNCL);
			nfrBgs = BioGraph.fastaFileToGraphs(resNFR);
			nclBgs = BioGraph.fastaFileToGraphs(resNCL);
			assertTrue(true); // succeed
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false); // fail
		}
		/* make sure the graphs were read properly */
		assertNotNull(nfrBgs);
		assertNotNull(nclBgs);

		/* make sure that retrieval works for graphs that exist in the trie */
		
		for (BioGraph b: nfrBgs) {
			assertNotNull(nfrData.getNodes(b));
			assertTrue(nfrData.getNodes(b).size() > 0);
		}

		for (BioGraph b: nclBgs) {
			assertNotNull(nclData.getNodes(b));
			assertTrue(nclData.getNodes(b).size() > 0);
		}
	}
}
