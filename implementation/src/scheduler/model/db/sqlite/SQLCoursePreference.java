package scheduler.model.db.sqlite;

import scheduler.model.db.IDBCoursePreference;
/**
 * The Class SQLCoursePreference implements all methods of the IDBCoursePreference class (part of the IDatabase interface).
 * This class represents an instructors' preference for a given course in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLCoursePreference extends SQLObject implements IDBCoursePreference {
	Integer instructorID;
	Integer courseID;
	Integer preference;
	
	public SQLCoursePreference(Integer id, Integer instructorID, Integer courseID, Integer preference) {
		super(id);
		this.instructorID = instructorID;
		this.courseID = courseID;
		this.preference = preference;
	}
	public SQLCoursePreference(SQLCoursePreference that) {
		this(that.id, that.instructorID, that.courseID, that.preference);
	}

	@Override
	public int getPreference() { return preference; }
	@Override
	public void setPreference(int preference) { this.preference = preference; }
}
