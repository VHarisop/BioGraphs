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
import gr.demokritos.biographs.io.BioInput;
import gr.demokritos.biographs.indexing.TreeDatabase;
import gr.demokritos.biographs.indexing.comparators.*;
import gr.demokritos.biographs.indexing.structs.Stats;

import java.util.List;
import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OrdWeightTest {

	static int numNeighbours = 1;
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;

	/**
	 * Builds and prints a tree database
	 */
	static Stats buildAndPrint(
			TreeDatabase<String> trd,
			BioGraph[] bgs,
			String methodLabel) {

		try {
			trd.buildIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		Stats stat = new Stats(methodLabel);
		stat.setBins(trd);
		for (BioGraph b: bgs) {
			List<String> ans = trd.getKNearestNeighbours(b, true, numNeighbours);
			stat.addResult(b.getLabel(), ans.toArray(new String[ans.size()]));
		}
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
			numNeighbours = Integer.parseInt(args[2]);
		}

		TreeComparator tComp = new OrdWeightComparator();
		TreeDatabase<String> trdV = new TreeDatabase<String>(tComp) {
			@Override
			public String getGraphFeature(BioGraph bG) {
				return bG.getLabel();
			}
		};

		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[1];
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsTest = BioInput.fastaFileToGraphs(testFile);

			statList[0] = buildAndPrint(trdV, bGraphsTest, "ord_weight");
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
