package model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class CoursesTestSimpleDB extends CoursesTest {
	IDatabase createDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
