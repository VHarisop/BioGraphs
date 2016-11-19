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
	private static Logger logger =
			Logger.getLogger(QueryUtils.class.getName());

	/**
	 * Sets the {@link Logger} used by this class.
	 *
	 * @param newLogger the new logger to be used
	 */
	public static void setLogger(Logger newLogger) {
		logger = newLogger;
	}

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
		 * of length [seqSize]. If this window is larger
		 * than the string itself, only add the string
		 * instead.
		 */
		if (qLen < seqSize) {
			blocks.add(query);
			logger.info("Query string too short - added " + query);
		}
		else {
			for (int index = 0; (index + seqSize) < qLen; ++index) {
				blocks.add(query.substring(index, index + seqSize));
			}
			/* Add the rightmost [seqSize] chars */
			blocks.add(query.substring(qLen - seqSize, qLen));
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
		 * Gather all subsequences with a window of length [seqSize].
		 * If the length of the indexed string is less than [seqSize],
		 * add the whole string instead.
		 */
		if (qLen < seqSize) {
			blocks.add(data);
			logger.info("Indexed string too short - added " + data);
		}
		else {
			/*
			 * Non-overlapping splits
			 */
			for (int index = 0; (index + seqSize) < qLen; index += seqSize) {
				blocks.add(data.substring(index, index + seqSize));
			}
			/* Add the rightmost [seqSize] chars */
			blocks.add(data.substring(qLen - seqSize, qLen));
		}
		return blocks;
	}
}
