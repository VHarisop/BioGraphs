package gr.demokritos.iit.biographs.indexing.structs;

import java.util.Objects;

/**
 * SparseEntry is a class that contains the encoding resulting from a
 * sparse projection. It is intended to be serialized and stored in a
 * {@link gr.demokritos.iit.biographs.indexing.databases.TrieIndex}
 * or
 * {@link gr.demokritos.iit.biographs.indexing.databases.RadixIndex}.
 * @author vharisop
 *
 */
public class SparseEntry extends DatabaseEntry<double[]> {
	/**
	 * Creates a new {@link SparseEntry} with a specified label for the
	 * underlying data, using a provided encoding vector.
	 * @param label the label of the entry
	 * @param encoding
	 */
	public SparseEntry(final String label, final double[] encoding) {
		super(label, encoding);
	}

	/**
	 * Converts a vector encoding to a String representation,
	 * dedicating [num_bits] bits to each "bin". First, the values in
	 * all the bins are "quantized" to an integer in [0, num_bits - 1].
	 *
	 * @param vec the vector containing the encoding
	 * @param num_bits the number of bits
	 * @return the vector's bitfield representation
	 */
	@Override
	protected final String vectorToBits(final double[] vec, final int num_bits) {
		final StringBuilder repr = new StringBuilder(num_bits * vec.length);
		final double fact = num_bits;
		for (final double element : vec) {
			/* map vec[i] / 64 ratio to [0, 1] range */
			final double num_set = Math.min(element / fact, 1.0);

			/* convert to int between [0, Nbits] */
			final int ones =
				(Math.min(num_bits - 1, (int) (num_set * fact)));
			/* Add proper number of leading 1s */
			for (int j = 0; j < ones; ++j) {
				repr.append("1");
			}
			/* Add proper number of trailing 0s */
			for (int j = ones; j < num_bits; ++j) {
				repr.append("0");
			}
		}
		return repr.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(label);
	}

	/**
	 * Two {@link SparseEntry} objects are considered equal if
	 * they refer to the same graph and map to the same
	 * {@link #encoding}.
	 */
	@Override
	public boolean equals(final Object other) {
		if (null == other) {
			return false;
		}

		if (!(other instanceof TrieEntry)) {
			return false;
		}

		final SparseEntry eOther = (SparseEntry) other;
		if (this.getLabel().equals(eOther.getLabel())) {
			return getEncoding().equals(eOther.getEncoding());
		}
		else {
			return false;
		}
	}
}
