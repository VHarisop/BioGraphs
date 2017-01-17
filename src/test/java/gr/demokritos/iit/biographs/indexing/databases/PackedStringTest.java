package gr.demokritos.iit.biographs.indexing.databases;


import gr.demokritos.iit.jinsect.JUtils;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackedStringTest extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public PackedStringTest( final String testName ) {
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(PackedStringTest.class);
	}

	public void testPacking() {
		final String aLH =     "00000000010011000000000001001000";
		final String twoLs =   "00000000010011000000000001001100";
		assertTrue(JUtils.packCharArray(twoLs).equalsIgnoreCase("LL"));
		assertTrue(JUtils.packCharArray(aLH).equalsIgnoreCase("LH"));
	}
}
