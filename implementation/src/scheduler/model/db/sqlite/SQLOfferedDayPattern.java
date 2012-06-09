package scheduler.model.db.sqlite;

import scheduler.model.db.IDBOfferedDayPattern;
/**
 * The Class SQLOfferedDayPattern implements all methods of the IDBOfferedDayPattern class (part of the IDatabase interface).
 * This class represents the relationship between a course and it's day pattern in the SQLite database.
 * @author kayleneS
 *
 */
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
