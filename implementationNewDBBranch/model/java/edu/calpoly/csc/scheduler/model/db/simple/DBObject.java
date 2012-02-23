package edu.calpoly.csc.scheduler.model.db.simple;

import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.IDBObject;

abstract class DBObject implements IDBObject, Serializable {
	Integer id;
	
	@Override
	public int getID() { return id; }
	
	public DBObject(Integer id) {
		this.id = id;
	}
}
