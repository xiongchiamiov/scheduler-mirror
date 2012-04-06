package scheduler.model.db;

import junit.framework.TestCase;

public abstract class DatabaseTestCase extends TestCase {
	abstract protected IDatabase createBlankDatabase();
}
