package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;

public class DBDocument extends DBObject implements IDBDocument {
	String name;
	boolean isTrashed;
	int originalID;
	
	public DBDocument(Integer id, String name) {
		super(id);
		this.name = name;
	}
	
	public DBDocument(DBDocument that) {
		this(that.id, that.name);
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
