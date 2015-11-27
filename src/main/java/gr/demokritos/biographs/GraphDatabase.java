package gr.demokritos.biographs;

import org.apache.commons.collections4.trie.PatriciaTrie;
import java.util.ArrayList;

/**
 * A class that handles a graph database, consisting of <tt>BioGraph</tt> 
 * objects. A Patricia Trie is maintained for indexing, where the keys are
 * the DFS codes of the graphs. 
 *
 * @author VHarisop
 */
public class GraphDatabase {

	/* the path of the graph database */
	private String path;

	/* boolean variable indicating if the database
	 * resides in RAM */
	private boolean inMem;

	/* Trie implementation of an index on graphs */
	private PatriciaTrie trieIndex;

	/* an array list of graphs to be kept in memory */
	private ArrayList<BioGraph> graphArray;
	private int arrayIndex;
	
	/**
	 * Creates a blank GraphDatabase object.
	 */
	public GraphDatabase() { 
		path = null;
		inMem = true;

		// init trie index
		initIndex();
	}

	/**
	 * Creates a new GraphDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public GraphDatabase(String path) {
		this.path = path;
		inMem = false;

		// init trie index
		initIndex();
	}

	/**
	 * Initializes the patriciaTrie that maintains
	 * an index on the database's graphs.
	 */
	private void initIndex() {
		trieIndex = new PatriciaTrie();	
		graphArray = new ArrayList();
		arrayIndex = -1;
	}


	/**
	 * Add a new graph to the graph database, updating
	 * the index on the trie as well. 
	 * @param bg the <tt>BioGraph</tt> to be added
	 */
	public void addGraph(BioGraph bg) {

		// get the dfsCode of the graph as key
		String dfsCode = bg.getDfsCode();
		
		// add the graph to the in-memory array
		arrayIndex++; graphArray.add(bg);

		// update the trie containing the indices
		addGraph(dfsCode, arrayIndex);
	}


	/**
	 * Add a new index entry for a graph in the trieIndex 
	 * maintained. The graph's dfs code is used as a key and
	 * the index of the graph in the in-memory array becomes
	 * the new index entry.
	 * @param dfsCode a <tt>String</tt> containing the DFS code
	 * @param index the index of the graph in the in-memory array
	 */
	private void addGraph(String dfsCode, int index) {
		if (!(trieIndex.containsKey(dfsCode))) {
			
			/* if the given dfs code is not in the trie, create a new 
		 	 * ArrayList of graph indices */
			ArrayList<Integer> indices = new ArrayList<Integer>();
			indices.add(index);

			trieIndex.put(dfsCode, indices);
		}
		else {
			/* if the given dfs code is already in the trie, modify
			 * the list of graph indices maintained for the key */
			ArrayList<Integer> indices = (ArrayList) trieIndex.get(dfsCode);
			indices.add(index);

			/* update trie with new array */
			trieIndex.put(dfsCode, indices);
		}
	}


	/**
	 * Obtain a list of graphs that match a given dfsCode.
	 * @param dfsCode a <tt>String</tt> containing the query code
	 * @return an <tt>ArrayList</tt> of BioGraphs that match
	 */
	public ArrayList<BioGraph> getGraphs(String dfsCode) {
		ArrayList<Integer> indices = this.getGraphIndices(dfsCode);
		ArrayList<BioGraph> graphs = new ArrayList<BioGraph>();

		for (int i: indices) {
			graphs.add(graphArray.get(i));
		}

		return graphs;
	}

	/**
	 * Obtain a list of indices pointing to places at the in-memory array
	 * that contain graphs that match the provided dfsCode. 
	 * @param dfsCode a <tt>String</tt> containing the query dfs code
	 * @return an <tt>ArrayList</tt> of integers containing the indices
	 * 		   in the in-memory array
	 */
	private ArrayList<Integer> getGraphIndices(String dfsCode) {
		ArrayList<Integer> indices = new ArrayList<Integer>();
		indices = (ArrayList) trieIndex.get(dfsCode);

		return indices;
	}
}
