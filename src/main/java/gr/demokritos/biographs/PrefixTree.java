/******************************************************************************
 *  Copyright 2002-2015, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/


package gr.demokritos.biographs;

import java.util.LinkedList;

public class PrefixTree<Value> {

	// ascii
	private static final int R = 128;

	private Node root;
	private int N;

	/* R-way trie node */
	private static class Node {
		private Object val;
		private Node[] next = new Node[R];
	}

	/**
	 * Initialize an empty prefix tree.
	 */
	public PrefixTree() {}

	/**
	 * Returns the value associated with the given key.
	 * @param key the key
	 * @return the value associated with the key, if it's in
	 *         the prefix tree, else <tt>null</tt>
	 * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
	 */
	public Value get(String key) {
		Node x = get(root, key, 0);
		if (x == null) return null;
		return (Value) x.val;
	}

	public boolean contains(String key) {
		return get(key) != null;
	}

   	private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c], key, d+1);
    }

	/**
	 * Inserts a key-value pair into the tree, overwriting the old
	 * value with the new value if the key is already in the tree.
	 * If the value is <tt>null</tt>, it deletes the key from the 
	 * prefix tree.
	 * @param key the key
	 * @param val the value
	 * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
	 */
	public void put(String key, Value val) {
		if (val == null) 
			delete(key);
		else 
			root = put(root, key, val, 0);

	}

	private Node put(Node x, String key, Value val, int d) {
		if (x == null)
			x = new Node();
		if (d == key.length()) {
			if (x.val == null) N++;
			x.val = val;
			return x;
		}
		/* find where to insert val */
		char c = key.charAt(d);
		x.next[c] = put(x.next[c], key, val, d + 1);
		return x;
	}

	/**
	 * Returns the number of key-value pairs in the prefix tree.
	 * @return the number of key-value pairs in the tree
	 */
	public int size() {
		return N;
	}

	/**
	 * @return <tt>true</tt> if the tree is empty, else <tt>false</tt>
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/** 
	 * Returns all keys in the prefix tree as an <tt>Iterable</tt>.
	 * @return all keys in the prefix tree as an <tt>Iterable</tt>
	 */
	public Iterable<String> keys() {
		return keysWithPrefix("");
	}

	/**
	 * Returns all of the keys in the tree that start with <tt>prefix</tt>
	 * @param prefix a string prefix
	 * @return all of the keys that start with <tt>prefix</tt> as an iterable
	 */
	public Iterable<String> keysWithPrefix(String prefix) {
		LinkedList<String> results = new LinkedList<String>();
		Node x = get(root, prefix, 0);
		collect(x, new StringBuilder(prefix), results);
		return results;
	}

	private void collect(Node x, StringBuilder pref, LinkedList<String> results) {
		if (x == null) return;
		if (x.val != null) 
			results.add(pref.toString());
		for (char c = 0; c < R; c++) {
			pref.append(c);
			collect(x.next[c], pref, results);
			pref.deleteCharAt(pref.length() - 1);
		}
	}

	/**
	 * Returns all of the keys in the tree that match <tt>pattern</tt>,
	 * where * symbol is treated as a wildcard character.
	 * @param pattern a string pattern
	 * @return all of the keys that match <tt>pattern</tt> as an iterable
	 */
	public Iterable<String> keysThatMatch(String pattern) {
		LinkedList<String> results = new LinkedList<String>();
		collect(root, new StringBuilder(), pattern, results);
		return results;
	}

	private void collect
	(Node x, StringBuilder prefix, String pattern, LinkedList<String> results)
	{
		if (x == null) return;
		int d = prefix.length();
		int pl = pattern.length();
		if (d == pl && x.val != null) {
			results.add(prefix.toString());
		}
		if (d == pl)
			return;
		char c = pattern.charAt(d);
		if (c == '*') {
			for (char ch = 0; ch < R; ch++) {
				prefix.append(ch);
				collect(x.next[ch], prefix, pattern, results);
				prefix.deleteCharAt(pl - 1);
			}
		}
		else {
			prefix.append(c);
			collect(x.next[c], prefix, pattern, results);
			prefix.deleteCharAt(pl - 1);
		}
	}
	
	/**
	 * Removes a key from the prefix tree, if it is present.
	 * @param key the key
	 * @throws NullPointerException if <tt>keys</tt> is <tt>null</tt>
	 */
	public void delete(String key) {
		root = delete(root, key, 0);
	}

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            if (x.val != null) N--;
            x.val = null;
        }
        else {
            char c = key.charAt(d);
            x.next[c] = delete(x.next[c], key, d+1);
        }

        // remove subtrie rooted at x if it is completely empty
        if (x.val != null) return x;
        for (int c = 0; c < R; c++)
            if (x.next[c] != null)
                return x;
        return null;
    }
}
