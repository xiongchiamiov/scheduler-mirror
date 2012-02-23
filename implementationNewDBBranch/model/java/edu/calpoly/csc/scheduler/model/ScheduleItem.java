package edu.calpoly.csc.scheduler.model;

import java.util.Set;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;

public class ScheduleItem implements Identified {
	IDBScheduleItem underlying;
	private Integer courseID;
	private Integer locationID;
	private Integer instructorID;
	
	ScheduleItem(IDBScheduleItem underlying, Integer courseID, Integer locationID, Integer instructorID) {
		this.underlying = underlying;
		this.courseID = courseID;
		this.locationID = locationID;
		this.instructorID = instructorID;
	}

	public int getID() { return underlying.getID(); }
	public int getSection() { return underlying.getSection(); }
	public void setSection(int section) { this.underlying.setSection(section); }
	public int getCourseID() { return courseID; }
	public void setCourseID(int courseID) { this.courseID = courseID; }
	public int getLocationID() { return locationID; }
	public void setLocationID(int locationID) { this.locationID = locationID; }
	public int getInstructorID() { return instructorID; }
	public void setInstructorID(int instructorID) { this.instructorID = instructorID; }
	public Set<Day> getDays() { return underlying.getDays(); }
	public void setDays(Set<Day> days) { underlying.setDays(days); }
	public int getStartHalfHour() { return underlying.getStartHalfHour(); }
	public void setStartHalfHour(int startHalfHour) { underlying.setStartHalfHour(startHalfHour); }
	public int getEndHalfHour() { return underlying.getEndHalfHour(); }
	public void setEndHalfHour(int endHalfHour) { underlying.setEndHalfHour(endHalfHour); }
	public boolean isPlaced() { return underlying.isPlaced(); }
	public void setIsPlaced(boolean placed) { underlying.setIsPlaced(placed); }
	public boolean isConflicted() { return underlying.isConflicted(); }
	public void setIsConflicted(boolean conflicted) { underlying.setIsConflicted(conflicted); }

	public Set<Integer> getLabIDs() { assert(false); return null; }
}