package scheduler.model;

import scheduler.model.db.IDatabase;

public class AlgorithmJUnitTestSQLDB extends AlgorithmJUnitTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.sqlite.SQLdb(); }
}
