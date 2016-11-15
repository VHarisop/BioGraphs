/* This file is part of BioGraphs.
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

package gr.demokritos.biographs.algorithms;

import java.util.*;
import org.jgrapht.alg.util.*;

import gr.demokritos.biographs.*;
import gr.demokritos.iit.jinsect.structs.*;

/**
 * A class that finds the maximum spanning tree for a {@link BioGraph}
 * using the Kruskal algorithm on the graph's edges, sorted by reverse
 * order of weights.
 *
 * @author VHarisop
 */
public final class MaximumSpanningTree {
	/**
	 * The total cost of the spanning tree.
	 */
	protected double treeCost;

	/**
	 * The set of included edges.
	 */
	private Set<Edge> edgeList;

	/**
	 * Creates a new maximum spanning tree using the Kruskal algorithm
	 * on edges sorted by descending weight order.
	 *
	 * @param bg the {@link BioGraph} to build the tree for
	 */
	public MaximumSpanningTree(BioGraph bg) {
		UnionFind<JVertex> forest = 
			new UnionFind<JVertex>(bg.getGraph().vertexSet());

		/* get all the edges of the graph */
		ArrayList<Edge> allEdges = 
			new ArrayList<Edge>(bg.getGraph().edgeSet());

		/* sort the edges in reverse (descending) order of weight */
		Collections.sort(
				allEdges,
				new Comparator<Edge>() {
					@Override
					public int compare(Edge eA, Edge eB) {
						return Double.valueOf(eB.edgeWeight()).compareTo(
								eB.edgeWeight());
					}
				});

		treeCost = 0.0;
		edgeList = new HashSet<Edge>();
		int edge_count = 0;
		for (Edge e: allEdges) {
			JVertex vS = bg.getGraph().getEdgeSource(e);
			JVertex vT = bg.getGraph().getEdgeTarget(e);
			/* skip union if the two vertices are already
			 * in the same connected component */
			if (forest.find(vS).equals(forest.find(vT)))
			{
				continue;
			}
			forest.union(vS, vT);
			edgeList.add(e);
			treeCost += e.edgeWeight();

			// allow a maximum of 2 edges
			if (1 <= edge_count) {
				break;
			}
			edge_count++;
		}
	}

	/**
	 * Returns the set of edges that belong to the maximum spanning tree.
	 * 
	 * @return the maximum spanning tree's edge set
	 */
	public Set<Edge> getTreeEdges() {
		return edgeList;
	}

	/**
	 * Returns the total weight of the maximum spanning tree.
	 *
	 * @return the maximum spanning tree's weight
	 */
	public double getTreeWeight() {
		return treeCost;
	}
}
