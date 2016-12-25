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

import gr.demokritos.iit.biographs.BioGraph;

/**
 * An abstract class that handles a graph database, consisting of BioGraph
 * objects. Provides auxiliary methods for reading fasta files into the database,
 * checking if the database is empty, etc.
 *
 * @author VHarisop
 */
public abstract class GraphDatabase {
	/**
	 * The datatype that the graphs of this database represent.
	 */
	public enum GraphType {
		WORD, DNA
	}

	/**
	 * the directory of the graph database, if any 
	 */
	protected String path;

	/**
	 * A field indicating the type of the data that the graphs in
	 * this database actually represent (words or biosequences).
	 */
	protected GraphType type;

	/**
	 * The number of graphs this database contains.
	 */
	protected int size;

	/**
	 * Creates a blank GraphDatabase object.
	 */
	public GraphDatabase() {
		size = 0;
		path = null;
	}

	/**
	 * Creates a new GraphDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public GraphDatabase(String path) {
		size = 0;
		this.path = path;
	}

	/**
	 * Returns the number of the graphs that this database
	 * contains.
	 *
	 * @return the database's size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files for graphs that represent a specified type of data.
	 *
	 * @param path the path of the file or directory
	 * @param gType the type of the graph data
	 * @throws Exception if an error occurs when reading the data
	 */
	public void build(File path, GraphType gType) throws Exception {
		this.type = gType;
		buildIndex(path);
	}

	/**
	 * @see #build(File, GraphType) build
	 *
	 * @param path a string containing the path of the file or directory
	 * @param gType the type of the graph data
	 * @throws Exception if an error occurs when reading the data
	 */
	public void build(String path, GraphType gType) throws Exception {
		build(new File(path), gType);
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 * @throws Exception if something goes wrong during index creation
	 */
	public abstract void buildIndex(String path) throws Exception;

	/**
	 * Builds a graph database index from a given file or a directory 
	 * of files.
	 *
	 * @param path a path containing one or multiple files
	 * @throws Exception if something goes wrong during index creation
	 */
	public abstract void buildIndex(File path) throws Exception;

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioGraph object to be added
	 */
	public abstract void addGraph(BioGraph bg);
}
