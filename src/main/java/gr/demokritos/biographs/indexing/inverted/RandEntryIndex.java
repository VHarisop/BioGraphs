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
import gr.demokritos.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.biographs.indexing.preprocessing.*;
import gr.demokritos.biographs.indexing.structs.*;

/**
 * A class that uses randomized vector lookup in an inverted index to pick the
 * nearest neighbour for query graphs.
 *
 * @author VHarisop
 */
public class RandEntryIndex extends GraphDatabase {

	/**
	 * A hashmap that matches vertices to Tree maps that contain integer to
	 * biograph list pairs. The integer keys are frequency counts and count
	 * how many times the hashmap's key (vertex) has been seen in which graph.
	 */
	protected HashMap<Integer, FreqTree<GraphIndexEntry>> invIndex;

	/**
	 * The {@link IndexVector} used internally by this database to hash
	 * added graphs' vertices.
	 */
	protected IndexVector indVec;

	/**
	 * Creates a blank RandEntryIndex object.
	 */
	public RandEntryIndex() { 
		super();
		initIndex();
	}

	/**
	 * Creates a new RandEntryIndex object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public RandEntryIndex(String path) {
		super(path);
		initIndex();
	}

	/**
	 * Initialize the inverted index.
	 */
	protected void initIndex() {
		invIndex = new HashMap<Integer, FreqTree<GraphIndexEntry>>();

		/* create the default index vector for DNA data */
		indVec = new IndexVector(GraphType.DNA);
		indVec.setHashStrategy(Strategies.dnaHash());
		indVec.setBins(16);
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
			if (type == GraphType.DNA) {
				for (GraphIndexEntry e: Utils.fastaFileToEntries(fPath, indVec))
				{
					addEntry(e);
				}
			}
			else {
				for (GraphIndexEntry e: Utils.wordFileToEntries(fPath, indVec))
				{
					addEntry(e);
				}
			}
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
				if (type == GraphType.DNA) {
					for (GraphIndexEntry e: Utils.fastaFileToEntries(f, indVec))
					{
						addEntry(e);
					}
				}
				else {
					for (GraphIndexEntry e: Utils.wordFileToEntries(f, indVec))
					{
						addEntry(e);
					}
				}
			}
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
		addEntry(new GraphIndexEntry(bg, indVec));
	}

	/**
	 * Adds a new graph entry to the database, updating the inverted index.
	 * @param entry the graph entry to add
	 */
	public void addEntry(GraphIndexEntry entry) {
		/* Don't forget to update our size! */
		this.size++;
		/**
		 * <i>METHOD</i>:
		 * 1 - get index hash encoding of the graph
		 * 2 - for every index in the encoding, associate the graph with the
		 * FreqTree that corresponds to the index's encoding value.
		 */
		int[] vecEnc = entry.getEncoding();
		for (int i = 0; i < vecEnc.length; ++i) {
			FreqTree<GraphIndexEntry> vTree;
			if (!(invIndex.containsKey(i))) {
				vTree = new FreqTree<GraphIndexEntry>();
				vTree.addGraph(vecEnc[i], entry);
			}
			else {
				vTree = invIndex.get(i);
				vTree.addGraph(vecEnc[i], entry);
			}
			invIndex.put(i, vTree);
		}
	}

	/**
	 * Gets the keys of the invertedIndex.
	 * 
	 * @return a set containing all the keys of the map
	 */
	public Set<Integer> exposeKeys() {
		return invIndex.keySet();
	}

	/**
	 * Gets the key-value pairs of the inverted index.
	 *
	 * @return a set containing all of the entries of the map
	 */
	public Set<Map.Entry<Integer, FreqTree<GraphIndexEntry>>> exposeEntries() {
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
		for (FreqTree<GraphIndexEntry> eTree: invIndex.values()) {
			bins[iCnt++] = eTree.size();
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
	public Set<GraphIndexEntry> getMatches(BioGraph bG, int tolerance) {
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
		int epsilon = bG.getWindowSize() + tolerance;
		int[] vecEnc = indVec.encodeGraph(bG);

		/* initial set of results */
		Set<GraphIndexEntry> results = new HashSet<GraphIndexEntry>();

		/* Lookup lists of containments for all vertices and intersect them
		 * step by step. The initial list is unset */
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < vecEnc.length; ++i) {
			indices.add(i);
		}

		int iSize = indices.size();
		int from, to, subSize = 4;

		/** 
		 * <i>METHOD</i>:
		 * perform 4 runs of intersection-and-nearest-neighbour searches,
		 * merging the resulting sets and returning them as nearest matches
		 * to the graph query. The size of the sets must be kept small, as
		 * larger sizes may lead to more intersections and loss in accuracy.
		 */
		for (int i = 0; i < 4; ++i) {
			from = subSize * i;
			to = Math.min(from + subSize, iSize);
			List<Integer> keys = indices.subList(from, to);
			GraphIndexEntry eFound = intersectAndFind(vecEnc, keys, epsilon);
			if (null == eFound)
				continue;
			else
				results.add(eFound);
		}
		return results;
	}

	/**
	 * Given a collection of vertices that can be looked up in the inverted
	 * index, computes the resulting set from intersecting the lookup results
	 * from all the vertices, and among that set, computes the closest graph
	 * in terms of hash vector distance to a query graph.
	 *
	 * @param vecEnc the index vector of the query graph
	 * @param indices the indices to be used as keys
	 * @param epsilon the search index frequency tolerance
	 * @return a single element that contains the entry closest
	 * to the query graph, in terms of hash vector distance
	 */
	private GraphIndexEntry intersectAndFind
	(int[] vecEnc, Collection<Integer> indices, int epsilon) 
	{
		int currIndex = 0,
			sizeMax = indices.size() - 1;

		/* initialize parameters required to compute the intersection
		 * and find the closest graph
		 */
		Set<GraphIndexEntry> soFar = new HashSet<GraphIndexEntry>(1);
		GraphIndexEntry bgFound = null;

		/* flag indicating if the set of results has been initialized */
		boolean unset = true;

		for (int iCurr: indices) {
			currIndex++;

			/* get matching freq tree to the current index */
			FreqTree<GraphIndexEntry> vTree = invIndex.get(iCurr);

			/* if no FreqTree exists for this vertex, it must be a newly
			 * encountered vertex - skip intersection phase! */
			if (vTree == null || vTree.size() == 0)
				continue;

			/* get incoming weight of vertex, get containments with epsilon
			 * equal to the window size */
			int vWeight = vecEnc[iCurr];
			Set<GraphIndexEntry> contain = 
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
				Set<GraphIndexEntry> backup = 
					new HashSet<GraphIndexEntry>(soFar);

				/* if I have performed enough intersections, compute the
				 * minimum graph to return to the caller */
				if (currIndex > sizeMax) {
					return getClosest(vecEnc, backup);
				}
				/* otherwise, go ahead with the intersections */
				backup.retainAll(contain);
				if (backup.size() == 0) {
					return getClosest(vecEnc, soFar);
				}
				else {
					/* soFar becomes the result of the intersection */
					soFar = backup;
				}

				/* if, at some point, result set is empty, skip next iteration
				 * and return the result which is null itself */
				if (soFar.size() == 0) {
					return null;
				}
			}
		}
		return getClosest(vecEnc, soFar);
	}

	/**
	 * Finds the closest, in terms of hash vector hamming distance, graph
	 * to a query BioGraph from a set of candidates.
	 *
	 * @param bQuery the query graph
	 * @param candidates the set of graphs to pick the nearest neighbour from
	 * @return the nearest biograph from the candidate set
	 */
	private GraphIndexEntry
	getClosest(int[] vecEnc, Set<GraphIndexEntry> candidates) {
		/* initialize query vector and minimum distance / graph */
		int mDist = Integer.MAX_VALUE; GraphIndexEntry gieMin = null;
		
		/* loop over all candidates to find minimum hamming distance */
		for (GraphIndexEntry e: candidates) {
			int dist = ClusterDistance.hamming(vecEnc, e.getEncoding());
			if (dist < mDist) {
				mDist = dist;
				gieMin = e;
			}
		}
		return gieMin;
	}

	/**
	 * @see #getMatches(BioGraph, int) getMatches
	 */
	public Set<GraphIndexEntry> getMatches(BioGraph bQuery) {
		return getMatches(bQuery, 1);
	}
}
