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
import gr.demokritos.biographs.indexing.preprocessing.*;
import gr.demokritos.biographs.indexing.structs.*;
import gr.demokritos.biographs.indexing.GraphDatabase.GraphType;

import java.util.*;

/**
 * This class represents an entry in the graph index
 * created by BioGraphs that is used by
 * {@link gr.demokritos.biographs.indexing.databases.TrieIndex}
 * It contains the original graph's label and its hashed vector encoding,
 * providing a method for serializing it to a binary string using a given
 * number of bits per index.
 *
 * @author VHarisop
 */
public final class TrieEntry {
	/**
	 * The label of the graph that this entry refers to.
	 */
	protected String label;

	/**
	 * The hashed vector encoding of the graph that the entry refers to.
	 */
	protected byte[] indexEncoding;

	/**
	 * Creates a new TrieEntry object from a {@link BioGraph} using
	 * its maximal spanning tree representation.
	 *
	 * @param bG the graph that the entry refers to
	 */
	public TrieEntry(BioGraph bG) {
		this.label = bG.getLabel();

		/*
		 * get the standard index encoding
		 */
		IndexVector indVec = new IndexVector(GraphType.DNA);
		indVec.setHashStrategy(Strategies.dnaHash());
		indVec.setBins(16);

		/*
		 * Obtain the hashed vector encoding for the graph and cache it
		 * to avoid multiple computations
		 */
		indexEncoding = indVec.getGraphEncoding(bG);
	}
	
	/**
	 * Converts an index vector encoding to a bitfield representation,
	 * dedicating [num_bits] bits to each "bin". First, the values in
	 * all the bins are "quantized" to an integer in [0, num_bits - 1].
	 *
	 * @param vec the integer vector containing the encoding
	 * @param num_bits the number of bits
	 * @return the vector's bitfield representation
	 */
	protected String vectorToBits(byte[] vec, int num_bits) {
		String repr = "";
		final float fact = (float) num_bits;
		for (int i = 0; i < vec.length; ++i) {
			/* map vec[i] / 64 ratio to [0, 1] range */
			final float num_set = Math.min(((float) vec[i]) / fact, 1f);

			/* convert to int between [0, Nbits] */
			final int ones =
				(int) (Math.min(num_bits - 1, (int) (num_set * fact)));

			for (int j = 0; j < num_bits; ++j) {
				if (j < ones)
					repr += "1";
				else
					repr += "0";
			}
		}
		return repr;
	}

	/**
	 * Returns the hashed vector encoding of the graph this entry represents.
	 *
	 * @return the hashed vector encoding of the underlying graph
	 */
	public byte[] getEncoding() {
		return indexEncoding;
	}

	/**
	 * Simple getter for the entry's graph label.
	 *
	 * @return the label of the graph the entry refers to
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Simple getter for the entry's key.
	 *
	 * @return the key of the entry
	 */
	public String getKey() {
		return vectorToBits(indexEncoding, 64);
	}

	/**
	 * Gets the entry's serialized key of a given order.
	 *
	 * @param order the order for serialization
	 * @return the key of the entry
	 */
	public String getKey(int order) {
		return vectorToBits(indexEncoding, order);
	}
	
	/**
	 * The hash of the index entry is determined uniquely by the
	 * hash of the label of the graph that the entry refers to.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(label);
	}

	/**
	 * Two {@link TrieEntry} objects are considered equal if
	 * they refer to the same graph.
	 */
	@Override
	public boolean equals(Object other) {
		if (null == other)
			return false;

		if (!(other instanceof TrieEntry))
			return false;

		TrieEntry eOther = (TrieEntry) other;
		if (this.getLabel().equals(eOther.getLabel())) {
			if (this.getEncoding().equals(eOther.getEncoding()))
				return true;
			else
				return false;
		}
		else {
			return false;
		}
	}
}
