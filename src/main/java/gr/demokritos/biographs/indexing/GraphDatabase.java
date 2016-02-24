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
import java.util.ArrayList;

import gr.demokritos.biographs.BioGraph;

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
	 * Creates a blank GraphDatabase object.
	 */
	public GraphDatabase() { 
		path = null;
	}

	/**
	 * Creates a new GraphDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public GraphDatabase(String path) {
		this.path = path;
	}

	/**
	 * Compares 2 doubles for equality, checking if the absolute value of their
	 * difference is under a very small threshold.
	 *
	 * @param a the first number
	 * @param b the second number
	 * @return true if the doubles should be considered equal, else false
	 */
	public static boolean compareDouble(double a, double b) {
		return (Math.abs(a - b) < 0.0000001);
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
