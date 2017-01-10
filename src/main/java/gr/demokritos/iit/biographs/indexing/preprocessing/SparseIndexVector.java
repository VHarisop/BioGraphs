package gr.demokritos.iit.biographs.indexing.preprocessing;

import java.util.Map;
import java.util.logging.Logger;

import gr.demokritos.iit.biographs.BioGraph;
import gr.demokritos.iit.biographs.indexing.structs.SparseEntry;
import gr.demokritos.iit.jinsect.Logging;
import gr.demokritos.iit.jinsect.comparators.SparseProjectionComparator;

/**
 * An index vector to encode graphs using a SparseProjectionComparator.
 * @author vharisop
 */
public class SparseIndexVector {
	protected static final Logger logger =
		Logging.getLogger(SparseIndexVector.class.getName());
	protected final Map<Character, Integer> characterIndex;
	protected final SparseProjectionComparator spc;

	/**
	 * Creates a new {@link SparseIndexVector} using a specified character
	 * map with a target final dimension.
	 * @param charIndex a map from characters to indices
	 * @param rank the n-gram rank to use
	 * @param finalDim the target dimension of the final vector
	 */
	public SparseIndexVector(
		final Map<Character, Integer> charIndex,
		final int rank,
		final int finalDim)
	{
		characterIndex = charIndex;
		spc = new SparseProjectionComparator(
			charIndex, rank, finalDim,
			SparseProjectionComparator.Projection.SIGN_CONSISTENT);
	}

	/**
	 * Encodes a {@link BioGraph} using the stored sparse projector
	 * and returns the resulting {@link SparseEntry}.
	 * @param bGraph the graph to be encoded
	 * @return the resulting entry
	 */
	public final SparseEntry encodeGraph(final BioGraph bGraph) {
		return new SparseEntry(
			bGraph.getLabel(),
			spc.getProjectedVectorParallel(bGraph.getGraph()));
	}
}
