package gr.demokritos.biographs.indexing.preprocessing;

import gr.demokritos.biographs.*;
import gr.demokritos.iit.jinsect.structs.*;

import java.util.HashMap;
/**
 * The simplest vertex encoder, which essentially returns the sum of the
 * vertex's incident weights for encoding.
 */
public final class VertexDegreeEncoder 
	implements EncodingStrategy<Integer> 
{
	/**
	 * A hashmap used for caching results to increase efficiency.
	 */
	protected HashMap<JVertex, Integer> cachedDegrees;

	/**
	 * The default maximum capacity of the hashmap used for caching. When
	 * maximum capacity is reached, the hashmap is flushed.
	 */
	static final int MAX_CAP = 100;

	/**
	 * The graph whose vertices are encoded.
	 */
	protected final UniqueJVertexGraph uvgFrom;

	/**
	 * A running index used for tracking the usage of {@link #cachedDegrees}.
	 */
	protected int cacheUsage = 0;

	/**
	 * Creates a new VertexDegreeEncoder to operate on a Biograph.
	 * 
	 * @param bgFrom the BioGraph to operate on
	 */
	public VertexDegreeEncoder(BioGraph bgFrom) {
		this.uvgFrom = bgFrom.getGraph();
		cachedDegrees = new HashMap<JVertex, Integer>(MAX_CAP);
	}

	/**
	 * Creates a new VertexDegreeEncoder to operate on a UniqueJVertexGraph.
	 *
	 * @param uvg the vertex graph to operate on
	 */
	public VertexDegreeEncoder(UniqueJVertexGraph uvg) {
		this.uvgFrom = uvg;
		cachedDegrees = new HashMap<JVertex, Integer>(MAX_CAP);
	}

	/**
	 * @see EncodingStrategy#encode(JVertex) encode
	 */
	public Integer encode(JVertex vCurr) {
		Integer cachedVal = cachedDegrees.get(vCurr);
		if (cachedVal != null) {
			return cachedVal;
		}
		else {
			Integer newVal = uvgFrom.edgesOf(vCurr).size();
			if (cacheUsage == MAX_CAP) {
				this.resetCache();
				cachedDegrees.put(vCurr, newVal);
			}
			return newVal;
		}
	}

	/**
	 * Resets the cache of weights.
	 */
	protected void resetCache() {
		cacheUsage = 0;
		cachedDegrees.clear();
	}
}
