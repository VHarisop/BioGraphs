package gr.demokritos.biographs;

import java.io.File;
import java.util.Map.Entry;

/**
 * An abstract class representing a database entry to be used in
 * {@link GraphDatabase} objects. It implements the 
 * {@link java.util.Map.Entry} interface.
 *
 * @author VHarisop
 */
public abstract class BioData<K> 
implements Entry<K, File> 
{
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
	@Override
	public K getKey() {
		return key;
	}

	/**
	 * {@link BioData#file}
	 */
	@Override
	public File getValue() {
		return file;
	}

	@Override
	public File setValue(File toSet) {
		File oldVal = file;
		file = toSet;
		return oldVal;
	}
}
