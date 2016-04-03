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

package gr.demokritos.biographs.indexing.structs;

/**
 * Simple class to hold database entries that consist
 * of generic key-value pairs.
 */
public final class DatabaseEntry<K, V> {
	/**
	 * The key of the entry.
	 */
	protected K key;

	/**
	 * The value of the entry
	 */
	protected V value;

	/**
	 * Creates a new DatabaseEntry from a key-value pair.
	 *
	 * @param key the entry's key
	 * @param value the entry's value
	 */
	public DatabaseEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Simple getter for the entry's key.
	 *
	 * @return the key of the entry
	 */
	public K getKey() {
		return key;
	}

	/**
	 * Simple getter for the entry's value.
	 *
	 * @return the value of the entry
	 */
	public V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return ((key == null) ? 0 : key.hashCode()) ^
		       ((value == null) ? 0 : value.hashCode());
	}
}
