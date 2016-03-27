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
import org.jgrapht.*;
import org.jgrapht.alg.interfaces.*;
import org.jgrapht.alg.util.*;

import gr.demokritos.biographs.*;
import gr.demokritos.iit.jinsect.structs.*;

/**
 * A class that represents a {@link BioGraph} object using its {@link #K}
 * "heaviest" edges and vertices with the largest incoming weight sum.
 *
 * @author VHarisop
 */
public final class MaximalStructure {
	/**
	 * The set of maximal edges.
	 */
	private Collection<Edge> edgeList;

	/**
	 * The set of maximal vertices.
	 */
	private Collection<JVertex> vertexList;

	/**
	 * The number of maximal edges/vertices to maintain,
	 * by default 5.
	 */
	protected int K;
	
	/**
	 * Creates a new {@link MaximalStructure} object for a specified graph
	 * using a given number of edges and vertices to be included in its
	 * representation.
	 *
	 * @param bg the {@link BioGraph} to build the tree for
	 * @param K the number of edges/vertices to be included
	 */
	public MaximalStructure(BioGraph bg, int K) {
		this.K = K;
		this(bg);
	}

	/**
	 * Creates a new {@link MaximalStructure} object for a specified graph
	 * using the default number of edges and vertices to be included in its
	 * representation.
	 *
	 * @param bg the {@link BioGraph} to build the tree for
	 */
	public MaximalStructure(BioGraph bg) {
		/* get all the edges of the graph */
		ArrayList<Edge> allEdges = 
			new ArrayList<Edge>(bg.getGraph().edgeSet());

		/* get all the vertices of the graph */
		ArrayList<JVertex> allVertices =
			new ArrayList<JVertex>(bg.getGraph().vertexSet());

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

		UniqueJVertexGraph uvg = bg.getGraph();

		/* sort the vertices in descending order of incoming weight */
		Collections.sort(
				allVertices,
				new Comparator<JVertex>() {
					@Override
					public int compare(JVertex vA, JVertex vB) {
						double wA = uvg.incomingWeightSumOf(vA);
						double wB = uvg.incomingWeightSumOf(vB);
						return Double.valueOf(wB).compareTo(wA);
					}
				});

		edgeList = new LinkedHashSet<Edge>(K);
		vertexList = new LinkedHashSet<JVertex>(K);
		for (int i = 0; i < 5; ++i) {
			edgeList.add(allEdges.get(i));
			vertexList.add(allVertices.get(i));
		}
	}

	/**
	 * Returns the set of edges that belong to the maximum spanning tree.
	 * 
	 * @return the maximum spanning tree's edge set
	 */
	public Collection<Edge> getMaximalEdges() {
		return edgeList;
	}

	public Collection<JVertex> getMaximalVertices() {
		return vertexList;
	}
}
