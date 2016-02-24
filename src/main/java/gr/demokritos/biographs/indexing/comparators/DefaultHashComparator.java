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
import gr.demokritos.biographs.Utils;
import gr.demokritos.ntree.NodeComparator;
import gr.demokritos.ntree.Node;
import gr.demokritos.biographs.indexing.distances.ClusterDistance;
import gr.demokritos.biographs.indexing.preprocessing.HashedVector;

/**
 * A custom comparator that encodes each graph using a 
 * {@link gr.demokritos.biographs.indexing.preprocessing.VertexHashVector}
 * object and compares the partial sums of their vertex hash vectors.
 *
 */
public class DefaultHashComparator
	implements NodeComparator<BioGraph> 
{
	/**
	 * The VertexHashVector object used internally by the comparator.
	 */
	protected HashedVector vHash;

	/**
	 * Creates a new {@link DefaultHashComparator} that uses a specified
	 * {@link HashedVector} to compute encoding vectors for the
	 * graphs it will compare.
	 *
	 * @param hVec the {@link HashedVector} to use in comparisons
	 */
	public DefaultHashComparator(HashedVector hVec) {
		this.vHash = hVec;
	}

	/**
	 * Creates a new {@link DefaultHashComparator} that uses a default
	 * hash vector object, with a specified number of bins.
	 *
	 * @param numBins the number of bins to use in hashing
	 */
	public DefaultHashComparator(int numBins) {
		this.vHash = 
			new HashedVector().withBins(numBins);
	}
	/**
	 * @see NodeComparator#getDistance 
	 */
	public double getDistance(Node<BioGraph> ndA, Node<BioGraph> ndB) {
		BioGraph bgA = ndA.getKey(), bgB = ndB.getKey();
		double[] vecA = this.vHash.encodeGraph(bgA.getGraph());
		double[] vecB = this.vHash.encodeGraph(bgB.getGraph());

		/* compute the sum of absolute vector differences */
		return ClusterDistance.hamming(vecA, vecB);
	}

	@Override
	public int compare(Node<BioGraph> nodeA, Node<BioGraph> nodeB) {
		BioGraph bgA = nodeA.getKey();
		BioGraph bgB = nodeB.getKey();

		double[] vecA = this.vHash.encodeGraph(bgA.getGraph());
		double[] vecB = this.vHash.encodeGraph(bgB.getGraph());

		double diff = 0.0;
		try {
			diff = Utils.getHammingDistance(vecA, vecB);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Double.compare(diff, 0.0);
	}
}

