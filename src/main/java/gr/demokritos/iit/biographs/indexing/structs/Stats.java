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

package gr.demokritos.iit.biographs.indexing.structs;

import java.util.*;

import gr.demokritos.iit.biographs.indexing.TreeDatabase;

/** 
 * Class to be used for serializing results. 
 *
 * @author VHarisop
 */
public final class Stats {
	/**
	 * The label of the indexing method
	 */
	protected String MethodLabel;

	/**
	 * The array of test results
	 */
	protected List<Result> ResultList;

	/**
	 * An array containing the size of each bin
	 * from a tree database index.
	 */
	protected int[] binSizes;

	/**
	 * Contains the maximum query time in seconds.
	 */
	protected double maxTime;

	/**
	 * Contains the size of the queried database.
	 */
	protected int DatabaseSize;

	/**
	 * Contains the number of mutations in the queries.
	 */
	protected int mutations;

	/**
	 * Contains the search tolerance value for queries.
	 */
	protected int tolerance;

	/**
	 * Contains the mean query time in seconds.
	 */
	protected double meanTime;

	/**
	 * Creates a new Stats object to hold results for
	 * an indexing method to be applied.
	 *
	 * @param methodLabel the label of the method
	 */
	public Stats(String methodLabel) {
		MethodLabel = methodLabel;
		ResultList = new ArrayList<Result>();
	}

	/**
	 * Adds a new test result to the list of results.
	 *
	 * @param test the query string
	 * @param results a list of results for the query
	 */
	public void addResult(String test, String ... results) {
		ResultList.add(new Result(test, results));
	}

	/**
	 * Gets the list lengths for the keys of a database
	 * and stores them at an array.
	 *
	 * @param trd the database
	 */
	public void setBins(TreeDatabase<?> trd) {
		binSizes = trd.binSizes();
	}

	/**
	 * Sets the bin sizes of the Stats object.
	 *
	 * @param bins an array containing the bin sizes
	 */
	public void setBins(int[] bins) {
		binSizes = bins;
	}

	/**
	 * Sets the {@link #DatabaseSize} field of the object.
	 */
	public void setDatabaseSize(int size) {
		DatabaseSize = size;
	}

	/**
	 * Sets the {@link #mutations} field of the object.
	 */
	public void setMutations(int mutations) {
		this.mutations = mutations;
	}

	/**
	 * Sets the {@link #tolerance} field of the object.
	 */
	public void setTolerance(int tol) {
		this.tolerance = tol;
	}

	/**
	 * Sets the query times used in some test.
	 * @param maxTime the maximum query time in milliseconds
	 * @param sumTime the total query time
	 * @param numTests the number of tests performed
	 */
	public void setTimes(long maxTime, long sumTime, int numTests) {
		this.maxTime = maxTime / 1000.0;
		this.meanTime = (sumTime / 1000.0) / numTests;
	}

	/**
	 * A class used internally for storing results.
	 */
	public final class Result {
		public String query;
		public String[] results;

		public Result(String test, String ... result) {
			query = test;
			results = new String[result.length]; int i = 0;
			for (String s: result) {
				results[i++] = s;
			}
		}
	}
}
