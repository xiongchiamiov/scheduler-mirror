package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Set;

public class ScheduleItemGWT implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int courseID;
	private int instructorID;
	private int locationID;
	private int section;
	private Set<DayGWT> days;
	private int startHalfHour;
	private int endHalfHour;
	private boolean placed;
	private boolean conflicted;
	
	public ScheduleItemGWT() { }
	
	public ScheduleItemGWT(int id, int courseID, int instructorID, int locationID,
			int section, Set<DayGWT> days, int startHalfHour, int endHalfHour,
			boolean placed, boolean conflicted) {
		super();
		this.id = id;
		this.courseID = courseID;
		this.instructorID = instructorID;
		this.locationID = locationID;
		this.section = section;
		this.days = days;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
		this.placed = placed;
		this.conflicted = conflicted;
	}
	
	public int getID() { return id; }
	public void setID(int id) { this.id = id; }
	public int getCourseID() { return courseID; }
	public void setCourseID(int courseID) { this.courseID = courseID; }
	public int getInstructorID() { return instructorID; }
	public void setInstructorID(int instructorID) { this.instructorID = instructorID; }
	public int getLocationID() { return locationID; }
	public void setLocationID(int locationID) { this.locationID = locationID; }
	public int getSection() { return section; }
	public void setSection(int section) { this.section = section; }
	public Set<DayGWT> getDays() { return days; }
	public void setDays(Set<DayGWT> days) { this.days = days; }
	public int getStartHalfHour() { return startHalfHour; }
	public void setStartHalfHour(int startHalfHour) { this.startHalfHour = startHalfHour; }
	public int getEndHalfHour() { return endHalfHour; }
	public void setEndHalfHour(int endHalfHour) { this.endHalfHour = endHalfHour; }
	public boolean isPlaced() { return placed; }
	public void setPlaced(boolean placed) { this.placed = placed; }
	public boolean isConflicted() { return conflicted; }
	public void setConflicted(boolean conflicted) { this.conflicted = conflicted; }
}
