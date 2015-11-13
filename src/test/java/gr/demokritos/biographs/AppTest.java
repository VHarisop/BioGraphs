package gr.demokritos.biographs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }


	/**
	 * Verify that subgraph isomorphism test works
	 * for comparing undirected NGGs. 
	 */
	public void testSubgraphIso() 
	{
		DocumentNGramGraph dngA = new DocumentNGramGraph();
		DocumentNGramGraph dngB = new DocumentNGramGraph();

		dngA.setDataString("ACTA");
		dngB.setDataString("ACTAG");

		boolean res = NggIsomorphismTester.subgraphIsomorphic(dngA, dngB);
		assertTrue( res );
	}

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
