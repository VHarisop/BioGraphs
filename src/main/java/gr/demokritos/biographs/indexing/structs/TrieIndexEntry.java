/* This file is part of BioGraphs.
 *
 * BioGraphs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BioGraphs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BioGraphs.  If not, see <http://www.gnu.org/licenses/>. */

package gr.demokritos.biographs.indexing.structs;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.algorithms.*;
import java.util.Objects;
import gr.demokritos.iit.jinsect.structs.Edge;

/**
 * This class represents an entry in the graph index
 * created by BioGraphs that is used by
 * {@link gr.demokritos.biographs.indexing.databases.TrieDatabase}.
 * It contains the original graph's label as well as the labels of
 * the maximal edges of its maximal spanning tree.
 *
 * @author VHarisop
 */
public final class TrieIndexEntry {
	/**
	 * The label of the graph that this entry refers to.
	 */
	protected String graphLabel;

	/**
	 * The hashed vector encoding of the graph that the entry refers to.
	 */
	protected String indexEncoding;

	/**
	 * Creates a new TrieIndexEntry object from a {@link BioGraph} using
	 * its maximal spanning tree representation.
	 *
	 * @param bG the graph that the entry refers to
	 */
	public TrieIndexEntry(BioGraph bG) {
		this.graphLabel = bG.getLabel();

		/* index encoding is set to the concatenation of the maximal
		 * tree's edge labels
		 */
		this.indexEncoding = "";
		for (Edge e: new MaximumSpanningTree(bG).getTreeEdges()) {
			this.indexEncoding += e.getLabels();
		}
	}

	/**
	 * Returns the maximal tree encoding of the graph that this
	 * entry refers to.
	 *
	 * @return the maximal tree encoding of the graph this index points to
	 */
	public String getEncoding() {
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
	 * Two {@link TrieIndexEntry} objects are considered equal if
	 * they refer to the same graph.
	 */
	@Override
	public boolean equals(Object other) {
		if (null == other)
			return false;

		if (!(other instanceof TrieIndexEntry))
			return false;

		TrieIndexEntry eOther = (TrieIndexEntry) other;
		return this.getLabel().equals(eOther.getLabel());
	}
}
