/* Copyright (C) 2016 VHarisop
 * This file is part of BioGraphs.
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

package gr.demokritos.biographs.indexing.databases;

import java.util.*;

import org.apache.commons.math3.util.Pair;

import java.io.File;
import java.io.FileFilter;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.Utils;
import gr.demokritos.biographs.algorithms.*;
import gr.demokritos.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.ntree.*;
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.biographs.indexing.comparators.DefaultHashComparator;
import gr.demokritos.biographs.indexing.preprocessing.*;


/**
 * An abstract class that implements a graph database using graph similarity.
 * Here, the similarity measure used is the graphs' structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.jutils}
 *
 * @author VHarisop
 */
public class ClusterGraphDatabase extends GraphDatabase {
	/**
	 * An {@link NTree} to be used for in-cluster indexing.
	 */
	protected NTree<BioGraph> treeIndex;

	/**
	 * A custom comparator to be used for {@link #treeIndex} that
	 * compares graphs based on a distance metric.
	 */
	protected NodeComparator<BioGraph> bgComp;

	/**
	 * A clusterer to be used for clustering biographs in a
	 * preprocessing step.
	 */
	protected Clustering graphClusterer;

	/**
	 * The number of clusters to use.
	 */
	protected int numClusters;

	/**
	 * A list of centroids resulting from clustering.
	 */
	protected List<HashVector> centroids;

	/**
	 * A hashmap that contains biograph assignments to centroids.
	 */
	protected HashMap<HashVector, List<BioGraph>> clusters;

	/**
	 * The number of iterations to be used in the clustering phase.
	 */
	protected int iters;

	/**
	 * Indicates if DNA data are represented, to use an alternative
	 * hashing strategy.
	 */
	protected boolean usesDna = false;

	/**
	 * The hash vector generator to be used - this will be affected
	 * by {@link #usesDna} since a different hashing strategy is used
	 * for DNA graph vertices.
	 */
	protected DefaultHashVector dhv;
	
	/**
	 * Creates a blank ClusterGraphDatabase object with a specified
	 * number of clusters to be used and a custom number of iterations.
	 *
	 * @param numClusters the number of clusters to use
	 */
	public ClusterGraphDatabase(int numClusters, int iters) { 
		super();
		initIndex(8, 26, numClusters, iters);
	}
	
	/**
	 * Creates a blank ClusterGraphDatabase object using a specified
	 * branching factor for its subtrees, a specified number of bins
	 * for hashing and a custom number of iterations.
	 *
	 * @param nClusters the number of clusters to use
	 * @param n the branching factor to be used
	 * @param nBins the number of bins for hashing
	 * @param iters the number of iterations to be used for clustering
	 */
	public ClusterGraphDatabase(int nClusters, int n, int nBins, int iters) { 
		super();
		initIndex(n, nBins, nClusters, iters);
	}

	/**
	 * Initialize the database.
	 *
	 * @param n the tree's branching factor
	 * @param nBins the number of bins to use
	 * @param nClusters the number of initial clusters
	 * @param iters the specified number of iterations for clustering
	 */
	protected void initIndex(int n, int nBins, int nClusters, int iters) {
		this.bgComp = new DefaultHashComparator(nBins);
		this.treeIndex = new NTree<BioGraph>(bgComp, nBins);
		this.clusters = new HashMap<HashVector, List<BioGraph>>(nClusters);
		this.numClusters = nClusters;
		this.iters = iters;
	}

	/**
	 * Builds a graph database index from a given file or directory of files
	 * that contains graphs that represent a specified data type.
	 *
	 * @param fPath the file or directory to get the data from
	 * @param type the {@link GraphDatabase.GraphType} of the database
	 * @throws Exception in case the file or directory doesn't exist or
	 * the data cannot be read
	 */
	public void build(File fPath, GraphType type) throws Exception {
		switch (type) {
			case WORD:
				this.usesDna = false;
				dhv = new DefaultHashVector().withBins(26);
				buildWordIndex(fPath);
				break;
			case DNA:
			default:
				this.usesDna = true;
				dhv = new DefaultHashVector(new DinucleotideHash()).withBins(10);
				this.bgComp = new DefaultHashComparator(10);
				buildIndex(fPath);
		}
	}

