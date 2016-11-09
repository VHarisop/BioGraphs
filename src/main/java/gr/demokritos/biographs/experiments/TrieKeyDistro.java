/* This file is part of BioGraphs.
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

package gr.demokritos.biographs.experiments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.indexing.databases.TrieIndex;
import gr.demokritos.biographs.indexing.structs.TrieEntry;
import gr.demokritos.biographs.io.BioInput;

/**
 * A class that performs queries from biological sequences using
 * {@link BioGraph} objects.
 *
 * @author VHarisop
 */
public final class TrieKeyDistro {
	/**
	 * Gson builder for result printing
	 */
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/*
	 * Size of subsequence.
	 */
	private int seqSize;

	/**
	 * The length of the window in which {@link TrieEntry} objects
	 * are created in the database for every offset.
	 */
	private int window = 10;

	/**
	 * The graph database to perform queries against.
	 */
	private TrieIndex graphIndex;

	/**
	 * Creates a new TrieKeyDistro object that performs queries by
	 * splitting the query strings into overlapping subsequences
	 * of a specified length.
	 *
	 * @param K the size of the subsequences
	 */
	public TrieKeyDistro(int K) {
		seqSize = K;
		graphIndex = new TrieIndex();
	}

	/**
	 * Creates a new TrieKeyDistro object that performs queries by
	 * splitting the query strings into overlapping subsequences
	 * of a specified length.
	 *
	 * @param K the size of the subsequences
	 * @param win the length of the window used for consecutive
	 * entries
	 */
	public TrieKeyDistro(int K, int win) {
		this(K);
		window = win;
	}

	/**
	 * Creates a new {@link TrieKeyDistro} object that performs queries
	 * by splitting the query strings into overlapping subsequences
	 * of a specified length, using a given order for serialization
	 * of encoding vectors.
	 *
	 * @param K the size of the subsequences
	 * @param win the length of the window used for consecutive entries
	 * @param order the order of the encoding vectors' serialization
	 */
	public TrieKeyDistro(int K, int win, int order) {
		seqSize = K;
		window = win;
		graphIndex = new TrieIndex(order);
	}

	/**
	 * Initializes the database index, reading all data from
	 * a file.
	 *
	 * @param dataFile the file containing the database graphs
	 */
	public void initIndex(File dataFile) {
		try {
			for (Map.Entry<String, String> e:
					BioInput.fromFastaFileToEntries(dataFile).entrySet())
			{
				/*
				 * Split database graphs into non-overlapping sequences
				 * of length K and store them separately into the database
				 * using the same labels.
				 */
				for (String s: splitString(e.getValue())) {
					graphIndex.addGraph(new BioGraph(s, e.getKey()));
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Simple getter for the size (in #entries) of the
	 * underlying {@link TrieIndex}.
	 *
	 * @return the total number of entries in the underlying database
	 */
	public int getSize() {
		return graphIndex.getSize();
	}

	/**
	 * @see #initIndex(File) initIndex
	 */
	public void initIndex(String dataPath) {
		initIndex(new File(dataPath));
	}

	/**
	 * Splits a data string into non-overlapping subsequences as
	 * a preprocessing step for an index, returning the list of
	 * subsequences.
	 *
	 * @param data the data string
	 * @return the list of generated subsequences
	 */
	private List<String> splitString(String data) {
		int index = 0;
		final int qLen = data.length();
		List<String> blocks = new ArrayList<String>();
		while ((index + seqSize) < qLen) {
			for (int i = 0; i < window; ++i) {
				if (index + i + seqSize >= qLen)
					break;
				blocks.add(data.substring(index + i, index + i + seqSize));
			}
			index += seqSize;
		}
		return blocks;
	}

	/**
	 * Prints out the leaf sizes of this database.
	 */
	public void printLeafSizes() {
		HashMap<Integer, Integer> distroMap = new HashMap<Integer, Integer>();
		for (List<?> lst: graphIndex.exposeValues()) {
			final int mSize = lst.size();
			if (distroMap.containsKey(mSize)) {
				distroMap.put(mSize, distroMap.get(mSize) + 1);
			}
			else {
				distroMap.put(mSize, 1);
			}
		}
		System.out.println(gson.toJson(distroMap));
	}

	/**
	 * A static main method that performs a short experiment using
	 * a sample graph database.
	 */
	public static void main(String[] args) {
		if (args.length <= 1) {
			System.out.println("Not enough parameters");
			return;
		}
		File data = new File(args[0]);
		int Ls = 150, order = 64;

		/*
		 * If another argument is present, it is assumed to be
		 * the length Ls of the stored sequence parts.
		 */
		if (args.length > 1) {
			try {
				Ls = Integer.parseInt(args[1]);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		/*
		 * If a 3rd argument is present, it is assumed to be
		 * equal to the order of the encoding vector.
		 */
		if (args.length > 2) {
			try {
				order = Integer.parseInt(args[2]);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		try {
			TrieKeyDistro bq = new TrieKeyDistro(Ls, 1, order);
			bq.initIndex(data);
			bq.printLeafSizes();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
