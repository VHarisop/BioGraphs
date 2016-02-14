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

/**
 * The default vertex encoder, which simply returns 1 for every vertex.
 */
public final class UnitVertexEncoder
	implements EncodingStrategy<Integer>
{
	/**
	 * Creates a new DefaultVertexEncoder object.
	 */
	public UnitVertexEncoder() {}

	public Integer encode(JVertex vCurr) {
		return new Integer(1);
	}
}
