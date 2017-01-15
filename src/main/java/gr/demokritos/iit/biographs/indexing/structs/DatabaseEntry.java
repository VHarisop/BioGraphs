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

/**
 * Simple class to hold database entries that consist
 * of generic key-value pairs.
 */
public abstract class DatabaseEntry<V> {
	/**
	 * The key of the entry.
	 */
	protected String label;

	/**
	 * The encoding of the entry
	 */
	protected V encoding;

	/**
	 * Creates a new DatabaseEntry from a key-value pair.
	 *
	 * @param label the entry's label
	 * @param encoding the entry's encoding
	 */
	public DatabaseEntry(final String label, final V encoding) {
		this.label = label;
		this.encoding = encoding;
	}

	/**
	 * Simple getter for the entry's key.
	 *
	 * @return the key of the entry
	 */
	public final String getLabel() {
		return label;
	}

	/**
	 * Simple getter for the entry's encoding.
	 *
	 * @return the encoding vector of the entry
	 */
	public final V getEncoding() {
		return encoding;
	}

	abstract protected String vectorToBits(final V vec, int num_bits);

	/**
	 * Gets the entry's serialized key of a given order.
	 *
	 * @param order the order for serialization
	 * @return the key of the entry
	 */
	public final String getKey(final int order) {
		return vectorToBits(encoding, order);
	}
}
