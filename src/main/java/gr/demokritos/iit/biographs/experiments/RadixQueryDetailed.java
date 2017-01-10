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

package gr.demokritos.iit.biographs.experiments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gr.demokritos.iit.biographs.BioGraph;
import gr.demokritos.iit.biographs.indexing.QueryUtils;
import gr.demokritos.iit.biographs.indexing.databases.RadixIndex;
import gr.demokritos.iit.biographs.indexing.databases.TrieIndex;
import gr.demokritos.iit.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.iit.biographs.indexing.structs.TrieEntry;
import gr.demokritos.iit.biographs.io.BioInput;

import gr.demokritos.iit.jinsect.Logging;

/**
 * A class that performs queries from biological sequences using
 * {@link BioGraph} objects and the {@link RadixIndex} implementation.
 * Also outputs detailed information about the queries, to be used
 * in debugging.
 *
 * @author VHarisop
 */
public final class RadixQueryDetailed {
	/* Create our own logger, register an output file */
	private static final Logger logger =
			Logging.getFileLogger(
					RadixQueryDetailed.class.getName(),
					"radix_query.log");
	/**
	 * Gson builder for result printing
	 */
	static Gson gson;

	/*
	 * Size of subsequence.
	 */
	private int seqSize;

	/**
	 * The graph database to perform queries against.
	 */
	private RadixIndex graphIndex;

	/**
	 * Creates a new TrieQuery object that performs queries by
	 * splitting the query strings into overlapping subsequences
	 * of a specified length.
	 *
	 * @param K the size of the subsequences
	 */
	public RadixQueryDetailed(int K) {
		seqSize = K;
		graphIndex = new RadixIndex();
	}

	/**
	 * Creates a new {@link RadixQueryDetailed} object that performs queries
	 * by splitting the query strings into overlapping subsequences
	 * of a specified length, using a given order for serialization
	 * of encoding vectors.
	 *
	 * @param K the size of the subsequences
	 * @param order the order of the encoding vectors' serialization
	 */
	public RadixQueryDetailed(int K, int order) {
		seqSize = K;
		graphIndex = new RadixIndex(order);
	}

