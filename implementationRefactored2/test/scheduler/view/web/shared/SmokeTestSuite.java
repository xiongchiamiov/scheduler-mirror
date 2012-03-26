package edu.calpoly.csc.scheduler.view.web.shared;

import junit.framework.TestCase;
import junit.framework.TestSuite;

// TODO: Auto-generated Javadoc
/**
 * The Class SmokeTestSuite.
 */
public class SmokeTestSuite extends TestCase {

	/**
	 * Instantiates a new smoke test suite.
	 *
	 * @param name the name
	 */
	public SmokeTestSuite(String name) {
		super(name);
	}
	
	/**
	 * Suite.
	 *
	 * @return the test suite
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Example tests (Junit3)");
		
		suite.addTest(new LoginTest());
		//suite.addTestSuite(BasicSelectSchedule.class);
		//suite.addTest(new CreateScheduleTest());
		
		return suite;
	}
}