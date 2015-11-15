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
	 * Verify that subgraph isomorphism check is negative
	 * for non subgraph-isomorphing NGGs.
	 */
	public void testSubgraphNonIso()
	{
		DocumentNGramGraph dngA = new DocumentNGramGraph();
		DocumentNGramGraph dngB = new DocumentNGramGraph();

		dngA.setDataString("AGTA");
		dngB.setDataString("ACTAG");

		boolean res = NggIsomorphismTester.subgraphIsomorphic(dngA, dngB);
		assertTrue( !res );
	}

	/** 
	 * Test the prefix tree implementation of biographs 
	 */
	public void testPrefixTree() 
	{
		PrefixTree<Integer> pt = new PrefixTree<Integer>();
		pt.put("tester", 1);
		pt.put("testing", 2);
		pt.put("teasing", 3);

		assertTrue(pt.size() == 3);

		int size = 0;
		for (String s: pt.keysWithPrefix("tes")) 
			size++;
		assertTrue(size == 2);

		size = 0;
		for (String s: pt.keysWithPrefix("tea"))
			size++;
		assertTrue(size == 1);
	}

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
