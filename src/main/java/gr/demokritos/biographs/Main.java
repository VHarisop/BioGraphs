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
import gr.demokritos.biographs.indexing.QuantTreeDatabase;
import gr.demokritos.biographs.indexing.CanonicalCodeComparator;
import gr.demokritos.biographs.indexing.TwoLevelSimComparator;

import gr.demokritos.iit.jinsect.jutils;

import java.io.File;
import java.util.*;

public class Main {

	public static void checkSim(File dataFile, File testFile) {
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
		
		BioGraph[] bgs;

		/* try to build the index and the biographs */
		try {
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

	public static void checkQuant(File dataFile, File testFile) {
		QuantTreeDatabase<String> qtd = 
			new QuantTreeDatabase<String>() {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};
		BioGraph[] bgs;
		try {
			qtd.buildWordIndex(dataFile);
			bgs = BioGraph.fromWordFile(testFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		/* TODO: getNearestNeighbours is problematic!!!! */
		System.out.println("QUANT");

		/* Iterate and print all keys 
		for (Map.Entry<String, Double> e: 
				qtd.getWeightMap().entrySet()) 
		{
			System.out.printf("\t[%s]: %3.3f\n", e.getKey(), e.getValue());
		}
		*/
		System.out.println("DATA");

		/* Query all test graphs for nearest neighbours */
		for (BioGraph bG: bgs) {
			List<String> ans = qtd.getNearestNeighbours(bG, true);
			if (ans == null) {
				throw new NullPointerException();
			}
			else {
				System.out.printf("%s:", bG.getLabel());
				for (String s: ans) {
					System.out.printf(" %s", s);
				}
				System.out.printf("\n");
			}
		}
	}

	public static void main(String[] args) {
		/* if no file was provided as argument, inform the user and exit */
		if (args.length < 2) {
			System.out.println("Missing file argument!");
			return; 
		}
		try {
			File fData = new File(args[0]);
			File fTest = new File(args[1]);
			/* check the performance of the quantization method */
			checkQuant(fData, fTest);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
}
