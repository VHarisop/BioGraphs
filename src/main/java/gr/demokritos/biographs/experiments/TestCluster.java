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
import gr.demokritos.biographs.indexing.databases.ClusterGraphDatabase;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestCluster {

	static int numBins = 20;
	static int numBranches = 8;
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;

	static Stats checkNearest(BioGraph[] bgs, ClusterGraphDatabase cgd) {
		Stats stat = new Stats("cluster");
		for (BioGraph bg : bgs) {
			BioGraph bGraph = cgd.getNearestNeighbour(bg);
			stat.addResult(bg.getLabel(), new String[] { bGraph.getLabel() });
		}
		for (int i: cgd.getClusterSizes()) {
			System.out.printf("%d ", i);
		}
		System.out.println();
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

		/* if argument was provided, set the number of bins for the hash */
		if (args.length >= 3) {
			numBins = Integer.parseInt(args[2]);
		}

		if (args.length >= 4) {
			numBranches = Integer.parseInt(args[3]);
		}

		ClusterGraphDatabase cgd = new ClusterGraphDatabase();
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[1];
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsTest = BioGraph.fromWordFile(testFile);

			cgd.buildWordIndex(dataFile);
			statList[0] = checkNearest(bGraphsTest, cgd);
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
