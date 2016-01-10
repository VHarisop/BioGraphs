package gr.demokritos.biographs;

import java.io.File;
import java.io.FileFilter;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import gr.demokritos.iit.jinsect.jutils;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * A class that implements a graph database using the graphs's similarity measure.
 * Here, the similarity measure used is the graph's structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.structs.UniqueJVertexGraph}. This
 * version of similarity database doesn't store the whole graph as key, but stores
 * the graph's sum of normalized edge weights instead. The values stored here are
 * the complete {@link BioGraph} objects.
 *
 * @author VHarisop
 */
public class MemSimilarityDatabase extends GraphDatabase {

	/**
	 * A Red-Black tree map implementation that associates biographs
	 * with lists of their s-similar biographs.
	 */
	protected TreeMap<BioGraph, List<BioGraph>> treeIndex;
	
	/**
	 * A custom comparator to be used for {@link #treeIndex} that
	 * compares graphs based on their s-similarity.
	 */
	protected Comparator<BioGraph> bgComp = new SimilarityComparator();

	/**
	 * Creates a blank MemSimilarityDatabase object.
	 */
	public MemSimilarityDatabase() { 
		super();
		treeIndex = new TreeMap(bgComp);
	}

	/**
	 * Creates a new MemSimilarityDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public MemSimilarityDatabase(String path) {
		super(path);
		treeIndex = new TreeMap(bgComp);
	}

	/**
	 * Builds a graph database index from a given file or directory
	 * of files.
	 *
	 * @param path a string containing a path to a file or directory
	 */
	@Override
	public void buildIndex(String path) throws Exception {
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
	public void buildIndex(File fPath) throws Exception {
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

			// add them all to the database
			for (File f: fileList) {
				BioGraph[] bgs = BioGraph.fastaFileToGraphs(f);
				for (BioGraph bG: bgs) {
					addGraph(bG);
				}
			}
		}
	}

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioGraph object to be indexed
	 */
	@Override
	public void addGraph(BioGraph bg) {
		List<BioGraph> nodes = treeIndex.get(bg);
		
		// if key was not there, initialize label array
		if (nodes == null) {
			nodes = new ArrayList<BioGraph>();
		}

		nodes.add(bg);
		treeIndex.put(bg, nodes);
	}

	/**
	 * Gets the keys of the underlying tree map of the database.
	 * 
	 * @return a set containing all the keys of the map
	 */
	public Set<BioGraph> exposeKeys() {
		return treeIndex.keySet();
	}

	/**
	 * Gets the nodes corresponding to the biograph query, whose
	 * similarity to the query biojgraph is 0.
	 * @param bg the {@link BioGraph} to be searched for
	 * @return a list of labels corresponding to FASTA entries
	 */
	public List<BioGraph> getNodes(BioGraph bg) {
		return treeIndex.get(bg);
	}

	/**
	 * Gets the nodes corresponding to the list of query biographs, and
	 * returns them in an array of {@link java.util.Map.Entry} objects.
	 *
	 * @param bGraphs the {@link BioGraph} array of query graphs
	 * @return the list of Entries that map biographs to nodes
	 */
	public Entry<BioGraph, List<BioGraph>>[] getNodes(BioGraph[] bGraphs) {
		Entry<BioGraph, List<BioGraph>>[] results = new MemEntry[bGraphs.length];
		for (int iCnt = 0; iCnt < bGraphs.length; ++iCnt) {
			results[iCnt] = 
				new MemEntry(bGraphs[iCnt], getNodes(bGraphs[iCnt]));
		}

		return results;
	}
}

/**
 * Utility class that implements Map.Entry for specific types 
 */
final class MemEntry implements Entry<BioGraph, List<BioGraph>> {
	private final BioGraph key;
	private List<BioGraph> value;

	public MemEntry(BioGraph bKey, List<BioGraph> listValues) {
		key = bKey;
		value = listValues;
	}

	@Override
	public BioGraph getKey() {
		return key;
	}

	@Override
	public List<BioGraph> getValue() {
		return value;
	}

	@Override
	public List<BioGraph> setValue(List<BioGraph> newValues) {
		List<BioGraph> old = value;
		value = newValues;
		return old;
	}
}
