package scheduler.model.db;


public class DatabaseTestSimpleDB extends DatabaseTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
