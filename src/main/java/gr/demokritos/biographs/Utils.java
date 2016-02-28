package gr.demokritos.biographs;

import java.io.File;
import java.util.*;
import gr.demokritos.biographs.indexing.preprocessing.*;
import gr.demokritos.biographs.indexing.structs.*;

import org.biojava.nbio.core.sequence.DNASequence;

/**
 * A class containing various utilities used in preprocessing
 * tasks necessary for several indexing methods.
 *
 * @author VHarisop
 */
public final class Utils {
	/**
	 * Computes the partial sums of a double array.
	 *
	 * @param dArray the array whose partial sums are needed
	 * @return an array of equal length containing the partial sums
	 */
	public static double[] getPartialSums(double[] dArray) {
		// allocate a new array to hold the partial sums
		double[] pSums = new double[dArray.length];
		double runSum = 0.0;

		/* iterate over each entry, compute the new partial sum
		 * and update the running sum value */
		for (int i = 0; i < dArray.length; ++i) {
			pSums[i] = runSum + dArray[i];
			runSum += dArray[i];
		}
		return pSums;
	}

	/**
	 * Computes the partial sums of an integer array.
	 *
	 * @param iArray the array whose partial sums are needed
	 * @return an array of equal length containing the partial sums
	 */
	public static int[] getPartialSums(int[] iArray) {
		// allocate a new array to hold the partial sums
		int[] pSums = new int[iArray.length];
		int runSum = 0;

		/* iterate over each entry, compute the new partial sum
		 * and update the running sum value */
		for (int i = 0; i < iArray.length; ++i) {
			pSums[i] = runSum + iArray[i];
			runSum += iArray[i];
		}
		return pSums;
	}

	/**
	 * Computes the hamming distance between two encoding vectors
	 * of integral numeric type.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the hamming distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static int getHammingDistance(int[] vecA, int[] vecB) 
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Enoding vector lengths differ!");
		}
		int diff = 0;
		for (int i = 0; i < vecA.length; ++i) {
			diff += Math.abs(vecA[i] - vecB[i]);
		}
		return diff;
	}

	/**
	 * Computes the hamming distance between two encoding vectors
	 * of floating point numeric type.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the hamming distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static double getHammingDistance(double[] vecA, double[] vecB) 
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Encoding vector lengths differ!");
		}
		double diffs = 0.0;
		for (int i = 0; i < vecA.length; ++i) {
			diffs += Math.abs(vecA[i] - vecB[i]);
		}
		return diffs;
	}

	/**
	 * Computes the euclidean distance between two encoding vectors
	 * containing integers.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the euclidean distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static int getEuclideanDistance(int[] vecA, int[] vecB) 
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Encoding vector lengths differ!");
		}
		int diffs = 0;
		for (int i = 0; i < vecA.length; ++i) {
			diffs += (vecA[i] - vecB[i]) * (vecA[i] - vecB[i]);
		}
		return diffs;
	}

	/**
	 * Computes the euclidean distance between two encoding vectors
	 * of floating point numeric type.
	 * @param vecA the first vector
	 * @param vecB the second vector
	 * @return the euclidean distance of the two vectors
	 * @throws Exception if the lengths of the two vectors differ
	 */
	public static double getEuclideanDistance(double[] vecA, double[] vecB) 
		throws Exception {
		if (vecA.length != vecB.length) {
			throw new Exception("Encoding vector lengths differ!");
		}
		double diffs = 0.0;
		for (int i = 0; i < vecA.length; ++i) {
			diffs += (vecA[i] - vecB[i]) * (vecA[i] - vecB[i]);
		}
		return diffs;
	}

	/**
	 * Determines if two double values are equal with respect to a numerical
	 * threshold and returns true or false depending on the result.
	 *
	 * @param a the first value
	 * @param b the second value
	 * @return a boolean indicating if the two values are equal or not
	 */
	public static boolean equalDoubles(double a, double b) {
		return (Math.abs(a - b) < 0.0000001) ? true : false;
	}

	/**
	 * Given a sample mean and standard deviation, standardizes a vector that
	 * might or might not belong to the sample in order for it to be suitable
	 * for comparison in classification methods.
	 *
	 * @param vec the vector to standardize
	 * @param means the means of the reference sample, one for each dimension
	 * @param devs the standard deviations of the reference sample, one for each
	 * dimension
	 * @return the standardized vector
	 */
	public static double[]
	standardize(double[] vec, double means[], double devs[]) {
		double[] znew = new double[vec.length];
		for (int i = 0; i < vec.length; ++i) {
			znew[i] = (vec[i] - means[i]) / devs[i];
		}
		return znew;
	}

	/**
	 * Given a {@link HashedVector} and a FASTA file, returns an array of
	 * database entries read from that file using the hashed vector.
	 *
	 * @param path the file containing FASTA entries
	 * @param hVec the hashed vector to use
	 * @return an array of {@link GraphIndexEntry}
	 * @throws Exception if an error occurs when reading the file
	 */
	public static GraphIndexEntry[]
	fastaFileToEntries(File path, IndexVector hVec) throws Exception {
		List<GraphIndexEntry> gis = new ArrayList<GraphIndexEntry>();
		for (Map.Entry<String, DNASequence> e:
				BioGraph.readFastaFile(path).entrySet())
		{
			gis.add(new GraphIndexEntry(
						BioGraph.fromSequence(e.getValue(), e.getKey()),
						hVec)
			);
		}
		return gis.toArray(new GraphIndexEntry[gis.size()]);
	}
}
