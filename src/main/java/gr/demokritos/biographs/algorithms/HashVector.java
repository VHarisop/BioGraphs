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

package gr.demokritos.biographs.algorithms;

import java.util.Objects;
import gr.demokritos.biographs.BioGraph;
import org.apache.commons.math3.ml.clustering.Clusterable;

public final class HashVector implements Clusterable {
	/**
	 * The hash vector's elements.
	 */
	protected double[] hashVec;

	/**
	 * The {@link BioGraph} object this hash vector refers to.
	 */
	protected BioGraph fromGraph;


	/**
	 * Initializes a HashVector object from a double array.
	 *
	 * @param elements the elements of the array
	 */
	public HashVector(double[] elements) {
		hashVec = elements;
		fromGraph = null;
	}

	/**
	 * Sets the biograph this vector refers to.
	 * @param bgFrom the biograph of the vector
	 */
	public void setGraph(BioGraph bgFrom) {
		fromGraph = bgFrom;
	}

	/**
	 * Gets the biograph this object refers to.
	 *
	 * @return this vector's source biograph
	 */
	public BioGraph getGraph() {
		return fromGraph;
	}

	/**
	 * @see Clusterable#getPoint
	 */
	public double[] getPoint() {
		return hashVec;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hashVec);
	}

	@Override
	public boolean equals(Object other) {
		if (null == other)
			return false;

		if (!(other instanceof HashVector))
			return false;

		HashVector otherVec = (HashVector) other;
		double[] otherPoints = otherVec.getPoint();
		if (otherPoints.length != hashVec.length)
			return false;

		for (int i = 0; i < hashVec.length; ++i) {
			if (Double.compare(hashVec[i], otherPoints[i]) != 0) {
				return false;
			}
		}
		return true;
	}
}
