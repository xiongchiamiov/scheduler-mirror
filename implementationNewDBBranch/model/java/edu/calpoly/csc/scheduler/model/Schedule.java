package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;

public class Schedule {
	private final IDBSchedule underlyingSchedule;
	
	Schedule(IDBSchedule underlyingSchedule) {
		this.underlyingSchedule = underlyingSchedule;
	}
}
