package edu.calpoly.csc.scheduler;


import junit.framework.TestCase;
import junit.framework.TestSuite;

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

		suite.addTestSuite(DocumentsTestSimpleDB.class);
		suite.addTestSuite(InstructorsTestSimpleDB.class);
		suite.addTestSuite(InstructorsPreferencesTestSimpleDB.class);
		suite.addTestSuite(LocationsTestSimpleDB.class);
		suite.addTestSuite(TempAlgorithmTestSimpleDB.class);
		suite.addTestSuite(CSVTest.class);
		
		return suite;
	}
}