	/**
	 * @see #build(File, GraphType) build
	 */
	public void build(String path, GraphType type) throws Exception {
		build(new File(path), type);
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	@Override
	public void buildIndex(String path) throws Exception {
		File fPath = new File(path);
		buildIndex(fPath);
	}

	/**
	 * Builds a graph database index from a given file or a directory 
	 * of files.
	 *
	 * @param fPath a path containing one or multiple files
	 */
	@Override
	public void buildIndex(File fPath) throws Exception {
		if (!fPath.isDirectory()) {
			BioGraph[] bgs = BioGraph.fastaFileToGraphs(fPath);
			initClusters(bgs);
		}
		else {
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			// populate a list with all the graphs
			List<BioGraph> bgList = new ArrayList<BioGraph>();
			for (File f: fileList) {
				BioGraph[] bgs = BioGraph.fastaFileToGraphs(f);
				for (BioGraph bG: bgs) {
					bgList.add(bG);
				}
			}
			BioGraph[] bgArray = bgList.toArray(new BioGraph[bgList.size()]);
			initClusters(bgArray);
		}
	}

	/**
	 * Clusters a set of {@link BioGraph} objects, computing the centroids of
	 * the representation, and then adds each graph to its closest cluster.
	 * 
	 * @param graphs an array of {@link BioGraph} objects to be clustered
	 */
	protected void initClusters(BioGraph[] graphs) {
		if (this.usesDna) {
			graphClusterer = 
				new Clustering(graphs, numClusters, iters, new DinucleotideHash());
		}
		else {
			graphClusterer = 
				new Clustering(graphs, numClusters, iters);
		}
		centroids = graphClusterer.getCenters();
		for (BioGraph bG: graphs) {
			addToClusters(bG);
		}
	}

	/**
	 * Builds a graph database index from a given file or directory 
	 * of files which contain words without extra labels, as in the
	 * case of FASTA files.
	 *
	 * @param fPath a path pointing to a file or directory 
	 */
	public void buildWordIndex(File fPath) throws Exception {
		if (!fPath.isDirectory()) {
			BioGraph[] bgs = BioGraph.fromWordFile(fPath);
			/* collect centers and cluster the graphs */
			initClusters(bgs);
		}
		else {
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			List<BioGraph> allGraphs = new ArrayList<BioGraph>();
			// add them all to the database
			for (File f: fileList) {
				BioGraph[] bgs = BioGraph.fromWordFile(f);
				for (BioGraph bG: bgs) {
					allGraphs.add(bG);
				}
			}
			BioGraph[] graphArray = 
				allGraphs.toArray(new BioGraph[allGraphs.size()]);

			/* cluster the resulting array of graphs */
			initClusters(graphArray);
		}
	}

	/**
	 * Adds biograph <tt>bG</tt> to a cluster with index <tt>index</tt>
	 */
	private void addToClusters(BioGraph bG) {
		int clIndex = findClosestCluster(bG);
		HashVector center = centroids.get(clIndex);

		/* if this cluster hasn't been initialized, do it now */
		if (!(clusters.containsKey(center))) {
			List<BioGraph> graphs = new ArrayList<BioGraph>();
			graphs.add(bG);
			clusters.put(center, graphs);
		}
		else {
			/* add graph to cluster's list of already assigned graphs */
			List<BioGraph> graphs = clusters.get(center);
			graphs.add(bG);
			clusters.put(center, graphs);
		}
	}

	/**
	 * Returns an array containing the number of graphs assigned
	 * to each of the database's centroids.
	 *
	 * @return an array of cluster sizes
	 */
	public int[] getClusterSizes() {
		int[] toRet = new int[clusters.size()]; int index = 0;
		for (List<BioGraph> bList: clusters.values()) {
			toRet[index++] = bList.size();
		}
		return toRet;
	}

	/**
	 * Finds the index of the centroid that is closest to the encoding
	 * vector of a BioGraph.
	 *
	 * @param bG the query biograph
	 * @return the index of the closest centroid
	 */
	private int findClosestCluster(BioGraph bG) {
		double[] graphVec = this.dhv.encodeGraph(bG);

		boolean unset = true;
		int minIndex = 0, tempIndex = 0;
		double minDist = 0.0, tempDist;
		for (HashVector cl: centroids) {
			double[] clVec = cl.getPoint();
			try {
				tempDist = Utils.getHammingDistance(graphVec, clVec);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				tempDist = 0.0;
			}
			if (unset) {
				minDist = tempDist;
				unset = false;
				tempIndex++;
				continue;
			}
			if (minDist > tempDist) {
				minDist = tempDist;
				minIndex = tempIndex;
			}
			tempIndex++;
		}
		return minIndex;
	}

	/**
	 * Finds the K closest centroids to a query graph.
	 */
	private int[] findKClosestClusters(BioGraph bG, int K) {
		double[] graphVec = this.dhv.encodeGraph(bG);

		// array of index - distance pairs
		List<Pair<Integer, Double>> pairs =
			new ArrayList<Pair<Integer, Double>>();
		double weight; int index = 0;
		for (HashVector cl: centroids) {
			double[] clVec = cl.getPoint();
			try {
				weight = Utils.getHammingDistance(graphVec, clVec);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				weight = Double.MAX_VALUE;
			}
			/* add to array of pairs */
			pairs.add(Pair.create(index, weight));
			index++;
		}
		Collections.sort(pairs, new Comparator<Pair<Integer, Double>>() {
			@Override
			public int compare(
					Pair<Integer, Double> a,
					Pair<Integer, Double> b)
			{
				return a.getSecond().compareTo(b.getSecond());
			}
		});

		/* get the indices of the K closest centroids */
		int[] kClosest = new int[Math.min(K, pairs.size())];
		for (int i = 0; i < Math.min(K, pairs.size()); ++i) {
			kClosest[i] = pairs.get(i).getFirst();
		}
		return kClosest;
	}

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioGraph object to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		treeIndex.addData(bg);
	}

