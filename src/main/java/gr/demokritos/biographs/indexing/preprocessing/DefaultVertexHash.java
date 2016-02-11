package gr.demokritos.biographs.indexing.preprocessing;

import gr.demokritos.iit.jinsect.structs.JVertex;

/**
 * The default class used for hashing the labels of JVertex objects.
 * Each vertex is hashed according to its label's initial letter.
 */
public final class DefaultVertexHash
	implements HashingStrategy<JVertex> 
{
	/**
	 * Creates a new DefaultVertexHash object.
	 */
	public DefaultVertexHash() {}

	/**
	 * Computes the hash value of a JVertex object as the ascii value
	 * of its label's first letter.
	 *
	 * @param toHash the vertex to be hashed
	 * @return the hash value
	 */
	public int hash(JVertex toHash) {
		/* get ascii val of initial letter of label */
		return (int) toHash.getLabel().charAt(0);
	}
}
