package scheduler.model;

import scheduler.model.db.IDatabase;

public class ScheduleItemsTestSimpleDB extends ScheduleItemsTest {
	@Override
	public IDatabase createBlankDatabase() {
		return new scheduler.model.db.simple.Database();
	}
}
