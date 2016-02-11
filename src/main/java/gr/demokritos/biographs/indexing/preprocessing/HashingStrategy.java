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
