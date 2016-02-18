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

package gr.demokritos.biographs.algorithms;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.Utils;
import gr.demokritos.biographs.indexing.preprocessing.DefaultHashVector;
import gr.demokritos.biographs.indexing.preprocessing.HashingStrategy;
import gr.demokritos.iit.jinsect.structs.JVertex;

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
	 * The vector of the points' mean values for each dimension.
	 */
	protected double[] pop_mean;

	/**
	 * The vector of the points' standard deviations for each dimension.
	 */
	protected double[] pop_dev;

	/**
	 * The list of clusters.
	 */
	protected List<CentroidCluster<HashVector>> clusters;

	/**
	 * The distance measure used in clustering, with the
	 * default set to be the hamming distance of the standardized
	 * version of the vectors.
	 */
	protected DistanceMeasure distMeasure = new DistanceMeasure() {
		static final long serialVersionUID = 1L;
		@Override
		public double compute(double[] vecA, double[] vecB) {
			double dist = 0.0;
			vecA = Utils.standardize(vecA, pop_mean, pop_dev);
			vecB = Utils.standardize(vecB, pop_mean, pop_dev);
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
	 * objects, and performs clustering on them using a specified number
	 * of iterations and initial clusters.
	 *
	 * @param bGraphs the array of graphs
	 * @param k the number of initial clusters
	 * @param iters the number of iterations
	 */
	public Clustering(BioGraph[] bGraphs, int k, int iters) {
		dataPoints = new ArrayList<HashVector>(bGraphs.length);

		/* add the vectors of all graphs to the data points */
		for (BioGraph bg: bGraphs) {
			double[] points = new DefaultHashVector().encodeGraph(bg);
			HashVector vec = new HashVector(points); vec.setGraph(bg);
			dataPoints.add(vec);
		}
		/* Compute feature means and standard deviations to standardize
		 * the vectors for each point */
		this.pop_mean = computeFeatureMeans(dataPoints);
		this.pop_dev = computeFeatureDeviations(dataPoints, this.pop_mean);

		int dim = dataPoints.get(0).getPoint().length;
		/* create the clusterer object that uses the specified distance
		 * measure and a maximum of 1000 iterations */
		vectorClusterer =
			new KMeansPlusPlusClusterer<HashVector>(k, iters, distMeasure);
		clusters = new ArrayList<CentroidCluster<HashVector>>();
		for (Cluster<HashVector> cl: vectorClusterer.cluster(dataPoints)) {
			clusters.add(centroidOf(cl.getPoints(), dim));
		}
	}
	
	/**
	 * Creates a new Clustering object given an array of {@link BioGraph}
	 * objects, and performs clustering on them using a specified number
	 * of iterations, initial clusters and a custom {@link HashingStrategy}.
	 *
	 * @param bGraphs the array of graphs
	 * @param k the number of initial clusters
	 * @param iters the number of iterations
	 */
	public Clustering(
			BioGraph[] bGraphs,
			int k,
			int iters,
			HashingStrategy<JVertex> hsg)
	{
		DefaultHashVector hvec = new DefaultHashVector(hsg).withBins(10);
		dataPoints = new ArrayList<HashVector>(bGraphs.length);

		/* add the vectors of all graphs to the data points */
		for (BioGraph bg: bGraphs) {
			double[] points = hvec.encodeGraph(bg);
			HashVector vec = new HashVector(points); vec.setGraph(bg);
			dataPoints.add(vec);
		}
		/* Compute feature means and standard deviations to standardize
		 * the vectors for each point */
		this.pop_mean = computeFeatureMeans(dataPoints);
		this.pop_dev = computeFeatureDeviations(dataPoints, this.pop_mean);

		int dim = dataPoints.get(0).getPoint().length;
		/* create the clusterer object that uses the specified distance
		 * measure and a maximum of 1000 iterations */
		vectorClusterer = 
			new KMeansPlusPlusClusterer<HashVector>(k, iters, distMeasure);
		clusters = new ArrayList<CentroidCluster<HashVector>>();
		for (Cluster<HashVector> cl: vectorClusterer.cluster(dataPoints)) {
			clusters.add(centroidOf(cl.getPoints(), dim));
		}
	}

	/**
	 * Computes the means of a list of vector points for every dimension
	 * of the feature vector.
	 *
	 * @param points the list of points
	 * @return an array of length equal to the dimension of the feature
	 * vectors containing the means of each feature
	 */
	protected double[] computeFeatureMeans(List<HashVector> points) {
		/* create a vector of means with length equal to the dimension
		 * of the data points */
		double[] means = new double[points.get(0).getPoint().length];

		/* for each hash vector, sum the feature at the proper dimension */
		for (HashVector hv: points) {
			for (int i = 0; i < means.length; ++i) {
				means[i] += hv.getPoint()[i];
			}
		}

		/* calculate the mean at the proper dimension */
		for (int i = 0; i < means.length; ++i) {
			means[i] /= points.size();
		}
		return means;
	}

	protected double[]
	computeFeatureDeviations(List<HashVector> points, double[] means) {
		double[] stdev = new double[points.get(0).getPoint().length];

		/* for all points, sum the squared difference from the mean
		 * for each dimension of the feature vector
		 */
		for (HashVector hv: points) {
			double[] pt = hv.getPoint();
			for (int i = 0; i < means.length; ++i) {
				stdev[i] += (pt[i] - means[i]) * (pt[i] - means[i]);
			}
		}

		/* compute the value /N (variance) and then take the square root
		 * to compute standard deviation
		 */
		for (int i = 0; i < stdev.length; ++i) {
			stdev[i] /= points.size();
			stdev[i] = Math.sqrt(stdev[i]);
		}

		return stdev;
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
		for (int i = 0; i < dim; ++i) {
			centroid[i] = 0.0;
		}
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
