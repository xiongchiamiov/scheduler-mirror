package scheduler.model;

import scheduler.model.db.IDatabase;

public class CoursesTestSimpleDB extends CoursesTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
