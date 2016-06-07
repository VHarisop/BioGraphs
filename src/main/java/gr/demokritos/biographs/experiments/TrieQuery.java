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

import java.util.*;
import java.io.File;
import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.biographs.indexing.preprocessing.IndexVector;
import gr.demokritos.biographs.indexing.structs.TrieEntry;
import gr.demokritos.biographs.indexing.databases.TrieIndex;
import gr.demokritos.biographs.indexing.*;
import gr.demokritos.biographs.io.BioInput;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A class that performs queries from biological sequences using
 * {@link BioGraph} objects.
 *
 * @author VHarisop
 */
public class TrieQuery {
	/**
	 * Gson builder for result printing
	 */
	static Gson gson;

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
	 * Creates a new TrieQuery object that performs queries by
	 * splitting the query strings into overlapping subsequences
	 * of a specified length.
	 *
	 * @param K the size of the subsequences
	 */
	public TrieQuery(int K) {
		seqSize = K;
		graphIndex = new TrieIndex();
	}

	/**
	 * Creates a new TrieQuery object that performs queries by
	 * splitting the query strings into overlapping subsequences
	 * of a specified length.
	 *
	 * @param K the size of the subsequences
	 * @param win the length of the window used for consecutive
	 * entries
	 */
	public TrieQuery(int K, int win) {
		this(K);
		window = win;
	}

