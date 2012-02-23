package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class Document {
	private final IDatabase database;
	
	IDBDocument underlyingDocument;
	
	Document(IDatabase database, IDBDocument underlyingDocument) {
		this.database = database;
		assert(underlyingDocument != null);
		this.underlyingDocument = underlyingDocument;
	}
	

	// PERSISTENCE FUNCTIONS

	public Document insert() {
		database.insertDocument(underlyingDocument);
		return this;
	}

	public void update() {
		database.updateDocument(underlyingDocument);
	}
	
	public void delete() {
		database.deleteDocument(underlyingDocument);
	}

	

	// ENTITY ATTRIBUTES

	public int getID() { return underlyingDocument.getID(); }

	public String getName() { return underlyingDocument.getName(); }
	public void setName(String string) { underlyingDocument.setName(string); }

	public int getStartHalfHour() { return underlyingDocument.getStartHalfHour(); }
	public void setStartHalfHour(int startHalfHour) { underlyingDocument.setStartHalfHour(startHalfHour); }

	public int getEndHalfHour() { return underlyingDocument.getEndHalfHour(); }
	public void setEndHalfHour(int endHalfHour) { underlyingDocument.setEndHalfHour(endHalfHour); }
	
	
}
