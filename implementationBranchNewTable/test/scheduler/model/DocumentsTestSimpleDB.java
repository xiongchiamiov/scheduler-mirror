package scheduler.model;

import scheduler.model.db.IDatabase;

public class DocumentsTestSimpleDB extends DocumentsTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
