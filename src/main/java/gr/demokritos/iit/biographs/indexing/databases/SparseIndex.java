package gr.demokritos.iit.biographs.indexing.databases;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gr.demokritos.iit.biographs.BioGraph;
import gr.demokritos.iit.biographs.indexing.GraphDatabase;
import gr.demokritos.iit.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.iit.biographs.io.BioInput;
import gr.demokritos.iit.jinsect.comparators.SparseProjectionComparator;
import gr.demokritos.iit.jinsect.structs.Pair;

public class SparseIndex extends GraphDatabase {
	protected List<Pair<String, double[]>> data;
	protected SparseProjectionComparator spc;

	/**
	 * Creates a new {@link SparseIndex} with a target dimensionality
	 * for the projected feature vector.
	 * @param featDim the dimension of the projected vector
	 */
	public SparseIndex(final int featDim) {
		super();
		final Map<Character, Integer> charIndex = new HashMap<>();
		charIndex.put('A', 0);
		charIndex.put('C', 1);
		charIndex.put('G', 2);
		charIndex.put('T', 3);
		spc = new SparseProjectionComparator(
			charIndex, 3, featDim,
			SparseProjectionComparator.Projection.SIGN_CONSISTENT);
		data = new ArrayList<>();
	}

	/**
	 * Creates a new {@link SparseIndex} with a target dimensionality
	 * for the projected feature vector, operating on a given path.
	 * @param path the path of the data
	 * @param featDim the dimension of the projected vector
	 */
	public SparseIndex(final String path, final int featDim) {
		this(featDim);
		this.path = path;
	}

	@Override
	public int getSize() {
		return data.size();
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
		data.add(new Pair<String, double[]>(
			bg.getLabel(), spc.getProjectedVectorParallel(bg.getGraph())));
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
	 * Selects the closest graphs, by terms of manhattan distance between
	 * projected vectors, that match a given tolerance criterion.
	 * @param bg the query graph
	 * @param tolerance the tolerance
	 * @return a list of labels
	 */
	public List<String> select(final BioGraph bg, final double tolerance) {
		final double[] pVec = spc.getProjectedVectorParallel(bg.getGraph());
		return data.stream()
			.filter(p -> ClusterDistance.hamming(
				pVec, p.getSecond()) < tolerance)
			.map(p -> p.getFirst())
			.collect(Collectors.toList());
	}
}
