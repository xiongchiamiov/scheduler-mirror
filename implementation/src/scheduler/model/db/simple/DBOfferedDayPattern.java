package scheduler.model.db.simple;

import scheduler.model.db.IDBOfferedDayPattern;

public class DBOfferedDayPattern extends DBObject implements IDBOfferedDayPattern {
	private static final long serialVersionUID = 1337L;
	
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

	public void sanityCheck() {
		assert(courseID != null);
		assert(dayPatternID != null);
	}
}
