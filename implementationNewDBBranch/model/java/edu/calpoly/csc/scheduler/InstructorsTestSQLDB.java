package edu.calpoly.csc.scheduler;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class InstructorsTestSQLDB extends InstructorsTest {
	IDatabase createDatabase() { return new edu.calpoly.csc.scheduler.model.db.sqlite.SQLdb(); }
}
