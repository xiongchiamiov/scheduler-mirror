package scheduler.model.sqldb;

import scheduler.model.CSVTest;
import scheduler.model.db.IDatabase;

public class CSVTestSQLDB extends CSVTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
