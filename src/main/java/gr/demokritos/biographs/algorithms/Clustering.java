package gr.demokritos.biographs.algorithms;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.indexing.preprocessing.Utils;
import gr.demokritos.biographs.indexing.preprocessing.DefaultHashVector;

import org.apache.commons.math3.ml.clustering.*;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import java.util.*;

public final class Clustering {	
	/**
	 * The clusterer to be used for KMeans clustering.
	 */
	protected Clusterer<HashVector> vectorClusterer;

	/**
	 * The list of vectors to be clustered.
	 */
	protected List<HashVector> dataPoints;

	/**
	 * The list of clusters.
	 */
	protected List<CentroidCluster<HashVector>> clusters;

	/**
	 * The distance measure used in clustering, with the
	 * default set to be the hamming distance.
	 */
	protected DistanceMeasure distMeasure = new DistanceMeasure() {
		static final long serialVersionUID = 1L;
		@Override
		public double compute(double[] vecA, double[] vecB) {
			double dist = 0.0;
			try {
				dist = Utils.getHammingDistance(vecA, vecB);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			return dist;
		}
	};

	/**
	 * Creates a new Clustering object given an array of {@link BioGraph}
	 * objects, and performs clustering on them.
	 *
	 * @param bGraphs the array of graphs
	 * @param k the number of desired clusters
	 */
	public Clustering(BioGraph[] bGraphs, int k) {
		dataPoints = new ArrayList<HashVector>(bGraphs.length);
		for (BioGraph bg: bGraphs) {
			double[] points = new DefaultHashVector().encodeGraph(bg);
			dataPoints.add(new HashVector(points));
		}

		int dim = dataPoints.get(0).getPoint().length;

		/* create the clusterer object that uses the specified distance
		 * measure and a maximum of 1000 iterations */
		vectorClusterer = 
			new KMeansPlusPlusClusterer<HashVector>(k, 1000, distMeasure);
		clusters = new ArrayList<CentroidCluster<HashVector>>();
		for (Cluster<HashVector> cl: vectorClusterer.cluster(dataPoints)) {
			clusters.add(centroidOf(cl.getPoints(), dim));
		}
	}

	/**
	 * Returns the list of clusters that were calculated when clustering
	 * the data points.
	 *
	 * @return a list of clusters
	 */
	public List<CentroidCluster<HashVector>> getClusters() {
		return clusters;
	}

	/**
	 * Gets the centers of the clusters.
	 *
	 * @return a list of hash vectors that are the centers of the clusters
	 */
	public List<HashVector> getCenters() {
		List<HashVector> res = new ArrayList<HashVector>(clusters.size());
		for (CentroidCluster<HashVector> cl: clusters) {
			res.add(new HashVector(cl.getCenter().getPoint()));
		}
		return res;
	}

	/**
	 * Utility function because CentroidCluster is not visible
	 * in cluster() method.
	 */
	private CentroidCluster<HashVector> 
		centroidOf(final Collection<HashVector> points, final int dim) {
		final double[] centroid = new double[dim];
		for (final HashVector v: points) {
			final double[] point = v.getPoint();
			for (int i = 0; i < centroid.length; i++) {
				centroid[i] += point[i];
			}
		}
		for (int i = 0; i < centroid.length; i++) {
			centroid[i] /= points.size();
		}
		return new CentroidCluster<HashVector>(new HashVector(centroid));
	}
}
