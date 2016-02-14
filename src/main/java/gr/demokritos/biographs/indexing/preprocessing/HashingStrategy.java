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

/**
 * An interface that must be implemented by all classes used
 * for hashing vertex labels.
 */
public interface HashingStrategy<T> {
	/**
	 * Computes a hash value for a specified object.
	 * @param toHash the object to be hashed
	 * @return the object's hash value
	 */
	public int hash(T toHash);
}
