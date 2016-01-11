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

package gr.demokritos.biographs;

import java.io.File;
import java.io.FileFilter;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import gr.demokritos.iit.jinsect.jutils;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * A class that implements a graph database using the graphs's similarity measure.
 * Here, the similarity measure used is the graph's structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.structs.UniqueJVertexGraph}. This
 * version of similarity database doesn't store the whole graph as key, but stores
 * the graph's sum of normalized edge weights instead.
 *
 * @author VHarisop
 */
public class CachedSimilarityDatabase extends GraphDatabase {

	/**
	 * A Red-Black tree map implementation that associates biograph normalized
	 * weight sums with lists of FASTA strings (labels).
	 */
	protected TreeMap<Double, List<String>> treeIndex;

	/**
	 * Creates a blank CachedSimilarityDatabase object.
	 */
	public CachedSimilarityDatabase() { 
		super();
		treeIndex = new TreeMap();
	}

	/**
	 * Creates a new CachedSimilarityDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public CachedSimilarityDatabase(String path) {
		super(path);
		treeIndex = new TreeMap();
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
	 * @param bg the BioGraph object to be indexed
	 */
	@Override
	public void addGraph(BioGraph bg) {
		// acquire the normalized weight sum
		double gWeight = bg.getGraph().totalNormWeight();

		List<String> nodeLabels = treeIndex.get(gWeight);
		
		// if key was not there, initialize label array
		if (nodeLabels == null) {
			nodeLabels = new ArrayList<String>();
		}

		nodeLabels.add(bg.bioLabel);
		treeIndex.put(gWeight, nodeLabels);
	}

	/**
	 * Gets the keys of the underlying tree map of the database.
	 * 
	 * @return a set containing all the keys of the map
	 */
	public Set<Double> exposeKeys() {
		return treeIndex.keySet();
	}

	/**
	 * Gets the nodes corresponding to the biograph query, whose
	 * similarity to the query biojgraph is 0.
	 * @param bg the {@link BioGraph} to be searched for
	 * @return a list of labels corresponding to FASTA entries
	 */
	public List<String> getNodes(BioGraph bg) {
		double qWeight = bg.getGraph().totalNormWeight(); 
		return treeIndex.get(qWeight);
	}

	/**
	 * Gets the nodes corresponding to the list of query biographs, and
	 * returns them in an array of {@link java.util.Map.Entry} objects.
	 *
	 * @param bGraphs the {@link BioGraph} array of query graphs
	 * @return the list of Entries that map biographs to nodes
	 */
	public Entry<BioGraph, List<String>>[] getNodes(BioGraph[] bGraphs) {
		Entry<BioGraph, List<String>>[] results = new IndexEntry[bGraphs.length];
		for (int iCnt = 0; iCnt < bGraphs.length; ++iCnt) {
			results[iCnt] = 
				new IndexEntry(bGraphs[iCnt], getNodes(bGraphs[iCnt]));
		}

		return results;
	}
}

/**
 * Utility class that implements Map.Entry for specific types 
 */
final class IndexEntry implements Entry<BioGraph, List<String>> {
	private final BioGraph key;
	private List<String> value;

	public IndexEntry(BioGraph bKey, List<String> listValues) {
		key = bKey;
		value = listValues;
	}

	@Override
		public BioGraph getKey() {
			return key;
		}

	@Override
		public List<String> getValue() {
			return value;
		}

	@Override
		public List<String> setValue(List<String> newValues) {
			List<String> old = value;
			value = newValues;
			return old;
		}

}
