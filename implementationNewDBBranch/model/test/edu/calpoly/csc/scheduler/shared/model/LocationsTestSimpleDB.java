package model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class LocationsTestSimpleDB extends LocationsTest {
	IDatabase createDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
