package edu.calpoly.csc.scheduler.model;

import java.util.Collection;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
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

	public Document insert() throws DatabaseException {
		model.documentCache.insert(this);
		return this;
	}

	public void update() throws DatabaseException {
		model.documentCache.update(underlyingDocument);
	}
	
	public void delete() throws DatabaseException {
		for (Schedule schedule : getSchedules())
			schedule.delete();
		for (Location location : getLocations())
			location.delete();
		for (Instructor instructor : getInstructors())
			instructor.delete();
		for (Course course : getCourses())
			course.delete();
		
		model.documentCache.delete(this);
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

	public Collection<Schedule> getSchedules() throws DatabaseException{
		return model.findSchedulesForDocument(this);
	}

	public Collection<Instructor> getInstructors() throws DatabaseException{
		return model.findInstructorsForDocument(this);
	}

	public Collection<Course> getCourses() throws DatabaseException {
		return model.findCoursesForDocument(this);
	}
	
	public Collection<Location> getLocations() throws DatabaseException {
		return model.findLocationsForDocument(this);
	}
}
