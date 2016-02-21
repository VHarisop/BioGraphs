package gr.demokritos.biographs.indexing;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;
import gr.demokritos.iit.jinsect.jutils;

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
	static TreeDatabase<String> trd;
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

	public void testRecall() {
		String wordFile = "/words.txt";
		try {
			Comparator<BioGraph> bgComp = new Comparator<BioGraph>() {
				@Override
				public int compare(BioGraph bgA, BioGraph bgB) {
					double sSim = 
						bgA.getGraph().getDegreeRatioSum() -
						bgB.getGraph().getDegreeRatioSum();

					if (GraphDatabase.compareDouble(sSim, 0.0)) {
						return jutils.compareCanonicalCodes(
								bgA.getGraph(),
								bgB.getGraph());
					} else {
						return Double.compare(sSim, 0.0);
					}
				}
			};
			File res = new File(getClass().getResource(wordFile).toURI());
			TreeDatabase<String> trd = new TreeDatabase<String>(bgComp) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

			trd.build(res, GraphType.WORD);
			BioGraph[] bgs = BioGraph.fromWordFile(res);

			/* make sure that graphs that were indexed in the database
			 * return nonempty K nearest neighbours when queried */
			for (BioGraph b: bgs) {
				List<String> matches = 
					trd.getKNearestNeighbours(b, true, 3);
				assertNotNull(matches);
				assertTrue(matches.size() > 0);

				List<String> matches_noninclusive = 
					trd.getKNearestNeighbours(b, false, 3);
				assertNotNull(matches_noninclusive);
				assertTrue(matches_noninclusive.size() > 0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			assertTrue(false);
		}
	}
}
