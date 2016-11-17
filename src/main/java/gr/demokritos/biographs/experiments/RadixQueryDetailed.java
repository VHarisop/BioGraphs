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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.indexing.databases.RadixIndex;
import gr.demokritos.biographs.indexing.databases.TrieIndex;
import gr.demokritos.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.biographs.indexing.structs.TrieEntry;
import gr.demokritos.biographs.io.BioInput;

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
	private static final Logger logger;
	static {
		logger = Logger.getLogger(RadixQueryDetailed.class.getName());
		try {
			logger.addHandler(new FileHandler("radix_query.log", true));
		}
		catch (IOException ex) {
			logger.warning(
				"Could not set log file -- logging to console instead");
		}
	}
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
			for (Map.Entry<String, String> e:
					BioInput.fromFastaFileToEntries(dataFile).entrySet())
			{
				/*
				 * Split database graphs into non-overlapping sequences
				 * of length K and store them separately into the database
				 * using the same labels.
				 */
				splitString(e.getValue())
					.forEach(s -> {
						graphIndex.addGraph(new BioGraph(s, e.getKey()));
					});
			}
		}
		catch(Exception ex) {
			logger.log(Level.SEVERE, "", ex);
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
	 * Splits a data string into non-overlapping subsequences as
	 * a preprocessing step for an index, returning the list of
	 * subsequences.
	 *
	 * @param data the data string
	 * @return the list of generated subsequences
	 */
	private List<String> splitString(String data) {
		final int qLen = data.length();
		List<String> blocks = new ArrayList<String>();
		/* [seqSize]-length steps, as subsequences should not overlap */
		for (int index = 0; (index + seqSize) < qLen; index += seqSize) {
			blocks.add(data.substring(index, index + seqSize));
		}
		try {
			/* Add the rightmost [seqSize] chars */
			final String sub = data.substring(qLen - seqSize, qLen);
			blocks.add(sub);
		}
		catch (StringIndexOutOfBoundsException ex) {
			/* Out of bounds means qLen < Ls, so we add
			 * the whole string */
			blocks.add(data);
			logger.info("Out of bounds error, added whole " + data);
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

		/* boolean indicating if an absolute match has been found */
		boolean absMatch = false;

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
			for (TrieEntry t: graphIndex.select(bg)) {
				final int entryDist = ClusterDistance.hamming(
						t.getEncoding(),
						enc);

				/* If an entry from the same graph is found,
				 * report it as well as its distance
				 */
				if (t.getLabel().equals(eQuery.getLabel())) {
					logger.info(String.format(
						"Found entry from same graph -- Dist: %d",
						entryDist));
				}
				/*
				 * If distance is larger than the tolerance,
				 * skip to next iteration.
				 */
				if (entryDist > tolerance) {
					continue;
				}
				/*
				 * Otherwise, add to matches.
				 */
				matches.add(t);

				/*
				 * If an absolutely matching entry was found,
				 * set the absMatch flag to break on next iteration
				 */
				if (entryDist == 0) {
					absMatch = true;
				}
			}
			/*
			 * Keep searching until a range equal to the
			 * seqSize has been searched.
			 */
			// if (++loopcnt > (2 * seqSize)) {
				/*
				 * Break the search if two full search windows
				 * have been exhausted and an absolute match has
				 * been already found.
				 */
				// if (absMatch) {
				//	;
					// break;
				//}
			//}
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
