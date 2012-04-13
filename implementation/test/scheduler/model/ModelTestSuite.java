package scheduler.model;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public class ModelTestSuite {

	/**
	 * The testsuite meant to fully exercise model code 
	 * Current code coverage:
	 *
	 * @param name the name
	 */
	public ModelTestSuite(String name) {
		super();
	}
	
	/**
	 * Suite.
	 *
	 * @return the test suite
	 */
	public static TestSuite suite() {
		TestSuite suite = new TestSuite("Model tests");
		suite.addTest(new AlgorithmTestSimpleDB());
		suite.addTest(new CoursesTestSimpleDB());
		suite.addTest(new DocumentsTestSimpleDB());
		suite.addTest(new DocumentsTestSQLDB());
		suite.addTest(new InstructorsPreferencesTestSimpleDB());
		suite.addTest(new InstructorsTestSimpleDB());
		suite.addTest(new InstructorsTestSQLDB());
		suite.addTest(new LocationsTestSimpleDB());
		suite.addTest(new ScheduleItemsTestSimpleDB());
		suite.addTest(new UsersTestSimpleDB());
		suite.run(new TestResult());
		
		return suite;
	}
}
