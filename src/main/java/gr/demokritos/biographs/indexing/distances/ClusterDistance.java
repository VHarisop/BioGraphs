package gr.demokritos.biographs.indexing.distances;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.preprocessing.DefaultHashVector;

/**
 * A class computing the distance between two graphs that is intended
 * to be used when clustering on the graphs is involved.
 */
public class ClusterDistance {
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
	public static double hamming(BioGraph bgA, BioGraph bgB) {
		DefaultHashVector vHash = new DefaultHashVector();
		double dist;
		try {
			dist = Utils.getHammingDistance(
					vHash.encodeGraph(bgA),
					vHash.encodeGraph(bgB));
		} catch (Exception ex) {
			ex.printStackTrace();
			dist = Double.MAX_VALUE;
		}
		return dist;
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
	public static double euclidean(BioGraph bgA, BioGraph bgB) {
		DefaultHashVector vHash = new DefaultHashVector();
		double dist;
		try {
			dist = Utils.getEuclideanDistance(
					vHash.encodeGraph(bgA),
					vHash.encodeGraph(bgB));
		} catch (Exception ex) {
			ex.printStackTrace();
			dist = Double.MAX_VALUE;
		}
		return dist;
	}
}
