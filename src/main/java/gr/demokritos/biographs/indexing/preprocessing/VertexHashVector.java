package gr.demokritos.biographs.indexing.preprocessing;

import java.util.TreeMap;

import gr.demokritos.iit.jinsect.structs.*;

/**
 * A class that hashes vertex labels based on an arbitrary hash function
 * and creates a vector that maps each hash value to a number of occurences.
 */
public class VertexHashVector {
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
	 * The length of the resulting vector. Increase for resolution,
	 * decrease for smaller size.
	 */
	protected int K = 20;

	/**
	 * A boolean indicating if the hash vector should be transformed
	 * to a partial sum vector.
	 */
	protected boolean usePartial;

	/**
	 * Creates an empty VertexHashVector object using the default hashing method.
	 */
	public VertexHashVector() {
		vertexMap = new TreeMap<Integer, Integer>();
		hashStrategy = new DefaultVertexHash();
		usePartial = false;
	}

	/**
	 * Creates an empty LabelHash object using a specified hash method.
	 *
	 * @param hashSg the hash method to use
	 */
	public VertexHashVector(HashingStrategy<JVertex> hashSg) {
		hashStrategy = hashSg;
		vertexMap = new TreeMap<Integer, Integer>();
		usePartial = false;
	}

	/**
	 * Sets the {@link #usePartial} flag in this object and returns
	 * the modified object.
	 *
	 * @return the modified VertexHashVector
	 */
	public VertexHashVector withPartialSums() {
		this.usePartial = true;
		return this;
	}

	/**
	 * Sets the {@link #K} value, which is the number of distinct hash bins
	 * and returns the modified object.
	 *
	 * @param newK the new number of bins
	 * @return the modified VertexHashVector
	 */
	public VertexHashVector withBins(int newK) {
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
	 * Adds a new vertex to the hash vector.
	 *
	 * @param toAdd the vertex to be added
	 */
	protected void addVertex(JVertex toAdd) {
		/* hash value modulo K */
		int hashVal = (hashStrategy.hash(toAdd) % this.K);
		Integer previous = vertexMap.get(hashVal);

		/* if the hash key is new, it only occured once so far */
		if (previous == null) {
			vertexMap.put(hashVal, 1);
		}
		/* otherwise, add 1 to its previous value */
		else {
			vertexMap.put(hashVal, previous.intValue() + 1);
		}
	}
	/* TODO: Add an encoding strategy for vector cells! */

	/**
	 * Encodes a {@link UniqueJVertexGraph} object using label hashing on each
	 * of its vertices.
	 *
	 * @param uvg the graph to encode
	 * @return an integer vector that encodes the graph
	 */
	public int[] encodeGraph(UniqueJVertexGraph uvg) {
		/* make sure the map is reset before encoding */
		this.clear();

		/* hash each of the graph's vertices */
		for (JVertex v: uvg.vertexSet()) {
			addVertex(v);
		}

		/* populate the vector according to the values stored in the map */
		int[] vec = new int[this.K];
		for (int i = 0; i < this.K; ++i) {
			Integer val = vertexMap.get(i);
			vec[i] = (val == null) ? 0 : val;
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
	 * Gets the sum of the vector resulting from encoding the graph.
	 *
	 * @param UniqueJVertexGraph the graph that is encoded
	 * @return the sum of the values of the vector encoding the graph
	 */
	public int getHashVectorSum(UniqueJVertexGraph uvg) {
		int sum = 0;
		for (int curr: encodeGraph(uvg)) {
			sum += curr;
		}
		return sum;
	}

	/**
	 * Resets the VertexHashVector object, erasing all entries from the
	 * map and resetting all other values.
	 */
	public void clear() {
		vertexMap.clear();
	}
}
