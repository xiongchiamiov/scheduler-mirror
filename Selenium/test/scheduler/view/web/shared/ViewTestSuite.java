package scheduler.view.web.shared;

import junit.framework.TestSuite;

/**
 * Container for all view testcases
 * current est. coverage: 
 */
public class ViewTestSuite extends TestSuite {

	/**
	 * Instantiates a new view test suite.
	 *
	 * @param name the name
	 */
	public ViewTestSuite(String name) {
		super();
	}
	
	/**
	 * Suite.
	 *
	 * @return the test suite
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("View testcases");
		
		//suite.addTestSuite(FirefoxLoginTest.class);
		suite.addTestSuite(GRCAcceptanceTestFirefox.class);
		suite.addTestSuite(InstructorsViewTest.class);
		
		return suite;
	}
}