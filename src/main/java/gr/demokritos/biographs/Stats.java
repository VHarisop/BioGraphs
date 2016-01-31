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

import java.util.*;

import gr.demokritos.biographs.indexing.TreeDatabase;

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
