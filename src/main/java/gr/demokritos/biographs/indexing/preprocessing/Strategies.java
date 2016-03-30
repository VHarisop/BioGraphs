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
 * A class with static methods to create some of the most common encoding
 * and hashing strategies for preprocessing.
 *
 * @author VHarisop
 */
public final class Strategies {
	/**
	 * Creates a simple {@link HashingStrategy} that encodes vertices
	 * based on the initial letter of their label, and returning a distinct
	 * letter for each DNA base and a default value for all other cases.
	 *
	 * @return the hashing strategy described above
	 */
	public static final HashingStrategy<JVertex> simpleDnaHash() {
		return new HashingStrategy<JVertex>() {
			@Override
			public int hash(JVertex vCurr) {
				char c = vCurr.getLabel().charAt(0);
				switch (c) {
					case 'A':
						return 0;
					case 'C':
						return 1;
					case 'G':
						return 2;
					case 'T':
						return 3;
					default:
						return 4;
				}
			}
		};
	}

	/**
	 * Creates a new {@link HashingStrategy} that encodes vertices based
	 * on the dinucleotide pair suggested by the first two letters of the
	 * vertex's label.
	 *
	 * @return the {@link HashingStrategy<JVertex>} object described above
	 */
	public static final HashingStrategy<JVertex> dinucleotideHash() {
		return new HashingStrategy<JVertex>() {
			@Override
			public int hash(JVertex vCurr) {
				String sInit = vCurr.getLabel().substring(0, 2);
				switch (sInit) {
					case "AA":
						return 0;
					case "AC":
					case "CA":
						return 1;
					case "AG":
					case "GA":
						return 2;
					case "AT":
					case "TA":
						return 3;
					case "CC":
						return 4;
					case "CG":
					case "GC":
						return 5;
					case "CT":
					case "TC":
						return 6;
					case "GG":
						return 7;
					case "GT":
					case "TG":
						return 8;
					case "TT":
						return 9;
					default:
						return 10;
				}
			}
		};
	}

	/**
	 * Creates a new {@link HashingStrategy} that encodes vertices based on
	 * the initial letter of their labels, assigning to each letter a number
	 * between 0 (for the letter 'A') and 25 (for the letter 'Z').
	 *
	 * @return the {@link HashingStrategy<JVertex>} described above
	 */
	public static final HashingStrategy<JVertex> alphabetHash() {
		return new HashingStrategy<JVertex>() {
			@Override
			public int hash(JVertex vCurr) {
				char c = vCurr.getLabel().toUpperCase().charAt(0);
				return (int) c - (int) 'A';
			}
		};
	}

	/**
	 * Creates a new {@link HashingStrategy} that encodes vertices based on
	 * the two initial letters of their labels, for DNA labels.
	 *
	 * @return the {@link HashingStrategy<JVertex>} described above
	 */
	public static final HashingStrategy<JVertex> dnaHash() {
		return new HashingStrategy<JVertex>() {
			@Override
			public int hash(JVertex vCurr) {
				char cA = vCurr.getLabel().toUpperCase().charAt(0);
				char cB = vCurr.getLabel().toUpperCase().charAt(1);

				int retA, retB;
				switch (cA) {
					case 'A':
						retA = 0; break;
					case 'C':
						retA = 1; break;
					case 'G':
						retA = 2; break;
					case 'T':
						retA = 3; break;
					default:
						retA = -1;
				}
				switch (cB) {
					case 'A':
						retB = 0; break;
					case 'C':
						retB = 1; break;
					case 'G':
						retB = 2; break;
					case 'T':
						retB = 3; break;
					default:
						retB = -1;
				}
				return retA * 4 + retB;
			}
		};
	}

	/**
	 * Creates a new {@link EncodingStrategy} that assigns the sum of
	 * incident weights (from incoming + outgoing edges) to each
	 * {@link JVertex}.
	 *
	 * @return the {@link EncodingStrategy<Double>} described above
	 */
	public static final EncodingStrategy<Double> weightEncoding() {
		return new EncodingStrategy<Double>() {
			@Override
			public Double encode(JVertex vCurr, UniqueVertexGraph uvG) {
				return uvG.weightSumOf(vCurr);
			}
		};
	}

	/**
	 * Creates a new {@link EncodingStrategy} that assigns the sum of
	 * incoming edge weights to each {@link JVertex}.
	 *
	 * @return the {@link EncodingStrategy<Double>} described above
	 */
	public static final EncodingStrategy<Double> incomingWeightEncoding() {
		return new EncodingStrategy<Double>() {
			@Override
			public Double encode(JVertex vCurr, UniqueVertexGraph uvG) {
				return uvG.incomingWeightSumOf(vCurr);
			}
		};
	}
	
	/**
	 * Creates a new {@link EncodingStrategy} that assigns the number
	 * of incident edges to each {@link JVertex}.
	 *
	 * @return the {@link EncodingStrategy<Integer>} described above
	 */
	public static final EncodingStrategy<Integer> degreeEncoding() {
		return new EncodingStrategy<Integer>() {
			@Override
			public Integer encode(JVertex vCurr, UniqueVertexGraph uvG) {
				return uvG.edgesOf(vCurr).size();
			}
		};
	}
	
	/**
	 * Creates a new {@link EncodingStrategy} that assigns the number
	 * of incoming edges to each {@link JVertex}.
	 *
	 * @return the {@link EncodingStrategy<Integer>} described above
	 */
	public static final EncodingStrategy<Integer> inDegreeEncoding() {
		return new EncodingStrategy<Integer>() {
			@Override
			public Integer encode(JVertex vCurr, UniqueVertexGraph uvG) {
				return uvG.incomingEdgesOf(vCurr).size();
			}
		};
	}

	/**
	 * Creates a new {@link EncodingStrategy} that assigns the ratio of
	 * minimum over maximum of incident edge weights to each {@link JVertex}.
	 *
	 * @return the {@link EncodingStrategy<Integer>} described above
	 */
	public static final EncodingStrategy<Integer> weightRatioEncoding() {
		return new EncodingStrategy<Integer>() {
			@Override
			public Integer encode(JVertex vCurr, UniqueVertexGraph uvg) {
				double wMin = Double.MAX_VALUE;
				double wMax = Double.MIN_VALUE;

				/* find minimum and maximum incoming weights */
				for (Edge e: uvg.incomingEdgesOf(vCurr)) {
					if (e.edgeWeight() < wMin) {
						wMin = e.edgeWeight();
					}
					if (e.edgeWeight() > wMax) {
						wMax = e.edgeWeight();
					}
				}

				/* map ratio from [eps, 1] to [eps, 100] */
				double ratio = (wMin / wMax) * 100;
				return (int) Math.round(ratio);
			}
		};
	}
}
