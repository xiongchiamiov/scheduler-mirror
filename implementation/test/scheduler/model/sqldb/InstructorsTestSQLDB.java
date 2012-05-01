package scheduler.model.sqldb;

import scheduler.model.InstructorsTest;
import scheduler.model.db.IDatabase;

public class InstructorsTestSQLDB extends InstructorsTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.sqlite.SQLdb(); }
}
