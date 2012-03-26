package edu.calpoly.csc.scheduler.model.db.sqlite;

import edu.calpoly.csc.scheduler.model.db.IDBUser;

public class SQLUser extends SQLObject implements IDBUser {
	String username;
	boolean isAdmin;
	
	public SQLUser(Integer id, String username, boolean isAdmin) {
		super(id);
		this.username = username;
		this.isAdmin = isAdmin;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean isAdmin() {
		return isAdmin;
	}

	@Override
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

}
