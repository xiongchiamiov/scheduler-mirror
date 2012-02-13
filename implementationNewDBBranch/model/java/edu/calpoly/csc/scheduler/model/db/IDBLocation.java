package edu.calpoly.csc.scheduler.model.db;

public interface IDBLocation extends IDBObject {
	void setRoom(String room);
	String getRoom();
	
	String getType();
	void setType(String type);
	
	String getMaxOccupancy();
	void setMaxOccupancy(String maxOccupancy);
}
