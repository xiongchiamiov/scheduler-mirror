package scheduler.model.db.simple;

import scheduler.model.db.IDBSchedule;

public class DBSchedule extends DBObject implements IDBSchedule {
	private static final long serialVersionUID = 1337L;
	
	Integer documentID;
	
	public DBSchedule(Integer id, Integer documentID) {
		super(id);
		this.documentID = documentID;
	}
	
	public DBSchedule(DBSchedule that) {
		this(that.id, that.documentID);
	}

	public void sanityCheck() {
		assert(documentID != null);
	}
}
