package gr.demokritos.biographs.indexing.preprocessing;

import java.util.TreeMap;

import gr.demokritos.biographs.BioGraph;
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
	 * Creates an empty LabelHash object using a specified hash method.
	 *
	 * @param hashSg the hash method to use
	 */
	public DefaultHashVector(
			HashingStrategy<JVertex> hashSg) 
	{
		hashStrategy = hashSg;
		initParameters();
	}

	private void initParameters() {
		vertexMap = new TreeMap<Integer, Double>();
		usePartial = false;
	}

	/**
	 * Sets the {@link #encodingStrategy} to be used when adding a new vertex.
	 *
	 * @param encSg the encoding strategy to be used
	 * @return the modified DefaultHashVector
	 */
	public DefaultHashVector withEncoding(EncodingStrategy<Double> encSg) {
		this.encodingStrategy = encSg;
		return this;
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
	 * Adds a new vertex to the hash vector.
	 *
	 * @param toAdd the vertex to be added
	 */
	protected void addVertex(JVertex toAdd) {
		/* hash value modulo K */
		int hashVal = (hashStrategy.hash(toAdd) % this.K);
		Double previous = vertexMap.get(hashVal);
		Double code = encodingStrategy.encode(toAdd);

		/* if the hash key is new, it only occured once so far */
		if (previous == null) {
			vertexMap.put(hashVal, code);
		}
		/* otherwise, add 1 to its previous value */
		else {
			vertexMap.put(hashVal, previous.doubleValue() + code);
		}
	}
	/* TODO: Add an encoding strategy for vector cells! */

	/**
	 * Encodes a {@link UniqueJVertexGraph} object using label hashing on each
	 * of its vertices.
	 *
	 * @param uvg the graph to encode
	 * @return a double vector that encodes the graph
	 */
	public double[] encodeGraph(UniqueJVertexGraph uvg) {
		/* make sure the map is reset before encoding */
		this.clear();

		/* create a new encoding strategy */
		encodingStrategy = new DefaultEncodingStrategy(uvg);
		
		/* hash each of the graph's vertices */
		for (JVertex v: uvg.vertexSet()) {
			addVertex(v);
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
