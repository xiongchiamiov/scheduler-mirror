package edu.calpoly.csc.scheduler;


import junit.framework.*;

public class SampleTestSuite extends TestCase {

	/**
	 * Instantiates a new test suite.
	 *
	 * @param name the name
	 */
	public SampleTestSuite(String name) {
		super(name);
	}
	
	/**
	 * Suite.
	 *
	 * @return the test suite
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Example testsuite (Junit3)");

		suite.addTestSuite(ModelTest.class);
		suite.addTestSuite(CSVTest.class);
		
		return suite;
	}
}
