package scheduler.model;

import junit.framework.TestResult;
import junit.framework.TestSuite;

public class ModelTestSuite extends TestSuite {

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
//		suite.addTestSuite(AlgorithmTestSimpleDB.class);
		suite.addTestSuite(CoursesTestSimpleDB.class);
		suite.addTestSuite(DocumentsTestSimpleDB.class);
//		suite.addTestSuite(DocumentsTestSQLDB.class);
		suite.addTestSuite(InstructorsPreferencesTestSimpleDB.class);
		suite.addTestSuite(InstructorsTestSimpleDB.class);
//		suite.addTestSuite(InstructorsTestSQLDB.class);
		suite.addTestSuite(LocationsTestSimpleDB.class);
		suite.addTestSuite(ScheduleItemsTestSimpleDB.class);
		suite.addTestSuite(UsersTestSimpleDB.class);
		suite.run(new TestResult());
		
		return suite;
	}
}
