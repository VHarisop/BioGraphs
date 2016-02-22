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

package gr.demokritos.biographs.indexing.preprocessing;

import java.util.TreeMap;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.Utils;
import gr.demokritos.iit.jinsect.structs.*;

/**
 * A class that hashes vertex labels based on an arbitrary hash function
 * and creates a vector that maps each distinct hash value to a sum of
 * encoding values.
 */
public class VertexVectorHasher {
	/**
	 * The underlying {@link java.util.TreeMap}, where the hash - count
	 * mappings are stored when a new vertex is added.
	 */
	protected TreeMap<Integer, Double> vertexMap;

	/**
	 * The hashing strategy used when adding a new vertex to the mappings.
	 */
	protected HashingStrategy<JVertex> hashStrategy;

	/**
	 * The encoding strategy used when adding a new vertex to the mappings.
	 */
	protected EncodingStrategy<Double> encodingStrategy;

	/**
	 * The length of the resulting vector. Increase for resolution,
	 * decrease for smaller size.
	 */
	protected int K = 26;

	/**
	 * A boolean indicating if the hash vector should be transformed
	 * to a partial sum vector.
	 */
	protected boolean usePartial;

	/**
	 * A utility class for building VertexVectorHasher objects with custom parameters.
	 */
	public static class Builder {
		private boolean usePartial;
		private HashingStrategy<JVertex> hashSg;
		private int K;

		/**
		 * Creates a new Builder object with default parameters.
		 */
		public Builder() {
			this.hashSg = new DefaultHashStrategy();
			this.K = 26;
			this.usePartial = false;
		}

		/**
		 * Sets the number of hashing bins to be used.
		 * @param K the number of hashing bins
		 */
		public Builder withK(int K) {
			this.K = K;
			return this;
		}

		/**
		 * Sets a flag that indicates whether partial sums
		 * should be used.
		 */
		public Builder withPartial() {
			this.usePartial = true;
			return this;
		}

		/**
		 * Sets the hashing strategy the object should use.
		 * @param hashSg the hashing strategy to use
		 */
		public Builder withHashStrategy(HashingStrategy<JVertex> hashSg) {
			this.hashSg = hashSg;
			return this;
		}

		/**
		 * Builds a new {@link VertexVectorHasher} with the builder's specified
		 * parameters.
		 *
		 * @return a new VertexVectorHasher object
		 */
		public VertexVectorHasher build() {
			return new VertexVectorHasher(this.hashSg, this.K, this.usePartial);
		}

	}

	/**
	 * Creates a new VertexVectorHasher object using the specified hashing
	 * strategy, bin size and flag for partial sum usage.
	 *
	 * @param hsg the hashing strategy to use
	 * @param K the number of hashing bins
	 * @param use a flag indicating if partial sums should be used
	 */
	public VertexVectorHasher(HashingStrategy<JVertex> hsg, int K, boolean use) {
		this.hashStrategy = hsg;
		this.K = K;
		this.usePartial = use;
		this.vertexMap = new TreeMap<Integer, Double>();
	}

	/**
	 * Simple getter for the hashing strategy used by this object.
	 * @return the object's hashing strategy
	 */
	public HashingStrategy<JVertex> getHashStrategy() {
		return hashStrategy;
	}

	/**
	 * Adds a new vertex to the hash vector.
	 *
	 * @param toAdd the vertex to be added
	 */
	protected void addVertex(JVertex toAdd, UniqueVertexGraph uvg) {
		/* hash value modulo K */
		int hashVal = (hashStrategy.hash(toAdd) % this.K);
		Double previous = vertexMap.get(hashVal);
		Double code = encodingStrategy.encode(toAdd, uvg);

		/* if the hash key is new, it only occured once so far */
		if (previous == null) {
			vertexMap.put(hashVal, code);
		}
		/* otherwise, add 1 to its previous value */
		else {
			vertexMap.put(hashVal, previous.doubleValue() + code);
		}
	}

	/**
	 * Encodes a {@link BioGraph} object using label hashing.
	 *
	 * @param bg the graph to encode
	 * @return a vector of doubles that encodes the graph
	 */
	public double[] encodeGraph(BioGraph bg) {
		return encodeGraph(bg.getGraph());
	}

	/**
	 * Encodes a {@link UniqueVertexGraph} object using label hashing on
	 * each of its vertices.
	 *
	 * @param uvg the graph to encode
	 * @return a double vector that encodes the graph
	 */
	protected double[] encodeGraph(UniqueVertexGraph uvg) {
		/* make sure the map is reset before encoding */
		this.clear();

		/* create a new default encoding strategy */
		encodingStrategy = new DefaultEncodingStrategy();
		
		/* hash each of the graph's vertices */
		for (JVertex v: uvg.vertexSet()) {
			addVertex(v, uvg);
		}

		/* populate the vector according to the values stored in the map */
		double[] vec = new double[this.K];
		for (int i = 0; i < this.K; ++i) {
			Double val = vertexMap.get(i);
			vec[i] = (val == null) ? 0.0 : val;
		}
		
		/* if partial sums should be used, return them instead */
		if (this.usePartial) {
			return Utils.getPartialSums(vec);
		}
		else {
			return vec;
		}
	}

	/**
	 * Resets the VertexVectorHasher object, erasing all entries from the
	 * map and resetting all other values.
	 */
	public void clear() {
		vertexMap.clear();
	}
}
