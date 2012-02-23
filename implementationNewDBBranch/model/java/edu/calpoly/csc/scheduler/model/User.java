package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDBUser;

public class User implements Identified {
	private final Model model;
	
	final IDBUser underlyingUser;
	
	User(Model model, IDBUser underlyingUser) {
		this.model = model;
		this.underlyingUser = underlyingUser;
	}

	// PERSISTENCE FUNCTIONS

	public User insert() throws DatabaseException {
		model.userCache.insert(this);
		return this;
	}

	public void update() throws DatabaseException {
		model.userCache.update(underlyingUser);
	}
	
	public void delete() throws DatabaseException {
		model.userCache.delete(this);
	}


	// ENTITY ATTRIBUTES

	public String getUsername() { return underlyingUser.getUsername(); }
	public void setUsername(String username) { underlyingUser.setUsername(username); }
	
	public boolean isAdmin() { return underlyingUser.isAdmin(); }
	public void setAdmin(boolean isAdmin) { underlyingUser.setAdmin(isAdmin); }

	public int getID() { return underlyingUser.getID(); }
}
