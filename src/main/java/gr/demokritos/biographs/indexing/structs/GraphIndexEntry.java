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

import java.util.Objects;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.indexing.preprocessing.IndexVector;

/**
 * This class represents an entry in the graph index
 * created by BioGraphs. It contains information about the label of the
 * graph it is associated to, as well as the hash vector encoding of its
 * graph.
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
		if (null == other) {
			return false;
		}

		if (!(other instanceof GraphIndexEntry)) {
			return false;
		}

		final GraphIndexEntry eOther = (GraphIndexEntry) other;
		if (this.getLabel().equals(eOther.getLabel())) {
			final int[] otherEnc = eOther.getEncoding();
			for (int i = 0; i < indexEncoding.length; ++i) {
				if (indexEncoding[i] != otherEnc[i]) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
}
