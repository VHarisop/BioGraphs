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

package gr.demokritos.biographs.indexing.comparators;

import gr.demokritos.biographs.BioGraph;
import gr.demokritos.biographs.indexing.preprocessing.VertexHashVector;

/**
 * A custom comparator that encodes each graph using a 
 * {@link gr.demokritos.biographs.indexing.preprocessing.VertexHashVector}
 * object and compares the partial sums of their vertex hash vectors.
 *
 */
public class VertexHashComparator
	implements TreeComparator 
{
	/**
	 * The VertexHashVector object used internally by the comparator.
	 */
	protected VertexHashVector vHash;

	public VertexHashComparator(int numBins) {
		this.vHash = new VertexHashVector().withBins(numBins).withPartialSums();
	}
	/**
	 * @see TreeComparator#getDistance 
	 */
	public double getDistance(BioGraph bgA, BioGraph bgB) {
		int[] hashVectorA = this.vHash.encodeGraph(bgA.getGraph()),
			  hashVectorB = this.vHash.encodeGraph(bgB.getGraph());

		/* compute the sum of the vector differences
		 * vectors have the same length */
		int sum = 0;
		for (int i = 0; i < hashVectorA.length; ++i) {
			sum += Math.abs(hashVectorA[i] - hashVectorB[i]);
		}
		return sum;
	}

	@Override
	public int compare(BioGraph bgA, BioGraph bgB) {
		int vHashA = this.vHash.getHashVectorSum(bgA.getGraph()),
			vHashB = this.vHash.getHashVectorSum(bgB.getGraph());

		return vHashA - vHashB;
	}
}

