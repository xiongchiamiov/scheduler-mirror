package model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class DocumentsTestSimpleDB extends DocumentsTest {
	IDatabase createDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
