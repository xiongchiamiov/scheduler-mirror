package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;

public class Schedule {
	IDBSchedule underlyingSchedule;
	
	Schedule(IDBSchedule underlyingSchedule) {
		this.underlyingSchedule = underlyingSchedule;
	}

	public int getID() { return underlyingSchedule.getID(); }
}