	/**
	 * Creates a new {@link TrieQuery} object that performs queries
	 * by splitting the query strings into overlapping subsequences
	 * of a specified length, using a given order for serialization
	 * of encoding vectors.
	 *
	 * @param K the size of the subsequences
	 * @param win the length of the window used for consecutive entries
	 * @param order the order of the encoding vectors' serialization
	 */
	public TrieQuery(int K, int win, int order) {
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
	protected List<String> splitString(String data) {
		int index = 0, qLen = data.length();
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
	 * Splits a query string into overlapping subsequences as
	 * a preprocessing step for a query, returning the list of
	 * subsequences.
	 *
	 * @param query the query string
	 * @return the list of generated subsequences
	 */
	protected List<String> splitQueryString(String query) {
		int index = 0, qLen = query.length();
		int rem = qLen - seqSize;
		List<String> blocks = new ArrayList<String>();
		while ((index + seqSize) < qLen) {
			blocks.add(query.substring(index, index + seqSize));
			
			/**
			 * Increase by window len, since we want <i>overlapping</i>
			 * subsequences
			 */
			index += window;
		}
		return blocks;
	}


	/**
	 * Performs a search in the graph database for matches of a specific
	 * query string with a given label.
	 *
	 * @param query the query string
	 * @param label the query label
	 * @param tolerance the search tolerance for BioGraph bins
	 * @return a set of matching {@link TrieEntry}s.
	 */
	public Set<TrieEntry>
	getMatches(String query, String label, int tolerance)
	{
		List<String> blocks = this.splitQueryString(query);
		Set<TrieEntry> matches = new HashSet<TrieEntry>();

		/* loop counter */
		int loopcnt = 0;

		/* boolean indicating if an absolute match has been
		 * found */
		boolean absMatch = false;

		/*
		 * search all graphs with a preselected tolerance
		 */
		for (String bl: blocks) {
			BioGraph bg = new BioGraph(bl, label);
			TrieEntry eQuery = new TrieEntry(bg);
			
			final byte[] enc = eQuery.getEncoding();

			/*
			 * Get the closest TrieEntry objects and
			 * keep all whose distances are lower than
			 * a tolerance.
			 */
			for (TrieEntry t: graphIndex.select(bg)) {
				final int entryDist = ClusterDistance.hamming(
						t.getEncoding(),
						enc);
				/*
				 * If distance is larger than the tolerance,
				 * skip to next iteration.
				 */
				if (entryDist > tolerance) {
					continue;
				}

				matches.add(t);

				/*
				 * If an absolutely matching entry was found,
				 * set the absMatch flag to break on next iteration
				 */
				if (entryDist == 0) {
					absMatch = true;
					break;
				}
			}
			/*
			 * Keep searching until a range twice the
			 * seqSize has been searched.
			 */
			if (++loopcnt <= (seqSize / window)) {
				continue;
			}
			else {
				/*
				 * If a sequence with 0 distance was found and a full window
				 * has already been exhausted, stop searching and return all
				 * results so far.
				 */
				if (absMatch) {
					break;
				}
			}
		}
		return matches;
	}

	/**
	 * Computes the hamming distance between two byte vectors, returning
	 * the maximum distance if at some point their distance exceeds a given
	 * bound.
	 *
	 * @param a the first vector
	 * @param b the second vector
	 * @param bound the distance bound
	 * @return the bounded distance between the two vectors
	 */
	public int boundedHamming(byte[] a, byte[] b, int bound) {
		int sum = 0;
		for (int i = 0; i < a.length; ++i) {
			sum += Math.abs(a[i] - b[i]);
			if (sum > bound) {
				return Integer.MAX_VALUE;
			}
		}
		return sum;
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
		File test = new File(args[1]);
		int Ls = 150, tol = 5, order = 64;

		/*
		 * If another argument is present, it is assumed to be
		 * the length Ls of the stored sequence parts.
		 */
		if (args.length > 2) {
			try {
				Ls = Integer.parseInt(args[2]);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		/*
		 * If a 4th argument is present, it is assumed to be
		 * equal to the search tolerance on retrieved sequence
		 * distances.
		 */
		if (args.length > 3) {
			try {
				tol = Integer.parseInt(args[3]);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		/*
		 * If a 5th argument is present, it is assumed to be
		 * equal to the order of the encoding vector.
		 */
		if (args.length > 4) {
			try {
				order = Integer.parseInt(args[4]);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		try {
			/*
			 * Create a GsonBuilder to output formatted results.
			 */
			gson = new GsonBuilder().setPrettyPrinting().create();

			// TrieQuery bq = new TrieQuery(150, 1); // this worked fine
			TrieQuery bq = new TrieQuery(Ls, 1, order);
			bq.initIndex(data);

			long totalTime = 0L;
			int eCnt = 0, hits = 0, totalMatches = 0;
			double accuracy, avgMatchSize;

			for (Map.Entry<String, String> e:
					BioInput.fromFastaFileToEntries(test).entrySet())
			{
				String lbl = e.getKey(), dt = e.getValue();

				/*
				 * Perform the query and measure time elapsed
				 */
				long start = System.currentTimeMillis();
				Set<TrieEntry> matches = bq.getMatches(dt, lbl, tol);
				long end = System.currentTimeMillis();

				/*
				 * Update parameters:
				 * 1) total elapsed time
				 * 2) total number of matches
				 * 3) hits / accuracy
				 */
				totalTime += (end - start);
				totalMatches += matches.size();
				eCnt++;

				/*
				 * Update hits for accuracy calculation
				 */
				boolean found = false;
				for (TrieEntry ent: matches) {
					if (ent.getLabel().equals(lbl)) {
						hits++;
						found = true;
						break;
					}
				}
			}
			/*
			 * Create a Result object to be serialized to JSON.
			 */
			Result res = new Result(
				((double) hits) / eCnt,
				totalTime / ((double) eCnt),
				((double) totalMatches) / eCnt,
				bq.getSize()
			);
			System.out.println(gson.toJson(res));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * A static class used internally only for serializing
	 * experimental results to JSON.
	 */
	static class Result {
		private double accuracy;
		private double avgQueryTime;
		private double avgMatches;
		private int size;

		public Result(
			double accuracy,
			double avgQueryTime,
			double avgMatches,
			int size)
		{
			this.accuracy = accuracy;
			this.avgQueryTime = avgQueryTime;
			this.avgMatches = avgMatches;
			this.size = size;
		}
	}
}
