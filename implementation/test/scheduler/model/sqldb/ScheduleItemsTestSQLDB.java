package scheduler.model.sqldb;

import scheduler.model.ScheduleItemsTest;
import scheduler.model.db.IDatabase;

public class ScheduleItemsTestSQLDB extends ScheduleItemsTest {
	@Override
	public IDatabase createBlankDatabase() {
		return new scheduler.model.db.simple.Database();
	}
}
