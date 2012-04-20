package scheduler.model.db.sqlite;

import scheduler.model.db.IDBOfferedDayPattern;

public class SQLOfferedDayPattern extends SQLObject implements IDBOfferedDayPattern {
	Integer courseID;
	Integer dayPatternID;
	
	public SQLOfferedDayPattern(Integer id, Integer courseID, Integer dayPatternID) {
		super(id);
		this.courseID = courseID;
		this.dayPatternID = dayPatternID;
	}
	
	public SQLOfferedDayPattern(SQLOfferedDayPattern that) {
		this(that.id, that.courseID, that.dayPatternID);
	}
}
