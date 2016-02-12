package gr.demokritos.biographs.indexing.preprocessing;

import gr.demokritos.iit.jinsect.structs.JVertex;

/**
 * The default vertex encoder, which simply returns 1 for every vertex.
 */
public final class UnitVertexEncoder
	implements EncodingStrategy<Integer>
{
	/**
	 * Creates a new DefaultVertexEncoder object.
	 */
	public UnitVertexEncoder() {}

	public Integer encode(JVertex vCurr) {
		return new Integer(1);
	}
}
