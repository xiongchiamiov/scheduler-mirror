package scheduler.model;

import scheduler.model.db.IDatabase;

public class InstructorsTestSQLDB extends InstructorsTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.sqlite.SQLdb(); }
}
