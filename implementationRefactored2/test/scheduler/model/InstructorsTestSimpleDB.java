package scheduler.model;

import scheduler.model.db.IDatabase;

public class InstructorsTestSimpleDB extends InstructorsTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
