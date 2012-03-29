package scheduler.model;

import scheduler.model.db.IDatabase;

public class InstructorsPreferencesTestSimpleDB extends InstructorsPreferencesTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