	/**
	 * Gets the nearest neighbour of a query graph.
	 *
	 * @param bg the query graph
	 * @return the nearest neighbouring graph
	 */
	public BioGraph getNearestNeighbour(BioGraph bg) {
		/* get the centroid closest to the graph and return the closest
		 * biograph in the cluster that contains that centroid
		 */
		return getClosestInCluster(bg, findClosestCluster(bg));
	}

	/**
	 * Finds the biograph closest to a query biograph in a specified cluster.
	 *
	 * @param bg the query graph
	 * @param centroidIndex the index of the centroid of the cluster
	 * @return the closest biograph to the query in that cluster
	 */
	protected BioGraph getClosestInCluster(BioGraph bg, int centroidIndex) {
		HashVector centroid = centroids.get(centroidIndex);
		List<BioGraph> cand = clusters.get(centroid);
		double minDist = ClusterDistance.hamming(bg, cand.get(0));
		int minIndex = 0, currIndex = 0;

		for (BioGraph cGraph: cand) {
			double dist = ClusterDistance.hamming(bg, cGraph);
			if (dist < minDist) {
				minDist = dist;
				minIndex = currIndex;
			}
			currIndex++;
		}
		return cand.get(minIndex);
	}

	/**
	 * Gets the k-nearest neighbours of a biograph, which are the biographs
	 * closest to the query graph in k distinct clusters.
	 *
	 * @param bg the query graph
	 * @param K the number of neighbours to return
	 */
	public List<BioGraph> kNearestNeighbours(BioGraph bg, int K) {
		int retSize = Math.min(K, centroids.size());
		int[] kCenters = findKClosestClusters(bg, retSize);
		List<BioGraph> results = new ArrayList<BioGraph>(retSize);
		for (int i = 0; i < retSize; ++i) {
			results.add(getClosestInCluster(bg, kCenters[i]));
		}
		return results;
	}
}
