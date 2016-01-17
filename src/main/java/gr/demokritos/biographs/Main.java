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
		System.out.println("CCODE:\n");
		for (BioGraph bg: bgs) {
			List<BioGraph> near = trdCanon.getKNearestNeighbours(bg, true, 2);
			System.out.printf("%s: \n", bg.getLabel());
			for (BioGraph b: near) {
				sSim = 
					jutils.graphStructuralSimilarity(bg.getGraph(), b.getGraph());
				System.out.printf("\t%s - %.3f\n", b.getLabel(), sSim);
			}
		}
		System.out.println("\n\n");

		System.out.println("2SIM:\n");
		for (BioGraph bg: bgs) {
			List<BioGraph> near = trdSim.getKNearestNeighbours(bg, true, 2);
			System.out.printf("%s: \n", bg.getLabel());
			for (BioGraph b: near) {
				sSim = 
					jutils.graphStructuralSimilarity(bg.getGraph(), b.getGraph());
				System.out.printf("\t%s - %.3f\n", b.getLabel(), sSim);
			}
		}
	}
}
