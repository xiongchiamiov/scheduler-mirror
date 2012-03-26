package edu.calpoly.csc.scheduler.model.db;

import java.util.Set;

import edu.calpoly.csc.scheduler.model.Day;

public interface IDBScheduleItem extends IDBObject {
	int getSection();
	void setSection(int section);
	public Set<Day> getDays();
	public void setDays(Set<Day> days);
	public int getStartHalfHour();
	public void setStartHalfHour(int startHalfHour);
	public int getEndHalfHour();
	public void setEndHalfHour(int endHalfHour);
	public boolean isPlaced();
	public void setIsPlaced(boolean placed);
	public boolean isConflicted();
	public void setIsConflicted(boolean conflicted);
}