	/**
	 * Initializes the database index, reading all data from
	 * a file.
	 *
	 * @param dataFile the file containing the database graphs
	 */
	private void initIndex(File dataFile) {
		try {
			BioInput.fromFastaFileToEntries(dataFile).forEach((k, v) -> {
				/*
				 * Split database graphs into non-overlapping sequences
				 * of length K and store them separately into the database
				 * using the same labels.
				 */
				QueryUtils.splitIndexedString(v, seqSize)
					.forEach(s -> graphIndex.addGraph(new BioGraph(s, k)));
			});
		}
		catch(IOException ex) {
			logger.severe(ex.getMessage());
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
	 * Splits a query string into overlapping subsequences as
	 * a preprocessing step for a query, returning the list of
	 * subsequences.
	 *
	 * @param query the query string
	 * @return the list of generated subsequences
	 */
	protected List<String> splitQueryString(String query) {
		final int qLen = query.length();
		List<String> blocks = new ArrayList<String>();
		/* Unitary length steps, since we want overlapping subsequences */
		for (int index = 0; (index + seqSize) < qLen; ++index) {
			blocks.add(query.substring(index, index + seqSize));
		}
		try {
			/* Add the rightmost [seqSize] chars */
			final String sub = query.substring(qLen - seqSize, qLen);
			blocks.add(sub);
		}
		catch (StringIndexOutOfBoundsException ex) {
			/* Out of bounds means qLen < Ls, so we add
			 * the whole query string */
			blocks.add(query);
			logger.info("Out of bounds error, queried whole " + query);
		}
		return blocks;
	}

	/**
	 * Returns a list of {@link TrieEntry} objects that exactly
	 * match a given query string.
	 * @param query the query string
	 * @return a list of matching entries
	 */
	public List<TrieEntry> getExactMatches(String query) {
		return graphIndex.getNodes(query);
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
	getMatches(String query, String label, final int tol)
	{
		List<String> blocks = QueryUtils.splitQueryString(query, seqSize);
		Set<TrieEntry> matches = new HashSet<TrieEntry>();

		/*
		 * search all graphs with a preselected tolerance
		 */
		for (String bl: blocks) {
			final BioGraph bg = new BioGraph(bl, label);
			final TrieEntry eQuery = new TrieEntry(bg);
			final byte[] enc = eQuery.getEncoding();

			logger.info("Query string: " + bl);
			/*
			 * Get the closest TrieEntry objects and
			 * keep all whose distances are lower than
			 * a tolerance.
			 */
			graphIndex.selectAsStream(bg)
				.filter(t ->
					ClusterDistance.hamming(t.getEncoding(), enc) <= tol)
				.forEach(t -> {
					/*
					 * Otherwise, add to matches. If .add() fails, log it
					 * to the log file.
					 */
					if (!(matches.add(t))) {
						logger.warning(String.format(
								"Could not add %s to the match set!",
								t.getLabel()));
					}
				});
		}
		return matches;
	}

	/**
	 * Gets the exact matches for a given query.
	 * @param query the query string
	 * @param label the label of the sequence
	 * @param tol the search tolerance
	 * @return the set of matching entries
	 */
	public Set<TrieEntry>
	getExactMatches(String query, String label, final int tol)
	{
		List<String> blocks = QueryUtils.splitQueryString(query, seqSize);
		Set<TrieEntry> matches = new HashSet<TrieEntry>();
		/*
		 * Retrieve all exact matches
		 */
		for (String bl: blocks) {
			final BioGraph bg = new BioGraph(bl, label);
			final TrieEntry eQuery = new TrieEntry(bg);
			final byte[] enc = eQuery.getEncoding();
			/* Put all matches in the set */
			graphIndex.getNodesAsStream(bg)
				.filter(t ->
					ClusterDistance.hamming(t.getEncoding(), enc) <= tol)
				.forEach(t -> matches.add(t));
		}
		return matches;
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
		int Ls = 30, tol = 5, order = 64;

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
			logger.info("Initializing RadixQueryDetailed...");
			RadixQueryDetailed bq = new RadixQueryDetailed(Ls, order);
			bq.initIndex(data);

			long totalTime = 0L;
			int eCnt = 0, hits = 0, totalMatches = 0;

			List<Long> qTimes = new ArrayList<Long>();
			List<Integer> matchList = new ArrayList<Integer>();
			for (Map.Entry<String, String> e:
				BioInput.fromFastaFileToEntries(test).entrySet())
			{
				final String lbl = e.getKey(), dt = e.getValue();
				/*
				 * Perform the query and measure time elapsed
				 */
				final long start = System.currentTimeMillis();
				Set<TrieEntry> matches = bq.getMatches(dt, lbl, tol);
				final long end = System.currentTimeMillis();
				logger.info(String.format(
						"Queried %s -- Time: %d ms", lbl, end - start));
				/*
				 * Update parameters:
				 * 1) total elapsed time
				 * 2) total number of matches
				 * 3) hits / accuracy
				 * 4) number of times
				 */
				totalTime += (end - start);
				totalMatches += matches.size();
				qTimes.add(end - start);
				eCnt++;
				/*
				 * Update hits for accuracy calculation
				 */
				boolean found = false;
				for (TrieEntry ent: matches) {
					final String entLabel = ent.getLabel();
					if (entLabel.equals(lbl)) {
						hits++;
						found = true;
						break;
					}
				}
				if (!found) {
					/* If not found, inform user about it and output
					 * the alternative matching sequences found, if any
					 */
					logger.warning(String.format(
						"Original sequence not found for %s - Found: ",
						lbl));
					for (TrieEntry ent: matches) {
						logger.warning(ent.getLabel());
					}
					/* Get exact matches as well */
					Set<TrieEntry> exact = bq.getExactMatches(dt, lbl, tol);
					for (TrieEntry ent: exact) {
						if (ent.getLabel().equals(lbl)) {
							hits++;
							break;
						}
					}
				}
				/*
				 * Update list of answer set sizes
				 */
				matchList.add(matches.size());
			}
			/*
			 * calculate standard deviation from average time
			 */
			final double avgTime = totalTime / ((double) eCnt);
			final double runSum = qTimes.stream()
					.mapToDouble(t -> (t - avgTime) * (t - avgTime))
					.sum();
			final double stdevTime = Math.sqrt(runSum / eCnt);
			/*
			 * calculate standard deviation from average number of matches
			 */
			final double avgMatches = totalMatches / ((double) eCnt);
			final double mRunSum = matchList.stream()
					.mapToDouble(m -> (m - avgMatches) * (m - avgMatches))
					.sum();
			final double stdevMatches = Math.sqrt(mRunSum / eCnt);
			/*
			 * Create a Result object to be serialized to JSON.
			 */
			final Result res = new Result(
					((double) hits) / eCnt,
					avgTime,
					stdevTime,
					avgMatches,
					stdevMatches,
					bq.getSize());
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
	@SuppressWarnings("unused")
	static class Result {
		private double accuracy;
		private double avgQueryTime;
		private double stdevQueryTime;
		private double avgMatches;
		private double stdevMatches;
		private int size;

		/**
		 * Creates a new Result object with all the statistics
		 * provided from the caller.
		 */
		public Result(
			double accuracy,
			double avgQueryTime,
			double stdevQueryTime,
			double avgMatches,
			double stdevMatches,
			int size)
		{
			this.accuracy = accuracy;
			this.avgQueryTime = avgQueryTime;
			this.stdevQueryTime = stdevQueryTime;
			this.avgMatches = avgMatches;
			this.stdevMatches = stdevMatches;
			this.size = size;
		}
	}
}
