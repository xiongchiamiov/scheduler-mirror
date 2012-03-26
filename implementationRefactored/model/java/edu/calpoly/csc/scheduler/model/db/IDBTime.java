package edu.calpoly.csc.scheduler.model.db;


public interface IDBTime extends IDBObject {
	public int getDay();
	public void setDay(int day);
	
	public int getHalfHour();
	public void setHalfHour(int halfHour);
}
