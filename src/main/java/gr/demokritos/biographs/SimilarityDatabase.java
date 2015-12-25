package gr.demokritos.biographs;

import java.io.File;
import java.io.FileFilter;

import java.lang.Math;

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

	/**
	 * A Red-Black tree map implementation that associates biographs
	 * with lists of FASTA strings (labels).
	 */
	protected TreeMap<BioJGraph, List<String>> treeIndex;

	/**
	 * A custom comparator to be used for {@link #treeIndex} that
	 * compares graphs based on their s-similarity.
	 */
	protected Comparator<BioJGraph> bgComp = new Comparator<BioJGraph>() {
		@Override 
		public int compare(final BioJGraph bgA, final BioJGraph bgB) {
			double sSim = 
				jutils.graphStructuralSimilarity(bgA.getGraph(), bgB.getGraph());
			
			if (eqDouble(sSim, 0.0)) {
				return 0;
			}
			if (sSim > 0)
				return 1;

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
			// get all files in a list
			File[] fileList = fPath.listFiles(new FileFilter() {
				public boolean accept(File toFilter) {
					return toFilter.isFile();
				}
			});

			// add them all to the database
			for (File f: fileList) {
				BioJGraph[] bgs = BioJGraph.fastaFileToGraphs(f);
				for (BioJGraph bG: bgs) {
					addGraph(bG);
				}
			}
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

	/**
	 * Gets the nodes corresponding to the biograph query.
	 * @param bg the {@link BioJGraph} key to be searched for
	 * @return a list of labels corresponding to FASTA entries
	 */
	public List<String> getNodes(BioJGraph bg) {
		return treeIndex.get(bg);
	}

	private static boolean eqDouble(double a, double b) {
		return (Math.abs(a - b) < 0.000001);
	}


}
