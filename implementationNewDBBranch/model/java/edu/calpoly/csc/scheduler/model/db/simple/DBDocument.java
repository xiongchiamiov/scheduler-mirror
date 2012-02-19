package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;

public class DBDocument extends DBObject implements IDBDocument {
	String name;
	boolean isTrashed;
	Integer originalID; // null if this is an original
	
	public DBDocument(Integer id, String name, Integer originalID) {
		super(id);
		this.name = name;
		this.originalID = originalID;
	}
	
	public DBDocument(DBDocument that) {
		this(that.id, that.name, that.originalID);
	}
	
	@Override
	public String getName() { return name; }
	@Override
	public void setName(String name) { this.name = name; }

	@Override
	public boolean isTrashed() { return isTrashed; }
	@Override
	public void setIsTrashed(boolean isTrashed) { this.isTrashed = isTrashed; }
}
