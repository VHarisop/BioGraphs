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

package gr.demokritos.iit.biographs.indexing;

import java.util.*;

import gr.demokritos.iit.biographs.*;
import gr.demokritos.iit.biographs.indexing.GraphDatabase.GraphType;
import gr.demokritos.iit.biographs.indexing.preprocessing.*;

/**
 * A class that implements an entry to be stored in a
 * {@link TreeIndex}.
 *
 * @author VHarisop
 */
public class TreeEntry {
	protected int[] indexEncoding;
	protected String feature;
	protected String label;

	/**
	 * Creates a new TreeEntry for a BioGraph.
	 *
	 * @param bg the biograph to be represented
	 */
	public TreeEntry(BioGraph bg) {
		IndexVector indVec = new IndexVector(GraphType.DNA);
		indVec.setHashStrategy(Strategies.dnaHash());
		indVec.setBins(16);

		indexEncoding = indVec.encodeGraph(bg);
		label = bg.getLabel();
		feature = bg.getDfsCode();
	}

	/**
	 * Getter for the underlying graph's label.
	 *
	 * @return the entry's label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Getter for the string representation of the graph.
	 *
	 * @return the graph's string representation
	 */
	public String getFeature() {
		return feature;
	}

	/**
	 * Getter for the index encoding of the graph.
	 *
	 * @return the graph's index encoding
	 */
	public int[] getEncoding() {
		return indexEncoding;
	}

	@Override
	public int hashCode() {
		return Objects.hash(label);
	}

	@Override
	public boolean equals(Object other) {
		if (null == other)
			return false;

		if (!(other instanceof TreeEntry))
			return false;

		TreeEntry eOther = (TreeEntry) other;
		if (eOther.getLabel().equals(label)) {
			int[] oEnc = eOther.getEncoding();
			for (int i = 0; i < oEnc.length; ++i) {
				if (indexEncoding[i] != oEnc[i])
					return false;
			}
			return true;
		}
		else {
			return false;
		}
	}
}
