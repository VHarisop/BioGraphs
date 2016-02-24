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
import gr.demokritos.biographs.indexing.preprocessing.HashedVector;
import gr.demokritos.biographs.indexing.structs.Stats;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestHashNaive {

	static int numBins = 20;
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;

	private static double distance(BioGraph a, BioGraph b) {
		HashedVector vHash = new HashedVector().withBins(numBins);
		double[] vecA = vHash.encodeGraph(a.getGraph());
		double[] vecB = vHash.encodeGraph(b.getGraph());
		double ret = 0.0;
		try {
			ret = Utils.getHammingDistance(vecA, vecB);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
	}
	public static int findMin(BioGraph bg, BioGraph[] bgs) {
		boolean unseen = true;
		double minDist = 0.0; int minIndex = 0, currIndex = 0;
		for (BioGraph b: bgs) {
			if (unseen) {
				minDist = distance(bg, b);
				minIndex = currIndex;
				currIndex++;
				unseen = false;
				continue;
			}
			
			if (minDist > distance(bg, b)) {
				minDist = distance(bg, b);
				minIndex = currIndex;
			}
			currIndex++;
		}
		return minIndex;
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

		BioGraph[] bGraphsData = null;
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[1];
		Stats stat = new Stats("vhash");
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsData = BioGraph.fromWordFile(dataFile);
			bGraphsTest = BioGraph.fromWordFile(testFile);

			for (BioGraph bg: bGraphsTest) {
				int minInd = findMin(bg, bGraphsData);
				String res = bGraphsData[minInd].getLabel();
				stat.addResult(bg.getLabel(), res);
			}
			statList[0] = stat;
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
