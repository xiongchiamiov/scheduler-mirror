package scheduler.model;

import scheduler.model.db.IDatabase;

public class UsersTestSimpleDB extends UsersTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
