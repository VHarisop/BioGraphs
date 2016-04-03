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
import gr.demokritos.biographs.indexing.inverted.*;
import gr.demokritos.biographs.indexing.structs.*;

import java.io.File;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RandEntryTest {
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;
	static int maxTolerance = 0;
	static int mutationNumber = 0;

	static Stats checkIndex
	(BioGraph[] bgs, RandEntryIndex invInd, int tolerance) {
		Stats stat = new Stats("entry_rand_index");
		stat.setDatabaseSize(invInd.getSize());
		stat.setMutations(mutationNumber);
		stat.setTolerance(tolerance);
		long maxTime = 0L, sumTime = 0L;
		for (BioGraph bg : bgs) {
			/* measure per-item query time to extract maximum and mean
			 * query times */
			long startTime = System.currentTimeMillis();
			Set<GraphIndexEntry> ans = invInd.getMatches(bg, tolerance);
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
				for (GraphIndexEntry gIn: ans) {
					labels[ind++] = gIn.getLabel();
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

		if (args.length >= 3) {
			maxTolerance = Integer.parseInt(args[2]);
		}

		if (args.length >= 4) {
			mutationNumber = Integer.parseInt(args[3]);
		}

		RandEntryIndex invInd = new RandEntryIndex();
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[maxTolerance + 1];
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsTest = BioGraph.fastaFileToGraphs(testFile);

			/* build index and measure building time */
			long startTime = System.currentTimeMillis();
			invInd.build(dataFile, GraphDatabase.GraphType.DNA);
			long stopTime = System.currentTimeMillis();
			System.err.printf("Building: %s s\n",
					String.valueOf((stopTime - startTime) / 1000.0));

			for (int tol = 0; tol <= maxTolerance; ++tol) {
				/* perform query, measure total and per-item query time */
				startTime = System.currentTimeMillis();
				statList[tol] = checkIndex(bGraphsTest, invInd, tol);
				stopTime = System.currentTimeMillis();
				System.err.printf("Total query: %s s\n",
						String.valueOf((stopTime - startTime) / 1000.0));
			}
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
