package edu.calpoly.csc.scheduler;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class InstructorsPreferencesTestSimpleDB extends InstructorsPreferencesTest {
	IDatabase createDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
