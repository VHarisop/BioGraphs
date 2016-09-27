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

package gr.demokritos.biographs.indexing;

import java.io.File;
import java.io.FileFilter;

import java.util.*;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.Utils;
import gr.demokritos.biographs.io.BioInput;
import gr.demokritos.biographs.indexing.preprocessing.IndexVector;
import gr.demokritos.biographs.indexing.comparators.*;
import gr.demokritos.biographs.indexing.structs.*;
import gr.demokritos.biographs.indexing.distances.ClusterDistance;


/**
 * An abstract class that implements a graph database using graph similarity.
 * Here, the similarity measure used is the graphs' structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.jutils}
 *
 * @author VHarisop
 */
public abstract class TreeDatabase<V> extends GraphDatabase {

	/**
	 * A Red-Black tree map implementation that associates biographs
	 * with lists of value types, which can be any type of feature able
	 * to be extracted from a BioGraph. 
	 * @see #getGraphFeature
	 */
	protected TreeMap<BioGraph, List<V>> treeIndex;

	/**
	 * A custom comparator to be used for {@link #treeIndex} that
	 * compares graphs based on their s-similarity.
	 */
	protected Comparator<BioGraph> bgComp;

	/**
	 * A field indicating if the data of this database's graphs comes from
	 * words or biological sequences.
	 */
	protected GraphType type;

	/**
	 * Creates a blank TreeDatabase object.
	 */
	public TreeDatabase() { 
		super();
		initIndex();
	}

	/**
	 * Creates a new TreeDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public TreeDatabase(String path) {
		super(path);
		initIndex();
	}

	
	/**
	 * Creates a blank TreeDatabase object using a custom provided
	 * comparator.
	 * 
	 * @param bgC the custom comparator to be used
	 */
	public TreeDatabase(Comparator<BioGraph> bgC) {
		super();
		initIndex(bgC);
	}

	/**
	 * Creates a new TreeDatabase object for maintaining a database
	 * in a given directory using a custom comparator.
	 *
	 * @param path the directory in which the database resides
	 * @param bgC the custom comparator to be used
	 */
	public TreeDatabase(String path, Comparator<BioGraph> bgC) {
		super(path);
		initIndex(bgC);
	}
	
	/**
	 * Initialize the database's comparator and tree index.
	 */
	protected void initIndex() {
		this.bgComp = new OrdWeightComparator();
		this.treeIndex = new TreeMap<BioGraph, List<V>>(this.bgComp);
	}

	/**
	 * Initialize the database's tree index with a custom comparator.
	 *
	 * @param bgC the comparator to use
	 */
	protected void initIndex(Comparator<BioGraph> bgC) {
		this.bgComp = bgC;
		this.treeIndex = new TreeMap<BioGraph, List<V>>(this.bgComp);
	}

