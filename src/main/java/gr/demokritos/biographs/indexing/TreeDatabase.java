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
import java.lang.Double;

import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.Comparator;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.iit.jinsect.jutils;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

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
	protected Comparator<BioGraph> bgComp = new SimilarityComparator();

	/**
	 * Creates a blank TreeDatabase object.
	 */
	public TreeDatabase() { 
		super();
		treeIndex = new TreeMap(bgComp);
	}

	/**
	 * Creates a new TreeDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public TreeDatabase(String path) {
		super(path);
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
