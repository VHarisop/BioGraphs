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

package gr.demokritos.biographs.indexing.inverted;

import java.io.File;
import java.io.FileFilter;

import java.util.*;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.Utils;
import gr.demokritos.biographs.indexing.*;
/* JInsect imports */
import gr.demokritos.iit.jinsect.structs.*;

/**
 * A class that uses randomized vector lookup in an inverted index to pick the
 * nearest neighbour for query graphs.
 *
 * @author VHarisop
 */
public class RandomInvertedIndex extends GraphDatabase {

	/**
	 * A hashmap that matches vertices to Tree maps that contain integer to
	 * biograph list pairs. The integer keys are frequency counts and count
	 * how many times the hashmap's key (vertex) has been seen in which graph.
	 */
	protected HashMap<JVertex, FreqTree> invIndex;

	/**
	 * Creates a blank RandomInvertedIndex object.
	 */
	public RandomInvertedIndex() { 
		super();
		initIndex();
	}

	/**
	 * Creates a new RandomInvertedIndex object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public RandomInvertedIndex(String path) {
		super(path);
		initIndex();
	}

	/**
	 * Initialize the inverted index.
	 */
	protected void initIndex() {
		invIndex = new HashMap<JVertex, FreqTree>();
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	@Override
	public void buildIndex(String path) throws Exception {
		File fPath = new File(path);
		buildIndex(fPath);
	}

	/**
	 * Builds a graph database index from a given file or a directory 
	 * of files.
	 *
	 * @param fPath a path containing one or multiple files
	 */
	@Override
	public void buildIndex(File fPath) throws Exception {
		if (!fPath.isDirectory()) {
			addAllGraphs(BioGraph.fastaFileToGraphs(fPath));
		}
		else {
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			// add them all to the database
			for (File f: fileList) {
				addAllGraphs(BioGraph.fastaFileToGraphs(f));
			}
		}
	}

	/**
	 * Builds a graph database index from a given file or directory 
	 * of files which contain words without extra labels, as in the
	 * case of FASTA files.
	 *
	 * @param fPath a path pointing to a file or directory 
	 */
	public void buildWordIndex(File fPath) throws Exception {
		if (!fPath.isDirectory()) {
			addAllGraphs(BioGraph.fromWordFile(fPath));
		}
		else {
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			// add them all to the database
			for (File f: fileList) {
				addAllGraphs(BioGraph.fromWordFile(f));
			}
		}
	}

	/**
	 * Helper function that adds an array {@link BioGraph} objects
	 * to the database's index.
	 *
	 * @see #addGraph(BioGraph) addGraph
	 */
	private void addAllGraphs(BioGraph[] bgs) {
		for (BioGraph bg: bgs) {
			bg.computeHashEncoding(true, 10);
			addGraph(bg);
		}
	}

	/**
	 * Adds a new graph to the database, updating the inverted index.
	 * For each vertex in the graph, this graph is added as an entry to
	 * the list of graphs associated with the vertex's incoming weight
	 * sum.
	 * 
	 * @param bg the BioGraph object to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		/*
		 * 1. get set of vertices, and for each one:
		 * 1b. add the graph to the FreqTree, associated with the vertex's
		 * incoming weight sum
		 * 
		 */
		UniqueJVertexGraph uvG = bg.getGraph();
		for (JVertex v: uvG.vertexSet()) {
			FreqTree vTree;
			
			/* get the vertex's incoming weight sum */
			int vInWeight = (int) uvG.incomingWeightSumOf(v);

			/* if FreqTree for this vertex didn't exist, create it */
			if (!(invIndex.containsKey(v))) {
				vTree = new FreqTree();
				/* associate the inweight of the vertex with this graph
				 * and let FreqTree handle the additions */
				vTree.addGraph(vInWeight, bg);
				invIndex.put(v, vTree);
			} 
			else {
				/* pickup already existing FreqTree and add the graph
				 * to list of graphs associated with vInWeight */
				vTree = invIndex.get(v);
				vTree.addGraph(vInWeight, bg);
				invIndex.put(v, vTree);
			}
		}
	}

	/**
	 * Gets the keys of the invertedIndex.
	 * 
	 * @return a set containing all the keys of the map
	 */
	public Set<JVertex> exposeKeys() {
		return invIndex.keySet();
	}

	/**
	 * Gets the key-value pairs of the inverted index.
	 *
	 * @return a set containing all of the entries of the map
	 */
	public Set<Map.Entry<JVertex, FreqTree>> exposeEntries() {
		return invIndex.entrySet();
	}

	/**
	 * Returns an array containing the size of frequency tree for
	 * each of the vertices..
	 *
	 * @return an array of list sizes corresponding to the sizes
	 * of each freq tree.
	 */
	public int[] binSizes() {
		int[] bins = new int[invIndex.size()];
		int iCnt = 0;
		for (Map.Entry<JVertex, FreqTree> ent: invIndex.entrySet()) {
			bins[iCnt++] = ent.getValue().size();
		}

		return bins;
	}

