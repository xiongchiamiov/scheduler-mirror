package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class InstructorsTestSQLDB extends InstructorsTest {
	public IDatabase createBlankDatabase() { return new edu.calpoly.csc.scheduler.model.db.sqlite.SQLdb(); }
}
