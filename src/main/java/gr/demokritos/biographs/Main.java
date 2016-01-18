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

package gr.demokritos.biographs;

import gr.demokritos.biographs.indexing.TreeDatabase;
import gr.demokritos.biographs.indexing.CanonicalCodeComparator;
import gr.demokritos.biographs.indexing.TwoLevelSimComparator;
import gr.demokritos.iit.jinsect.jutils;

import java.io.File;
import java.util.*;

public class Main {
	public static void main(String[] args) {

		/* if no file was provided as argument, inform the user and exit */
		if (args.length < 2) {
			System.out.println("Missing file argument!");
			return; 
		}

		/* Create a TreeDatabase ordered by canonical coding */
		TreeDatabase<BioGraph> trdCanon = 
			new TreeDatabase<BioGraph>(new CanonicalCodeComparator()) {
				@Override
				public BioGraph getGraphFeature(BioGraph bG) {
					return bG;
				}
			};	

		/* Create a TreeDatabase ordered by a two-level comparator */
		TreeDatabase<BioGraph> trdSim = 
			new TreeDatabase<BioGraph>(new TwoLevelSimComparator()) {
				@Override
				public BioGraph getGraphFeature(BioGraph bG) {
					return bG;
				}
			};
		/* set the database to use a two level comparator */

		BioGraph[] bgs;
		/* try to get the files and build the index and the biographs
		 * exit if the files don't exist or something goes wrong */
		try {
			File dataFile = new File(args[0]);
			File testFile = new File(args[1]);

			trdCanon.buildWordIndex(dataFile);
			trdSim.buildWordIndex(dataFile);
			bgs = BioGraph.fromWordFile(testFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return; 
		}
		
		double sSim;
		System.out.println("CCODE");
		for (BioGraph bg: bgs) {
			List<BioGraph> near = trdCanon.getKNearestNeighbours(bg, true, 2);
			System.out.printf("\n%s: ", bg.getLabel());
			for (BioGraph b: near) {
				sSim = 
					jutils.graphStructuralSimilarity(bg.getGraph(), b.getGraph());
				System.out.printf("%s (%.3f) ", b.getLabel(), sSim);
			}
		}
		System.out.println("\n");
		System.out.println("2SIM");
		for (BioGraph bg: bgs) {
			List<BioGraph> near = trdSim.getKNearestNeighbours(bg, true, 2);
			System.out.printf("\n%s: ", bg.getLabel());
			for (BioGraph b: near) {
				sSim = 
					jutils.graphStructuralSimilarity(bg.getGraph(), b.getGraph());
				System.out.printf("%s (%.3f) ", b.getLabel(), sSim);
			}
		}
		System.out.println("\n");
	}
}
