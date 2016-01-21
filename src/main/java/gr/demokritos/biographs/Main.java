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

import gr.demokritos.biographs.indexing.*;
import gr.demokritos.biographs.indexing.comparators.*;

import gr.demokritos.iit.jinsect.jutils;

import java.io.File;
import java.util.*;

public class Main {

	public static void checkVariance(File dataFile, BioGraph[] bgs) {
		Comparator<BioGraph> bgComp = new Comparator<BioGraph>() {
			@Override
			public int compare(BioGraph bgA, BioGraph bgB) {
				double wvs =
					bgA.getGraph().getTotalVarRatios() -
					bgB.getGraph().getTotalVarRatios();

				int ret = Double.compare(wvs, 0.0);
				if (ret == 0) {
					return 
						bgA.getDfsCode().compareTo(bgB.getDfsCode());
				}
				else 
					return ret;
			}
		};

		TreeDatabase<String> trdVar = 
			new TreeDatabase<String>(bgComp) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		try {
			trdVar.buildWordIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		for (BioGraph b: bgs) {
			List<String> ans = trdVar.getKNearestNeighbours(b, true, 5);
			System.out.printf("%s:", b.getLabel());
			for (String s: ans) { System.out.printf(" %s", s); }
			System.out.println();
		}
	}

	public static void checkRatio(File dataFile, BioGraph[] bgs) {
		Comparator<BioGraph> bgComp = new Comparator<BioGraph>() {
			@Override
			public int compare(BioGraph bgA, BioGraph bgB) {
				double sSim = 
					bgA.getGraph().getDegreeRatioSum() -
					bgB.getGraph().getDegreeRatioSum();

				if (GraphDatabase.compareDouble(sSim, 0.0)) {
					return jutils.compareCanonicalCodes(
							bgA.getGraph(),
							bgB.getGraph());
				} else 
					return Double.compare(sSim, 0.0);
			}
		};

		TreeDatabase<String> trdSim = 
			new TreeDatabase<String>(bgComp) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		try {
			trdSim.buildWordIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		for (BioGraph bg: bgs) {
			List<String> matches = 
				trdSim.getKNearestNeighbours(bg, true, 5);
			System.out.printf("%s:", bg.getLabel());
			for (String s: matches) {
				System.out.printf(" %s", s);
			}
			System.out.println();
		}
	}

	public static void checkSim(File dataFile, BioGraph[] bgs) {
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
		
		/* try to build the index and the biographs */
		try {
			trdCanon.buildWordIndex(dataFile);
			trdSim.buildWordIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return; 
		}

		double sSim;
		System.out.print("CCODE");
		for (BioGraph bg: bgs) {
			List<BioGraph> near = trdCanon.getKNearestNeighbours(bg, true, 2);
			System.out.printf("\n%s:", bg.getLabel());
			for (BioGraph b: near) {
				sSim = 
					jutils.graphStructuralSimilarity(bg.getGraph(), b.getGraph());
				System.out.printf(" %s", b.getLabel(), sSim);
			}
		}
		System.out.print("\n2SIM");
		for (BioGraph bg: bgs) {
			List<BioGraph> near = trdSim.getKNearestNeighbours(bg, true, 2);
			System.out.printf("\n%s:", bg.getLabel());
			for (BioGraph b: near) {
				sSim = 
					jutils.graphStructuralSimilarity(bg.getGraph(), b.getGraph());
				System.out.printf(" %s", b.getLabel(), sSim);
			}
		}
		System.out.println();

	}

	public static void checkQuant(File dataFile, BioGraph[] bgs) {
		QuantTreeDatabase<String> qtd = 
			new QuantTreeDatabase<String>() {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		try {
			qtd.buildWordIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		/* Query all test graphs for nearest neighbours */
		for (BioGraph bG: bgs) {
			List<String> ans = qtd.getKNearestNeighbours(bG, false, 3);
			if (ans == null) {
				throw new NullPointerException();
			}
			else {
				System.out.printf("%s:", bG.getLabel());
				for (String s: ans) {
					System.out.printf(" %s", s);
				}
				System.out.println();
			}
		}
	}

	public static void main(String[] args) {
		/* if no file was provided as argument, inform the user and exit */
		if (args.length < 2) {
			System.out.println("Missing file argument!");
			return; 
		}
		BioGraph[] bGraphs = null;
		try {
			File fData = new File(args[0]);
			File fTest = new File(args[1]);

			bGraphs = BioGraph.fromWordFile(fTest);

			/* check the performance of the custom comparators */
			// checkRatio(fData, bGraphs);
			// checkSim(fData, bGraphs);
			checkQuant(fData, bGraphs);
			// checkVariance(fData, bGraphs);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
}
