package gr.demokritos.biographs.algorithms;

import org.apache.commons.math3.ml.clustering.Clusterable;

public final class HashVector implements Clusterable {
	/**
	 * The hash vector's elements.
	 */
	protected double[] hashVec;

	/**
	 * Initializes a HashVector object from a double array.
	 *
	 * @param elements the elements of the array
	 */
	public HashVector(double[] elements) {
		hashVec = elements;
	}

	/**
	 * @see Clusterable#getPoint
	 */
	public double[] getPoint() {
		return hashVec;
	}
}
