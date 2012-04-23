package scheduler.model.db.simple;

import scheduler.model.db.IDBTimePreference;

public class DBTimePreference extends DBObject implements IDBTimePreference {
	private static final long serialVersionUID = 1337L;
	
	Integer instructorID;
	Integer timeID;
	int preference;
	
	public DBTimePreference(Integer id, Integer instructorID, Integer timeID, int preference) {
		super(id);
		this.instructorID = instructorID;
		this.timeID = timeID;
		this.preference = preference;
	}
	public DBTimePreference(DBTimePreference that) {
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
