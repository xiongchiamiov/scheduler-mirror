package scheduler.model.simpledb;

import scheduler.model.CSVTest;
import scheduler.model.db.IDatabase;

public class CSVTestSimpleDB extends CSVTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
