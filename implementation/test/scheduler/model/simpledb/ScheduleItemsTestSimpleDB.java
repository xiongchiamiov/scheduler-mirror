package scheduler.model.simpledb;

import scheduler.model.ScheduleItemsTest;
import scheduler.model.db.IDatabase;

public class ScheduleItemsTestSimpleDB extends ScheduleItemsTest {
	@Override
	public IDatabase createBlankDatabase() {
		return new scheduler.model.db.simple.Database();
	}
}
