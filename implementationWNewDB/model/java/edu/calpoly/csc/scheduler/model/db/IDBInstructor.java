package edu.calpoly.csc.scheduler.model.db;

public interface IDBInstructor extends IDBObject {
	String getFirstName();
	void setFirstName(String firstName);
	
	String getLastName();
	void setLastName(String lastName);
	
	String getUsername();
	void setUsername(String username);
	
	String getMaxWTU();
	void setMaxWTU(String maxWTU);
}
