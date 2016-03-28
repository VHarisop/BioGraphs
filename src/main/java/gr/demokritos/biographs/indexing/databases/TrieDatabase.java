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

package gr.demokritos.biographs.indexing.databases;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.biographs.indexing.structs.TrieIndexEntry;


/**
 * A class that extends {@link GraphDatabase}, using a PatriciaTrie as an index
 * that uses a string representation of each added graph as a key.
 *
 * The representation used by default is the graph's DFS code. If you wish to
 * use a different kind of representation, you *must* override the
 * {@link TrieDatabase#getGraphCode(BioGraph)} method.
 *
 *
 * @author VHarisop
 */
public class TrieDatabase extends GraphDatabase {
	/**
	 * A {@link org.apache.commons.collections4.trie.PatriciaTrie} 
	 * that is used for indexing graphs by using the graphs'
	 * {@link BioGraph#getDfsCode()} or {@link BioGraph#getCanonicalCode()}
	 * as keys.
	 */
	protected PatriciaTrie<List<String>> trieIndex;
	
	/**
	 * Creates a blank TrieDatabase object.
	 */
	public TrieDatabase() { 
		super();
		trieIndex = new PatriciaTrie<List<String>>();
	}

	/**
	 * Creates a new TrieDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public TrieDatabase(String path) {
		super(path);
		trieIndex = new PatriciaTrie<List<String>>();
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	@Override
	public void buildIndex(String path) 
	throws Exception
	{
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
			addAllGraphs(fPath);
		}
		else {
			/* get all files in a list, and for each file add all
			 * the resulting biographs to the database */
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
	 * Adds all graphs from a file to the database, choosing an appropriate
	 * reading method depending on the data type of the graphs this database
	 * indexes.
	 */
	private void addAllGraphs(File f) throws Exception {
		if (type == GraphType.DNA) {
			for (TrieIndexEntry e: Utils.fastaFileToTrieEntries(f)) {
				addEntry(e);
			}
		}
		else {
			for (BioGraph bg: BioGraph.fromWordFile(f)) {
				addGraph(bg);
			}
		}
	}

	/**
	 * Add a new trie entry to the database, appending it to the list
	 * of entries with the same code, if any.
	 *
	 * @param entry the entry to be added
	 */
	public void addEntry(TrieIndexEntry entry) {
		// update the database's size
		this.size++;
		// get the code of the graph as key
		String code = entry.getEncoding();
		List<String> labels = trieIndex.get(code);

		/* if key was not already there, initialize an array of indices 
		 * otherwise, add an entry to the pre-existing array */
		if (null == labels) {
			labels = new ArrayList<String>();
		}
		labels.add(entry.getLabel());
		// update trie with new array
		trieIndex.put(code, labels);
	}

	/**
	 * Add a new graph to the graph database, updating
	 * the index on the trie as well. 
	 * @param bg the BioGraph to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		// get the code of the graph as key
		String code = getGraphCode(bg);
		
		/* if key was not already there, initialize an array of indices 
		 * otherwise, add an entry to the pre-existing array */
		if (!(trieIndex.containsKey(code))) {
			List<String> labels = new ArrayList<String>();
			labels.add(bg.getLabel());
			
			// add to Trie
			trieIndex.put(code, labels);
		}
		else {
			List<String> labels = trieIndex.get(code);
			labels.add(bg.getLabel());

			// update trie with new array
			trieIndex.put(code, labels);
		}
	}

	/**
	 * Helper function that defines how a string representation
	 * is acquired from a {@link BioGraph} object. This method can
	 * and should be overriden for subclasses that want to use a
	 * different encoder / string representation.
	 *
	 * @param bGraph the biograph object
	 * @return a String representation of the biograph
	 */
	protected String getGraphCode(BioGraph bGraph) {
		return bGraph.getDfsCode();
	}

	/**
	 * Obtains a list of labels of graphs that match the dfs code 
	 * of the given graph.
	 *
	 * @param bg the query graph
	 * @return a list of labels 
	 */
	public List<String> getNodes(BioGraph bg) {
		return getNodes(getGraphCode(bg));
	}

	/**
	 * Obtain a list of labels of graphs that match the provided code. 
	 * @param dfsCode a <tt>String</tt> containing the query graph code
	 * @return an <tt>ArrayList</tt> of labels pointing to biographs
	 */
	public List<String> getNodes(String code) {
		List<String> labels = trieIndex.get(code);

		return labels;
	}

	/**
	 * Obtains a list of labels of graphs whose key is closest to the
	 * code of a query graph, using a bitwise-XOR metric.
	 *
	 * @param bQuery the graph to be queried for
	 * @return a list of labels that are the closest matches
	 */
	public List<String> select(BioGraph bQuery) {
		String code = getGraphCode(bQuery);
		List<String> labels = trieIndex.selectValue(code);
		return labels;
	}

	/**
	 * Returns a list of graph labels whose keys are the K closest to
	 * the key of a specified query graph.
	 *
	 * @param bQuery the graph to be queried for
	 * @param K the number of requested nearest neighbours
	 * @return a list of labels that are the closest matches
	 */
	public List<String> selectKNearest(BioGraph bQuery, int K) {
		String code = getGraphCode(bQuery);
		List<String> labels = trieIndex.selectValue(code);

		String prevKey = trieIndex.selectKey(code),
			   nextKey = trieIndex.selectKey(code);
		int returned = 1;

		/* collect results from the K closest keys, expanding equally in
		 * either direction, except in the case where there are no higher
		 * or lower keys to expand to.
		 */
		do {
			if (null != prevKey) {
				prevKey = trieIndex.previousKey(prevKey);
				labels.addAll(trieIndex.selectValue(prevKey));
				++returned;
			}
			if (null != nextKey) {
				nextKey = trieIndex.nextKey(nextKey);
				labels.addAll(trieIndex.selectValue(nextKey));
				++returned;
			}
			if ((nextKey == null) && (prevKey == null))
				break;
		} while (returned < K);
		return labels;
	}

	/**
	 * Simple getter for the database's trie keyset.
	 */
	public Set<String> exposeKeys() {
		return trieIndex.keySet();
	}
}
