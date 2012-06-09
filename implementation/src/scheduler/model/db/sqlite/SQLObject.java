package scheduler.model.db.sqlite;

import scheduler.model.db.IDBObject;
/**
 * The Class SQLObject implements all methods of the IDBObject class (part of the IDatabase interface).
 * This class represents a generic SQLObject and it's id that's used in the SQL database.
 * @author kayleneS
 *
 */
public class SQLObject implements IDBObject {
	Integer id;
	
	@Override
	public Integer getID() { return id; }
	
	public SQLObject(Integer id) {
		this.id = id;
	}

	@Override
	public boolean isTransient() { return id == null; }
}
