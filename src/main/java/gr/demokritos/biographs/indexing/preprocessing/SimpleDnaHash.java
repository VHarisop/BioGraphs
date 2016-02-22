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
 * A simple {@link HashingStrategy} that encodes a vertex from a graph that
 * represents biological data by hashing its label's possible initial letters
 * to consecutive values.
 *
 * @author VHarisop
 */
public final class SimpleDnaHash
	implements HashingStrategy<JVertex> 
{
	/**
	 * Creates a new SimpleDnaHash object.
	 */
	public SimpleDnaHash() {}

	/**
	 * Computes the hash value of a JVertex object from a biograph
	 * encoding biological data by grouping its pairs of dinucleotides.
	 *
	 * @param toHash the vertex to be hashed
	 * @return the hash value
	 */
	public int hash(JVertex toHash) {
		char c = toHash.getLabel().charAt(0);
		int ret;
		switch (c) {
			case 'A':
				ret = 0; break;
			case 'C':
				ret = 1; break;
			case 'G':
				ret = 2; break;
			case 'T':
				ret = 3; break;
			default:
				ret = 4;
		}
		return ret;
	}
}
