package scheduler.model.db.sqlite;

import scheduler.model.db.IDBTimePreference;
import scheduler.model.db.sqlite.SQLdb.Table;
/**
 * The Class SQLTimePreference implements all methods of the IDBTimePreference class (part of the IDatabase interface).
 * This class represents the relationship between a time, an instructor, and that instructor's preference of that time in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLTimePreference extends SQLObject implements IDBTimePreference {
	Integer timeID;
	Integer instructorID;
	int preference;
//	Integer day;
//	Integer time;
	
	public SQLTimePreference(Integer id, Integer timeID, Integer instructorID, Integer preference) {
		super(id);
		this.timeID = timeID;
		this.instructorID = instructorID;
		this.preference = preference;
///		this.day = day;
//		this.time = time;
	}
	
	public SQLTimePreference(SQLTimePreference that) {
		this(that.id, that.timeID, that.instructorID, that.preference);
	}
	
	public void sanityCheck() {
		assert(instructorID != null);
		assert(timeID != null);
	}
	
	@Override
	public int getPreference() { return preference; }
	@Override
	public void setPreference(int preference) { this.preference = preference; }
}
