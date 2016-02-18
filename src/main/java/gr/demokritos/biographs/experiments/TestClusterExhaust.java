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

package gr.demokritos.biographs.experiments;

import gr.demokritos.biographs.*;
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.biographs.indexing.databases.ClusterGraphDatabase;
import gr.demokritos.biographs.indexing.structs.Stats;

import java.io.File;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestClusterExhaust {
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;
	
	/**
	 * Finds the closest centroid to a list of query biographs, and returns
	 * the closest biograph to the query graph that belongs to the same cluster
	 * that is defined by that centroid.
	 */
	static Stats getKNearest(BioGraph[] bgs, ClusterGraphDatabase cgd, int K) {
		Stats stat = new Stats("cluster_exhaustive_" + String.valueOf(K) + "NN");
		for (BioGraph bg: bgs) {
			List<BioGraph> res = cgd.kNearestNeighbours(bg, K);
			String[] labels = new String[res.size()]; int index = 0;
			for (BioGraph r: res) {
				labels[index++] = r.getLabel();
			}
			stat.addResult(bg.getLabel(), labels);
		}
		stat.setBins(cgd.getClusterSizes());
		return stat;
	}

	/**
	 * Finds the closest centroid to a list of query biographs, and returns
	 * the closest biograph to the query graph that belongs to the same cluster
	 * that is defined by that centroid.
	 */
	static Stats getNearest(BioGraph[] bgs, ClusterGraphDatabase cgd) {
		Stats stat = new Stats("cluster_exhaustive");
		for (BioGraph bg: bgs) {
			BioGraph res = cgd.getNearestNeighbour(bg);
			stat.addResult(bg.getLabel(), new String[] { res.getLabel() });
		}
		stat.setBins(cgd.getClusterSizes());
		return stat;
	}

	public static void main(String[] args) 
	throws NumberFormatException 
	{
		/* if no file was provided as argument, inform the user and exit */
		if (args.length < 2) {
			System.out.println("Missing file argument!");
			return; 
		}

		int numClusters = 25;
		int iters = 100;
		boolean useDna = false;
		if (args.length >= 3) {
			numClusters = Integer.parseInt(args[2]);
		}

		if (args.length >= 4) {
			iters = Integer.parseInt(args[3]);
		}

		if (args.length >= 5) {
			if (args[4].equals("dna")) {
				useDna = true;
			}
			else {
				useDna = false;
			}
		}

		int numNeighbours = 3;
		if (args.length >= 6) {
			numNeighbours = Integer.parseInt(args[5]);
		}

		ClusterGraphDatabase cgd = new ClusterGraphDatabase(numClusters, iters);
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[2];
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			if (useDna) {
				bGraphsTest = BioGraph.fastaFileToGraphs(testFile);
				cgd.build(dataFile, GraphDatabase.GraphType.DNA);
			}
			else {
				bGraphsTest = BioGraph.fromWordFile(testFile);
				cgd.build(dataFile, GraphDatabase.GraphType.WORD);
			}

			statList[0] = getNearest(bGraphsTest, cgd);
			statList[1] = getKNearest(bGraphsTest, cgd, numNeighbours);
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
