package gr.demokritos.biographs;

import java.io.File;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.TreeMap;
import java.util.Comparator;

import gr.demokritos.iit.jinsect.jutils;

import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.io.FastaReaderHelper;

/**
 * A class that implements a graph database using the graphs's similarity measure.
 * Here, the similarity measure used is the graph's structural similarity, as is
 * implemented in {@link gr.demokritos.iit.jinsect.structs.UniqueJVertexGraph}.
 *
 * @author VHarisop
 */
public class SimilarityDatabase extends GraphDatabase {

	protected TreeMap<BioJGraph, List<String>> treeIndex;

	protected Comparator<BioJGraph> bgComp = new Comparator<BioJGraph>(){
		@Override
		public int compare(final BioJGraph bgA, final BioJGraph bgB) {
			double sSim = jutils.graphStructuralSimilarity(
									bgA.getGraph(), bgB.getGraph());

			// compare with respect to arithmetic precision
			if (IsomorphismTester.compareDouble(sSim, 0.0))
				return 0;

			if (sSim > 0)
				return 1;
			else
				return -1;						
		}
	};

	/**
	 * Creates a blank SimilarityDatabase object.
	 */
	public SimilarityDatabase() { 
		super();
		treeIndex = new TreeMap(bgComp);
	}

	/**
	 * Creates a new SimilarityDatabase object for maintaining
	 * a database in a given directory.
	 * @param path the directory in which the database resides
	 */
	public SimilarityDatabase(String path) {
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
			BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(fPath);
			for (BioJGraph bG: bgs) {
				addGraph(bG);
			}
		}
		else {
			throw new UnsupportedOperationException("Not implemented yet");
		}
	}

	/**
	 * Adds a new graph to the database, updating the index as well.
	 * 
	 * @param bg the BioJGraph object to be added
	 */
	@Override
	public void addGraph(BioJGraph bg) {
		List<String> nodeLabels = treeIndex.get(bg);

		// if key was not there, initialize label array
		if (nodeLabels == null) {
			nodeLabels = new ArrayList<String>();
		}
		nodeLabels.add(bg.bioLabel);
		treeIndex.put(bg, nodeLabels);
	}

	/**
	 * Gets the keys of the underlying tree map of the database.
	 * 
	 * @return a set containing all the keys of the map
	 */
	public Set<BioJGraph> exposeKeys() {
		return treeIndex.keySet();
	}
}
