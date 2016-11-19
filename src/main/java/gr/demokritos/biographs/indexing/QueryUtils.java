package gr.demokritos.biographs.indexing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Contains a set of utilities related to querying with
 * BioGraphs classes.
 * @author vharisop
 *
 */
public class QueryUtils {
	/*
	 * Private constructor as this is a helper class
	 */
	private QueryUtils() {}
	
	/*
	 * Private logger for this class
	 */
	private static final Logger logger =
			Logger.getLogger(QueryUtils.class.getName());
	
	/**
	 * Splits a query string into overlapping subsequences as
	 * a preprocessing step for a query, returning the list of
	 * subsequences.
	 *
	 * @param query the query string
	 * @param seqSize the length of the sliding window that
	 * isolates each subsequence
	 * @return a {@link List} of generated subsequences
	 */
	public static final List<String>
	splitQueryString(String query, final int seqSize) {
		final int qLen = query.length();
		List<String> blocks = new ArrayList<String>();
		/*
		 * Gather all subsequences with a sliding window
		 * of length [seqSize]
		 */
		for (int index = 0; (index + seqSize) < qLen; ++index) {
			blocks.add(query.substring(index, index + seqSize));
		}
		try {
			/* Add the rightmost [seqSize] chars */
			final String sub = query.substring(qLen - seqSize, qLen);
			blocks.add(sub);
		}
		catch (StringIndexOutOfBoundsException ex) {
			/* Out of bounds means qLen < Ls, so we add
			 * the whole query string */
			blocks.add(query);
			logger.info("Query string too short: added " + query);
		}
		return blocks;
	}
	
	/**
	 * Splits a string that will be indexed into non-overlapping
	 * subsequences of a specified length, returning the resulting
	 * collection. 
	 *
	 * @param data the string to be indexed
	 * @param seqSize the length of every indexed fragment
	 * @return a {@link List} of generated subsequences
	 */
	public static final List<String>
	splitIndexedString(String data, final int seqSize) {
		final int qLen = data.length();
		List<String> blocks = new ArrayList<String>();
		/*
		 * Gather all subsequences with a window of length [seqSize]
		 */
		for (int index = 0; (index + seqSize) < qLen; index += seqSize) {
			blocks.add(data.substring(index, index + seqSize));
		}
		try {
			/* Add the rightmost [seqSize] chars */
			final String sub = data.substring(qLen - seqSize, qLen);
			blocks.add(sub);
		}
		catch (StringIndexOutOfBoundsException ex) {
			/* Out of bounds means qLen < Ls, so we add
			 * the whole string as a fragment on its own */
			blocks.add(data);
			logger.info("Indexed string too short: added " + data);
		}
		return blocks;
	}
}
