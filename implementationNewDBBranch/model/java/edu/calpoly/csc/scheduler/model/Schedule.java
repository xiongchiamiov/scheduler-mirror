package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class Schedule {
	IDBSchedule underlyingSchedule;
	
	Schedule(IDBSchedule underlyingSchedule) {
		this.underlyingSchedule = underlyingSchedule;
	}
}
