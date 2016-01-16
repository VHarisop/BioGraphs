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
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import gr.demokritos.biographs.BioGraph;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * An abstract class that handles a graph database, consisting of BioGraph
 * objects. Provides auxiliary methods for reading fasta files into the database,
 * checking if the database is empty, etc.
 *
 * @author VHarisop
 */
public abstract class GraphDatabase {

	/**
	 * the directory of the graph database, if any 
	 */
	protected String path;

	/** 
	 * flag indicating if the database resides in RAM
	 */
	protected boolean inMem;

	/** 
	 * an array list of graphs to be kept in memory 
	 */
	protected ArrayList<BioGraph> graphArray;

	/**
	 * the current index of {@link GraphDatabase#graphArray} 
	 */
	protected int arrayIndex;
	
	/**
	 * Creates a blank GraphDatabase object.
	 */
	public GraphDatabase() { 
		path = null;
		inMem = true;

		graphArray = new ArrayList();
		arrayIndex = -1;
	}

	/**
	 * Creates a new GraphDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public GraphDatabase(String path) {
		this.path = path;
		inMem = false;

		graphArray = new ArrayList();
		arrayIndex = -1;
	}

	/**
	 * Gets the BioGraph located at a given index in the graph array.
	 * If the index is larger than the current array size, returns null instead.
	 *
	 * @param graphIndex the index of the graph we want to retrieve
	 * @return the biograph at the given index, or null if no such graph exists.
	 */
	public BioGraph getGraph(int graphIndex) {
		if (arrayIndex > graphIndex) 
			return null;

		return graphArray.get(graphIndex);
	}

	/**
	 * Checks if the graph database is empty by checking arrayIndex.
	 * 
	 * @return true if the graph array is empty, otherwise false.
	 */
	public boolean isEmpty() {
		return (arrayIndex > -1);
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	public abstract void buildIndex(String path) throws Exception;

	/**
	 * Builds a graph database index from a given file or a directory 
	 * of files.
	 *
	 * @param path a path containing one or multiple files
	 */
	public abstract void buildIndex(File path) throws Exception;

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioGraph object to be added
	 */
	public abstract void addGraph(BioGraph bg);
}
