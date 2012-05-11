package scheduler.model.sqldb;

import scheduler.model.InstructorsPreferencesTest;
import scheduler.model.db.IDatabase;

public class InstructorsPreferencesTestSQLDB extends InstructorsPreferencesTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.sqlite.SQLdb(); }
}
