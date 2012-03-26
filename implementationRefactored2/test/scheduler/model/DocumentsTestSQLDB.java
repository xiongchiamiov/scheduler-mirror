package scheduler.model;

import scheduler.model.db.IDatabase;

public class DocumentsTestSQLDB extends DocumentsTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.sqlite.SQLdb(); }
}
