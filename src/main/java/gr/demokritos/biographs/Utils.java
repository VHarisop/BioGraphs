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

package gr.demokritos.biographs;

import java.io.File;
import java.lang.Math;
import java.util.*;
import java.util.stream.*;
import gr.demokritos.biographs.indexing.preprocessing.*;
import gr.demokritos.biographs.indexing.structs.*;
import gr.demokritos.biographs.io.BioInput;
import gr.demokritos.iit.jinsect.io.LineReader;
import gr.demokritos.iit.jinsect.comparators.NGramGraphComparator;

import org.biojava.nbio.core.sequence.DNASequence;

/**
 * A class containing various utilities used in preprocessing
 * tasks necessary for several indexing methods.
 *
 * @author VHarisop
 */
public final class Utils {
	/**
	 * Computes the value similarity between two {@link BioGraph} objects
	 * using a {@link NGramGraphComparator}.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return the value similarity of the two graphs
	 */
	public static double
	getValueSimilarityBetween(BioGraph bgA, BioGraph bgB) {
		NGramGraphComparator ngcc = new NGramGraphComparator();
		return ngcc.getSimilarityBetween(
				bgA.getSuper(),
				bgB.getSuper()).ValueSimilarity;
	}

	/**
	 * Compares two doubles for equality using a precision of 1e-6.
	 *
	 * @param a the first double
	 * @param b the second double
	 * @return a boolean indicating whether the two numbers can be
	 * considered equal
	 */
	public static boolean compareDouble(double a, double b) {
		return Math.abs(a - b) < 0.000001;
	}

	/**
	 * Computes the partial sums of a double array.
	 *
	 * @param dArray the array whose partial sums are needed
	 * @return an array of equal length containing the partial sums
	 */
	public static double[] getPartialSums(double[] dArray) {
		// allocate a new array to hold the partial sums
		double[] pSums = new double[dArray.length];
		double runSum = 0.0;

		/* iterate over each entry, compute the new partial sum
		 * and update the running sum value */
		for (int i = 0; i < dArray.length; ++i) {
			pSums[i] = runSum + dArray[i];
			runSum += dArray[i];
		}
		return pSums;
	}

	/**
	 * Computes the partial sums of an integer array.
	 *
	 * @param iArray the array whose partial sums are needed
	 * @return an array of equal length containing the partial sums
	 */
	public static int[] getPartialSums(int[] iArray) {
		// allocate a new array to hold the partial sums
		int[] pSums = new int[iArray.length];
		int runSum = 0;

		/* iterate over each entry, compute the new partial sum
		 * and update the running sum value */
		for (int i = 0; i < iArray.length; ++i) {
			pSums[i] = runSum + iArray[i];
			runSum += iArray[i];
		}
		return pSums;
	}

	/**
	 * Computes the hamming distance between two encoding vectors
	 * of integral numeric type.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the hamming distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static int getHammingDistance(int[] vecA, int[] vecB)
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Enoding vector lengths differ!");
		}
		return IntStream.range(0, vecA.length)
			.map(i -> Math.abs(vecA[i] - vecB[i]))
			.sum();
	}

	/**
	 * Computes the hamming distance between two encoding vectors
	 * of floating point numeric type.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the hamming distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static double getHammingDistance(double[] vecA, double[] vecB)
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Encoding vector lengths differ!");
		}
		return IntStream.range(0, vecA.length)
			.mapToDouble(i -> Math.abs(vecA[i] - vecB[i]))
			.sum();
	}

	/**
	 * Computes the euclidean distance between two encoding vectors
	 * containing integers.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the euclidean distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static int getEuclideanDistance(int[] vecA, int[] vecB)
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Encoding vector lengths differ!");
		}
		return IntStream.range(0, vecA.length)
			.map(i -> (vecA[i] - vecB[i]) * (vecA[i] - vecB[i]))
			.sum();
	}

	/**
	 * Computes the euclidean distance between two encoding vectors
	 * of floating point numeric type.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the euclidean distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static double getEuclideanDistance(double[] vecA, double[] vecB)
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Encoding vector lengths differ!");
		}
		return IntStream.range(0, vecA.length)
			.mapToDouble(i -> (vecA[i] - vecB[i]) * (vecA[i] - vecB[i]))
			.sum();
	}

	/**
	 * Given a sample mean and standard deviation, standardizes a vector that
	 * might or might not belong to the sample in order for it to be suitable
	 * for comparison in classification methods.
	 *
	 * @param vec the vector to standardize
	 * @param means the means of the reference sample, one for each dimension
	 * @param devs the standard deviations of the reference sample, one for each
	 * dimension
	 * @return the standardized vector
	 */
	public static double[]
	standardize(double[] vec, double means[], double devs[]) {
		return IntStream.range(0, vec.length)
			.mapToDouble(i -> (vec[i] - means[i]) / devs[i])
			.toArray();
	}

	/**
	 * Given a {@link HashedVector} and a FASTA file, returns an array of
	 * database entries read from that file using the hashed vector.
	 *
	 * @param path the file containing FASTA entries
	 * @param hVec the hashed vector to use
	 * @return an array of {@link GraphIndexEntry}
	 * @throws Exception if an error occurs when reading the file
	 */
	public static GraphIndexEntry[]
	fastaFileToEntries(File path, IndexVector hVec) throws Exception {
		List<GraphIndexEntry> gis = new ArrayList<GraphIndexEntry>();
		for (Map.Entry<String, DNASequence> e:
				BioInput.readFastaFile(path).entrySet())
		{
			gis.add(new GraphIndexEntry(
						BioGraph.fromSequence(e.getValue(), e.getKey()),
						hVec)
			);
		}
		return gis.toArray(new GraphIndexEntry[gis.size()]);
	}

