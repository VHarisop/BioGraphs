package gr.demokritos.biographs;

import gr.demokritos.iit.jinsect.documentModel.representations.DocumentNGramGraph;
import gr.demokritos.iit.jinsect.structs.UniqueVertexGraph;
import java.util.HashMap;
import java.util.Set;

import salvo.jesus.graph.WeightedEdgeImpl;
import salvo.jesus.graph.Vertex;

/**
 * This class implements subgraph isomorphism testing
 * for n-gram graphs of documents. Subgraph isomorphism
 * testing for DocumentNGramGraphs can be done in polynomial 
 * time, since NGGs are implemented using unique vertex graphs
 * and, therefore, labels are unique.
 *
 * @author VHarisop
 */
public class NggIsomorphismTester {
	/* reimplementation of the max function */
	private static final int max(int numA, int numB) {
		return (numA > numB) ? numA : numB;
	}

	/* reimplementation of the min function */
	private static final int min(int numA, int numB) {
		return (numA > numB) ? numB : numA;
	}


	public static boolean subgraphIsomorphic(DocumentNGramGraph dngA, 
											 DocumentNGramGraph dngB)
	{
		/* get the maximum of the 2 graphs' minimum degrees
		   and the minimum of their maximum degrees */
		int dMin = max(dngA.getMinSize(), dngB.getMinSize());
		int dMax = min(dngA.getMaxSize(), dngB.getMaxSize());

		/* if dMin > dMax, then they have no common sizes
		   and therefore cannot be isomorphic */
		if (dMax < dMin) { return false; }

		/* check every degree for isomorphism and return
		/  false if no isomorphism is asserted at some degree */
		for (int tmp = dMin; tmp <= dMax; ++tmp) {
			if (!subgraphAux(
				dngA.getGraphLevelByNGramSize(tmp),
				dngB.getGraphLevelByNGramSize(tmp)))
				return false; 
		}

		/* if every degree was isomorphic, return True */
		return true;


	}

	/**
	 * Auxiliary function that checks subgraph isomorphism
	 * for 2 unique vertex graphs. It should only be called from inside
	 * {@link subgraphIsomorphic(DocumentNGramGraph, DocumentNGramGraph)}
	 * when checking two NGGs for subgraph isomorphism.
	 * @param uvgA the first unique vertex graph
	 * @param uvgB the second unique vertex graph
	 * @return true only if uvgA is a subgraph of and isomorphic to uvgB
	 */
	protected static boolean subgraphAux(UniqueVertexGraph uvgA, 
										 UniqueVertexGraph uvgB)
	{
		/* get vertex maps for both graphs */
		HashMap<String, Vertex> vcmA = uvgA.UniqueVertices;
		HashMap<String, Vertex> vcmB = uvgB.UniqueVertices;

		/* if the vertex map of uvgA is larger, it cannot be a subgraph */
		if (vcmA.size() > vcmB.size()) { return false; }

		/* TODO: maybe key checking should be removed? */

		/* iterate over all string keys and if
		 * some key is not found, return false */
		for (String key: vcmA.keySet()) {
			if (!vcmB.containsKey(key)) { return false; }
		}

		/* get the edge set of both graphs */
		Set<WeightedEdgeImpl> edgsa = uvgA.getEdgeSet();
		Set<WeightedEdgeImpl> edgsb = uvgB.getEdgeSet();
	
		for (WeightedEdgeImpl e: edgsa) {
			if (!edgsb.contains(e)) { return false; }
		}

		/* if all checks succeeded, return true */
		return true;
	}
}
