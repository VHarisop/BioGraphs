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

package gr.demokritos.iit.biographs.indexing.structs;

import java.util.Objects;

/**
 * This class represents an entry in the graph index
 * created by BioGraphs that is used by
 * {@link gr.demokritos.iit.biographs.indexing.databases.TrieIndex} and
 * {@link gr.demokritos.iit.biographs.indexing.databases.RadixIndex}.
 * It contains the original graph's label and its hashed vector encoding,
 * providing a method for serializing it to a binary string using a given
 * number of bits per index.
 *
 * @author VHarisop
 */
public final class TrieEntry extends DatabaseEntry<byte[]> {

	/**
	 * Creates a new TrieEntry object from with a given label and
	 * a specified encoding vector.
	 *
	 * @param label the label of the entry
	 * @param encoding the entry's encoding vector
	 */
	public TrieEntry(final String label, final byte[] encoding) {
		super(label, encoding);
	}

	/**
	 * Converts an index vector encoding to a bitfield representation,
	 * dedicating [num_bits] bits to each "bin". First, the values in
	 * all the bins are "quantized" to an integer in [0, num_bits - 1].
	 *
	 * @param vec the integer vector containing the encoding
	 * @param num_bits the number of bits
	 * @return the vector's bitfield representation
	 */
	@Override
	protected String vectorToBits(final byte[] vec, final int num_bits) {
		final StringBuilder repr = new StringBuilder(num_bits * vec.length);
		final float fact = num_bits;
		for (final byte element : vec) {
			/* map vec[i] / 64 ratio to [0, 1] range */
			final float num_set = Math.min((element) / fact, 1f);

			/* convert to int between [0, Nbits] */
			final int ones =
				(Math.min(num_bits - 1, (int) (num_set * fact)));
			/* Add proper number of leading 1s */
			for (int j = 0; j < ones; ++j) {
				repr.append("1");
			}
			/* Add proper number of trailing 0s */
			for (int j = ones; j < num_bits; ++j) {
				repr.append("0");
			}
		}
		return packCharArray(repr.toString());
	}

	/**
	 * Packs a char array (string) into a String of characters formed by
	 * concatenating consecutive groups of bits from the char array.
	 * @param charArray the {@link String} to pack
	 * @return the packed string
	 */
	public static final String packCharArray(final String charArray) {
		final StringBuilder packed = new StringBuilder();
		final int endIndex = charArray.length();
		for (int i = 0; i < (endIndex / 16) + 1; ++i) {
			/* Obtain a 16-bit string */
			final int toIndex = Math.min((i + 1) * 16, endIndex);
			final String charPack = charArray.substring(i * 16, toIndex);
			/* Obtain the character value that matches that int
			 * and append it to the string builder.
			 */
			if (charPack.isEmpty()) {
				/* We reached the end, nothing more to do */
				break;
			}
			packed.append((char) Integer.parseUnsignedInt(charPack, 2));
		}
		return packed.toString();
	}

	/**
	 * The hash of the index entry is determined uniquely by the
	 * hash of the label of the graph that the entry refers to.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(label);
	}

	/**
	 * Two {@link TrieEntry} objects are considered equal if
	 * they refer to the same graph and map to the same
	 * {@link #encoding}.
	 */
	@Override
	public boolean equals(final Object other) {
		if (null == other) {
			return false;
		}

		if (!(other instanceof TrieEntry)) {
			return false;
		}

		final TrieEntry eOther = (TrieEntry) other;
		if (this.getLabel().equals(eOther.getLabel())) {
			return getEncoding().equals(eOther.getEncoding());
		}
		else {
			return false;
		}
	}
}
