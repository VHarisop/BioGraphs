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
import gr.demokritos.iit.biographs.Logging;
import gr.demokritos.iit.biographs.indexing.QueryUtils;
import gr.demokritos.iit.biographs.indexing.databases.SparseIndex;
import gr.demokritos.iit.biographs.indexing.databases.TrieIndex;
import gr.demokritos.iit.biographs.indexing.structs.TrieEntry;
import gr.demokritos.iit.biographs.io.BioInput;

/**
 * A class that performs queries from biological sequences using
 * {@link BioGraph} objects.
 *
 * @author VHarisop
 */
public final class SparseQuery {
	/* Create our own logger, register an output file */
	private static final Logger logger = Logging.getFileLogger(
			SparseQuery.class.getName(),
			"sparse_query.log");

	/**
	 * Gson builder for result printing
	 */
	static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/*
	 * Size of subsequence.
	 */
	private final int seqSize;

	/**
	 * The graph database to perform queries against.
	 */
	private final SparseIndex graphIndex;

	/**
	 * Creates a new SparseQuery object that performs queries by
	 * splitting the query strings into overlapping subsequences
	 * of a specified length.
	 *
	 * @param K the size of the subsequences
	 * @param featDim the projected vector dimension
	 */
	public SparseQuery(final int K, final int featDim) {
		seqSize = K;
		graphIndex = new SparseIndex(featDim);
	}

	/**
	 * Initializes the database index, reading all data from
	 * a file.
	 *
	 * @param dataFile the file containing the database graphs
	 */
	private void initIndex(final File dataFile) {
		try {
			BioInput.fromFastaFileToEntries(dataFile).entrySet()
			.forEach(e -> {
				/* Split every string that must be indexed and
				 * add it to the radix tree.
				 */
				QueryUtils.splitIndexedString(e.getValue(), seqSize)
				.forEach(s -> {
					graphIndex.addGraph(new BioGraph(s, e.getKey()));
				});
			});
		}
		catch(final IOException ex) {
			if (null == ex.getMessage()) {
				logger.severe("Error occured during index initialization");
			}
			else {
				logger.severe(ex.getMessage());
			}
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
	 * Performs a search in the graph database for matches of a specific
	 * query string with a given label.
	 *
	 * @param query the query string
	 * @param label the query label
	 * @param tolerance the search tolerance for BioGraph bins
	 * @return a set of matching {@link TrieEntry}s.
	 */
	public Set<String>
	getMatches(final String query, final String label, final int tolerance)
	{
		final List<String> blocks = QueryUtils.splitQueryString(query, seqSize);
		final Set<String> matches = new HashSet<String>();
		/*
		 * search all graphs with a preselected tolerance
		 */
		for (final String bl: blocks) {
			final BioGraph bg = new BioGraph(bl, label);
			matches.addAll(graphIndex.select(bg, tolerance));
		}
		return matches;
	}

	/**
	 * A static main method that performs a short experiment using
	 * a sample graph database.
	 */
	public static void main(final String[] args) {
		if (args.length <= 1) {
			System.out.println("Not enough parameters");
			return;
		}
		final File data = new File(args[0]);
		final File test = new File(args[1]);
		int Ls = 40, tol = 2, order = 64;

		/*
		 * If another argument is present, it is assumed to be
		 * the length Ls of the stored sequence parts.
		 */
		if (args.length > 2) {
			try {
				Ls = Integer.parseInt(args[2]);
			}
			catch (final Exception ex) {
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
			catch (final Exception ex) {
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
			catch (final Exception ex) {
				ex.printStackTrace();
				return;
			}
		}

		try {
			logger.info("Initializing SparseQuery...");
			final SparseQuery bq = new SparseQuery(Ls, order);
			bq.initIndex(data);

			long totalTime = 0L;
			int eCnt = 0, hits = 0, totalMatches = 0;

			final List<Long> qTimes = new ArrayList<Long>();
			final List<Integer> matchList = new ArrayList<Integer>();
			for (final Map.Entry<String, String> e:
				BioInput.fromFastaFileToEntries(test).entrySet())
			{
				final String lbl = e.getKey(), dt = e.getValue();
				/*
				 * Perform the query and measure time elapsed
				 */
				logger.info(String.format("Querying %s", lbl));
				final long start = System.currentTimeMillis();
				final Set<String> matches = bq.getMatches(dt, lbl, tol);
				final long end = System.currentTimeMillis();
				logger.info(String.format("Time: %d ms", end - start));
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
				for (final String entLabel: matches) {
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
					for (final String ent: matches) {
						logger.warning(ent);
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
		catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * A static class used internally only for serializing
	 * experimental results to JSON.
	 */
	@SuppressWarnings("unused")
	static class Result {
		private final double accuracy;
		private final double avgQueryTime;
		private final double stdevQueryTime;
		private final double avgMatches;
		private final double stdevMatches;
		private final int size;

		/**
		 * Creates a new Result object with all the statistics
		 * provided from the caller.
		 */
		public Result(
				final double accuracy,
				final double avgQueryTime,
				final double stdevQueryTime,
				final double avgMatches,
				final double stdevMatches,
				final int size)
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
