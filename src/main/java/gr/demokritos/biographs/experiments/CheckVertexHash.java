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
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;
import gr.demokritos.biographs.indexing.TreeDatabase;
import gr.demokritos.biographs.indexing.comparators.VertexHashComparator;
import gr.demokritos.biographs.indexing.structs.Stats;

import java.io.File;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CheckVertexHash {

	/**
	 * Default number of neighbours to seek 
	 */
	static int numNeighbours = 1;

	/**
	 * Default number of bins
	 */
	static int binNumber = 20;

	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;

	
	public static Stats buildAndPrint(
			TreeDatabase<String> trd,
			BioGraph[] bgs,
			String methodLabel)
	{
		try {
			trd.build(dataFile, GraphType.WORD);
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

	public static Stats checkHashVectorSim(BioGraph[] bgs) {
		TreeDatabase<String> trd = 
			new TreeDatabase<String>(new VertexHashComparator(binNumber)) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		return buildAndPrint(trd, bgs, "vertex_hash");
	}

	public static void main(String[] args) 
	throws NumberFormatException 
	{
		/* if no file was provided as argument, inform the user and exit */
		if (args.length < 2) {
			System.out.println("Missing file argument!");
			return; 
		}

		/* if argument was provided, set number of neighbours */
		if (args.length >= 3) {
			numNeighbours = Integer.parseInt(args[2]);
		}

		/* if argument was provided, set the number of bins for the hash */
		if (args.length >= 4) {
			binNumber = Integer.parseInt(args[3]);
		}

		BioGraph[] bGraphs = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphs = BioGraph.fromWordFile(testFile);

			List<Stats> statList = new ArrayList<Stats>();
			statList.add(checkHashVectorSim(bGraphs));
			
			/* print all the stats */
			System.out.println(
				gson.toJson(statList.toArray(new Stats[statList.size()])));
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
}
