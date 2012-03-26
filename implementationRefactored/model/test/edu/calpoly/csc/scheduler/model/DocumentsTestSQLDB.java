package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class DocumentsTestSQLDB extends DocumentsTest {
	public IDatabase createBlankDatabase() { return new edu.calpoly.csc.scheduler.model.db.sqlite.SQLdb(); }
}
