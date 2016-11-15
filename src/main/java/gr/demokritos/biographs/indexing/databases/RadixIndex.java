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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.googlecode.concurrenttrees.radix.RadixTree;
import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.biographs.indexing.structs.TrieEntry;
import gr.demokritos.biographs.io.BioInput;


/**
 * A class that extends {@link GraphDatabase}, using a PatriciaTrie as an index
 * that uses a string representation of each added graph as a key.
 *
 * The representation used by default is the graph's DFS code. If you wish to
 * use a different kind of representation, you *must* override the
 * {@link RadixIndex#getGraphCode(BioGraph)} method.
 *
 *
 * @author VHarisop
 */
public final class RadixIndex extends GraphDatabase {
	/**
	 * A {@link RadixTree} that is used for indexing graphs by using a
	 * graph's relevant {@link TrieEntry#getEncoding()} to obtain its key.
	 */
	protected ByteRadixTree radixIndex;

	/**
	 * The default order (number of bits) used for the serialization
	 * of encoding vector cells.
	 */
	protected int order = 64;

	/**
	 * Creates a blank TrieIndex object.
	 */
	public RadixIndex() {
		super();
		radixIndex = new ByteRadixTree();
	}

	/**
	 * Creates a blank TrieIndex using a custom {@link #order}.
	 *
	 * @param order the custom order to be used
	 */
	public RadixIndex(int order) {
		this();
		this.order = order;
	}

	/**
	 * Creates a new TrieIndex object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public RadixIndex(String path) {
		super(path);
		radixIndex = new ByteRadixTree();
	}

	/**
	 * Creates a new {@link RadixIndex} object for maintaing a database
	 * in a given directory using a custom order.
	 *
	 * @param path the directory in which the database resides
	 * @param order the custom order to be used
	 */
	public RadixIndex(String path, int order) {
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
				@Override
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
	 *
	 * @param f the file to read from
	 */
	private void addAllGraphs(File f) throws Exception {
		if (type == GraphType.DNA) {
			Arrays.stream(BioInput.fastaFileToGraphs(f))
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
		List<TrieEntry> entries = radixIndex.getValueFor(key);

		/* if key was not already there, initialize an array of entries
		 * otherwise, add an entry to the pre-existing array */
		if (null == entries) {
			entries = new ArrayList<TrieEntry>();
		}
		/*
		 * update trie with new array
		 */
		entries.add(entry);
		radixIndex.put(key, entries);
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
	 * @return a {@link CharSequence} representation of the biograph
	 */
	protected CharSequence getGraphCode(BioGraph bGraph) {
		return new TrieEntry(bGraph).getKey(order);
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
	public final List<TrieEntry> getNodes(CharSequence key) {
		return radixIndex.getValueFor(key);
	}

	/**
	 * Obtains a list of graph entries whose key is closest to the
	 * code of a query graph, using a bitwise-XOR metric.
	 *
	 * @param bQuery the graph to be queried for
	 * @return a list of entries whose keys are the closest matches
	 */
	public final List<TrieEntry> select(BioGraph bQuery) {
		return radixIndex.select(getGraphCode(bQuery));
	}
}
