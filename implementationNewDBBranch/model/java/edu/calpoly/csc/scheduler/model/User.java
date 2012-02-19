package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBUser;

public class User {
	private final IDBUser underlyingUser;
	
	User(IDBUser underlyingUser) {
		this.underlyingUser = underlyingUser;
	}
	
	public String getUsername() { return underlyingUser.getUsername(); }
	public void setUsername(String username) { underlyingUser.setUsername(username); }
	
	public boolean isAdmin() { return underlyingUser.isAdmin(); }
	public void setAdmin(boolean isAdmin) { underlyingUser.setAdmin(isAdmin); }

	public Integer getID() { return underlyingUser.getID(); }
}
