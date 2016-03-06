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
import gr.demokritos.biographs.indexing.preprocessing.*;
import gr.demokritos.biographs.indexing.structs.GraphIndexEntry;

/**
 * A class that implements an inverted index that maps hash vector values
 * to the graphs that contain them, using a vertex's in degree for encoding.
 *
 * @author VHarisop
 */
public class EntryInvertedIndex extends GraphDatabase {
	/**
	 * A hashmap that matches string to Tree maps that contain integer to
	 * biograph list pairs. The integer keys are frequency counts and count
	 * how many times the hashmap's key (vertex label letter) has been seen in
	 * which graph.
	 */
	protected HashMap<Integer, EntryFreqTree> invIndex;

	/**
	 * The {@link IndexVector} used internally by this database to find
	 * graph indexes.
	 */
	protected IndexVector indVec;

	/**
	 * Creates a blank EntryInvertedIndex object.
	 */
	public EntryInvertedIndex() { 
		super();
		initIndex();
	}

	/**
	 * Creates a new EntryInvertedIndex object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public EntryInvertedIndex(String path) {
		super(path);
		initIndex();
	}

	/**
	 * Initialize the inverted index.
	 */
	protected void initIndex() {
		invIndex = new HashMap<Integer, EntryFreqTree>();

		/* create the default index vector for DNA data */
		indVec = new IndexVector(GraphType.DNA);
		indVec.setHashStrategy(Strategies.dnaHash());
		indVec.setBins(16);
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * for graphs that encode a specified data type.
	 *
	 * @param path the file or directory to read the data from
	 * @param gType the type of data contained in the graphs
	 */
	public void build(File path, GraphType gType) throws Exception {
		this.type = gType;
		buildIndex(path);
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
	 *
	 * @param bg the {@link BioGraph} to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		addEntry(new GraphIndexEntry(bg, indVec));
	}

	/**
	 * Adds a new entry to the database.
	 * 
	 * @param entry the {@link GraphIndexEntry} to be added
	 */
	public void addEntry(GraphIndexEntry entry) {
		/* Don't forget to update our size! */
		this.size++;
		/**
		 * <i>METHOD</i>:
		 * 1 - get index hash encoding of the graph
		 * 2 - for every index in the encoding, associate the graph with
		 * the EntryFreqTree that corresponds to the index's encoding value.
		 */
		int[] vecEnc = entry.getEncoding();
		for (int i = 0; i < vecEnc.length; ++i) {
			int hVal = vecEnc[i];
			EntryFreqTree vTree;
			if (!(invIndex.containsKey(i))) {
				vTree = new EntryFreqTree();
				/* associate the encoding value of the vector at the
				 * current index with this graph and let EntryFreqTree handle
				 * the additions */
				vTree.addGraph(hVal, entry);
			}
			else {
				/* pick up the already existing EntryFreqTree and add the graph
				 * to the list of graphs already associated with that value
				 * */
				vTree = invIndex.get(i);
				vTree.addGraph(hVal, entry);
			}
			/* finally, add the new or modified tree to the inverted index */
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
	public Set<Map.Entry<Integer, EntryFreqTree>> exposeEntries() {
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
		for (EntryFreqTree eTree: invIndex.values()) {
			bins[iCnt++] = eTree.size();
		}
		return bins;
	}

	/**
	 * Returns a set of graphs entries whose index vectors are an
	 * exact match with the index vector of a query graph. If the 
	 * graph was originally part of the database, it is guaranteed
	 * to return a non-empty set of results containing the original
	 * graph - otherwise, the result may be null.
	 * This method is the <i>fastest</i> way to retrieve a graph
	 * for which containment is certain.
	 *
	 * @param bG the query graph
	 * @return a set of {@link GraphIndexEntry} objects whose index
	 * vectors match exactly the query's index vector
	 */
	public Set<GraphIndexEntry> getExactMatches(BioGraph bG) {
		int epsilon = 0;
		int[] vecEnc = indVec.encodeGraph(bG);

		/* initial set of results and a flag indicating if
		 * it has been initialised or not */
		Set<GraphIndexEntry> soFar = null;
		boolean unset = true;

		for (int i = 0; i < vecEnc.length; ++i) {
			EntryFreqTree vTree = invIndex.get(i);
			/* should not happen unless the graph was not
			 * originally part of the database */
			if (null == vTree || vTree.size() == 0)
				continue;

			/* initialize result set */
			if (unset) {
				soFar = vTree.getFreq(vecEnc[i], epsilon);
				unset = false;
				continue;
			}
			else {
				/* if graph belongs to the database, this intersection
				 * is guaranteed to be non-empty */
				soFar.retainAll(vTree.getFreq(vecEnc[i], epsilon));

				/* if, at some point, the intersection result is empty,
				 * it means that the graph was not originally part of
				 * the database - therefore, we return null */
				if (soFar.size() == 0)
					return null;
			}
		}
		return soFar;
	}

	/**
	 * Gets the matches of a query graph with a specified tolerance to
	 * containment frequences.
	 *
	 * @param bG the query graph
	 * @param tolerance the containment tolerance
	 * @return a set of matching graph entries, or null if none exist
	 */
	public Set<GraphIndexEntry> getMatches(BioGraph bG, int tolerance) {
		/**
		 * <i>METHOD</i>:
		 * 1 - get the query graph's int hash encoding
		 * 2 - use this vector as a key to look up containing graphs in the
		 *     index's corresponding EntryFreqTree, with tolerance (epsilon)
		 *     equal to the n-gram size used in the graph - if the index's value
		 *     has no associated EntryFreqTree, simply go to next vertex
		 * 3 - take the intersection of all lists retrieved in step 2
		 * 4 - return this list as an answer
		 */
		int epsilon = bG.getWindowSize() + tolerance;
		int[] vecEnc = indVec.encodeGraph(bG);

		/* initial set of results and a flag indicating if it
		 * has been initialized or not */
		Set<GraphIndexEntry> soFar = null;
		boolean unset = true; 

		/* Lookup lists of containments for all encoding values and intersect them
		 * step by step. The initial list is unset */
		for (int i = 0; i < vecEnc.length; ++i) {
			EntryFreqTree vTree = invIndex.get(i);

			/* if no EntryFreqTree exists for this vertex, it must be a newly
			 * encountered vertex - skip intersection phase! */
			if (vTree == null || vTree.size() == 0)
				continue;

			/* get indegree of vertex and containments using
			 * epsilon equal to the window size + tolerance */
			int vWeight = vecEnc[i];

			/* if set of results is unset, initialize now
			 * and skip to next iteration */
			if (unset) {
				soFar = vTree.getFreq(vWeight, epsilon);
				unset = false;
				continue;
			}
			else {
				/* compute the intersection of the sets */
				soFar.retainAll(vTree.getFreq(vWeight, epsilon));
				
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
	public Set<GraphIndexEntry> getMatches(BioGraph bQuery) {
		return getMatches(bQuery, 0);
	}
}
