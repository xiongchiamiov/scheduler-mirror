package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBUser;
import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class User implements Identified {
	private final IDatabase database;
	
	final IDBUser underlyingUser;
	
	User(IDatabase database, IDBUser underlyingUser) {
		this.database = database;
		this.underlyingUser = underlyingUser;
	}

	// PERSISTENCE FUNCTIONS

	public User insert() {
		database.insertUser(underlyingUser);
		return this;
	}

	public void update() {
		database.updateUser(underlyingUser);
	}
	
	public void delete() {
		database.deleteUser(underlyingUser);
	}


	// ENTITY ATTRIBUTES

	public String getUsername() { return underlyingUser.getUsername(); }
	public void setUsername(String username) { underlyingUser.setUsername(username); }
	
	public boolean isAdmin() { return underlyingUser.isAdmin(); }
	public void setAdmin(boolean isAdmin) { underlyingUser.setAdmin(isAdmin); }

	public int getID() { return underlyingUser.getID(); }
}
