package gr.demokritos.biographs;

import gr.demokritos.biographs.indexing.TreeDatabase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.util.*;

/**
 * Unit test for BioGraphs' facilities that employ generics, like
 * the TreeDatabase class.
 */
public class KnnTest 
	extends TestCase
{
	static TreeDatabase trd;
	static BioGraph bgTest;
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public KnnTest( String testName )
	{
		super( testName );
		initTree();
	}

	private void initTree() {
		trd = new TreeDatabase<String>() {
			@Override
			public String getGraphFeature(BioGraph bG) {
				return bG.getLabel();
			}
		};
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( KnnTest.class );
	}
	
	public void testBuild() {
		String fileName = "/3061_consistent_nucleosomes.fa";
		try {
			// build index
			File res = new File(getClass().getResource(fileName).toURI());
			trd.buildIndex(res);
			
			assertTrue(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false); // fail
		}

		String nclTest = "/testFile02.fasta";
		try {
			File test = new File(getClass().getResource(nclTest).toURI());
			bgTest = BioGraph.fromFastaFile(test);

			assertNotNull(bgTest);
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}

	public void testKnn() {
		// make sure query graph has been read
		assertNotNull(bgTest);

		// make sure list of nearest neighbours exists and is not empty
		List<String> near = trd.getKNearestNeighbours(bgTest, true, 4);
		assertNotNull(near);
		assertTrue(near.size() > 0);

		// print list of NNs
		for (String s: near) {
			System.out.println(s);
		}
	}
}
