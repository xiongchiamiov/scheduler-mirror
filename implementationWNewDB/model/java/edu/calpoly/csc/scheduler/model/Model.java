package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Model {
	final IDatabase database;
	
	public Model(IDatabase database) {
		this.database = database;
	}

	public String generateUnusedUsername() { return database.generateUnusedUsername(); }

	public User insertUser(String username, boolean b) {
		return new User(database.insertUser(username, b));
	}

	public Document insertDocument(String name) {
		return new Document(database.insertDocument(name));
	}

	public Document findDocumentByID(int documentID) throws NotFoundException {
		return new Document(database.findDocumentByID(documentID));
	}
}
