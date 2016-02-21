package gr.demokritos.biographs.indexing.inverted;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.biographs.*;
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
	 * Test that the {@link InvertedIndex} class works properly.
	 */
	public void testInvertedIndex() {
		String nclIndex = "/3061_consistent_nucleosomes.fa";

		InvertedIndex nclData = new InvertedIndex();
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

	public void testRandomIndex() {
		String nclIndex = "/3061_consistent_nucleosomes.fa";
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
