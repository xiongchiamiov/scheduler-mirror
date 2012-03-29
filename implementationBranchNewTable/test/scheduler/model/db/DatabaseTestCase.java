package scheduler.model.db;

import junit.framework.TestCase;

public abstract class DatabaseTestCase extends TestCase {
	protected IDatabase createBlankDatabase() {
		return new scheduler.model.db.simple.Database();
	}
}
