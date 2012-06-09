package scheduler.model.db.sqlite;

import scheduler.model.db.IDBSchedule;
/**
 * The Class SQLSchedule implements all methods of the IDBSchedule class (part of the IDatabase interface).
 * This class represents a schedule in the SQLite database.
 * @author kayleneS
 *
 */
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
