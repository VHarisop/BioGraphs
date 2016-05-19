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

import gr.demokritos.iit.jinsect.comparators.NGramGraphComparator;
import gr.demokritos.iit.jinsect.representations.NGramJGraph;
import gr.demokritos.biographs.*;
import gr.demokritos.biographs.io.BioInput;
import gr.demokritos.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.biographs.indexing.preprocessing.*;
import gr.demokritos.biographs.indexing.structs.Stats;
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DnaTestHashNaive {
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;

	/**
	 * Computes the distance between two biographs using their
	 * vector encodings.
	 */
	static double distance(BioGraph a, BioGraph b) {
		IndexVector vHash = new IndexVector(GraphType.DNA);

		int[] vecA = vHash.encodeGraph(a);
		int[] vecB = vHash.encodeGraph(b);
		return ClusterDistance.hamming(vecA, vecB);
	}

	/**
	 * Computes the distance between two graphs based on their
	 * value similarity - use this method for baseline performance.
	 */
	static double vSimDistance(BioGraph a, BioGraph b) {
		NGramJGraph nggA = a.getSuper(),
					nggB = b.getSuper();

		return new NGramGraphComparator().
			getSimilarityBetween(nggA, nggB).asDistance();
	}
	
	static int findBaselineMin(BioGraph bg, BioGraph[] bgs) {
		boolean unseen = true;
		double minDist = 0.0; int minIndex = 0, currIndex = 0;
		for (BioGraph b: bgs) {
			double dist = vSimDistance(bg, b);
			// no graph examined yet, initialize distance and index
			if (unseen) {
				minDist = dist;
				minIndex = currIndex;
				currIndex++;
				unseen = false;
				continue;
			}

			// update min index if new distance is better
			if (minDist > dist) {
				minDist = dist;
				minIndex = currIndex;
			}

			// to next index
			currIndex++;
		}
		return minIndex;
	}

	/**
	 * Finds the index of the closest matching graph
	 */
	static int findMin(BioGraph bg, BioGraph[] bgs) {
		boolean unseen = true;
		double minDist = 0.0; int minIndex = 0, currIndex = 0;
		for (BioGraph b: bgs) {
			// no graph examined yet, initialize distance and index
			if (unseen) {
				minDist = distance(bg, b);
				minIndex = currIndex;
				currIndex++;
				unseen = false;
				continue;
			}
			
			// update min index if new distance is better
			if (minDist > distance(bg, b)) {
				minDist = distance(bg, b);
				minIndex = currIndex;
			}

			// to next index
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

		BioGraph[] bGraphsData = null;
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[2];
		Stats statA = new Stats("vhash");
		Stats statB = new Stats("vsim_baseline");
		try {
			/* read biographs from dataset and mutated test file */
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsData = BioInput.fastaFileToGraphs(dataFile);
			bGraphsTest = BioInput.fastaFileToGraphs(testFile);
			String res;

			/* find the most similar one via exhaustive matching */
			for (BioGraph bg: bGraphsTest) {
				int minInd = findMin(bg, bGraphsData);
				int minSimInd = findBaselineMin(bg, bGraphsData);
				
				res = bGraphsData[minInd].getLabel();
				statA.addResult(bg.getLabel(), res);

				res = bGraphsData[minSimInd].getLabel();
				statB.addResult(bg.getLabel(), res);
			}

			/* output the results to json */
			statList[0] = statA;
			statList[1] = statB;
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
