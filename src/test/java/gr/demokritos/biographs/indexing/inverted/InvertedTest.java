package gr.demokritos.biographs.indexing.inverted;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;

import java.io.File;
import java.util.Set;

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
	
	/**
	 * Verify that the {@link RandomInvertedIndex} class works properly.
	 */
	public void testRandomInvertedIndex() {
		String nclIndex = "/synth.fa";
		RandomInvertedIndex rndIndex = new RandomInvertedIndex();
		BioGraph[] nclBgs = null;
		try {
			// build database index 
			File resNCL = new File(getClass().getResource(nclIndex).toURI());
			rndIndex.buildIndex(resNCL);
			nclBgs = BioGraph.fastaFileToGraphs(resNCL);
			assertTrue(true); // succeed
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false); // fail
		}
		/* make sure the graphs were read properly */
		assertNotNull(nclBgs);
		for (BioGraph b: nclBgs) {
			Set<BioGraph> matches = rndIndex.getMatches(b);
			assertNotNull(matches);
			assertTrue(matches.size() > 0);
		}
	}
}
