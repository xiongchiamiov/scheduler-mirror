package scheduler.model;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import scheduler.model.simpledb.AlgorithmTestSimpleDB;
import scheduler.model.simpledb.CoursesTestSimpleDB;
import scheduler.model.simpledb.DocumentsTestSimpleDB;
import scheduler.model.simpledb.InstructorsPreferencesTestSimpleDB;
import scheduler.model.simpledb.InstructorsTestSimpleDB;
import scheduler.model.simpledb.LocationsTestSimpleDB;
import scheduler.model.simpledb.ScheduleItemsTestSimpleDB;
import scheduler.model.simpledb.UsersTestSimpleDB;
import scheduler.model.sqldb.AlgorithmTestSQLDB;
import scheduler.model.sqldb.CSVTestSQLDB;
import scheduler.model.sqldb.CoursesTestSQLDB;
import scheduler.model.sqldb.DocumentsTestSQLDB;
import scheduler.model.sqldb.InstructorsPreferencesTestSQLDB;
import scheduler.model.sqldb.InstructorsTestSQLDB;
import scheduler.model.sqldb.LocationsTestSQLDB;
import scheduler.model.sqldb.ScheduleItemsTestSQLDB;
import scheduler.model.sqldb.UsersTestSQLDB;

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

		//suite.addTestSuite(AlgorithmTestSimpleDB.class);
		//suite.addTestSuite(AlgorithmTestSQLDB.class);

		//suite.addTestSuite(CoursesTestSimpleDB.class);
		suite.addTestSuite(CoursesTestSQLDB.class);

//		suite.addTestSuite(CSVTestSimpleDB.class);
//		suite.addTestSuite(CSVTestSQLDB.class);
		
		//suite.addTestSuite(DocumentsTestSimpleDB.class);
		suite.addTestSuite(DocumentsTestSQLDB.class);

		//suite.addTestSuite(InstructorsPreferencesTestSimpleDB.class);
		suite.addTestSuite(InstructorsPreferencesTestSQLDB.class);
		
		//suite.addTestSuite(InstructorsTestSimpleDB.class);
		suite.addTestSuite(InstructorsTestSQLDB.class);

		//suite.addTestSuite(LocationsTestSimpleDB.class);
		suite.addTestSuite(LocationsTestSQLDB.class);

		//suite.addTestSuite(ScheduleItemsTestSimpleDB.class);
		suite.addTestSuite(ScheduleItemsTestSQLDB.class);

		//suite.addTestSuite(UsersTestSimpleDB.class);
		suite.addTestSuite(UsersTestSQLDB.class);
		
		suite.run(new TestResult());
		
		return suite;
	}
}
