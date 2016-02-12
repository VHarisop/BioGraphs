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
import gr.demokritos.ntree.NodeComparator;
import gr.demokritos.ntree.Node;
import gr.demokritos.biographs.indexing.preprocessing.DefaultHashVector;
import gr.demokritos.biographs.indexing.preprocessing.Utils;

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
	protected DefaultHashVector vHash;

	public DefaultHashComparator(int numBins) {
		this.vHash = 
			new DefaultHashVector().withBins(numBins);
	}
	/**
	 * @see NodeComparator#getDistance 
	 */
	public double getDistance(Node<BioGraph> ndA, Node<BioGraph> ndB) {
		BioGraph bgA = ndA.getKey(),
				 bgB = ndB.getKey();

		double[] vecA = this.vHash.encodeGraph(bgA.getGraph());
		double[] vecB = this.vHash.encodeGraph(bgB.getGraph());

		double ret = 0.0;
		/* compute the sum of absolute vector differences */
		try {
			ret = Utils.getHammingDistance(vecA, vecB);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return ret;
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

