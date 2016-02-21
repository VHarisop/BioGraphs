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
 * The default class used for hashing the labels of JVertex objects that
 * are used to encode biological data. The first two letters of each label
 * are identified as a dinucleotide pair and then assigned a distinct hash
 * value.
 *
 * @author VHarisop
 */
public final class DnaHashStrategy
	implements HashingStrategy<JVertex> 
{
	/**
	 * Creates a new DnaHashStrategy object.
	 */
	public DnaHashStrategy() {}

	/**
	 * Computes the hash value of a JVertex object from a biograph
	 * encoding biological data by grouping its pairs of dinucleotides.
	 *
	 * @param toHash the vertex to be hashed
	 * @return the hash value
	 */
	public int hash(JVertex toHash) {
		String pref = toHash.getLabel().substring(0, 2);
		int ret = 0;
		switch (pref) {
			case "AA":
				ret = 0;
				break;
			case "AC":
			case "CA":
				ret = 1;
				break;
			case "AG":
			case "GA":
				ret = 2;
				break;
			case "AT":
			case "TA":
				ret = 3;
				break;
			case "CC":
				ret = 4;
				break;
			case "CG":
			case "GC":
				ret = 5;
				break;
			case "CT":
			case "TC":
				ret = 6;
				break;
			case "GG":
				ret = 7;
				break;
			case "GT":
			case "TG":
				ret = 8;
				break;
			case "TT":
			default:
				ret = 9;
				break;
		}
		return ret;
	}
}
