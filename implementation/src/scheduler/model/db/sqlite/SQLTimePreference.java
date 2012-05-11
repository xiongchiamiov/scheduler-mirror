package scheduler.model.db.sqlite;

import scheduler.model.db.IDBTimePreference;

public class SQLTimePreference extends SQLObject implements IDBTimePreference {
	Integer instructorID;
	Integer timeID;
	int preference;
	
	public SQLTimePreference(Integer id, Integer instructorID, Integer timeID, int preference) {
		super(id);
		this.instructorID = instructorID;
		this.timeID = timeID;
		this.preference = preference;
	}
	public SQLTimePreference(SQLTimePreference that) {
		this(that.id, that.instructorID, that.timeID, that.preference);
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
