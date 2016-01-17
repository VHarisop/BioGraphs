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

import java.lang.Math;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.NavigableMap;
import java.util.Comparator;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.iit.jinsect.jutils;


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
	 * Creates a blank TreeDatabase object.
	 */
	public TreeDatabase() { 
		super();
		bgComp = new SimilarityComparator();
		treeIndex = new TreeMap(bgComp);
	}

	/**
	 * Creates a new TreeDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public TreeDatabase(String path) {
		super(path);
		bgComp = new SimilarityComparator();
		treeIndex = new TreeMap(bgComp);
	}

	/**
	 * Creates a blank TreeDatabase object using a custom provided
	 * comparator.
	 * 
	 * @param bgC the custom comparator to be used 
	 */
	public TreeDatabase(Comparator<BioGraph> bgC) {
		super();
		bgComp = bgC;
		treeIndex = new TreeMap(bgComp);
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
		bgComp = bgC;
		treeIndex = new TreeMap(bgComp);
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
			BioGraph[] bgs = BioGraph.fastaFileToGraphs(fPath);
			for (BioGraph bG: bgs) {
				addGraph(bG);
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
				BioGraph[] bgs = BioGraph.fastaFileToGraphs(f);
				for (BioGraph bG: bgs) {
					addGraph(bG);
				}
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
			BioGraph[] bgs = BioGraph.fromWordFile(fPath);
			for (BioGraph bG: bgs) {
				addGraph(bG);
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
				BioGraph[] bgs = BioGraph.fromWordFile(f);
				for (BioGraph bG: bgs) {
					addGraph(bG);
				}
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
	public Entry<BioGraph, List<V>>[] getNodes(BioGraph[] bGraphs) {
		Entry<BioGraph, List<V>>[] results = new VEntry[bGraphs.length];
		int iCnt;
		for (iCnt = 0; iCnt < bGraphs.length; ++iCnt) {
			results[iCnt] = 
				new VEntry<V>(bGraphs[iCnt], getNodes(bGraphs[iCnt]));
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
		double distLo = 
			Math.abs(jutils.graphStructuralSimilarity(
						bQuery.getGraph(),
						lower.getGraph()));
		double distHi = 
			Math.abs(jutils.graphStructuralSimilarity(
						bQuery.getGraph(), 
						higher.getGraph()));


		/* return the nodes of the "closest" distance, or both if the
		 * distances are equal */
		if (compareDouble(distLo, distHi)) {
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
		ArrayList<V> nodes = new ArrayList();

		// return up to min(size, K) neighbouring keys
		K = (K > treeIndex.size()) ? treeIndex.size() : K;

		if (include) {	
			/* if an exact match exists, add it 
			 * to the list of nodes to return */
			List<V> matches = getNodes(bQuery);
			if (matches != null) {
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
		Entry<BioGraph, List<V>> high = tail.pollFirstEntry();
		Entry<BioGraph, List<V>> low = head.pollLastEntry();
		double distLo, distHi;

		/* if stuff remains, loop */
		while (retCnt < K) {
		
			/* handle cases where either both maps have been depleted
			 * or one of them has been depleted */

			if (low == null && high == null) {
				// nothing remains, return
				return Collections.unmodifiableList(nodes);
			}

			/* add high entry to nodes, update entry as well */
			if (low == null) {
				nodes.addAll(high.getValue());
				high = tail.pollFirstEntry();
				retCnt++;
				continue;
			}

			/* add low entry to nodes, update entry as well */
			if (high == null) {
				nodes.addAll(low.getValue());
				low = head.pollLastEntry();
				retCnt++;
				continue;
			}

			/* if none of the maps is depleted yet, 
			 * we must compare at each step */
			distLo = Math.abs(jutils.graphStructuralSimilarity(
						bQuery.getGraph(),
						low.getKey().getGraph()));

			distHi = Math.abs(jutils.graphStructuralSimilarity(
						bQuery.getGraph(),
						high.getKey().getGraph()));

			/* Compare similarity difference and return the values of the
			 * key with the minimum difference */
			if (compareDouble(distLo, distHi)) {
				nodes.addAll(low.getValue());
				nodes.addAll(high.getValue());
				low = head.pollLastEntry();
				high = tail.pollFirstEntry();
				retCnt += 2;
			}
			else if (distLo < distHi) {
				nodes.addAll(low.getValue());
				low = head.pollLastEntry();
				retCnt++;
			}
			else if (distLo > distHi) {
				nodes.addAll(high.getValue());
				high = tail.pollFirstEntry();
				retCnt++;
			}
		} 
		return Collections.unmodifiableList(nodes);
	}

	/**
	 * Utility function that checks double equality up to a numerical threshold
	 */
	protected static boolean compareDouble(double a, double b) {
		return (Math.abs(a - b) < 0.0000001);
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
}

/**
 * Utility class that implements Map.Entry for specific types 
 */
final class VEntry<V> implements Entry<BioGraph, List<V>> {
	private final BioGraph key;
	private List<V> value;

	public VEntry(BioGraph bKey, List<V> listValues) {
		key = bKey;
		value = listValues;
	}

	@Override
	public BioGraph getKey() {
		return key;
	}

	@Override
	public List<V> getValue() {
		return value;
	}

	@Override
	public List<V> setValue(List<V> newValues) {
		List<V> old = value;
		value = newValues;
		return old;
	}

}
