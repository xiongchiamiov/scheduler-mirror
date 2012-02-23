package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBUser;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class User implements Identified {
	private final Model model;
	
	final IDBUser underlyingUser;
	
	User(Model model, IDBUser underlyingUser) {
		this.model = model;
		this.underlyingUser = underlyingUser;
	}

	// PERSISTENCE FUNCTIONS

	public User insert() throws NotFoundException {
		model.userCache.insert(this);
		return this;
	}

	public void update() {
		model.userCache.update(underlyingUser);
	}
	
	public void delete() {
		model.userCache.delete(this);
	}


	// ENTITY ATTRIBUTES

	public String getUsername() { return underlyingUser.getUsername(); }
	public void setUsername(String username) { underlyingUser.setUsername(username); }
	
	public boolean isAdmin() { return underlyingUser.isAdmin(); }
	public void setAdmin(boolean isAdmin) { underlyingUser.setAdmin(isAdmin); }

	public int getID() { return underlyingUser.getID(); }
}
