package gr.demokritos.biographs.indexing;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.databases.QuantTreeDatabase;
import gr.demokritos.iit.jinsect.structs.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.util.*;

/**
 * Unit test for {@link QuantTreeDatabase} class.
 */
public class QuantTreeTest 
	extends TestCase
{
	static TreeDatabase<String> trd;
	static BioGraph bgTest;
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public QuantTreeTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( QuantTreeTest.class );
	}

	public void testRecall() {
		String dataPath = "/words.txt";
		QuantTreeDatabase<String> qtd = null;
		BioGraph[] bgs = null;
		try {
			File dataFile = new File(getClass().getResource(dataPath).toURI());
			qtd = new QuantTreeDatabase<String>() {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};
			
			/* build a tree index and load the graphs into a 
			 * separate array as well */
			qtd.buildWordIndex(dataFile);
			bgs = BioGraph.fromWordFile(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue("Could not build index", false); // fail silently
		}

		assertNotNull(qtd);
		assertNotNull(bgs);

		for (BioGraph bg: bgs) {
			List<String> nodes = 
				qtd.getNodes(bg);

			/* make sure that the retrieved list is not null or empty */
			assertNotNull("Retrieved list is null!", nodes);
			assertTrue("Retrieved list is empty!", nodes.size() > 0);

			/* check if queried graph was there (it should be!) */
			boolean found = false;
			for (String s: nodes) {
				if (s.equals(bg.getLabel())) {
					found = true;
					break;
				}
			}

			/* make sure that label was eventually found */
			assertTrue(found);
		}

	}
	
	public void testKeys() {
		String fileName = "/testFile02.fasta";
		QuantTreeDatabase<String> qtd = null;
		try {
			// build the index
			File res = new File(getClass().getResource(fileName).toURI());
			qtd = new QuantTreeDatabase<String>()
			{	
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			}; 
			qtd.buildIndex(res);
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}

		/* make sure QuantTreeDatabase is not null */
		assertNotNull(qtd);

		/* make sure vCoder is not null */
		VertexCoder vCoder = qtd.getWeightMap();
		assertNotNull("Vertex Coder is null!", vCoder);

		/* make sure vCoder has been filled with entries */
		assertTrue("Vertex Coder has no entries!", vCoder.size() > 0);
	}
}
