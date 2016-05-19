package gr.demokritos.biographs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;
import gr.demokritos.biographs.io.BioInput;
import gr.demokritos.biographs.indexing.databases.TrieDatabase;
import gr.demokritos.iit.jinsect.structs.NGramVertex;

import java.io.File;

/**
 * Unit test for simple App.
 */
public class GeneralTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GeneralTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GeneralTest.class );
    }
	
	/**
	 * Verify that subgraph isomorphism test works
	 * for comparing BioGraphs.
	 */
	public void testIsomorphism() {
		BioGraph bgx = new BioGraph("ACTA");
		BioGraph bgy = new BioGraph("ACTAG");

		// bgx is initially subgraph isomorphic but not graph isomorphic
		assertTrue(IsomorphismTester.subgraphIsomorphic(bgx, bgy));
		assertFalse(IsomorphismTester.graphIsomorphic(bgx, bgy));

		// make the two graphs non-isomorphic
		bgx.setDataString("AGTA");
		assertFalse(IsomorphismTester.subgraphIsomorphic(bgx, bgy));
		assertFalse(IsomorphismTester.graphIsomorphic(bgx, bgy));

		// now the two are completely isomorphic
		bgx.setDataString("ACTAG");
		assertTrue(IsomorphismTester.graphIsomorphic(bgx, bgy));
		assertTrue(IsomorphismTester.subgraphIsomorphic(bgx, bgy));
	}


	/**
	 * Verify that {@link BioGraph#fromFastaFile()} and
	 * {@link BioGraph#fastaFileToGraphs()} work properly
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
			BioGraph[] bgs = BioInput.fastaFileToGraphs(res);
			int labelCnt = 0;
			
			for (BioGraph b: bgs) {
				assertNotNull(b);
				assertNotNull(b.bioLabel);
				assertTrue(b.bioLabel.equals(labels[labelCnt++]));
			}
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		fName = "/synth.fa";
		assertNotNull("Test file missing", getClass().getResource(fName));

		try {
			File res = new File(getClass().getResource(fName).toURI());
			BioGraph[] bgs = BioInput.fastaFileToGraphs(res);

			/* assert that all graphs have been read */
			int currIndex = 0;
			assertTrue(bgs.length == 500);
			for (BioGraph b: bgs) {
				assertNotNull(b);
				assertNotNull(b.bioLabel);

				/* assert labels have been read properly */
				assertTrue(b.bioLabel.equals("test_" +
							String.valueOf(currIndex)));
				currIndex++;
			}
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Verify that {@link TrieDatabase.getGraphCode()} can be overriden.
	 */
	public void testOverrideIndex() {
		// String fName = "/testFile01.fasta";
		String fName = "/files";
		TrieDatabase gData = new TrieDatabase() { 
				@Override
				protected String getGraphCode(BioGraph bG) {
					return bG.getCanonicalCode();
				}
			};

		try {
			File res = new File(getClass().getResource(fName).toURI());
			gData.build(res, GraphType.DNA);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		// make sure 3 different keys exist
		assertTrue(gData.exposeKeys().size() == 3);
	}
	/**
	 * Verify that {@link TrieDatabase.buildIndex()} works properly. 
	 */
	public void testIndex() {
		String fName = "/files";
		TrieDatabase gData = new TrieDatabase();
		try {
			File res = new File(getClass().getResource(fName).toURI());
			gData.build(res, GraphType.DNA);
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
		BioGraph bgx = new BioGraph("AGTAC");
		
		// this is the "correct" dfs code.
		String code = "GTA->AGT|TAC->AGT|TAC->GTA|";
		assertTrue(bgx.getDfsCode().equals(code));
		
		// this is the correct dfs code for starting at TAC
		code = "TAC->AGT|TAC->GTA|GTA->AGT|";
		assertTrue(bgx.getDfsCode(new NGramVertex("TAC")).equals(code));

	}
}
