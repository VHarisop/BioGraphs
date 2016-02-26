package gr.demokritos.biographs.indexing.structs;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.indexing.preprocessing.IndexVector;
import java.util.Objects;

/**
 * This class represents an entry in the graph index
 * created by BioGraphs. 
 *
 * @author VHarisop
 */
public final class GraphIndexEntry {
	/**
	 * The label of the graph that this entry refers to.
	 */
	protected String graphLabel;

	/**
	 * The hashed vector encoding of the graph that the entry refers to.
	 */
	protected int[] indexEncoding;

	/**
	 * Creates a new GraphIndexEntry object from a {@link BioGraph} using
	 * a given {@link IndexVector} to produce encodings.
	 *
	 * @param bG the graph that the entry refers to
	 * @param indVec the IndexVector to use for acquiring the encoding
	 */
	public GraphIndexEntry(BioGraph bG, IndexVector indVec) {
		this.graphLabel = bG.getLabel();
		this.indexEncoding = indVec.encodeGraph(bG);
	}

	/**
	 * Returns the hashed vector encoding of the graph that this
	 * entry refers to.
	 *
	 * @return the hashed vector encoding of the associated graph
	 */
	public int[] getEncoding() {
		return indexEncoding;
	}

	/**
	 * Simple getter for the entry's graph label.
	 *
	 * @return the label of the graph the entry refers to
	 */
	public String getLabel() {
		return this.graphLabel;
	}
	
	/**
	 * The hash of the index entry is determined uniquely by the
	 * hash of the label of the graph that the entry refers to.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(graphLabel);
	}

	/**
	 * Two {@link GraphIndexEntry} objects are considered equal if
	 * they refer to the same graph.
	 */
	@Override
	public boolean equals(Object other) {
		if (null == other)
			return false;

		if (!(other instanceof GraphIndexEntry))
			return false;

		GraphIndexEntry eOther = (GraphIndexEntry) other;

		return this.getEncoding().equals(eOther.getEncoding());
	}
}
