package gr.demokritos.biographs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.iit.jinsect.documentModel.representations.NGramJGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.structs.Edge;
import gr.demokritos.iit.jinsect.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
	 * for comparing BioJGraphs.
	 */
	public void testIso() {
		BioJGraph bgx = new BioJGraph("ACTA");
		BioJGraph bgy = new BioJGraph("ACTAG");

		boolean res = IsomorphismTester.subgraphIsomorphic(bgx, bgy);
		assertTrue(res);

		res = IsomorphismTester.graphIsomorphic(bgx, bgy);
		assertTrue(!res);

		bgx.setDataString("AGTA");
		res = IsomorphismTester.subgraphIsomorphic(bgx, bgy);
		assertTrue(!res);
		res = IsomorphismTester.graphIsomorphic(bgx, bgy);
		assertTrue(!res);

		bgx.setDataString("ACTAG");
		res = IsomorphismTester.graphIsomorphic(bgx, bgy);
		assertTrue(res);
		res = IsomorphismTester.subgraphIsomorphic(bgx, bgy);
		assertTrue(res);
	}

	/**
	 * Test using the toDot() method to print the graphs in DOT format.
	 */
	public void testDot() 
	{
		BioJGraph bgx = new BioJGraph("CTATAG");
		BioJGraph bgy = new BioJGraph("CTAG");

		System.out.println(bgx.toDot());
		System.out.println(bgy.toDot());

		assertTrue( true );
	}

	/**
	 * Verify that {@link BioJGraph.fromFastaFile()} and
	 * {@link BioJGraph.fastaFileToGraphs()} work properly
	 * for fasta files with one or multiple entries.
	 *
	 */
	public void testFasta() 
	{
		BioJGraph bgx = null;
		String fName = "/testFile01.fasta";
		assertNotNull("Test file missing", getClass().getResource(fName));

		try {
			bgx = BioJGraph.fromFastaFile(
				new File(getClass().getResource(fName).toURI()));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		assertNotNull(bgx);
		assertTrue(bgx.bioLabel.equals("AB000263"));
		
		try {
			File res = new File(getClass().getResource(fName).toURI());
			BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(res);
			
			for (BioJGraph b: bgs) {
				assertNotNull(b);
				assertNotNull(b.bioLabel);
			}
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
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
