package gr.demokritos.biographs.indexing.preprocessing;

import gr.demokritos.biographs.*;
import gr.demokritos.iit.jinsect.structs.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for BioGraphs' facilities that employ generics, like
 * the TreeDatabase class.
 */
public class HashingTest 
	extends TestCase
{
	static BioGraph bgTest;
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public HashingTest( String testName )
	{
		super( testName );
	}
	
	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( HashingTest.class );
	}
	
	public void testDefaultHash() {
		UniqueJVertexGraph uvg = new UniqueJVertexGraph();
		uvg.add(new NGramVertex("ACT"));
		uvg.add(new NGramVertex("TCA"));
		uvg.add(new NGramVertex("CTA"));
		uvg.add(new NGramVertex("AGT"));

		VertexHashVector vHashVec = new VertexHashVector().withBins(20);
		int[] graphEnc = vHashVec.encodeGraph(uvg);
		assertNotNull(graphEnc);
		assertTrue(graphEnc.length == 20);

		HashingStrategy<JVertex> dvh = new DefaultVertexHash(); int key;

		/* get hash of "A" */
		key = dvh.hash(new NGramVertex("ACT")) % 20;
		assertEquals(graphEnc[key], 2);

		/* and hash of "C" */
		key = dvh.hash(new NGramVertex("CTA")) % 20;
		assertEquals(graphEnc[key], 1);
	}

	/**
	 * Verify {@link VertexHashVector} works properly for a BioGraph object.
	 */
	public void testGraphHash() {
		BioGraph bgA = new BioGraph("reforests");
		BioGraph bgB = new BioGraph("renounces");

		VertexHashVector vHash = new VertexHashVector().withPartialSums();
		
		int[] graphEnc = vHash.encodeGraph(bgA.getGraph());
		assertNotNull(graphEnc);

		graphEnc = vHash.encodeGraph(bgB.getGraph());
		assertNotNull(graphEnc);
	}
}
