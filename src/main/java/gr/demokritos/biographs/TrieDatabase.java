package gr.demokritos.biographs;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.lang.UnsupportedOperationException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Set;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * A class that extends {@link GraphDatabase}, using a PatriciaTrie as an index
 * that uses the DFS codes of the graphs as keys.
 *
 * @author VHarisop
 */
public class TrieDatabase extends GraphDatabase {

	/* Trie implementation of an index on graphs */
	private PatriciaTrie trieIndex;
	
	/**
	 * Creates a blank TrieDatabase object.
	 */
	public TrieDatabase() { 
		super();
		trieIndex = new PatriciaTrie();
	}

	/**
	 * Creates a new TrieDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public TrieDatabase(String path) {
		super(path);
		trieIndex = new PatriciaTrie();
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	@Override
	public void buildIndex(String path) 
	throws Exception
	{
		File fPath = new File(path);
		buildIndex(fPath);
	}

	/**
	 * Builds a graph database index from a given file or a directory 
	 * of files.
	 *
	 * @param fPath a path containing one or multiple files
	 */
	@Override
	public void buildIndex(File fPath) 
	throws Exception 
	{
		if (!fPath.isDirectory()) {
			BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(fPath);
			for (BioJGraph bG: bgs) {
				addGraph(bG);
			}
		}
		else {
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			// add the graphs of each file to the database
			for (File f: fileList) {
				BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(f);
				for (BioJGraph bG: bgs) {
					addGraph(bG);
				}
			}
		}
	}


	/**
	 * Add a new graph to the graph database, updating
	 * the index on the trie as well. 
	 * @param bg the BioJGraph to be added
	 */
	@Override
	public void addGraph(BioJGraph bg) {
		// get the dfsCode of the graph as key
		String dfsCode = bg.getDfsCode();
		
		// add the graph to the in-memory array
		arrayIndex++; graphArray.add(bg);

		/* if key was not already there, initialize an array of indices 
		 * otherwise, add an entry to the pre-existing array */
		if (!(trieIndex.containsKey(dfsCode))) {
			ArrayList<Integer> indices = new ArrayList();
			indices.add(arrayIndex);
			
			// add to Trie
			trieIndex.put(dfsCode, indices);
		}
		else {
			ArrayList<Integer> indices = (ArrayList) trieIndex.get(dfsCode);
			indices.add(arrayIndex);

			// update trie with new array
			trieIndex.put(dfsCode, indices);
		}
	}

	/**
	 * Obtain a list of graphs that match a given dfsCode.
	 * @param dfsCode a <tt>String</tt> containing the query code
	 * @return an <tt>ArrayList</tt> of BioJGraphs that match
	 */
	public ArrayList<BioJGraph> getGraphs(String dfsCode) {
		ArrayList<Integer> indices = this.getGraphIndices(dfsCode);
		ArrayList<BioJGraph> graphs = new ArrayList<BioJGraph>();

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

	/**
	 * Simple getter for the database's trie keyset.
	 */
	public Set<String> exposeKeys() {
		return trieIndex.keySet();
	}
}
