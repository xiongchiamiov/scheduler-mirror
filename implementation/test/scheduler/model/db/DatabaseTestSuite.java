package scheduler.model.db;

import junit.framework.TestSuite;

public class DatabaseTestSuite {

	/**
	 * The testsuite meant to fully exercise db code 
	 * Current code coverage:
	 *
	 * @param name the name
	 */
	public DatabaseTestSuite(String name) {
		super();
	}
	
	/**
	 * Suite.
	 *
	 * @return the test suite
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Database tests");
		suite.addTest(new DatabaseTestSimpleDB());
		
		return suite;
	}
}
