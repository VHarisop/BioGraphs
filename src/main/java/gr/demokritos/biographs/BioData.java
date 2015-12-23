package gr.demokritos.biographs;

import java.io.File;

/**
 * An abstract class representing a database entry to be used in
 * {@link GraphDatabase} objects.
 *
 * @author VHarisop
 */
public abstract class BioData<K> {

	/**
	 * The path of the file containing the data
	 */
	protected File file;

	/**
	 * The object to be used as key for indexing
	 */
	protected K key;

	/**
	 * Creates a BioData object that represents data
	 * in a given file, using an object as key.
	 * @param key the object to be used as key
	 * @param fPath the path of the file
	 */
	public BioData(K key, File fPath) {
		this.key = key;
		file = fPath;
	}

	/**
	 * {@link BioData#key}
	 */
	public K getKey() {
		return key;
	}

	/**
	 * {@link BioData#file}
	 */
	public File getFile() {
		return file;
	}
}
