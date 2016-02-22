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

import gr.demokritos.iit.jinsect.structs.*;

/**
 * The simplest vertex encoder, which returns the number of the
 * vertex's incident edges as an encoding value.
 *
 */
public final class VertexDegreeEncoder 
	implements EncodingStrategy<Integer> 
{
	/**
	 * @see EncodingStrategy#encode(JVertex, UniqueJVertexGraph) encode
	 */
	public Integer encode(JVertex vCurr, UniqueJVertexGraph uvGraph) {
		return uvGraph.edgesOf(vCurr).size();
	}
}
