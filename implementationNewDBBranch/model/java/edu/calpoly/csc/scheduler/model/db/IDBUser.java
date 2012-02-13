package edu.calpoly.csc.scheduler.model.db;


public interface IDBUser extends IDBObject {
	public String getUsername();
	public void setUsername(String username);
	
	public boolean isAdmin();
	public void setAdmin(boolean isAdmin);
}
