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
import gr.demokritos.biographs.indexing.inverted.*;
import gr.demokritos.biographs.indexing.structs.Stats;

import java.io.File;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class TestInverted {
	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;

	static Stats checkIndex(BioGraph[] bgs, InvertedIndex invInd) {
		Stats stat = new Stats("inv_index");
		for (BioGraph bg : bgs) {
			Set<BioGraph> ans = invInd.getMatches(bg);
			if (ans == null || ans.size() == 0) {
				stat.addResult(bg.getLabel(), new String[] { "None" });
			}
			else {
				String[] labels = new String[ans.size()]; int ind = 0;
				for (BioGraph bIn: ans) {
					labels[ind++] = bIn.getLabel();
				}
				stat.addResult(bg.getLabel(), labels);
			}
		}
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

		InvertedIndex invInd = new InvertedIndex();
		BioGraph[] bGraphsTest = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		Stats[] statList = new Stats[1];
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphsTest = BioGraph.fastaFileToGraphs(testFile);

			invInd.buildIndex(dataFile);
			statList[0] = checkIndex(bGraphsTest, invInd);
			System.out.println(gson.toJson(statList));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return;
	}
}
