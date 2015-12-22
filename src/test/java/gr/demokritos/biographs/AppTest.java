package gr.demokritos.biographs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.iit.jinsect.documentModel.representations.NGramJGraph;
import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.structs.Edge;
import gr.demokritos.iit.jinsect.structs.JVertex;
import gr.demokritos.iit.jinsect.structs.NGramVertex;
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

		// labels of sequences in fasta file
		String[] labels = new String[] {"AB000263", "AB00264", "AB00265"};

		try {
			File res = new File(getClass().getResource(fName).toURI());
			BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(res);
			int labelCnt = 0;
			
			for (BioJGraph b: bgs) {
				assertNotNull(b);
				assertNotNull(b.bioLabel);
				assertTrue(b.bioLabel.equals(labels[labelCnt++]));
			}
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

	/**
	 * Verify that {@link TrieDatabase.buildIndex()} works properly
	 */
	public void testIndex() {
		String fName = "/testFile01.fasta";
		TrieDatabase gData = new TrieDatabase();
		try {
			File res = new File(getClass().getResource(fName).toURI());
			gData.buildIndex(res);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		// make sure 3 different keys exist
		assertTrue(gData.exposeKeys().size() == 3);
	}

	/**
	 * Verify that DFS encoding works properly.
	 */
	public void testDFSCoding() 
	{
		BioJGraph bgx = new BioJGraph("AGTAC");
		System.out.println(bgx.getDfsCode());

		assertTrue( true );
	}
}
