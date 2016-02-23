/* Copyright (C) 2016 VHarisop
 * This file is part of BioGraphs.
 *
 * BioGraphs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BioGraphs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BioGraphs.  If not, see <http://www.gnu.org/licenses/>. */

package gr.demokritos.biographs;

import gr.demokritos.iit.jinsect.representations.NGramJGraph;
import gr.demokritos.iit.jinsect.structs.*;

import java.util.HashMap;
import java.util.Set;
import java.lang.Math;

/* TODO: 
 * 1) expand IsomorphismTester to use caching.
 */

/**
 * This class implements (sub-)graph isomorphism testing
 * for n-gram graphs of documents. (Sub-)graph isomorphism
 * testing for NGramJGraphs can be done in polynomial 
 * time, since NGGs are implemented using unique vertex graphs
 * and, therefore, labels are unique.
 *
 * @author VHarisop
 */
public class IsomorphismTester {
	/* reimplementation of the max function */
	public static final int max(int numA, int numB) {
		return (numA > numB) ? numA : numB;
	}

	/* reimplementation of the min function */
	public static final int min(int numA, int numB) {
		return (numA > numB) ? numB : numA;
	}

	/* compare two doubles with respect to precision */
	public static boolean compareDouble(Double a, Double b) {
		return (Math.abs(a - b) < 0.000001);
	}

	/**
	 * Checks two {@link NGramJGraph}s for subgraph isomorphism. 
	 * Checking is done based on  vertex labels, meaning that edges 
	 * of different weights that connect two vertices with the same 
	 * labels are considered compatible for isomorphism.
	 * @param dngA the first graph
	 * @param dngB the second graph
	 * @return true if dngA is subgraph isomorphic to dngB
	 */
	public static boolean subgraphIsomorphic
	(NGramJGraph dngA, NGramJGraph dngB)
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
	 * Checks two {@link NGramJGraph}s for graph isomorphism.
	 * Two graphs will be considered isomorphic if they share the same
	 * set of vertices and edges and their edge weights differ no more than
	 * a very small numerical threshold.
	 *
	 * @param dngA the first graph
	 * @param dngB the second graph
	 * @return true if the graphs are isomorphic, else false
	 */
	public static boolean graphIsomorphic
	(NGramJGraph dngA, NGramJGraph dngB)
	{
		/* check if the degree ranges of the 2 graphs
		 * are compatible */
		int dMin = max(dngA.getMinSize(), dngB.getMinSize());
		int dMax = min(dngA.getMaxSize(), dngB.getMaxSize());

		/* if dMin > dMax, they can't be isomorphic */
		if (dMax < dMin) { return false; }

		/* check graphs of every compatible degree for
		 * exact isomorphism, break if non isomorphic */
		for (int tmp = dMin; tmp <= dMax; ++tmp) {
			if (!graphAux(
				dngA.getGraphLevelByNGramSize(tmp),
				dngB.getGraphLevelByNGramSize(tmp)))
				return false;
		}

		/* return true if every degree was isomorphic */
		return true;
	}

	/**
	 * Auxiliary function that checks graph isomorphism
	 * for 2 unique vertex graphs. It should only be called from inside
	 * {@link #graphIsomorphic(NGramJGraph, NGramJGraph)}
	 * when checking two NGGs for subgraph isomorphism.
	 * @param uvgA the first unique vertex graph
	 * @param uvgB the second unique vertex graph
	 * @return true only if uvgA is graph isomorphic to uvgB
	 */
	protected static boolean graphAux
	(UniqueVertexGraph uvgA, UniqueVertexGraph uvgB)
	{
		/* get the Edge maps of both graphs */
		Set<Edge> edgA = uvgA.edgeSet();
		Set<Edge> edgB = uvgB.edgeSet();

		double wOther;

		/* if the edge maps differ in size, 
		 * they cannot be isomorphic */
		if (edgA.size() != edgB.size()) { return false; }

		for (Edge e: edgA) {

			// if edge is not present in the other graph, return false 
			if (!(edgB.contains(e))) {
				return false;
			}
			
			// otherwise, get the edge weight in uvgB
			wOther = uvgB.getEdgeWeight(e);

			/* if edge is present but weight differs,
			 * return false as well */
			if (!compareDouble(e.edgeWeight(), wOther))
				return false;
		}

		/* if all checks succeed, return true */
		return true;
	}

	/**
	 * Auxiliary function that checks subgraph isomorphism
	 * for 2 unique vertex graphs. It should only be called from inside
	 * {@link #subgraphIsomorphic(NGramJGraph, NGramJGraph)}
	 * when checking two NGGs for subgraph isomorphism.
	 * @param uvgA the first unique vertex graph
	 * @param uvgB the second unique vertex graph
	 * @return true only if uvgA is a subgraph of and isomorphic to uvgB
	 */
	protected static boolean subgraphAux
	(UniqueVertexGraph uvgA, UniqueVertexGraph uvgB)
	{
		/* get vertex maps for both graphs */
		HashMap<String, JVertex> vcmA = uvgA.UniqueVertices;
		HashMap<String, JVertex> vcmB = uvgB.UniqueVertices;

		/* if the vertex map of uvgA is larger, it cannot be a subgraph */
		if (vcmA.size() > vcmB.size()) { return false; }

		/* TODO: maybe key checking should be removed? */

		/* iterate over all string keys and if
		 * some key is not found, return false */
		for (String key: vcmA.keySet()) {
			if (!vcmB.containsKey(key)) { return false; }
		}

		/* get the edge set of both graphs */
		Set<Edge> edgsa = uvgA.edgeSet();
		Set<Edge> edgsb = uvgB.edgeSet();
	
		if (edgsb.containsAll(edgsa)) {
			return true;
		}
		else {
			return false;
		}
	}
}
