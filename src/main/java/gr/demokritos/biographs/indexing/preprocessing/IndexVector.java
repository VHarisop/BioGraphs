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
import gr.demokritos.biographs.indexing.GraphDatabase;
import gr.demokritos.iit.jinsect.structs.*;

/**
 * A class that hashes vertex labels based on an arbitrary hash function
 * and creates a vector that maps each hash value to the in-degree of the
 * vertex.
 *
 * @author VHarisop
 */
public class IndexVector {
	/**
	 * The underlying {@link java.util.TreeMap}, where the hash - count
	 * mappings are stored when a new vertex is added.
	 */
	protected TreeMap<Integer, Integer> vertexMap;

	/**
	 * The hashing strategy used when adding a new vertex to the mappings.
	 */
	protected HashingStrategy<JVertex> hashStrategy;

	/**
	 * The encoding strategy used when adding a new vertex to the mappings.
	 */
	protected EncodingStrategy<Integer> encodingStrategy;

	/**
	 * The length of the resulting vector. Increase for resolution,
	 * decrease for smaller size.
	 */
	protected int K = 26;

	/**
	 * Creates an empty DefaultHashVector object using the default method
	 * for hashing.
	 */
	public IndexVector() {
		hashStrategy = Strategies.alphabetHash();
		initParameters();
	}

	/**
	 * Creates an empty DefaultHashVector object using the default method
	 * for hashing based on a specified graph type. If the graph type
	 * suggests using with biological data, the default hash strategy is
	 * hashing based on dinucleotides, while in the other case the hash
	 * strategy used is an alphabetic hash. Vector size differs
	 * as well, with 10 being the default value for DNA and 26 for words.
	 *
	 * @param gType the graph type to base the hash strategy on
	 */
	public IndexVector(GraphDatabase.GraphType gType) {
		if (gType == GraphDatabase.GraphType.DNA) {
			hashStrategy = Strategies.dinucleotideHash();
			initParameters(10);
		}
		else {
			hashStrategy = Strategies.alphabetHash();
			initParameters(26);
		}

		/* only a certain encoding strategy can be used */
		encodingStrategy = Strategies.inDegreeEncoding();
	}

	/**
	 * Creates an empty LabelHash object using a specified hash method.
	 *
	 * @param hashSg the hash method to use
	 */
	public IndexVector(HashingStrategy<JVertex> hashSg) 
	{
		hashStrategy = hashSg;
		initParameters();
	}

	private void initParameters() {
		vertexMap = new TreeMap<Integer, Integer>();
	}

	private void initParameters(int bins) {
		vertexMap = new TreeMap<Integer, Integer>();
		K = bins;
	}

	/**
	 * Simple getter for the encoding strategy used by this object.
	 * @return the object's encoding strategy
	 */
	public EncodingStrategy<Integer> getEncodingStrategy() {
		return encodingStrategy;
	}

	/**
	 * Sets a new encoding strategy to be used by this object.
	 * @param newSg the new strategy
	 */
	public void setEncodingStrategy(EncodingStrategy<Integer> newSg) {
		encodingStrategy = newSg;
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
		Integer previous = vertexMap.get(hashVal);
		Integer code = encodingStrategy.encode(toAdd, uvg);

		/* if the hash key is new, it only occured once so far */
		if (previous == null) {
			vertexMap.put(hashVal, code);
		}
		/* otherwise, add 1 to its previous value */
		else {
			vertexMap.put(hashVal, previous.intValue() + code);
		}
	}

	/**
	 * Encodes a {@link UniqueVertexGraph} object using label hashing on each
	 * of its vertices.
	 *
	 * @param uvg the graph to encode
	 * @return an int vector that encodes the graph
	 */
	public int[] encodeGraph(UniqueVertexGraph uvg) {
		/* make sure the map is reset before encoding */
		this.clear();

		/* create a new encoding strategy */
		encodingStrategy = Strategies.inDegreeEncoding();
		
		/* hash each of the graph's vertices */
		for (JVertex v: uvg.vertexSet()) {
			addVertex(v, uvg);
		}

		/* populate the vector according to the values stored in the map */
		int[] vec = new int[this.K];
		for (int i = 0; i < this.K; ++i) {
			Integer val = vertexMap.get(i);
			vec[i] = (val == null) ? 0 : val;
		}
		
		return vec;
	}

	/**
	 * Encodes a {@link BioGraph} object using label hashing.
	 *
	 * @param bg the graph to encode
	 * @return a vector of doubles that encodes the graph
	 */
	public int[] encodeGraph(BioGraph bg) {
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
