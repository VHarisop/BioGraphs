package gr.demokritos.biographs;

import org.apache.commons.collections4.trie.PatriciaTrie;

import java.lang.UnsupportedOperationException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
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

	/**
	 * A {@link org.apache.commons.collections4.trie.PatriciaTrie} 
	 * that is used for indexing graphs by using the graphs'
	 * {@link BioGraph#getDfsCode()} as keys.
	 */
	protected PatriciaTrie trieIndex;
	
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
			BioGraph[] bgs = BioGraph.fastaFileToGraphs(fPath);
			for (BioGraph bG: bgs) {
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
				BioGraph[] bgs = BioGraph.fastaFileToGraphs(f);
				for (BioGraph bG: bgs) {
					addGraph(bG);
				}
			}
		}
	}


	/**
	 * Add a new graph to the graph database, updating
	 * the index on the trie as well. 
	 * @param bg the BioGraph to be added
	 */
	@Override
	public void addGraph(BioGraph bg) {
		// get the dfsCode of the graph as key
		String dfsCode = bg.getDfsCode();
		
		/* if key was not already there, initialize an array of indices 
		 * otherwise, add an entry to the pre-existing array */
		if (!(trieIndex.containsKey(dfsCode))) {
			ArrayList<String> labels = new ArrayList();
			labels.add(bg.bioLabel);
			
			// add to Trie
			trieIndex.put(dfsCode, labels);
		}
		else {
			ArrayList<String> labels = (ArrayList) trieIndex.get(dfsCode);
			labels.add(bg.bioLabel);

			// update trie with new array
			trieIndex.put(dfsCode, labels);
		}
	}

	/**
	 * Obtains a list of labels of graphs that match the dfs code 
	 * of the given graph.
	 *
	 * @param bg the query graph
	 * @return a list of labels 
	 */
	public List<String> getNodes(BioGraph bg) {
		return getNodes(bg.getDfsCode());
	}


	/**
	 * Obtain a list of labels of graphs that match the provided dfsCode. 
	 * @param dfsCode a <tt>String</tt> containing the query dfs code
	 * @return an <tt>ArrayList</tt> of labels pointing to biographs
	 */
	public List<String> getNodes(String dfsCode) {
		List<String> labels = new ArrayList<String>();
		labels = (ArrayList) trieIndex.get(dfsCode);

		return labels;
	}

	/**
	 * Simple getter for the database's trie keyset.
	 */
	public Set<String> exposeKeys() {
		return trieIndex.keySet();
	}
}
