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
