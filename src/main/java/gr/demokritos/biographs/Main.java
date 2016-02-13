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
import gr.demokritos.biographs.indexing.structs.NTreeDatabase;

import java.io.File;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {

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

	public static Stats buildAndPrintTrie(
			TrieDatabase trd,
			BioGraph[] bgs)
	{
		try {
			trd.buildWordIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		Stats stat = new Stats("trie");
		for (BioGraph b: bgs) {
			List<String> ans = trd.select(b);
			stat.addResult(b.getLabel(), ans.toArray(new String[ans.size()]));
		}
		return stat;
	}
	
	public static Stats buildAndPrint(
			TreeDatabase<String> trd,
			BioGraph[] bgs,
			String methodLabel)
	{
		try {
			trd.buildWordIndex(dataFile);
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

	public static Stats checkTrie(BioGraph[] bgs) {
		TrieDatabase trd = new TrieDatabase() {
			@Override
			protected String getGraphCode(BioGraph bg) {
				return bg.getDfsCode();
			}
		};

		return buildAndPrintTrie(trd, bgs);
	}

	public static Stats checkEntropy(BioGraph[] bgs) {
		TreeDatabase<String> entData = 
			new TreeDatabase<String>(new TotalEntropyComparator()) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		return buildAndPrint(entData, bgs, "entropy");
	}
	
	public static Stats checkSimNTree(BioGraph[] bgs) {
		Comparator<BioGraph> bgComp = 
			new SimilarityComparator();

		TreeDatabase<BioGraph> trdVar = 
			new TreeDatabase<BioGraph>(bgComp) {
				@Override
				public BioGraph getGraphFeature(BioGraph bG) {
					return bG;
				}
			};

		try {
			trdVar.buildWordIndex(dataFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Stats stat = new Stats("sim_ntree");
		stat.setBins(trdVar);
		for (BioGraph b: bgs) {
			List<BioGraph> ans = 
				trdVar.getKNearestNeighbours(b, true, numNeighbours);

			NTreeDatabase ntree = new NTreeDatabase(8, 26);

			for (BioGraph bg: ans) {
				ntree.addGraph(bg);
			}

			BioGraph[] answers = ntree.getKNearestNeighbours(b, numNeighbours);
			String[] labels = new String[answers.length]; int i = 0;
			for (BioGraph bAns: answers) {
				labels[i++] = bAns.getLabel();
			}
			stat.addResult(
					b.getLabel(), 
					labels);

		}

		return stat;
	}

	public static Stats checkSimTrie(BioGraph[] bgs) {
		Comparator<BioGraph> bgComp = 
			new SimilarityComparator();

		TreeDatabase<BioGraph> trdVar = 
			new TreeDatabase<BioGraph>(bgComp) {
				@Override
				public BioGraph getGraphFeature(BioGraph bG) {
					return bG;
				}
			};

		try {
			trdVar.buildWordIndex(dataFile);
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

	public static Stats checkVariance(BioGraph[] bgs) {
		/* Comparator that uses the ratio of degree variances */
		Comparator<BioGraph> bgComp = 
			new VarianceComparator(VarianceComparator.Type.RATIO);

		TreeDatabase<String> trdVar = 
			new TreeDatabase<String>(bgComp) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		/* build index and print results */
		return buildAndPrint(trdVar, bgs, "variance");
	}

	public static Stats checkRatio(BioGraph[] bgs) {
		/* Custom comparator based on the degree ratio sum */
		Comparator<BioGraph> bgComp = new Comparator<BioGraph>() {
			@Override
			public int compare(BioGraph bgA, BioGraph bgB) {
				return Double.compare(
						bgA.getGraph().getDegreeRatioSum(),
						bgB.getGraph().getDegreeRatioSum());

			}
		};

		TreeDatabase<String> trdSim = 
			new TreeDatabase<String>(bgComp) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		return buildAndPrint(trdSim, bgs, "degree_ratio");
	}

	public static Stats checkSim(BioGraph[] bgs) {
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

	public static Stats checkHashVectorSim(BioGraph[] bgs) {
		TreeDatabase<String> trd = 
			new TreeDatabase<String>(new VertexHashComparator(26)) {
				@Override
				public String getGraphFeature(BioGraph bG) {
					return bG.getLabel();
				}
			};

		return buildAndPrint(trd, bgs, "vertex_hash");
	}

	public static Stats checkSimpleSim(BioGraph[] bgs) {
		/* simple s-similarity indexing */
		TreeDatabase<String> trdSim = 
			new SimilarityDatabase();

		return buildAndPrint(trdSim, bgs, "similarity");
	}

	public static Stats checkQuant(BioGraph[] bgs) {
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
			return null;
		}

		Stats stat = new Stats("quantization");
		for (BioGraph bG: bgs) {
			List<String> ans;
			try {
				ans = qtd.getKNearestNeighbours(bG, true, numNeighbours);
			} catch (Exception ex) {
				System.out.printf("Crash on %s!\n", bG.getLabel());
				continue;
			}
			if (ans == null) {
				throw new NullPointerException();
			}
			else {
				stat.addResult(
						bG.getLabel(),
						ans.toArray(new String[ans.size()]));
			}
		}
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

		BioGraph[] bGraphs = null;
		gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			dataFile = new File(args[0]);
			testFile = new File(args[1]);
			bGraphs = BioGraph.fromWordFile(testFile);

			List<Stats> statList = new ArrayList<Stats>();
			
			/* check the performance of the custom comparators */
			statList.add(checkRatio(bGraphs));
			statList.add(checkSim(bGraphs));
			statList.add(checkSimpleSim(bGraphs));
			statList.add(checkQuant(bGraphs));
			statList.add(checkTrie(bGraphs));
			statList.add(checkVariance(bGraphs));
			statList.add(checkSimTrie(bGraphs));
			statList.add(checkEntropy(bGraphs));
			statList.add(checkSimNTree(bGraphs));
			
			/* print all the stats */
			System.out.println(
				gson.toJson(statList.toArray(new Stats[statList.size()])));
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}
}
