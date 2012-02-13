package edu.calpoly.csc.scheduler.model;

import java.util.Collection;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;

public class Document {
	IDBDocument underlyingDocument;
	
	Collection<Schedule> schedules;
	Collection<Course> courses;
	Collection<Instructor> instructors;
	Collection<Location> locations;
	
	public Document(IDBDocument underlyingDocument) {
		assert(underlyingDocument != null);
		this.underlyingDocument = underlyingDocument;
	}
	
	public int getID() { return underlyingDocument.getID(); }

	public String getName() { return underlyingDocument.getName(); }

	public void setName(String string) { underlyingDocument.setName(string); }
}
