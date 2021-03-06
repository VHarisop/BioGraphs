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
import gr.demokritos.biographs.indexing.inverted.*;
import gr.demokritos.biographs.indexing.structs.*;
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;

import java.io.File;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ExactEntryTest {
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;
	static int maxTolerance = 0;

	static Stats 
	checkIndex(BioGraph[] bgs, EntryInvertedIndex invInd) {
		Stats stat = new Stats("entry_smart_index_exact");
		stat.setDatabaseSize(invInd.getSize());
		stat.setMutations(0);
		stat.setTolerance(0);
		long maxTime = 0L, sumTime = 0L;
		for (BioGraph bg : bgs) {
			/* measure per-item query time to extract maximum and mean
			 * query times */
			long startTime = System.currentTimeMillis();
			// Set<BioGraph> ans = new HashSet<BioGraph>();
			Set<GraphIndexEntry> ans = invInd.getExactMatches(bg);
			long stopTime = System.currentTimeMillis();
			if ((stopTime - startTime) > maxTime) {
				maxTime = stopTime - startTime;
			}
			sumTime += stopTime - startTime;

			if (ans == null || ans.size() == 0) {
				stat.addResult(bg.getLabel(), "None");
			}
			else {
				String[] labels = new String[ans.size()]; int ind = 0;
				for (GraphIndexEntry bIn: ans) {
					labels[ind++] = bIn.getLabel();
				}
				stat.addResult(bg.getLabel(), labels);
			}
		}
		stat.setTimes(maxTime, sumTime, bgs.length);
		stat.setBins(invInd.binSizes());
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
		
		EntryInvertedIndex invInd = new EntryInvertedIndex();
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[1];
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsTest = BioInput.fastaFileToGraphs(testFile);

			/* build index and measure building time */
			long startTime = System.currentTimeMillis();
			invInd.build(dataFile, GraphType.DNA);
			long stopTime = System.currentTimeMillis();
			System.err.printf("Building: %s s\n",
					String.valueOf((stopTime - startTime) / 1000.0));

			/* perform query, measure total and per-item query time */
			startTime = System.currentTimeMillis();
			statList[0] = checkIndex(bGraphsTest, invInd);
			stopTime = System.currentTimeMillis();
			System.err.printf("Total query: %s s\n",
					String.valueOf((stopTime - startTime) / 1000.0));
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
