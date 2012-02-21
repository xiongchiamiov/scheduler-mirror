package edu.calpoly.csc.scheduler.model;

import java.util.Collection;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;

public class Document {
	IDBDocument underlyingDocument;
	
	public Document(IDBDocument underlyingDocument) {
		assert(underlyingDocument != null);
		this.underlyingDocument = underlyingDocument;
	}
	
	public int getID() { return underlyingDocument.getID(); }

	public String getName() { return underlyingDocument.getName(); }
	public void setName(String string) { underlyingDocument.setName(string); }

	public int getStartHalfHour() { return underlyingDocument.getStartHalfHour(); }
	public void setStartHalfHour(int startHalfHour) { underlyingDocument.setStartHalfHour(startHalfHour); }

	public int getEndHalfHour() { return underlyingDocument.getEndHalfHour(); }
	public void setEndHalfHour(int endHalfHour) { underlyingDocument.setEndHalfHour(endHalfHour); }
}
