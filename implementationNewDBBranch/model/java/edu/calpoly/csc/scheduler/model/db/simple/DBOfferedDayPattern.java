package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBOfferedDayPattern;

public class DBOfferedDayPattern extends DBObject implements IDBOfferedDayPattern {
	int courseID;
	int dayPatternID;
	
	public DBOfferedDayPattern(Integer id, int courseID, int dayPatternID) {
		super(id);
		this.courseID = courseID;
		this.dayPatternID = dayPatternID;
	}
	
	public DBOfferedDayPattern(DBOfferedDayPattern that) {
		this(that.id, that.courseID, that.dayPatternID);
	}
}
