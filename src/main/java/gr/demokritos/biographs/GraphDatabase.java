package gr.demokritos.biographs;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.lang.UnsupportedOperationException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Set;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * A class that handles a graph database, consisting of <tt>BioJGraph</tt> 
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
	private ArrayList<BioJGraph> graphArray;
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
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
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
	 * @param path a path containing one or multiple files
	 */
	public void buildIndex(File path) 
	throws Exception 
	{
		if (!path.isDirectory()) {
			BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(path);
			for (BioJGraph bG: bgs) {
				addGraph(bG);
			}
		}
		else {
			throw new UnsupportedOperationException("Not implemented yet!");
		}
	}


	/**
	 * Add a new graph to the graph database, updating
	 * the index on the trie as well. 
	 * @param bg the <tt>BioJGraph</tt> to be added
	 */
	public void addGraph(BioJGraph bg) {
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
	 * A wrapper method that reads DNA sequences from a file, given its pathname.
	 * @see readFastaDNASequence
	 *
	 * @param fName a <tt>String</tt> containing the path of the file
	 * @return a <tt>LinkedHashMap</tt> of String/Sequence pairs.
	 */
	public static LinkedHashMap<String, DNASequence> readFastaFile(String fName) 
	throws Exception
	{
		return readFastaFile(new File(fName));
	}

	/**
	 * A wrapper method around BioJava's <tt>readFastaDNASequence</tt> in order
	 * to facilicate reading DNA sequences from FASTA files. 
	 *
	 * @param inFile the <tt>File</tt> from which to read the sequences
	 * @return a <tt>LinkedHashMap</tt> of String/Sequence pairs.
	 */
	public static LinkedHashMap<String, DNASequence> readFastaFile(File inFile) 
	throws Exception 
	{
		return FastaReaderHelper.readFastaDNASequence(inFile);
	}

	/**
	 * Simple getter for the database's trie keyset.
	 */
	public Set<String> exposeKeys() {
		return trieIndex.keySet();
	}
}
