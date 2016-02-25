package gr.demokritos.biographs.indexing.inverted;

import gr.demokritos.biographs.BioGraph;
import java.util.*;

public class FreqTree extends TreeMap<Integer, Set<BioGraph>> {
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
	 * Adds a new graph to the database associated with a specified
	 * frequency. If other graphs are already associated with the same
	 * key, the key's value set is updated.
	 *
	 * @param key the frequency to associate with
	 * @param bGraph the graph to add
	 */
	public void addGraph(int key, BioGraph bGraph) {
		Set<BioGraph> in = super.get(key);
		if (null == in) {
			/* initialize the set of graphs now, add it */
			in = new HashSet<BioGraph>(); in.add(bGraph);
			super.put(key, in);
		}
		else {
			in.add(bGraph);
			super.put(key, in);
		}
	}

	/**
	 * Retrieve a set of biographs that are associated with a specified
	 * frequency, +/- a tolerance value that is provided by the user, if
	 * any, or else, the default tolerance (3) is used.
	 *
	 * @param key the frequency to look up
	 * @param tolerance the frequency tolerance - set this field to
	 * <tt>null</tt> if the default tolerance is required
	 * @return a set of graphs that match the requested frequency
	 * range
	 */
	public Set<BioGraph> getFreq(int key, Integer tolerance) {
		int lookup_eps;
		if (null == tolerance) {
			lookup_eps = eps;
		}
		else {
			lookup_eps = tolerance.intValue() + eps;
		}
		Set<BioGraph> results = new HashSet<BioGraph>();

		/* set the limits of the lookup (endFreq is +1 because
		 * of the way TreeMap.subMap(key, key) works. */
		int startFreq = Math.max(key - lookup_eps, 0);
		int endFreq = key + lookup_eps + 1;

		for (Map.Entry<Integer, Set<BioGraph>> ent: 
				super.subMap(startFreq, endFreq).entrySet())
		{
			results.addAll(ent.getValue());
		}
		return results;
	}

	/**
	 * @see #getFreq(int, int) getFreq
	 */
	public Set<BioGraph> getFreq(int key) {
		return getFreq(key, null);
	}
}
