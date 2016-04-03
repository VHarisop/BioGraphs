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
import gr.demokritos.biographs.algorithms.MaximumSpanningTree;
import gr.demokritos.biographs.indexing.databases.TrieDatabase;
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;
import gr.demokritos.biographs.indexing.structs.Stats;
import gr.demokritos.iit.jinsect.structs.*;

import java.io.File;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestTrie {

	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;
	static int mutationNumber = 0;

	static Stats checkNearestK(BioGraph[] bgs, TrieDatabase ntd, int K) {
		Stats stat = new Stats("trie_maxtree");
		stat.setMutations(mutationNumber);
		stat.setDatabaseSize(ntd.getSize());
		stat.setTolerance(0);
		long maxTime = 0L, sumTime = 0L;
		for (BioGraph bg : bgs) {
			long startTime = System.currentTimeMillis();
			List<String> res = ntd.select(bg);
			long stopTime = System.currentTimeMillis();
			if ((stopTime - startTime) > maxTime) {
				maxTime = stopTime - startTime;
			}
			sumTime += stopTime - startTime;
			String[] resLabels = res.toArray(new String[res.size()]);
			stat.addResult(bg.getLabel(), resLabels);
		}
		stat.setTimes(maxTime, sumTime, bgs.length);
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

		if (args.length >= 3) {
			mutationNumber = Integer.parseInt(args[2]);
		}

		TrieDatabase ntD = new TrieDatabase() {
			@Override
			protected String getGraphCode(BioGraph bG) {
				String res = "";
				for (Edge e: new MaximumSpanningTree(bG).getTreeEdges()) {
					res += e.getLabels();
				}
				return res;
			}
		};
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[1];
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsTest = BioGraph.fastaFileToGraphs(testFile);

			ntD.build(dataFile, GraphType.DNA);
			statList[0] = checkNearestK(bGraphsTest, ntD, 1);
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
