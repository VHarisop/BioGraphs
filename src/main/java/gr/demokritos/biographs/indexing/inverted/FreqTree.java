package gr.demokritos.biographs.indexing.inverted;

import java.util.*;

/**
 * A {@link TreeMap} extension that associates entries with their
 * occurence frequencies.
 *
 * @author VHarisop
 */
public class FreqTree<V> extends TreeMap<Integer, Set<V>> {
	static final long serialVersionUID = 1L;

	/**
	 * The default tolerance factor when performing lookups.
	 */
	protected int eps = 3;

	/**
	 * Initializes an empty FreqTree.
	 */
	public FreqTree() {
		super();
	}

	/**
	 * Adds a new entry to the database associated with a specified
	 * frequency. If other entries are already associated with the same
	 * key, the key's value set is updated.
	 *
	 * @param key the frequency to associate with
	 * @param entry the entry to add
	 */
	public void addGraph(int key, V entry) {
		Set<V> in = super.get(key);
		/* if previously null, initialize the set of entries */
		if (null == in) {
			in = new HashSet<V>();
		}
		in.add(entry);
		super.put(key, in);
	}

	/**
	 * Retrieve a set of entries that are associated with a specified
	 * frequency, +/- a tolerance value that is provided by the user, if
	 * any, or else, the default tolerance (3) is used.
	 *
	 * @param key the frequency to look up
	 * @param tolerance the frequency tolerance - set this field to
	 * <tt>null</tt> if the default tolerance is required
	 * @return a set of entries that match the requested frequency
	 * range
	 */
	public Set<V> getFreq(int key, Integer tolerance) {
		int lookup_eps;
		if (null == tolerance) {
			lookup_eps = eps;
		}
		else {
			lookup_eps = tolerance.intValue() + eps;
		}
		Set<V> results = new HashSet<V>();

		/* set the limits of the lookup (endFreq is +1 because
		 * of the way TreeMap.subMap(key, key) works. */
		int startFreq = Math.max(key - lookup_eps, 0);
		int endFreq = key + lookup_eps + 1;

		for (Map.Entry<Integer, Set<V>> ent:
				super.subMap(startFreq, endFreq).entrySet())
		{
			results.addAll(ent.getValue());
		}
		return results;
	}

	/**
	 * @see #getFreq(int, int) getFreq
	 */
	public Set<V> getFreq(int key) {
		return getFreq(key, null);
	}
}
