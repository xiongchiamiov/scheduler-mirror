package edu.calpoly.csc.scheduler.model.db.sqlite;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.simple.DBDocument;

public class SQLDocument extends SQLObject implements IDBDocument {
	String name;
	boolean isTrashed;
	Integer originalID; 
	int startHalfHour;
	int endHalfHour;
	
	public SQLDocument(Integer id, String name, Integer originalID, int startHalfHour, int endHalfHour) {
		super(id);
		this.name = name;
		this.originalID = originalID;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
	}
	
	@Override
	public String getName() { return name; }
	@Override
	public void setName(String name) { this.name = name; }

	@Override
	public boolean isTrashed() { return isTrashed; }
	@Override
	public void setIsTrashed(boolean isTrashed) { this.isTrashed = isTrashed; }

	@Override
	public int getStartHalfHour() { return startHalfHour; }
	@Override
	public void setStartHalfHour(int halfHour) { startHalfHour = halfHour; }

	@Override
	public int getEndHalfHour() { return endHalfHour; }
	@Override
	public void setEndHalfHour(int halfHour) { endHalfHour = halfHour; }
	
	public Integer getOriginalId() { return originalID; }
}
