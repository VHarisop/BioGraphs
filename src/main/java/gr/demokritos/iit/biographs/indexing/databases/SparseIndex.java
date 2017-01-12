package gr.demokritos.iit.biographs.indexing.databases;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.trie.PatriciaTrie;

import gr.demokritos.iit.biographs.BioGraph;
import gr.demokritos.iit.biographs.indexing.GraphDatabase;
import gr.demokritos.iit.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.iit.biographs.indexing.preprocessing.SparseIndexVector;
import gr.demokritos.iit.biographs.indexing.structs.SparseEntry;
import gr.demokritos.iit.biographs.io.BioInput;

public class SparseIndex extends GraphDatabase {
	/**
	 * The trie used internally
	 */
	protected final PatriciaTrie<List<SparseEntry>> dataTree =
		new PatriciaTrie<>();

	/**
	 * The sparse projector used internally
	 */
	protected final SparseIndexVector sparseVec;

	/**
	 * The order of bits used per vector element
	 */
	protected final int order;

	/**
	 * Creates a new {@link SparseIndex} with a target dimensionality
	 * for the projected feature vector.
	 * @param featDim the dimension of the projected vector
	 */
	public SparseIndex(final int order, final int featDim) {
		super();
		final Map<Character, Integer> charIndex = new HashMap<>();
		charIndex.put('A', 0);
		charIndex.put('C', 1);
		charIndex.put('G', 2);
		charIndex.put('T', 3);
		sparseVec = new SparseIndexVector(charIndex, 3, featDim);
		this.order = order;
	}

	/**
	 * Creates a new {@link SparseIndex} with a target dimensionality
	 * for the projected feature vector, operating on a given path.
	 * @param path the path of the data
	 * @param featDim the dimension of the projected vector
	 */
	public SparseIndex(final String path, final int order, final int featDim)
	{
		this(order, featDim);
		this.path = path;
	}

	@Override
	public int getSize() {
		return dataTree.size();
	}

	@Override
	public void buildIndex(final String path) throws Exception {
		buildIndex(new File(path));
	}

	@Override
	public void buildIndex(final File path) throws Exception {
		if (!path.isDirectory()) {
			addAllGraphs(path);
		}
		else {
			/* get all files in a list, and for each file add all
			 * the resulting biographs to the database */
			final File[] fileList = path.listFiles(
				(FileFilter) toFilter -> toFilter.isFile());
			for (final File f: fileList) {
				addAllGraphs(f);
			}
		}
	}

	@Override
	public void addGraph(final BioGraph bg) {
		addEntry(sparseVec.encodeGraph(bg));
	}

	/**
	 * Adds all graphs from a file to the database, choosing an appropriate
	 * reading method depending on the data type of the graphs this database
	 * indexes.
	 *
	 * @param f the file to read from
	 */
	private void addAllGraphs(final File f) throws Exception {
		switch (type) {
			case DNA:
				BioInput.fastaFileToGraphStream(f)
					.forEach(g -> addGraph(g));
				break;
			default:
				throw new UnsupportedOperationException(
					"Graph type not supported");

		}
	}

	/**
	 * Add a new sparse entry to the database, appending it to the list
	 * of entries with the same code, if any.
	 *
	 * @param entry the entry to be added
	 */
	public void addEntry(final SparseEntry entry) {
		// update the database's size
		this.size++;

		/*
		 * Get already existing entries with the same key first, if any
		 */
		final String key = entry.getKey(order);
		List<SparseEntry> entries = dataTree.get(key);

		/* if key was not already there, initialize an array of entries
		 * otherwise, add an entry to the pre-existing array */
		if (null == entries) {
			entries = new ArrayList<>();
		}
		/*
		 * update trie with new array
		 */
		entries.add(entry);
		dataTree.put(key, entries);
	}

	/**
	 * Selects the closest graphs, by terms of manhattan distance between
	 * projected vectors, that match a given tolerance criterion.
	 * @param bg the query graph
	 * @param tolerance the tolerance
	 * @return a list of entries
	 */
	public List<SparseEntry> select(final BioGraph bg, final double tolerance) {
		final SparseEntry queryEntry = sparseVec.encodeGraph(bg);
		return dataTree.select(queryEntry.getKey(order)).getValue()
			.stream()
			.filter(se -> ClusterDistance.hamming(
					queryEntry.getEncoding(), se.getEncoding()) < tolerance)
			.collect(Collectors.toList());
	}
}
