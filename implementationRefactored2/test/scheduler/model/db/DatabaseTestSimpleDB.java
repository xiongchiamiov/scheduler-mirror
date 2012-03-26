package edu.calpoly.csc.scheduler.model.db;


public class DatabaseTestSimpleDB extends DatabaseTest {
	public IDatabase createBlankDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