	/**
	 * Given a {@link IndexVector} and a normal text file, returns an
	 * array of database entries read from that file using the index
	 * vector.
	 *
	 * @param path the file containing the entries
	 * @param hVec the index vector to use
	 * @return an array of {@link GraphIndexEntry}
	 * @throws Exception if an error occurs when reading the file
	 */
	public static GraphIndexEntry[]
	wordFileToEntries(File path, IndexVector hVec) throws Exception {
		List<GraphIndexEntry> gis = new ArrayList<GraphIndexEntry>();
		for (String s: new LineReader().getLines(path)) {
			gis.add(new GraphIndexEntry(
						new BioGraph(s, s),
						hVec)
			);
		}
		return gis.toArray(new GraphIndexEntry[gis.size()]);
	}

	/**
	 * Function that creates 4 nested loops for intersecting DNA indices
	 * whose loop variables iterate among
	 * [center - range, center + range].
	 *
	 * @param center the center of the loop
	 * @param range the range of the loop variables
	 * @param sum the desired sum of the loop variables
	 */
	public static List<Integer[]>
	dnaIndexLoop(int center, int range, int sum) {
		List<Integer[]> indices = new ArrayList<Integer[]>();
		for (int i = -range; i <= range; ++i) {
			for (int j = -range; j <= range; ++j) {
				if (i + j > sum)
					break;
				for (int k = -range; k <= range; ++k) {
					if (i + j + k > sum)
						break;
					for (int l = -range; l <= range; ++l) {
						if (l + k + j + i > sum)
							break;
						if (l + k + j + i < sum)
							continue;
						indices.add(new Integer[] {
							center + i,
							center + j,
							center + k,
							center + l
						});
					}
				}
			}
		}
		return indices;
	}

	/**
	 * Function that creates 4 nested loops for intersecting DNA indices.
	 * @param range the range of the loop variables
	 * @param sum the desired sum of the loop variables
	 */
	public static List<Integer[]> dnaIndexLoop(int range, int sum) {
		List<Integer[]> indices = new ArrayList<Integer[]>();
		for (int i = 0; i < range; ++i) {
			for (int j = 0; j < range; ++j) {
				if (i + j > sum)
					break;
				for (int k = 0; k < range; ++k) {
					if (i + j + k > sum)
						break;
					for(int l = 0; l < range; ++l) {
						if (l + k + j + i > sum)
							break;
						if (l + k + j + i < sum)
							continue;
						indices.add(new Integer[] {i, j, k, l});
					}
				}
			}
		}
		return indices;
	}

	/**
	 * A generic for-loop with arbitrary max nesting depth, whose loop indices
	 * must sum up to a specified number.
	 *
	 * @param range the range of the loop
	 * @param maxSum the number that the loop variables must sum up to
	 * @param maxLevel the maximum nesting level
	 * @return a list of {@link Integer[]} entries, containing the indices
	 * of the generic loop that satisfy the desired properties
	 */
	public static List<Integer[]>
	genericFor(int range, int maxSum, int maxLevel) {
		/* initialize queue and result list for genericFor invocation */
		List<Integer> queue = new ArrayList<Integer>();
		List<Integer[]> results = new ArrayList<Integer[]>();

		/* call genericFor internally to populate result list */
		genericFor(range, 0, maxSum, queue, 0, maxLevel, results);
		return results;
	}

	/**
	 * A generic for-loop with arbitrary nesting, whose loop indices must
	 * sum at most up to a specified number.
	 *
	 * @param rng the range of index variables
	 * @param rs the running sum of the index variables
	 * @param ms the maximum sum of the index variables
	 * @param q a queue containing the loop indices
	 * @param cl the current nesting level
	 * @param ml the max nesting level
	 * @param res a list of results to be populated
	 */
	protected static void genericFor
	(int rng, int rs, int ms, List<Integer> q, int cl, int ml, List<Integer[]> res)
	{
		if (cl == ml) {
			/* if running sum is not the desired one,
			 * die gracefully */
			if (rs != ms)
				return;

			Integer[] inds = q.toArray(new Integer[q.size()]);
			/* otherwise, add combo to list of results */
			res.add(inds);
		}
		else {
			for (int i = 0; i < rng; ++i) {
				/* if we exceeded the maximum sum, break
				 * from loop */
				if (i + rs > ms) {
					break;
				}
				List<Integer> nq = new ArrayList<Integer>(q);
				nq.add(i);
				genericFor(rng, i + rs, ms, nq, cl + 1, ml, res);
			}
		}
	}

	/**
	 * Test the {@link #genericFor(int, int, int)} method
	 */
	public static void main(String[] args) {
		int rng = 5;
		int maxSum = 5;
		int maxLevels = 16;
		List<Integer[]> results = genericFor(rng, maxSum, maxLevels);
		for (Integer[] iArray: results) {
			for (int i: iArray) {
				System.out.print(i);
			}
			System.out.println();
		}
	}
}
