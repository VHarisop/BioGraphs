package gr.demokritos.biographs;

import gr.demokritos.biographs.indexing.TreeDatabase;
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

		/* Create a simple similarity database */
		TreeDatabase<BioGraph> trd = new TreeDatabase<BioGraph>() {
			@Override
			public BioGraph getGraphFeature(BioGraph bG) {
				return bG;
			}
		};		

		BioGraph[] bgs;
		/* try to get the files and build the index and the biographs
		 * exit if the files don't exist or something goes wrong */
		try {
			File dataFile = new File(args[0]);
			File testFile = new File(args[1]);

			trd.buildWordIndex(dataFile);
			bgs = BioGraph.fromWordFile(testFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return; 
		}
		
		double sSim;
		for (BioGraph bg: bgs) {
			List<BioGraph> near = trd.getNearestNeighbours(bg, true);
			System.out.printf("%s: \n", bg.getLabel());
			for (BioGraph b: near) {
				sSim = 
					jutils.graphStructuralSimilarity(bg.getGraph(), b.getGraph());
				System.out.printf("\t%s - %.3f\n", b.getLabel(), sSim);
			}
		}
	}
}
