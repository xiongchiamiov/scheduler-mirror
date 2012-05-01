package scheduler.model.simpledb;

import scheduler.model.LocationsTest;
import scheduler.model.db.IDatabase;

public class LocationsTestSimpleDB extends LocationsTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
