package edu.calpoly.csc.scheduler.model.db.sqlite;

import edu.calpoly.csc.scheduler.model.db.IDBObject;

public class SQLObject implements IDBObject {
	Integer id;
	
	@Override
	public Integer getID() { return id; }
	
	public SQLObject(int id) {
		this.id = id;
	}

	@Override
	public boolean isTransient() { return id == null; }
}
