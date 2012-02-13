package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;

public class DBCoursePreference extends DBObject implements IDBCoursePreference {
	int instructorID;
	int courseID;
	int preference;
	
	public DBCoursePreference(Integer id, int instructorID, int courseID, int preference) {
		super(id);
		this.instructorID = instructorID;
		this.courseID = courseID;
		this.preference = preference;
	}
	public DBCoursePreference(DBCoursePreference that) {
		this(that.id, that.instructorID, that.courseID, that.preference);
	}

	@Override
	public int getPreference() { return preference; }
	@Override
	public void setPreference(int preference) { this.preference = preference; }
}
