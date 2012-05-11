package scheduler.model.sqldb;

import scheduler.model.UsersTest;
import scheduler.model.db.IDatabase;

public class UsersTestSQLDB extends UsersTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.sqlite.SQLdb(); }
}
