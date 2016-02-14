package gr.demokritos.biographs.indexing.distances;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.preprocessing.DefaultHashVector;

/**
 * A class computing the distance between two graphs that is intended
 * to be used when clustering on the graphs is involved.
 */
public class ClusterDistance {
	
	/**
	 * Computes the clustering distance between two graphs, which is
	 * assumed to be the hamming distance of their hash-encoded vectors.
	 *
	 * @param bgA the first graph
	 * @param bgB the second graph
	 * @return the hamming distance between the two graphs' hash-encoded
	 * vectors
	 */
	public static double clusterDistance(BioGraph bgA, BioGraph bgB) {
		DefaultHashVector vHash = new DefaultHashVector();
		double dist;
		try {
			dist = Utils.getHammingDistance(
					vHash.encodeGraph(bgA),
					vHash.encodeGraph(bgB));
		} catch (Exception ex) {
			ex.printStackTrace();
			dist = 0.0;
		}
		return dist;
	}
}