	/**
	 * Gets the matches of a biograph with a specified tolerance to
	 * containment frequences.
	 *
	 * @param bG the query graph
	 * @param tolerance the containment tolerance
	 * @return a set of matching graphs, or null if none exist
	 */
	public Set<BioGraph> getMatches(BioGraph bG, int tolerance) {
		/**
		 * <i>METHOD</i>:
		 * 1 - for each of the query graph's vertices, get inWeightSum
		 * 2 - use this sum as a key to look up containing graphs in the
		 *     vertex's corresponding FreqTree, with tolerance (epsilon)
		 *     equal to the n-gram size used in the graph - if the vertex
		 *     has no associated FreqTree, simply go to next vertex
		 * 3 - randomize the order of the vertices by which the FreqTree is
		 *     looked up
		 * 4 - take the intersection of a chunk of 3 or 4 random vertices,
		 *     and look for the graph closest to the query graph in the
		 *     resulting set
		 * 5 - return the graph as an answer
		 */
		UniqueJVertexGraph uvG = bG.getGraph();
		int epsilon = bG.getWindowSize() + tolerance;

		/* initial set of results */
		Set<BioGraph> results = new HashSet<BioGraph>();

		/* Lookup lists of containments for all vertices and intersect them
		 * step by step. The initial list is unset */
		List<JVertex> vertices = new ArrayList<JVertex>();
		for (JVertex v: uvG.vertexSet()) {
			vertices.add(v);
		}

		/* shuffle and pick chunks of the vertices */
		Collections.shuffle(vertices);
		int vSize = vertices.size();
		int from, to, subSize = 3;

		/** 
		 * <i>METHOD</i>:
		 * perform 4 runs of intersection-and-nearest-neighbour searches,
		 * merging the resulting sets and returning them as nearest matches
		 * to the graph query. The size of the sets must be kept small, as
		 * larger sizes may lead to more intersections and loss in accuracy.
		 */
		for (int i = 0; i < 4; ++i) {
			from = subSize * i;
			to = Math.min(from + subSize, vSize);
			List<JVertex> keys = vertices.subList(from, to);
			results.addAll(intersectAndFind(bG, keys, epsilon));
		}
		return results;

	}

	/**
	 * Given a collection of vertices that can be looked up in the inverted
	 * index, computes the resulting set from intersecting the lookup results
	 * from all the vertices, and among that set, computes the closest graph
	 * in terms of hash vector distance to a query graph.
	 *
	 * @param bQuery the query graph
	 * @param vertices the vertices to be used as keys
	 * @param epsilon the search index frequency tolerance
	 * @return a single-element set that contains the biograph closest
	 * to the query graph, in terms of hash vector distance
	 */
	private Set<BioGraph> intersectAndFind
	(BioGraph bQuery, Collection<JVertex> vertices, int epsilon) 
	{
		/* get the underlying unique vertex graph of the query graph */
		UniqueJVertexGraph uvG = bQuery.getGraph();
		int currIndex = 0,
			sizeMax = vertices.size() - 1;

		/* initialize parameters required to compute the intersection
		 * and find the closest graph
		 */
		Set<BioGraph> soFar = null;
		BioGraph bgFound = null;

		/* flag indicating if the set of results has been initialized */
		boolean unset = true;

		for (JVertex v: vertices) {
			currIndex++;

			/* get matching freq tree to the vertex */
			FreqTree vTree = invIndex.get(v);

			/* if no FreqTree exists for this vertex, it must be a newly
			 * encountered vertex - skip intersection phase! */
			if (vTree == null || vTree.size() == 0)
				continue;

			/* get incoming weight of vertex, get containments with epsilon
			 * equal to the window size */
			int vWeight = (int) uvG.incomingWeightSumOf(v);
			Set<BioGraph> contain = 
				vTree.getFreq(vWeight, epsilon);

			/* if set of results is unset, initialize now
			 * and skip to next iteration */
			if (unset) {
				soFar = contain;
				unset = false;
				continue;
			}
			else {
				/* compute the intersection of the sets - use a temporary
				 * copy to return last valid set if at some point the result
				 * is null */
				Set<BioGraph> backup = new HashSet<BioGraph>(soFar);

				/* if I have performed enough intersections, compute the
				 * minimum graph to return to the caller */
				if (currIndex > sizeMax) {
					bgFound = getClosest(bQuery, backup);
					Set<BioGraph> toRet = new HashSet<BioGraph>(); 
					toRet.add(bgFound);
					return toRet; 
				}
				/* otherwise, go ahead with the intersections */
				backup.retainAll(contain);
				if (backup.size() == 0) {
					return soFar;
				}
				else {
					soFar = new HashSet<BioGraph>(backup);
				}

				/* if, at some point, result set is empty, skip next iteration
				 * and return the result which is null itself */
				if (soFar.size() == 0) {
					return null;
				}
			}
		}
		return soFar;
	}

	/**
	 * Finds the closest, in terms of hash vector hamming distance, graph
	 * to a query BioGraph from a set of candidates.
	 *
	 * @param bQuery the query graph
	 * @param candidates the set of graphs to pick the nearest neighbour from
	 * @return the nearest biograph from the candidate set
	 */
	private BioGraph getClosest(BioGraph bQuery, Set<BioGraph> candidates) {
		/* initialize query vector and minimum distance / graph */
		double[] vQuery = bQuery.getHashEncoding(true, 10);
		double mDist = Double.MAX_VALUE; BioGraph bgMin = null;
		
		/* loop over all candidates to find minimum hamming distance */
		for (BioGraph b: candidates) {
			double dist = Double.MAX_VALUE;
			try {
				dist = Utils.getHammingDistance(
						b.getHashEncoding(true, 10),
						vQuery);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (dist < mDist) {
				mDist = dist;
				bgMin = b;
			}
		}
		return bgMin;
	}

	/**
	 * @see #getMatches(BioGraph, int) getMatches
	 */
	public Set<BioGraph> getMatches(BioGraph bQuery) {
		return getMatches(bQuery, 1);
	}
}
