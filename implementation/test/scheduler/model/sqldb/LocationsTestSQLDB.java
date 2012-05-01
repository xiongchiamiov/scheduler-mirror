package scheduler.model.sqldb;

import scheduler.model.LocationsTest;
import scheduler.model.db.IDatabase;

public class LocationsTestSQLDB extends LocationsTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
