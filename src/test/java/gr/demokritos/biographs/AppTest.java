package gr.demokritos.biographs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.utils;

import java.util.ArrayList;

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
	 * for comparing isomorphic BioGraphs.
	 */
	public void testSubgraphIso() 
	{
		BioGraph bgx = new BioGraph("ACTA");
		BioGraph bgy = new BioGraph("ACTAG");
		
		boolean res = NggIsomorphismTester.subgraphIsomorphic(bgx, bgy);
		assertTrue( res );
	}

	/**
	 * Verify that exact graph isomorphism works
	 * for comparing a BioGraph with itself, and
	 * returns false if the BioGraph is slightly 
	 * altered.
	 */
	public void testGraphIso()
	{
		BioGraph bgx = new BioGraph("ACTAGA");
		BioGraph bgy = new BioGraph("ACTAGA");

		boolean res = NggIsomorphismTester.graphIsomorphic(bgx, bgy);
		assertTrue( res );

		bgy.setDataString("ACTAG");
		res = NggIsomorphismTester.graphIsomorphic(bgx, bgy);
		assertTrue( !res );
	}

	/**
	 * Test using the toDot() method to print the graphs in DOT format.
	 */
	public void testDot() 
	{
		BioGraph bgx = new BioGraph("CTATAG");
		BioGraph bgy = new BioGraph("CTAG");

		System.out.println(bgx.toDot());
		System.out.println(bgy.toDot());

		assertTrue( true );
	}

	/**
	 * Verify that subgraph isomorphism check is negative
	 * for non subgraph-isomorphic NGGs.
	 */
	public void testSubgraphNonIso()
	{
		BioGraph bgx = new BioGraph("AGTA");
		BioGraph bgy = new BioGraph("ACTAG");

		boolean res = NggIsomorphismTester.subgraphIsomorphic(bgx, bgy);
		assertTrue( !res );
	}

	/**
	 * Verify that DFS encoding works properly.
	 */
	public void testDFSCoding() 
	{
		BioGraph bgx = new BioGraph("AGTAC");
		System.out.println(bgx.getDfsCode());

		assertTrue( true );
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
}
