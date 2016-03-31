package gr.demokritos.biographs.indexing.distances;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.preprocessing.IndexVector;

/**
 * A class computing the distance between two graphs that is intended
 * to be used when clustering on the graphs is involved.
 */
public class ClusterDistance {
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
		} catch (Exception ex) {
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
		} catch (Exception ex) {
			ex.printStackTrace();
			dist = Integer.MAX_VALUE;
		}
		return dist;
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
		int dist;
		try {
			dist = Utils.getEuclideanDistance(encA, encB);
		} catch (Exception ex) {
			ex.printStackTrace();
			dist = Integer.MAX_VALUE;
		}
		return dist;
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
		double dist;
		try {
			dist = Utils.getEuclideanDistance(encA, encB);
		} catch (Exception ex) {
			ex.printStackTrace();
			dist = Double.MAX_VALUE;
		}
		return dist;
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
		IndexVector vHash = new IndexVector();
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
		IndexVector vHash = new IndexVector();
		return euclidean(vHash.encodeGraph(bgA), vHash.encodeGraph(bgB));
	}
}
