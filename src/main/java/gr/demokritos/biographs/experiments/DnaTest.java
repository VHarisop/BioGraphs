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
import gr.demokritos.biographs.indexing.*;
import gr.demokritos.biographs.indexing.comparators.*;
import gr.demokritos.biographs.indexing.databases.*;
import gr.demokritos.biographs.indexing.structs.Stats;

import java.io.File;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DnaTest {
	/**
	 * Default number of neighbours to seek 
	 */
	static int numNeighbours = 1;

	/**
	 * Gson builder
	 */
	static Gson gson;

	/**
	 * Files from which to pull database and testing graphs.
	 */
	static File testFile = null, dataFile = null;

	static Stats buildAndPrintTrie(
			TrieDatabase trd,
			BioGraph[] bgs)
	{
		try {
			trd.buildIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		Stats stat = new Stats("trie");
		for (BioGraph b: bgs) {
			List<String> ans = trd.selectKNearest(b, numNeighbours);
			stat.addResult(b.getLabel(), ans.toArray(new String[ans.size()]));
		}
		return stat;
	}
	
	static Stats buildAndPrint(
			TreeDatabase<String> trd,
			BioGraph[] bgs,
			String methodLabel)
	{
		try {
			trd.buildIndex(dataFile);
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

	static Stats checkTrie(BioGraph[] bgs) {
		TrieDatabase trd = new TrieDatabase() {
			@Override
			protected String getGraphCode(BioGraph bg) {
				return bg.getCanonicalCode();
			}
		};

		return buildAndPrintTrie(trd, bgs);
	}

	static Stats checkSimTrie(BioGraph[] bgs) {
		Comparator<BioGraph> bgComp = new SimilarityComparator();

		TreeDatabase<BioGraph> trdVar = 
			new TreeDatabase<BioGraph>(bgComp) {
				@Override
				public BioGraph getGraphFeature(BioGraph bG) {
					return bG;
				}
			};

		try {
			trdVar.buildIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Stats stat = new Stats("sim_trie");
		stat.setBins(trdVar);
		for (BioGraph b: bgs) {
			List<BioGraph> ans = 
				trdVar.getKNearestNeighbours(b, true, numNeighbours);

			TrieDatabase trie = new TrieDatabase() {
				@Override
				public String getGraphCode(BioGraph bG) {
					return bG.getCanonicalCode();
				}
			};

			for (BioGraph bg: ans) {
				trie.addGraph(bg);
			}

			List<String> answers = trie.select(b);
			stat.addResult(
					b.getLabel(), 
					answers.toArray(new String[answers.size()]));

		}

		return stat;
	}

	static Stats checkSim(BioGraph[] bgs) {
		/* Create a TreeDatabase ordered by a two-level comparator
		 * that uses s-similarity first and the graph's canonical
		 * code at the next level */
		TreeDatabase<String> trdSim = 
			new TreeDatabase<String>(new TwoLevelSimComparator()) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};
	
		return buildAndPrint(trdSim, bgs, "similarity_and_canonical_code");
	}

	static Stats checkNearest(BioGraph[] bgs, ClusterHybridDatabase cgd) {
		Stats stat = new Stats("cluster");
		for (BioGraph bg : bgs) {
			BioGraph bGraph = cgd.getNearestNeighbour(bg);
			stat.addResult(bg.getLabel(), bGraph.getLabel());
		}
		stat.setBins(cgd.getClusterSizes());
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

		if (args.length == 3) {
			numNeighbours = Integer.parseInt(args[2]);
		}

		ClusterHybridDatabase cgd = new ClusterHybridDatabase(50, 150);
		BioGraph[] bGraphs = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphs = BioGraph.fastaFileToGraphs(testFile);

			List<Stats> statList = new ArrayList<Stats>();
			
			cgd.buildIndex(dataFile);
			/* check the performance of the custom comparators */
			statList.add(checkTrie(bGraphs));
			statList.add(checkNearest(bGraphs, cgd));
			
			/* print all the stats */
			System.out.println(
				gson.toJson(statList.toArray(new Stats[statList.size()])));
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
}
