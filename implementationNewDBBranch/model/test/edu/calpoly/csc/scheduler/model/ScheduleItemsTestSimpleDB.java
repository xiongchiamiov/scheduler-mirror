package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class ScheduleItemsTestSimpleDB extends ScheduleItemsTest {
	@Override
	public IDatabase createBlankDatabase() {
		return new edu.calpoly.csc.scheduler.model.db.simple.Database();
	}
}
