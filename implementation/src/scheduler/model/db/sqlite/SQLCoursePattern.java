package scheduler.model.db.sqlite;

import scheduler.model.db.IDBOfferedDayPattern;

public class SQLCoursePattern extends SQLObject implements IDBOfferedDayPattern {
	Integer courseID, patternID;
	
	public SQLCoursePattern(Integer id, Integer courseID, Integer patternID) {
		super(id);
		this.courseID = courseID;
		this.patternID = patternID;
	}

	public Integer getCourseID() {return courseID;}
	public void setCourseID(Integer course) {
		courseID = course;
	}
	
	public Integer getPatternID() {return patternID;}
	public void setPatternID(Integer pattern) {
		patternID = pattern;
	}
}