	/**
	 * Builds a graph database index from a given file or directory and
	 * a specified type of data source.
	 *
	 * @param path the file or directory to build from
	 * @param gType the {@link GraphType} of the graphs to be indexed
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
	 * Adds a set of data in a file into the database, reading them
	 * with a method depending on the graph type.
	 */
	private void addAllGraphs(File path) throws Exception {
		if (type == GraphType.DNA) {
			for (BioGraph bg: BioInput.fastaFileToGraphs(path)) {
				addGraph(bg);
			}
		}
		else {
			for (BioGraph bg: BioInput.fromWordFile(path)) {
				addGraph(bg);
			}
		}
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
			addAllGraphs(fPath);
		}
		else {
			/* get all files in a list, and then add all graphs for
			 * each file to the database
			 */
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});
			for (File f: fileList) {
				addAllGraphs(f);
			}
		}
	}

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioGraph object to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		List<V> nodeValues = treeIndex.get(bg);

		// if key was not there, initialize label array
		if (nodeValues == null) {
			nodeValues = new ArrayList<V>();
		}
		nodeValues.add(getGraphFeature(bg));
		treeIndex.put(bg, nodeValues);
	}

	/**
	 * Gets the keys of the underlying tree map of the database.
	 * 
	 * @return a set containing all the keys of the map
	 */
	public Set<BioGraph> exposeKeys() {
		return treeIndex.keySet();
	}

	/**
	 * Gets the key-value pairs of the underlying tree
	 * map of the database.
	 *
	 * @return a set containing all of the entries of the map
	 */
	public Set<Map.Entry<BioGraph, List<V>>> exposeEntries() {
		return treeIndex.entrySet();
	}

	/**
	 * Returns an array containing the size of the list at each key.
	 *
	 * @return an array of list sizes corresponding the the map's keys
	 */
	public int[] binSizes() {
		int[] bins = new int[treeIndex.size()];
		int iCnt = 0;
		for (Map.Entry<BioGraph, List<V>> ent: treeIndex.entrySet()) {
			bins[iCnt++] = ent.getValue().size();
		}

		return bins;
	}

	/**
	 * Gets the nodes corresponding to the biograph query, whose
	 * similarity to the query biojgraph is 0.
	 * @param bg the {@link BioGraph} key to be searched for
	 * @return a list of node values
	 */
	public List<V> getNodes(BioGraph bg) {
		return treeIndex.get(bg);
	}

	/**
	 * Gets the nodes corresponding to a list of query biographs,
	 * and returns them in an array of entries.
	 * @param bGraphs the {@link BioGraph} array of query graphs
	 * @return a list of Entries that map biographs to their resulting nodes
	 */
	public List<DatabaseEntry<BioGraph, List<V>>> getNodes(BioGraph[] bGraphs) {
		List<DatabaseEntry<BioGraph, List<V>>> results = 
			new ArrayList<DatabaseEntry<BioGraph, List<V>>>();
		int iCnt;
		for (iCnt = 0; iCnt < bGraphs.length; ++iCnt) {
			results.add(new DatabaseEntry<BioGraph, List<V>>(
						bGraphs[iCnt], getNodes(bGraphs[iCnt])));
		}

		return results;
	}

	/**
	 * Gets the node list corresponding to the nearest key to the query
	 * graph, including exact matches if an inclusive search is desired.
	 *
	 * @param bQuery the query graph
	 * @param include a flag indicating if exact matches should be included
	 * @return the unmodifiable node list of the nearest neighbouring key
	 */
	public List<V> getNearestNeighbours(BioGraph bQuery, boolean include) {
		if (include) {
			/* if an exact match exists, return it */
			List<V> matches = getNodes(bQuery);
			if (matches != null) {
				return Collections.unmodifiableList(matches);
			}
		}
		BioGraph lower = treeIndex.lowerKey(bQuery);
		BioGraph higher = treeIndex.higherKey(bQuery);

		if (lower == null && higher == null) {
			return null;
		}

		if (higher == null) 
			return Collections.unmodifiableList(getNodes(lower));
		
		if (lower == null)
			return Collections.unmodifiableList(getNodes(higher));
		
		/* get the distance of similarities of both the lower and
		 * higher keys */
		double distLo = getDistBetween(bQuery, lower);
		double distHi = getDistBetween(bQuery, higher);

		/* return the nodes of the "closest" distance, or both if the
		 * distances are equal */
		if (Utils.compareDouble(distLo, distHi)) {
			ArrayList<V> nodes = new ArrayList<V>();
			nodes.addAll(getNodes(lower));
			nodes.addAll(getNodes(higher));
			return Collections.unmodifiableList(nodes);
		}
		else if (distLo < distHi) {
			return Collections.unmodifiableList(getNodes(lower));
		}
		else /* if (distLo > distHi) */ {
			return Collections.unmodifiableList(getNodes(higher));
		}
	}

	/**
	 * Gets the node list corresponding to the K keys nearest to the query
	 * graph, including exact matches if an inclusive search is desired.
	 *
	 * @param bQuery the query graph
	 * @param include a flag indicating if exact matches should be included
	 * @param K the order of nearest neighbours
	 * @return the unmodifiable node list of the K nearest neighbouring keys
	 */
	public List<V> getKNearestNeighbours(BioGraph bQuery, boolean include, int K) {
		int retCnt = 0;
		ArrayList<V> nodes = new ArrayList<V>();

		// return up to min(size, K) neighbouring keys
		K = (K > treeIndex.size()) ? treeIndex.size() : K;

		if (include) {	
			/* if an exact match exists, add it 
			 * to the list of nodes to return */
			List<V> matches = getNodes(bQuery);
			if (matches != null && (matches.size() > 0)) {
				nodes.addAll(matches);
				retCnt += 1;
			}
		}

		if (retCnt == K) {
			return Collections.unmodifiableList(nodes);
		}

		/* get tail and head views. The head view should be accessed in reverse
		 * order, since it contains entries with keys "less" than the query */
		NavigableMap<BioGraph, List<V>> tail = 
			treeIndex.tailMap(bQuery, false);
		NavigableMap<BioGraph, List<V>> head = 
			treeIndex.headMap(bQuery, false);

		// lower values should be polled in reverse order
		Map.Entry<BioGraph, List<V>> high = tail.higherEntry(bQuery);
		Map.Entry<BioGraph, List<V>> low = head.lowerEntry(bQuery);
		double distLo, distHi;

		/* if stuff remains, loop */
		while (retCnt < K) {
		
			/* handle cases where either both maps have been depleted
			 * or one of them has been depleted */

			if (low == null && high == null) {
				// nothing remains, return
				return Collections.unmodifiableList(nodes);
			}

			/* add high entry to nodes, update tail map to start from next
			 * entry higher */
			if (low == null) {
				tail = tail.tailMap(high.getKey(), false);
				nodes.addAll(new ArrayList<V>(high.getValue()));
				high = tail.firstEntry();
				retCnt++; continue;
			}

			/* add low entry to nodes, update head map to start from next 
			 * entry lower than the one retrieved */
			if (high == null) {
				head = head.headMap(low.getKey(), false);
				nodes.addAll(new ArrayList<V>(low.getValue()));
				low = head.lastEntry();
				retCnt++; continue;
			}

			/* if none of the maps is depleted yet, 
			 * we must compare at each step */
			distLo = getDistBetween(bQuery, low.getKey());
			distHi = getDistBetween(bQuery, high.getKey());

			/* Compare similarity difference and return the values of the
			 * key with the minimum difference */
			if (Utils.compareDouble(distLo, distHi)) {
				nodes.addAll(new ArrayList<V>(low.getValue()));
				nodes.addAll(new ArrayList<V>(high.getValue()));
				tail = tail.tailMap(high.getKey(), false); 
				high = tail.firstEntry();
				head = head.headMap(low.getKey(), false); 
				low = head.lastEntry();
				retCnt += 2;
			}
			else if (distLo < distHi) {
				nodes.addAll(new ArrayList<V>(low.getValue()));
				head = head.headMap(low.getKey(), false);
				low = head.lastEntry();
				retCnt++;
			}
			else if (distLo > distHi) {
				nodes.addAll(new ArrayList<V>(high.getValue()));
				tail = tail.tailMap(high.getKey(), false);
				high = tail.firstEntry();
				retCnt++;
			}
		}
		return Collections.unmodifiableList(nodes);
	}

	/**
	 * Returns a feature from a given biograph to be stored in the list
	 * of values kept at each node of the database tree. 
	 * Classes extending TreeDatabase must override this method.
	 *
	 * @param bg the graph from which to extract the feature
	 * @return a graph feature
	 */
	public abstract V getGraphFeature(BioGraph bg);

	/**
	 * Returns a double value indicating the distance between two {@link BioGraph}
	 * objects. This method is used in comparisons such as those needed in nearest
	 * neighbour queries.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return the distance between the two graphs
	 */
	protected double getDistBetween(BioGraph bgA, BioGraph bgB) {
		int[] encA; int[] encB;
		IndexVector vHash;
		if (type == GraphType.DNA) {
			vHash = new IndexVector(GraphType.DNA);
		}
		else {
			vHash = new IndexVector(GraphType.WORD);
		}
		encA = vHash.encodeGraph(bgA);
		encB = vHash.encodeGraph(bgB);
		return (double) ClusterDistance.hamming(encA, encB);
	}
}
