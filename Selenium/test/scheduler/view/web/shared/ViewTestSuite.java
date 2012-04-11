package scheduler.view.web.shared;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Container for all view testcases
 * current est. coverage: 
 */
public class ViewTestSuite extends TestCase {

	/**
	 * Instantiates a new view test suite.
	 *
	 * @param name the name
	 */
	public ViewTestSuite(String name) {
		super(name);
	}
	
	/**
	 * Suite.
	 *
	 * @return the test suite
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("View testcases");
		
		suite.addTest(new FirefoxLoginTest());
		
		return suite;
	}
}