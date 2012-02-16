package edu.calpoly.csc.scheduler.model;

import java.util.Collection;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;

public class Schedule {
	public static class Item {
		private int section;
		private int courseID;
		private int locationID;
		private int instructorID;

		Item(int section, int courseID, int locationID, int instructorID) {
			this.section = section;
			this.courseID = courseID;
			this.locationID = locationID;
			this.instructorID = instructorID;
		}
		
		int getSection() { return section; }
		void setSection(int section) { this.section = section; }
		
		public int getCourseID() { return courseID; }
		public void setCourseID(int courseID) { this.courseID = courseID; }
		public int getLocationID() { return locationID; }
		public void setLocationID(int locationID) { this.locationID = locationID; }
		public int getInstructorID() { return instructorID; }
		public void setInstructorID(int instructorID) { this.instructorID = instructorID; }
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
