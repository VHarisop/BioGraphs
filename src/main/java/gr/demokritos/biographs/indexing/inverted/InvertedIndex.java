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
import gr.demokritos.biographs.indexing.*;

/* JInsect imports */
import gr.demokritos.iit.jinsect.structs.*;

/**
 * An abstract class that implements a graph database using graph similarity.
 * Here, the similarity measure used is the graphs' structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.jutils}
 *
 * @author VHarisop
 */
public class InvertedIndex extends GraphDatabase {

	/**
	 * A hashmap that matches vertices to Tree maps that contain integer to
	 * biograph list pairs. The integer keys are frequency counts and count
	 * how many times the hashmap's key (vertex) has been seen in which graph.
	 */
	protected HashMap<JVertex, FreqTree> invIndex;

	/**
	 * Creates a blank InvertedIndex object.
	 */
	public InvertedIndex() { 
		super();
		initIndex();
	}

	/**
	 * Creates a new InvertedIndex object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public InvertedIndex(String path) {
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
		UniqueVertexGraph uvG = bg.getGraph();
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
		// throw new UnsupportedOperationException("Not implemented!");

		/**
		 * <i>METHOD</i>:
		 * 1 - for each of the query graph's vertices, get inWeightSum
		 * 2 - use this sum as a key to look up containing graphs in the
		 *     vertex's corresponding FreqTree, with tolerance (epsilon)
		 *     equal to the n-gram size used in the graph - if the vertex
		 *     has no associated FreqTree, simply go to next vertex
		 * 3 - take the intersection of all lists retrieved in step 2
		 * 4 - return this list as an answer
		 */
		UniqueVertexGraph uvG = bG.getGraph();
		int epsilon = bG.getWindowSize() + tolerance;

		/* initial set of results and a flag indicating if it
		 * has been initialized or not */
		Set<BioGraph> soFar = null;
		boolean unset = true; 

		/* Lookup lists of containments for all vertices and intersect them
		 * step by step. The initial list is unset */
		for (JVertex v: uvG.vertexSet()) {
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
	 * @see #getMatches(BioGraph, int) getMatches
	 */
	public Set<BioGraph> getMatches(BioGraph bQuery) {
		return getMatches(bQuery, 0);
	}
}
