package scheduler.model.db.sqlite;

import scheduler.model.db.IDBSchedule;

public class SQLSchedule extends SQLObject implements IDBSchedule {
	Integer documentID;
	
	public SQLSchedule(Integer id, Integer documentID) {
		super(id);
		this.documentID = documentID;
	}
	
	public SQLSchedule(SQLSchedule that) {
		this(that.id, that.documentID);
	}

}
