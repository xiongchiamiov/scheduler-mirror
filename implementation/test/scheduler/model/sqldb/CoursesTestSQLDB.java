package scheduler.model.sqldb;

import scheduler.model.CoursesTest;
import scheduler.model.db.IDatabase;

public class CoursesTestSQLDB extends CoursesTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
