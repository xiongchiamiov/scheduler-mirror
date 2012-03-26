package edu.calpoly.csc.scheduler.view.web.shared;


import junit.framework.*;

public class SampleTestSuite extends TestCase {

	/**
	 * Instantiates a new smoke test suite.
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
		
		suite.addTest(new PairTest());
//		suite.addTest(new CSVTest());
		
		return suite;
	}
}
