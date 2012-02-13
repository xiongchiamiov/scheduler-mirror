package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBUser;

public class User {
	private final IDBUser underlyingUser;
	
	User(IDBUser underlyingUser) {
		this.underlyingUser = underlyingUser;
	}
}
