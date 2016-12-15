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

package gr.demokritos.biographs.indexing.distances;

import java.util.stream.IntStream;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.Utils;
import gr.demokritos.biographs.indexing.preprocessing.IndexVector;

/**
 * A class computing the distance between two graphs that is intended
 * to be used when clustering on the graphs is involved and contains
 * several static methods for hamming and euclidean distance metrics.
 */
public final class ClusterDistance {
	/**
	 * Computes the hamming distance between two vectors, returning
	 * the system's max value for doubles if their lengths differ.
	 *
	 * @param encA the first vector
	 * @param encB the second vector
	 * @return the hamming distance between the two vectors
	 */
	public static double hamming(double[] encA, double[] encB) {
		double dist;
		try {
			dist = Utils.getHammingDistance(encA, encB);
		} catch (final Exception ex) {
			ex.printStackTrace();
			dist = Double.MAX_VALUE;
		}
		return dist;
	}

	/**
	 * Computes the hamming distance between two integer vectors, returning
	 * the system's max value for integers if their lengths differ.
	 *
	 * @param encA the first vector
	 * @param encB the second vector
	 * @return the hamming distance between the two vectors
	 */
	public static int hamming(int[] encA, int[] encB) {
		int dist;
		try {
			dist = Utils.getHammingDistance(encA, encB);
		} catch (final Exception ex) {
			ex.printStackTrace();
			dist = Integer.MAX_VALUE;
		}
		return dist;
	}

	/**
	 * Computes the hamming distance between two byte vectors, returning
	 * the system's max value for integers if their lengths differ.
	 *
	 * @param encA the first vector
	 * @param encB the second vector
	 * @return the hamming distance between the two vectors
	 */
	public static int hamming(byte[] encA, byte[] encB) {
		if (encA.length != encB.length) {
			return Integer.MAX_VALUE;
		}
		else {
			return IntStream.range(0, encA.length)
				.map(i -> Math.abs(encA[i] - encB[i]))
				.sum();
		}
	}

	/**
	 * Computes the euclidean distance between two vectors, returning
	 * the system's max value for integers if their lengths differ.
	 *
	 * @param encA the first vector
	 * @param encB the second vector
	 * @return the euclidean distance between the two vectors
	 */
	public static int euclidean(int[] encA, int[] encB) {
		try {
			return Utils.getEuclideanDistance(encA, encB);
		} catch (final Exception ex) {
			ex.printStackTrace();
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * Computes the euclidean distance between two byte vectors, returning
	 * the system's max value for integers if their lengths differ.
	 *
	 * @param encA the first vector
	 * @param encB the second vector
	 * @return the euclidean distance between the two vectors
	 */
	public static int euclidean(byte[] encA, byte[] encB) {
		if (encA.length != encB.length) {
			return Integer.MAX_VALUE;
		}
		else {
			return IntStream.range(0, encA.length)
				.map(i -> (encA[i] - encB[i]) * (encA[i] - encB[i]))
				.sum();
		}
	}

	/**
	 * Computes the euclidean distance between two vectors, returning
	 * the system's max value for doubles if their lengths differ.
	 *
	 * @param encA the first vector
	 * @param encB the second vector
	 * @return the euclidean distance between the two vectors
	 */
	public static double euclidean(double[] encA, double[] encB) {
		try {
			return Utils.getEuclideanDistance(encA, encB);
		} catch (final Exception ex) {
			ex.printStackTrace();
			return Double.MAX_VALUE;
		}
	}

	/**
	 * Computes the hamming distance between two graphs, which is assumed
	 * to be the hamming distance of their hash-encoded vectors.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return the hamming distance between the two graphs' hash-encoded
	 * vectors. If an exception occurs during the computation, the max
	 * value for doubles is returned.
	 */
	public static int hamming(BioGraph bgA, BioGraph bgB) {
		final IndexVector vHash = new IndexVector();
		return hamming(vHash.encodeGraph(bgA), vHash.encodeGraph(bgB));
	}

	/**
	 * Computes the euclidean distance between two graphs, which is assumed
	 * to be the euclidean distance of their hash-encoded vectors.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return the euclidean distance between the two graphs' hash encoded
	 * vectors. If an exception occurs during the computation, the maximum
	 * value for doubles is returned.
	 */
	public static int euclidean(BioGraph bgA, BioGraph bgB) {
		final IndexVector vHash = new IndexVector();
		return euclidean(vHash.encodeGraph(bgA), vHash.encodeGraph(bgB));
	}
}
