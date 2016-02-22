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
 * An interface that must be implemented for classes that encode a vertex
 * from a UniqueJVertexGraph or BioGraph in some way. 
 * The resulting code can be of any meaningful type.
 *
 * @author VHarisop
 */
public interface EncodingStrategy<V> {
	/**
	 * Encodes a vertex from a given {@link UniqueJVertexGraph} or 
	 * a {@link gr.demokritos.biographs.BioGraph} object.
	 * @param vCurr the vertex to encode
	 * @param uvg the graph that the vertex resides in
	 * @return the vertex's assigned encoding value
	 */
	public V encode(JVertex vCurr, UniqueJVertexGraph uvg);
}
