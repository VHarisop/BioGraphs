package gr.demokritos.biographs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;
/**
 * Unit test for the nested loops of {@link Utils}.
 */
public class UtilsTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UtilsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( UtilsTest.class );
    }
	
	/**
	 * Verify that nested looping works properly.
	 */
	public void testLoop() {
		int range = 5, sum = 5;
		List<Integer[]> res =  Utils.dnaIndexLoop(range, sum);
		for (Integer[] r: res) {
			int sm = 0;
			for (int i : r) { sm += i; }
			assertTrue(sm == sum);
		}
	}
	
	public void testCenterLoop() {
		int range = 2, sum = 10, center = 5;
		List<Integer[]> res =  Utils.dnaIndexLoop(center, range, sum);
		for (Integer[] r: res) {
			int sm = 0;
			for (int i : r) { sm += i; }
			assertTrue(sm == sum);
		}
	}
	/**
	 * Verify that for loops with arbitrary nesting level
	 * work properly.
	 */
	public void testGenericLoop() {
		for (int levels = 2; levels < 10; ++levels) {
			int range = 3, sum = 2 * levels;
			/* make sure that the sum of the indices traversed
			 * is the desired one */
			for (Integer[] arr: Utils.genericFor(range, sum, levels)) {
				int curr_sum = 0;
				for (int i : arr) {
					curr_sum += i;
				}
				assertTrue(curr_sum == sum);
			}
		}
	}
}
