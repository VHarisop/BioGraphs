package gr.demokritos.biographs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for BioGraphs' facilities that employ generics, like
 * the TreeDatabase class..
 */
public class GenericsTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GenericsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GenericsTest.class );
    }
	
	public void testGenerics() {
		TreeDatabase trd = new TreeDatabase<String>() {
			@Override
			public String getGraphFeature(BioGraph bg) {
				return bg.bioLabel;
			}
		};
	}
}
