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
 * The default class used for hashing the labels of JVertex objects.
 * Each vertex is hashed according to its label's initial letter.
 */
public final class DefaultHashStrategy
	implements HashingStrategy<JVertex> 
{
	/**
	 * Creates a new DefaultHashStrategy object.
	 */
	public DefaultHashStrategy() {}

	/**
	 * Computes the hash value of a JVertex object as the ascii value
	 * of its label's first letter.
	 *
	 * @param toHash the vertex to be hashed
	 * @return the hash value
	 */
	public int hash(JVertex toHash) {
		/* get ascii val of initial letter of label */
		return (int) toHash.getLabel().charAt(0);
	}
}
