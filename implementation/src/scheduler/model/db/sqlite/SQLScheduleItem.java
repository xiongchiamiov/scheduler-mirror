package scheduler.model.db.sqlite;

import java.util.Set;

import scheduler.model.Day;
import scheduler.model.db.IDBScheduleItem;

public class SQLScheduleItem extends SQLObject implements IDBScheduleItem {
	Integer id, docID, instID, locID, courseID, startTime, endTime, dayPatternID, sectionNum;
	
	public SQLScheduleItem(Integer id, Integer docID, Integer instID, Integer locID, Integer courseID, Integer startTime, Integer endTime, Integer dayPatternID, Integer sectionNum) {

		super(id);
		this.id = id;
		this.docID = docID;
		this.instID = instID;
		this.locID = locID;
		this.courseID = courseID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dayPatternID = dayPatternID;
		this.sectionNum = sectionNum;
	}

	@Override
	public int getSection() {
		return sectionNum;
	}

	@Override
	public void setSection(int section) {
		this.sectionNum = section;
	}

	@Override
	public Set<Day> getDays() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDays(Set<Day> days) {
		// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return false;
	}

	@Override
	public void setIsPlaced(boolean placed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConflicted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIsConflicted(boolean conflicted) {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
