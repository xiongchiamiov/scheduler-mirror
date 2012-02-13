package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;

public class ScheduleItem {
	private final IDBScheduleItem underlyingScheduleItem;
	
	ScheduleItem(IDBScheduleItem underlyingScheduleItem) {
		this.underlyingScheduleItem = underlyingScheduleItem;
	}
}
