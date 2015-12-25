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
import gr.demokritos.iit.jinsect.jutils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	 * Verify that {@link BioJGraph#fromFastaFile()} and
	 * {@link BioJGraph#fastaFileToGraphs()} work properly
	 * for fasta files with one or multiple entries.
	 *
	 */
	public void testFasta() 
	{
		String fName = "/testFile01.fasta";
		assertNotNull("Test file missing", getClass().getResource(fName));

		// labels of sequences in fasta file
		String[] labels = new String[] {"AB00265"};

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

		fName = "/1099_consistent_NFR.fa";
		assertNotNull("Test file missing", getClass().getResource(fName));

		try {
			File res = new File(getClass().getResource(fName).toURI());
			BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(res);

			// assert that all graphs have been read
			assertTrue(bgs.length == 1099);
			for (BioJGraph b: bgs) {
				assertNotNull(b);
				assertNotNull(b.bioLabel);
			}

			// assert that labels have been read properly
			assertTrue(bgs[0].bioLabel.equals("chr1:37519-37552"));
			assertTrue(bgs[2].bioLabel.equals("chr1:38881-38901"));
			assertTrue(bgs[5].bioLabel.equals("chr1:52499-52513"));
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Verify that {@link TrieDatabase.buildIndex()} and 
	 * {@link SimilarityDatabase.buildIndex()} work properly.
	 */
	public void testIndex() {
		// String fName = "/testFile01.fasta";
		String fName = "/files";
		TrieDatabase gData = new TrieDatabase();
		SimilarityDatabase gSimData = new SimilarityDatabase();
		try {
			File res = new File(getClass().getResource(fName).toURI());
			gData.buildIndex(res);
			gSimData.buildIndex(res);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		// make sure 3 different keys exist
		assertTrue(gData.exposeKeys().size() == 3);
		assertTrue(gSimData.exposeKeys().size() == 3);
	}

	/**
	 * Test that index building and retrieval works properly
	 * for {@link SimilarityDatabase} classes.
	 */
	public void testRetrieval() {
		String sTest = "/testFile01.fasta";
		String nfrIndex = "/1099_consistent_NFR.fa";
		String nclIndex = "/3061_consistent_nucleosomes.fa";

		// String sIndex = "/testFile02.fasta";

		SimilarityDatabase nfrData = new SimilarityDatabase();
		SimilarityDatabase nclData = new SimilarityDatabase();

		try {
			// build database index 
			File resNFR = new File(getClass().getResource(nfrIndex).toURI());
			File resNCL = new File(getClass().getResource(nclIndex).toURI());
			
			nfrData.buildIndex(resNFR);
			nclData.buildIndex(resNCL);

			// build the test graph
			File res = new File(getClass().getResource(sTest).toURI());
			BioJGraph bgTest = BioJGraph.fromFastaFile(res);

			// assert that querying an existing graph gives non-null labels
			List<String> labels = nfrData.treeIndex.get(bgTest);
			assertNotNull(labels); 
			
			// assert that the existing graph is found in the returned list
			boolean found = false;
			for (String s: labels) {
				if (s.equals("chr1:39666-39676")) {
					found = true;
					break;
				}
			}
			assertTrue(found);

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
		BioJGraph bgx = new BioJGraph("AGTAC");
		
		// this is the "correct" dfs code.
		String code = "GTA->AGT|TAC->AGT|TAC->GTA|";
		assertTrue(bgx.getDfsCode().equals(code));
		
		// this is the correct dfs code for starting at TAC
		code = "TAC->AGT|TAC->GTA|GTA->AGT|";
		assertTrue(bgx.getDfsCode(new NGramVertex("TAC")).equals(code));

	}
}
