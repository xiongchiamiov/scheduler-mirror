package edu.calpoly.csc.scheduler.model;

import java.util.Collection;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;

public class Document implements Identified {
	private final Model model;
	
	IDBDocument underlyingDocument;
	
	Document(Model model, IDBDocument underlyingDocument) {
		this.model = model;
		assert(underlyingDocument != null);
		this.underlyingDocument = underlyingDocument;
	}
	

	// PERSISTENCE FUNCTIONS

	public Document insert() {
		model.database.insertDocument(underlyingDocument);
		return this;
	}

	public void update() {
		model.database.updateDocument(underlyingDocument);
	}
	
	public void delete() {
		for (Schedule schedule : getSchedules())
			schedule.delete();
		for (Location location : getLocations())
			location.delete();
		for (Instructor instructor : getInstructors())
			instructor.delete();
		for (Course course : getCourses())
			course.delete();
		
		model.database.deleteDocument(underlyingDocument);
	}

	

	// ENTITY ATTRIBUTES

	public int getID() { return underlyingDocument.getID(); }

	public String getName() { return underlyingDocument.getName(); }
	public void setName(String string) { underlyingDocument.setName(string); }

	public int getStartHalfHour() { return underlyingDocument.getStartHalfHour(); }
	public void setStartHalfHour(int startHalfHour) { underlyingDocument.setStartHalfHour(startHalfHour); }

	public int getEndHalfHour() { return underlyingDocument.getEndHalfHour(); }
	public void setEndHalfHour(int endHalfHour) { underlyingDocument.setEndHalfHour(endHalfHour); }
	
	
	
	
	// ENTITY RELATIONS

	public Collection<Schedule> getSchedules() {
		return model.findSchedulesForDocument(this);
	}

	public Collection<Instructor> getInstructors() {
		return model.findInstructorsForDocument(this);
	}

	public Collection<Course> getCourses() {
		return model.findCoursesForDocument(this);
	}
	
	public Collection<Location> getLocations() {
		return model.findLocationsForDocument(this);
	}
}
