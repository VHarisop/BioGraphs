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
import java.io.File;
import java.io.FileFilter;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.Utils;
import gr.demokritos.biographs.algorithms.*;
import gr.demokritos.ntree.*;
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.biographs.indexing.preprocessing.DefaultHashVector;


/**
 * An abstract class that implements a graph database using graph similarity.
 * Here, the similarity measure used is the graphs' structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.jutils}
 *
 * @author VHarisop
 */
public class ClusterHybridDatabase extends GraphDatabase {
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
	 * The number of hashing bins
	 */
	protected int numBins;

	/**
	 * The branching factor of the trees
	 */
	protected int branching;

	/**
	 * A list of centroids resulting from clustering.
	 */
	protected List<HashVector> centroids;

	/**
	 * A hashmap that contains biograph assignments to centroids.
	 */
	protected HashMap<HashVector, List<BioGraph>> clusters;
	protected HashMap<HashVector, NTreeDatabase> trees;

	/**
	 * The number of iterations to be used in the clustering phase.
	 */
	protected int iters;
	/**
	 * Creates a blank ClusterHybridDatabase object with a specified
	 * number of clusters to be used and a custom number of iterations.
	 *
	 * @param numClusters the number of clusters to use
	 */
	public ClusterHybridDatabase(int numClusters, int iters) { 
		super();
		initIndex(16, 26, numClusters, iters);

	}
	
	/**
	 * Creates a blank ClusterHybridDatabase object using a specified
	 * branching factor for its subtrees, a specified number of bins
	 * for hashing and a custom number of iterations.
	 *
	 * @param nClusters the number of clusters to use
	 * @param n the branching factor to be used
	 * @param nBins the number of bins for hashing
	 * @param iters the number of iterations to be used for clustering
	 */
	public ClusterHybridDatabase(int nClusters, int n, int nBins, int iters) { 
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
		this.numBins = nBins;
		this.branching = n;
		this.clusters = new HashMap<HashVector, List<BioGraph>>(nClusters);
		this.numClusters = nClusters;
		this.iters = iters;
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
			/* cluster the graphs and collect centers first */
			graphClusterer = new Clustering(bgs, this.numClusters, this.iters);
			centroids = graphClusterer.getCenters();
			for (BioGraph bG: bgs) {
				addToClusters(bG);
			}
			buildNTrees();
		}
		else {
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			List<BioGraph> allGraphs = new ArrayList<BioGraph>();
			// gather all graphs into the list
			for (File f: fileList) {
				BioGraph[] bgs = BioGraph.fastaFileToGraphs(f);
				for (BioGraph bG: bgs) {
					allGraphs.add(bG);
				}
			}
			BioGraph[] bgArray =
				allGraphs.toArray(new BioGraph[allGraphs.size()]);

			/* cluster all the graphs */
			graphClusterer =
				new Clustering(bgArray, this.numClusters, this.iters);
			centroids = graphClusterer.getCenters();

			/* add graphs to their assigned clusters */
			for (BioGraph bG: bgArray) {
				addToClusters(bG);
			}
			buildNTrees();
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

			/* cluster the graphs first, collect centers */
			graphClusterer = new Clustering(bgs, this.numClusters, this.iters);
			centroids = graphClusterer.getCenters();
			for (BioGraph bG: bgs) {
				addToClusters(bG);
			}
			buildNTrees();
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
			graphClusterer = 
				new Clustering(graphArray, this.numClusters, this.iters);
			centroids = graphClusterer.getCenters();
			for (BioGraph bG: allGraphs) {
				addToClusters(bG);
			}
			buildNTrees();
		}
	}

	private void buildNTrees() {
		trees = new HashMap<HashVector, NTreeDatabase>(clusters.size());
		for (Map.Entry<HashVector, List<BioGraph>> hv: clusters.entrySet()) {
			NTreeDatabase ntd = new NTreeDatabase(branching, numBins);
			for (BioGraph bG: hv.getValue()) {
				ntd.addGraph(bG);
			}
			trees.put(hv.getKey(), ntd);
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
	 * Finds the centroid that is closest to the encoding vector
	 * of a BioGraph.
	 */
	private int findClosestCluster(BioGraph bG) {
		DefaultHashVector dhv = new DefaultHashVector().withBins(26);
		double[] graphVec = dhv.encodeGraph(bG);

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
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioGraph object to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		return;
	}

	/**
	 * Gets the nearest neighbour of a query graph.
	 *
	 * @param bg the query graph
	 * @return the nearest neighbouring graph
	 */
	public BioGraph getNearestNeighbour(BioGraph bg) {
		/* get the centroid closest to the graph */
		HashVector closest =  centroids.get(findClosestCluster(bg));
		NTreeDatabase cand = trees.get(closest); 
		return cand.getNearestNeighbour(bg);
	}

	/**
	 * Gets the K nearest neighbours to a query {@link BioGraph} object
	 * via the approximation of nearest neighbours offered by {@link NTree}'s
	 * function interface.
	 * 
	 * @param bg the query graph
	 * @param K the number of neighbours to look for
	 * @return an array containing the K nearest neighbours
	 */
	public BioGraph[] getKNearestNeighbours(BioGraph bg, int K) {
		HashVector closest = centroids.get(findClosestCluster(bg));
		NTreeDatabase cand = trees.get(closest);
		return cand.getKNearestNeighbours(bg, K);
	}
}
