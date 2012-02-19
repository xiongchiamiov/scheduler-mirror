package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;

public class Schedule {
	public static class Item {
		private int section;
		private int courseID;
		private int locationID;
		private int instructorID;
		private Set<Integer> days;
		private int startHalfHour;
		private int endHalfHour;

		Item(int section, int courseID, int locationID, int instructorID, Set<Integer> days, int startHalfHour, int endHalfHour) {
			this.section = section;
			this.courseID = courseID;
			this.locationID = locationID;
			this.instructorID = instructorID;
		}
		
		public int getSection() { return section; }
		public void setSection(int section) { this.section = section; }
		public int getCourseID() { return courseID; }
		public void setCourseID(int courseID) { this.courseID = courseID; }
		public int getLocationID() { return locationID; }
		public void setLocationID(int locationID) { this.locationID = locationID; }
		public int getInstructorID() { return instructorID; }
		public void setInstructorID(int instructorID) { this.instructorID = instructorID; }
		public Set<Integer> getDays() { return days; }
		public void setDays(Set<Integer> days) { this.days = days; }
		public int getStartHalfHour() { return startHalfHour; }
		public void setStartHalfHour(int startHalfHour) { this.startHalfHour = startHalfHour; }
		public int getEndHalfHour() { return endHalfHour; }
		public void setEndHalfHour(int endHalfHour) { this.endHalfHour = endHalfHour; }
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
