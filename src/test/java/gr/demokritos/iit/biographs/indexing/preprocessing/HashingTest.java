package gr.demokritos.iit.biographs.indexing.preprocessing;

import gr.demokritos.iit.biographs.*;
import gr.demokritos.iit.biographs.indexing.GraphDatabase;
import gr.demokritos.iit.biographs.indexing.preprocessing.HashingStrategy;
import gr.demokritos.iit.biographs.indexing.preprocessing.IndexVector;
import gr.demokritos.iit.biographs.indexing.preprocessing.Strategies;
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
		UniqueVertexGraph uvg = new UniqueVertexGraph();
		uvg.add(new NGramVertex("ACT"));
		uvg.add(new NGramVertex("TCA"));
		uvg.add(new NGramVertex("CTA"));
		uvg.add(new NGramVertex("AGT"));

		/* make sure that the hash vector for DNA data has
		 * length 10 */
		IndexVector vHashVec = 
			new IndexVector(GraphDatabase.GraphType.DNA);
		assertEquals(10, vHashVec.encodeGraph(uvg).length);

		/* make sure that hashing based on dinucleotides
		 * is performed */
		HashingStrategy<JVertex> hashSg = Strategies.dinucleotideHash();
		int a = hashSg.hash(new NGramVertex("ACT"));
		int b = hashSg.hash(new NGramVertex("CAG"));
		assertEquals(a, b);

		a = hashSg.hash(new NGramVertex("TTG"));
		b = hashSg.hash(new NGramVertex("TAC"));
		assertFalse(a == b);
	}

	/**
	 * Verify {@link VertexHashVector} works properly for a BioGraph object.
	 */
	public void testGraphHash() {
		BioGraph bgA = new BioGraph("reforests");
		BioGraph bgB = new BioGraph("renounces");
		IndexVector hVec = 
			new IndexVector(GraphDatabase.GraphType.WORD);

		int[] graphEnc = hVec.encodeGraph(bgA.getGraph());
		assertNotNull(graphEnc);

		graphEnc = hVec.encodeGraph(bgB.getGraph());
		assertNotNull(graphEnc);
	}
}
