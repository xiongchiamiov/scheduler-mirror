package scheduler.model.db.simple;

import java.util.Set;

import scheduler.model.Day;
import scheduler.model.db.IDBScheduleItem;

public class DBScheduleItem extends DBObject implements IDBScheduleItem {
	Integer scheduleID;
	Integer instructorID, courseID, locationID;
	int section;
	Set<Day> days;
	int startHalfHour;
	int endHalfHour;
	boolean isPlaced;
	boolean isConflicted;
	Integer lectureScheduleItemID;
	
	public DBScheduleItem(Integer id, Integer scheduleID, Integer instructorID,
			Integer courseID, Integer locationID, int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced,
			boolean isConflicted, Integer lectureScheduleItemID) {
		super(id);
		this.scheduleID = scheduleID;
		this.instructorID = instructorID;
		this.courseID = courseID;
		this.locationID = locationID;
		this.section = section;
		this.days = days;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
		this.isPlaced = isPlaced;
		this.isConflicted = isConflicted;
		this.lectureScheduleItemID = lectureScheduleItemID;
	}
	public DBScheduleItem(DBScheduleItem that) {
		this(that.id, that.scheduleID, that.instructorID, that.courseID, that.locationID, that.section, that.days, that.startHalfHour, that.endHalfHour, that.isPlaced, that.isConflicted, that.lectureScheduleItemID);
	}

	public void sanityCheck() {
		assert(scheduleID != null);
		assert(instructorID != null);
		assert(courseID != null);
		assert(locationID != null);
		assert(days != null);
	}
	
	@Override
	public int getSection() { return section; }
	@Override
	public void setSection(int section) { this.section = section; }
	public Set<Day> getDays() {
		return days;
	}
	public void setDays(Set<Day> days) {
		this.days = days;
	}
	public int getStartHalfHour() {
		return startHalfHour;
	}
	public void setStartHalfHour(int startHalfHour) {
		this.startHalfHour = startHalfHour;
	}
	public int getEndHalfHour() {
		return endHalfHour;
	}
	public void setEndHalfHour(int endHalfHour) {
		this.endHalfHour = endHalfHour;
	}
	public boolean isPlaced() {
		return isPlaced;
	}
	public void setIsPlaced(boolean isPlaced) {
		this.isPlaced = isPlaced;
	}
	public boolean isConflicted() {
		return isConflicted;
	}
	public void setIsConflicted(boolean isConflicted) {
		this.isConflicted = isConflicted;
	}
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			assert(false);
			return null;
		}
	}
}
