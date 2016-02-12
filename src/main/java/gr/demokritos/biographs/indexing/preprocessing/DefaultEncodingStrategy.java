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

import gr.demokritos.iit.jinsect.structs.JVertex;
import gr.demokritos.iit.jinsect.structs.UniqueJVertexGraph;

public final class DefaultEncodingStrategy
	implements EncodingStrategy<Double> {

	/**
	 * The {@link UniqueJVertexGraph} on whose vertices this
	 * EncodingStrategy will operate on.
	 */
	protected final UniqueJVertexGraph uvGraph;
	/**
	 * Creates a new DefaultEncodingStrategy object that operates
	 * on a user-specified graph.
	 *
	 * @param uvGraph the graph to operatoe on
	 */
	public DefaultEncodingStrategy(UniqueJVertexGraph uvGraph) {
		this.uvGraph = uvGraph;
	}

	/**
	 * Returns the sum of this vertex's incoming and outgoing
	 * weights as an encoding.
	 *
	 * @param vCurr the vertex to encode
	 * @return the vertex's assigned encoding value
	 */
	public Double encode(JVertex vCurr) {
		return uvGraph.weightSumOf(vCurr);
	}
}
