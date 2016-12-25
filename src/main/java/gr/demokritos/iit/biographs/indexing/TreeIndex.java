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

package gr.demokritos.iit.biographs.indexing;

import java.io.File;
import java.io.FileFilter;

import java.util.*;

import gr.demokritos.iit.biographs.BioGraph;
import gr.demokritos.iit.biographs.Utils;
import gr.demokritos.iit.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.iit.biographs.io.BioInput;

/**
 * An abstract class that implements a graph database using the graph's
 * DFS code as an indexing key.
 *
 * @author VHarisop
 */
public abstract class TreeIndex<V> extends GraphDatabase {

	/**
	 * A Red-Black tree map implementation that associates index entries
	 * with lists of value types, which can be any type of feature able
	 * to be extracted from a BioGraph. 
	 * @see #getGraphFeature
	 */
	protected TreeMap<TreeEntry, List<V>> treeIndex;

	/**
	 * A custom comparator to be used for {@link #treeIndex} that
	 * compares graphs based on their entries.
	 */
	protected Comparator<TreeEntry> bgComp;

	/**
	 * A field indicating if the data of this database's graphs comes from
	 * words or biological sequences.
	 */
	protected GraphType type;

	/**
	 * Creates a blank TreeIndex object.
	 */
	public TreeIndex() { 
		super();
		initIndex();
	}

	/**
	 * Creates a new TreeIndex object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public TreeIndex(String path) {
		super(path);
		initIndex();
	}

	
	/**
	 * Creates a blank TreeIndex object using a custom provided
	 * comparator.
	 * 
	 * @param bgC the custom comparator to be used
	 */
	public TreeIndex(Comparator<TreeEntry> bgC) {
		super();
		initIndex(bgC);
	}

	/**
	 * Creates a new TreeIndex object for maintaining a database
	 * in a given directory using a custom comparator.
	 *
	 * @param path the directory in which the database resides
	 * @param bgC the custom comparator to be used
	 */
	public TreeIndex(String path, Comparator<TreeEntry> bgC) {
		super(path);
		initIndex(bgC);
	}
	
	/**
	 * Initialize the database's comparator and tree index.
	 */
	protected void initIndex() {
		this.bgComp = new Comparator<TreeEntry>() {
			public int compare(TreeEntry eA, TreeEntry eB) {
				return eA.getFeature().compareTo(eB.getFeature());
			}
		};
		this.treeIndex = new TreeMap<TreeEntry, List<V>>(this.bgComp);
	}

	/**
	 * Initialize the database's tree index with a custom comparator.
	 *
	 * @param bgC the comparator to use
	 */
	protected void initIndex(Comparator<TreeEntry> bgC) {
		this.bgComp = bgC;
		this.treeIndex = new TreeMap<TreeEntry, List<V>>(this.bgComp);
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
			throw new UnsupportedOperationException(
					"Graph type not supported!"
					);
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
		TreeEntry bQuery = treeEncode(bg);
		List<V> nodeValues = treeIndex.get(bQuery);

		// if key was not there, initialize label array
		if (nodeValues == null) {
			nodeValues = new ArrayList<V>();
		}
		nodeValues.add(getGraphFeature(bQuery));
		treeIndex.put(bQuery, nodeValues);
	}

	/**
	 * Gets the keys of the underlying tree map of the database.
	 * 
	 * @return a set containing all the keys of the map
	 */
	public Set<TreeEntry> exposeKeys() {
		return treeIndex.keySet();
	}

	/**
	 * Gets the key-value pairs of the underlying tree
	 * map of the database.
	 *
	 * @return a set containing all of the entries of the map
	 */
	public Set<Map.Entry<TreeEntry, List<V>>> exposeEntries() {
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
		for (Map.Entry<?, List<V>> ent: treeIndex.entrySet()) {
			bins[iCnt++] = ent.getValue().size();
		}

		return bins;
	}

	/**
	 * Gets the nodes corresponding to the biograph query, whose
	 * similarity to the query biojgraph is 0.
	 * @param bQuery the {@link TreeEntry} key to be searched for
	 * @return a list of node values
	 */
	public List<V> getNodes(TreeEntry bQuery) {
		return treeIndex.get(bQuery);
	}


	/**
	 * Gets the node list corresponding to the nearest key to the query
	 * graph, including exact matches if an inclusive search is desired.
	 *
	 * @param bg the query graph
	 * @param include a flag indicating if exact matches should be included
	 * @return the unmodifiable node list of the nearest neighbouring key
	 */
	public List<V> getNearestNeighbours(BioGraph bg, boolean include) {
		TreeEntry bQuery = treeEncode(bg);
		if (include) {
			/* if an exact match exists, return it */
			List<V> matches = getNodes(bQuery);
			if (matches != null) {
				return Collections.unmodifiableList(matches);
			}
		}
		TreeEntry lower = treeIndex.lowerKey(bQuery);
		TreeEntry higher = treeIndex.higherKey(bQuery);

		if (lower == null && higher == null) {
			return null;
		}

		if (higher == null) 
			return Collections.unmodifiableList(getNodes(lower));
		
		if (lower == null)
			return Collections.unmodifiableList(getNodes(higher));
		
		/* get the distance of similarities of both the lower and
		 * higher keys */
		int distLo = getDistBetween(bQuery, lower);
		int distHi = getDistBetween(bQuery, higher);

		/* return the nodes of the "closest" distance, or both if the
		 * distances are equal */
		if (distLo == distHi) {
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
	 * @param bg the query graph
	 * @param include a flag indicating if exact matches should be included
	 * @param K the order of nearest neighbours
	 * @return the unmodifiable node list of the K nearest neighbouring keys
	 */
	public List<V> getKNearestNeighbours(BioGraph bg, boolean include, int K) {
		TreeEntry bQuery = treeEncode(bg);
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
		NavigableMap<TreeEntry, List<V>> tail = 
			treeIndex.tailMap(bQuery, false);
		NavigableMap<TreeEntry, List<V>> head = 
			treeIndex.headMap(bQuery, false);

		// lower values should be polled in reverse order
		Map.Entry<TreeEntry, List<V>> high = tail.higherEntry(bQuery);
		Map.Entry<TreeEntry, List<V>> low = head.lowerEntry(bQuery);
		int distLo, distHi;

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
	 * Encodes a {@link BioGraph} as a {@link TreeEntry}.
	 *
	 * @param bg the graph to encode
	 * @return the tree entry corresponding to the graph
	 */
	protected TreeEntry treeEncode(BioGraph bg) {
		return new TreeEntry(bg);
	}

	/**
	 * Returns a feature from a given {@link TreeEntry} to be stored in the
	 * list of values kept at each node of the database tree. 
	 * Classes extending TreeIndex must override this method.
	 *
	 * @param bg the entry from which to extract the feature
	 * @return a graph feature
	 */
	public abstract V getGraphFeature(TreeEntry bg);

	/**
	 * Returns an integral value indicating the distance between two
	 * {@link TreeEntry} objects. This method is used in comparisons
	 * such as those needed in nearest neighbour queries.
	 *
	 * @param bgA the first entry
	 * @param bgB the second entry
	 * @return the distance between the two entries
	 */
	protected int getDistBetween(TreeEntry bgA, TreeEntry bgB) {
		return ClusterDistance.hamming(
					bgA.getEncoding(),
					bgB.getEncoding()
					);
	}
}
