package scheduler.model.db.sqlite;

import java.util.Iterator;
import java.util.Set;

import scheduler.model.Day;
import scheduler.model.db.IDBScheduleItem;

public class SQLScheduleItem extends SQLObject implements IDBScheduleItem {
	Integer id, docID, instID, locID, courseID, startTime, endTime, sectionNum;
	String dayPattern;
	Boolean isConflicted, isPlaced;
	Set<Day> days;
	
	public SQLScheduleItem(Integer id, Integer docID, Integer instID, Integer locID, Integer courseID, 
			Integer startTime, Integer endTime, String dayPattern, Integer sectionNum, Boolean isConflicted, 
			Boolean isPlaced, Set<Day> days) {
		super(id);
		this.id = id;
		this.docID = docID;
		this.instID = instID;
		this.locID = locID;
		this.courseID = courseID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayPattern = dayPattern;
		this.sectionNum = sectionNum;
		this.isConflicted = isConflicted;
		this.isPlaced = isPlaced;
		this.days = days;
	}
	 
	public Integer getDocID() {
		return docID;
	}
	
	public void setDocID(Integer docID) {
		this.docID = docID;
	}
	
	public Integer getLocID() {
		return locID;
	}
	
	public void setLocID(Integer locID) {
		this.locID = locID;
	}
	
	public Integer getInstID() {
		return instID;
	}
	
	public void setInstID(Integer instID) {
		this.instID = instID;
	}
	
	public Integer getCourseID() {
		return courseID;
	}
	
	public void setCourseID(Integer courseID) {
		this.courseID = courseID;
	}

	@Override
	public int getSection() {
		return sectionNum;
	}

	@Override
	public void setSection(int section) {
		this.sectionNum = section;
	}
	
	public String getDayPattern() {
		for (Day day : days) {
			dayPattern += day.toString();
		}
		return dayPattern;
	}

	@Override
	public Set<Day> getDays() {
		return days;
	}

	@Override
	public void setDays(Set<Day> days) {
		this.days = days;
	}

	@Override
	public int getStartHalfHour() {
		return this.startTime;
	}

	@Override
	public void setStartHalfHour(int startHalfHour) {
		this.startTime = startHalfHour;
	}

	@Override
	public int getEndHalfHour() {
		return this.endTime;
	}

	@Override
	public void setEndHalfHour(int endHalfHour) {
		this.endTime = endHalfHour;
	}

	@Override
	public boolean isPlaced() {
		return isPlaced;
	}

	@Override
	public void setIsPlaced(boolean placed) {
		isPlaced = placed;
	}

	@Override
	public boolean isConflicted() {
		return isConflicted;
	}

	@Override
	public void setIsConflicted(boolean conflicted) {
		isConflicted = conflicted;
	}	
}
