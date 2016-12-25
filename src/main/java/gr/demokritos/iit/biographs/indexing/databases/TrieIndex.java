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

package gr.demokritos.iit.biographs.indexing.databases;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.trie.PatriciaTrie;

import gr.demokritos.iit.biographs.BioGraph;
import gr.demokritos.iit.biographs.indexing.GraphDatabase;
import gr.demokritos.iit.biographs.indexing.structs.TrieEntry;
import gr.demokritos.iit.biographs.io.BioInput;


/**
 * A class that extends {@link GraphDatabase}, using a PatriciaTrie as an index
 * that uses a string representation of each added graph as a key.
 *
 * The representation used by default is the graph's DFS code. If you wish to
 * use a different kind of representation, you *must* override the
 * {@link TrieIndex#getGraphCode(BioGraph)} method.
 *
 *
 * @author VHarisop
 */
public final class TrieIndex extends GraphDatabase {
	/**
	 * A {@link org.apache.commons.collections4.trie.PatriciaTrie}
	 * that is used for indexing graphs by using the graphs'
	 * {@link BioGraph#getDfsCode()} or {@link BioGraph#getCanonicalCode()}
	 * as keys.
	 */
	protected final PatriciaTrie<List<TrieEntry>> trieIndex =
			new PatriciaTrie<>();

	/**
	 * The default order (number of bits) used for the serialization
	 * of encoding vector cells.
	 */
	protected int order = 64;

	/**
	 * Creates a blank TrieIndex object.
	 */
	public TrieIndex() {
		super();
	}

	/**
	 * Creates a blank TrieIndex using a custom {@link #order}.
	 *
	 * @param order the custom order to be used
	 */
	public TrieIndex(int order) {
		this();
		this.order = order;
	}

	/**
	 * Creates a new TrieIndex object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public TrieIndex(String path) {
		super(path);
	}

	/**
	 * Creates a new {@link TrieIndex} object for maintaing a database
	 * in a given directory using a custom order.
	 *
	 * @param path the directory in which the database resides
	 * @param order the custom order to be used
	 */
	public TrieIndex(String path, int order) {
		this(path);
		this.order = order;
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
		final File fPath = new File(path);
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
			final File[] fileList = fPath.listFiles(
					(FileFilter) toFilter -> toFilter.isFile());
			for (final File f: fileList) {
				addAllGraphs(f);
			}
		}
	}

	/**
	 * Adds all graphs from a file to the database, choosing an appropriate
	 * reading method depending on the data type of the graphs this database
	 * indexes.
	 *
	 * @param f the file to read from
	 */
	private void addAllGraphs(File f) throws Exception {
		if (type == GraphType.DNA) {
			BioInput.fastaFileToGraphStream(f)
				.forEach(g -> addGraph(g));
		}
		else {
			throw new UnsupportedOperationException(
					"Graph type not supported!");
		}
	}

	/**
	 * Add a new trie entry to the database, appending it to the list
	 * of entries with the same code, if any.
	 *
	 * @param entry the entry to be added
	 */
	public void addEntry(TrieEntry entry) {
		// update the database's size
		this.size++;

		/*
		 * Get already existing entries with the same key first, if any
		 */
		final String key = entry.getKey(this.order);
		List<TrieEntry> entries = trieIndex.get(key);

		/* if key was not already there, initialize an array of entries
		 * otherwise, add an entry to the pre-existing array */
		if (null == entries) {
			entries = new ArrayList<>();
		}
		/*
		 * update trie with new array
		 */
		entries.add(entry);
		trieIndex.put(key, entries);
	}

	/**
	 * Add a new graph to the graph database, updating
	 * the index on the trie as well.
	 * @param bg the BioGraph to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		addEntry(new TrieEntry(bg));
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
		return (new TrieEntry(bGraph)).getKey(this.order);
	}

	/**
	 * Obtains a list of labels of graphs that match the dfs code
	 * of the given graph.
	 *
	 * @param bg the query graph
	 * @return a list of labels
	 */
	public final List<TrieEntry> getNodes(BioGraph bg) {
		return getNodes(getGraphCode(bg));
	}

	/**
	 * Obtain a list of labels of graphs that match the provided key
	 * @param key a String containing the query graph key
	 * @return a list of entries pointing to biographs
	 */
	public final List<TrieEntry> getNodes(String key) {
		return trieIndex.get(key);
	}

	/**
	 * Obtains a list of graph entries whose key is closest to the
	 * code of a query graph, using a bitwise-XOR metric.
	 *
	 * @param bQuery the graph to be queried for
	 * @return a list of entries whose keys are the closest matches
	 */
	public final List<TrieEntry> select(BioGraph bQuery) {
		return trieIndex.selectValue(getGraphCode(bQuery));
	}

	/**
	 * Returns a list of graph entries whose keys are the K closest to
	 * the key of a specified query graph.
	 *
	 * @param bQuery the graph to be queried for
	 * @param K the number of requested nearest neighbours
	 * @return a list of entries that are the closest matches
	 */
	public final List<TrieEntry> selectKNearest(BioGraph bQuery, int K) {
		final String code = getGraphCode(bQuery);
		final List<TrieEntry> entries = trieIndex.selectValue(code);

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
				entries.addAll(trieIndex.selectValue(prevKey));
				++returned;
			}
			if (null != nextKey) {
				nextKey = trieIndex.nextKey(nextKey);
				entries.addAll(trieIndex.selectValue(nextKey));
				++returned;
			}
			if ((nextKey == null) && (prevKey == null)) {
				break;
			}
		}
		while (returned < K);
		return entries;
	}

	/**
	 * Simple getter for the database's trie keyset.
	 */
	public Set<String> exposeKeys() {
		return trieIndex.keySet();
	}

	/**
	 * Simple getter for the database's collection of values.
	 */
	public Collection<List<TrieEntry>> exposeValues() {
		return trieIndex.values();
	}
}
