package scheduler.model.db.simple;

import scheduler.model.db.IDBOfferedDayPattern;

public class DBOfferedDayPattern extends DBObject implements IDBOfferedDayPattern {
	Integer courseID;
	Integer dayPatternID;
	
	public DBOfferedDayPattern(Integer id, Integer courseID, Integer dayPatternID) {
		super(id);
		this.courseID = courseID;
		this.dayPatternID = dayPatternID;
	}
	
	public DBOfferedDayPattern(DBOfferedDayPattern that) {
		this(that.id, that.courseID, that.dayPatternID);
	}
}
