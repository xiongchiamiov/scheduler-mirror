package scheduler.model.db.sqlite;

import scheduler.model.db.IDBObject;

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
