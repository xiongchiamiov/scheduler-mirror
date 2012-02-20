package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;

public class Schedule {
	public static class Item {
		IDBScheduleItem underlying;
		private int courseID;
		private int locationID;
		private int instructorID;
		private Set<Day> days;
		private int startHalfHour;
		private int endHalfHour;
		private boolean placed;
		private Set<Integer> labIDs;

		Item(IDBScheduleItem underlying, int section, int courseID, int locationID, int instructorID, Set<Day> days, int startHalfHour, int endHalfHour, boolean placed, Set<Integer> labIDs) {
			this.underlying = underlying;
			this.courseID = courseID;
			this.locationID = locationID;
			this.instructorID = instructorID;
			this.days = days;
			this.startHalfHour = startHalfHour;
			this.endHalfHour = endHalfHour;
			this.placed = placed;
			this.labIDs = labIDs;
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
		public Set<Day> getDays() { return days; }
		public void setDays(Set<Day> days) { this.days = days; }
		public int getStartHalfHour() { return startHalfHour; }
		public void setStartHalfHour(int startHalfHour) { this.startHalfHour = startHalfHour; }
		public int getEndHalfHour() { return endHalfHour; }
		public void setEndHalfHour(int endHalfHour) { this.endHalfHour = endHalfHour; }
		public boolean isPlaced() { return placed; }
		public void setIsPlaced(boolean placed) { this.placed = placed; }
		public Set<Integer> getLabIDs() { return labIDs; }
		public void setLabIDs(Set<Integer> labIDs) { this.labIDs = labIDs; }
	}
	
	IDBSchedule underlyingSchedule;
	private Collection<Item> items;
	
	Schedule(IDBSchedule underlyingSchedule, Collection<Item> items) {
		this.underlyingSchedule = underlyingSchedule;
		this.items = items;
	}

	public Collection<Item> getItems() { return items; }
	public void setItems(Collection<Item> items) { this.items = items; }
}
