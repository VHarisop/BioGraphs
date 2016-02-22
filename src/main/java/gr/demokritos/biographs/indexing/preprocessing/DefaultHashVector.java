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
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.iit.jinsect.structs.*;

/**
 * A class that hashes vertex labels based on an arbitrary hash function
 * and creates a vector that maps each hash value to a number of occurences.
 */
public class DefaultHashVector {
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
	 * Creates an empty DefaultHashVector object using the default method
	 * for hashing.
	 */
	public DefaultHashVector() {
		hashStrategy = new DefaultHashStrategy();
		initParameters();
	}

	/**
	 * Creates an empty DefaultHashVector object using the default method
	 * for hashing based on a specified graph type. If the graph type
	 * suggests using with biological data, the default hash strategy is
	 * a {@link DinucleotideHash} object, while in the other case the hash
	 * strategy used is {@link DefaultHashStrategy}. Vector size differs
	 * as well, with 10 being the default value for DNA and 26 for words.
	 *
	 * @param gType the graph type to base the hash strategy on
	 */
	public DefaultHashVector(GraphDatabase.GraphType gType) {
		if (gType == GraphDatabase.GraphType.DNA) {
			hashStrategy = new DinucleotideHash();
			initParameters(10);
		}
		else {
			hashStrategy = new DefaultHashStrategy();
			initParameters(26);
		}
	}

	/**
	 * Creates an empty LabelHash object using a specified hash method.
	 *
	 * @param hashSg the hash method to use
	 */
	public DefaultHashVector(HashingStrategy<JVertex> hashSg) 
	{
		hashStrategy = hashSg;
		initParameters();
	}

	private void initParameters() {
		vertexMap = new TreeMap<Integer, Double>();
		usePartial = false;
	}

	private void initParameters(int bins) {
		vertexMap = new TreeMap<Integer, Double>();
		usePartial = false;
		K = bins;
	}

	/**
	 * Sets the {@link #usePartial} flag in this object and returns
	 * the modified object.
	 *
	 * @return the modified DefaultHashVector
	 */
	public DefaultHashVector withPartialSums() {
		this.usePartial = true;
		return this;
	}

	/**
	 * Sets the {@link #K} value, which is the number of distinct hash bins
	 * and returns the modified object.
	 *
	 * @param newK the new number of bins
	 * @return the modified DefaultHashVector
	 */
	public DefaultHashVector withBins(int newK) {
		this.K = newK;
		return this;
	}

	/**
	 * Simple getter for the hashing strategy used by this object.
	 * @return the object's hashing strategy
	 */
	public HashingStrategy<JVertex> getHashStrategy() {
		return hashStrategy;
	}

	/**
	 * Sets a new hashing strategy to be used by this object.
	 * @param newSg the new strategy
	 */
	public void setHashStrategy(HashingStrategy<JVertex> newSg) {
		hashStrategy = newSg;
	}
	
	/**
	 * Sets a new encoding strategy to be followed when adding vertices.
	 * @param encSg the new strategy to use
	 */
	public void setEncodingStrategy(EncodingStrategy<Double> encSg) {
		encodingStrategy = encSg;
	}

	/**
	 * Sets the number of bins to be used in hashing.
	 *
	 * @param newNum the new number of bins
	 */
	public void setBins(int newNum) {
		K = newNum;
	}

	/**
	 * Adds a new vertex to the hash vector.
	 *
	 * @param toAdd the vertex to be added
	 * @param uvg the graph that the vertex resides in
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
	 * Encodes a {@link UniqueVertexGraph} object using label hashing on each
	 * of its vertices.
	 *
	 * @param uvg the graph to encode
	 * @return a double vector that encodes the graph
	 */
	public double[] encodeGraph(UniqueVertexGraph uvg) {
		/* make sure the map is reset before encoding */
		this.clear();

		/* create a new encoding strategy */
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
	 * Encodes a {@link BioGraph} object using label hashing.
	 *
	 * @param bg the graph to encode
	 * @return a vector of doubles that encodes the graph
	 */
	public double[] encodeGraph(BioGraph bg) {
		return encodeGraph(bg.getGraph());
	}

	/**
	 * Resets the DefaultHashVector object, erasing all entries from the
	 * map and resetting all other values.
	 */
	public void clear() {
		vertexMap.clear();
	}
}
