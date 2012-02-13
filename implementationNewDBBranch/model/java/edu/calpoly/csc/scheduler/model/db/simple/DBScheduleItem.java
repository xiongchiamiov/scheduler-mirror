package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;

public class DBScheduleItem extends DBObject implements IDBScheduleItem {
	int scheduleID;
	int instructorID, courseID, locationID;
	int section;
	
	public DBScheduleItem(Integer id, int scheduleID, int instructorID, int courseID, int locationID, int section) {
		super(id);
		this.scheduleID = scheduleID;
		this.instructorID = instructorID;
		this.courseID = courseID;
		this.locationID = locationID;
		this.section = section;
	}
	public DBScheduleItem(DBScheduleItem that) {
		this(that.id, that.scheduleID, that.instructorID, that.courseID, that.locationID, that.section);
	}
	
	@Override
	public int getSection() { return section; }
	@Override
	public void setSection(int section) { this.section = section; }
}
