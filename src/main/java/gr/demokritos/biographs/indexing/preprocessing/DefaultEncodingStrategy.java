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
import gr.demokritos.iit.jinsect.structs.UniqueVertexGraph;

/**
 * The default encoding strategy that operates on the vertices of a
 * {@link UniqueVertexGraph}, assigning to each vertex an encoding
 * value equal to the sum of its incident weights.
 * 
 * @author VHarisop
 */
public final class DefaultEncodingStrategy
	implements EncodingStrategy<Double> {
	/**
	 * Returns the sum of a vertex's incoming and outgoing
	 * weights as an encoding.
	 *
	 * @param vCurr the vertex to encode
	 * @param uvGraph the graph that the vertex resides in
	 * @return the vertex's assigned encoding value
	 */
	public Double encode(JVertex vCurr, UniqueVertexGraph uvGraph) {
		return uvGraph.weightSumOf(vCurr);
	}
}
