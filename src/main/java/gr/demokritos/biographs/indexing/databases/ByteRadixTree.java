package gr.demokritos.biographs.indexing.databases;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultByteArrayNodeFactory;

import gr.demokritos.biographs.indexing.structs.TrieEntry;

public class ByteRadixTree
extends ConcurrentRadixTree<List<TrieEntry>> {
	/**
	 *
	 */
	private static final long serialVersionUID = 348335668340411699L;

	public ByteRadixTree() {
		super(new DefaultByteArrayNodeFactory());
	}

	/**
	 * @see {@link ConcurrentRadixTree#getValueForExactKey(CharSequence)
	 * @param seq the key {@link CharSequence}
	 * @return the value for this exact key
	 */
	public List<TrieEntry> get(CharSequence key) {
		return getValueForExactKey(key);
	}

	/**
	 * Selects the values for the keys closest to a query key
	 * @param key the key to search for
	 * @return an {@link List<TrieEntry>} with the values
	 */
	public List<TrieEntry> select(CharSequence key) {
		final List<TrieEntry> results = new ArrayList<>();
		getValuesForClosestKeys(key).forEach(lst -> results.addAll(lst));
		return results;
	}
}
