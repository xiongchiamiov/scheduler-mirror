package edu.calpoly.csc.scheduler.model;

import java.util.Collection;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;

public class Document extends Identified {
	private final Model model;

	private boolean tbaLocationLoaded;
	private Location tbaLocation;
	
	private boolean staffInstructorLoaded;
	private Instructor staffInstructor;
	
	IDBDocument underlyingDocument;
	
	Document(Model model, IDBDocument underlyingDocument) {
		this.model = model;
		assert(underlyingDocument != null);
		this.underlyingDocument = underlyingDocument;
		
		if (!underlyingDocument.isTransient())
			assert(!model.documentCache.inCache(underlyingDocument)); // make sure its not in the cache yet (how could it be, we're not even done with the constructor)
	}
	

	// PERSISTENCE FUNCTIONS

	public Document insert() throws DatabaseException {
		model.documentCache.insert(this);
		return this;
	}

	public void update() throws DatabaseException {
		if (staffInstructorIsSet())
			model.database.setDocumentStaffInstructor(underlyingDocument, staffInstructor.underlyingInstructor);
		if (tbaLocationIsSet())
			model.database.setDocumentTBALocation(underlyingDocument, tbaLocation.underlyingLocation);
		model.documentCache.update(this);
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

	public Integer getID() { return underlyingDocument.getID(); }

	public String getName() { return underlyingDocument.getName(); }
	public void setName(String string) { underlyingDocument.setName(string); }

	public int getStartHalfHour() { return underlyingDocument.getStartHalfHour(); }
	public void setStartHalfHour(int startHalfHour) { underlyingDocument.setStartHalfHour(startHalfHour); }

	public int getEndHalfHour() { return underlyingDocument.getEndHalfHour(); }
	public void setEndHalfHour(int endHalfHour) { underlyingDocument.setEndHalfHour(endHalfHour); }
	
	public boolean isTrashed() { return underlyingDocument.isTrashed(); }
	public void setIsTrashed(boolean isTrashed) { underlyingDocument.setIsTrashed(isTrashed); }
	
	
	
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
	

	// Location

	public Location getTBALocation() throws DatabaseException {
		if (!tbaLocationLoaded) {
			IDBLocation underlyingLocation = model.database.getDocumentTBALocationOrNull(underlyingDocument);
			if (underlyingLocation == null)
				tbaLocation = null;
			else
				tbaLocation = model.findLocationByID(underlyingLocation.getID());
			tbaLocationLoaded = true;
		}
		return tbaLocation;
	}

	public void setTBALocation(Location newLocation) {
		tbaLocation = newLocation;
		tbaLocationLoaded = true;
	}

	public boolean tbaLocationIsSet() { return tbaLocationLoaded; }
	

	// Instructor

	public Instructor getStaffInstructor() throws DatabaseException {
		if (!staffInstructorLoaded) {
			IDBInstructor underlyingInstructor = model.database.getDocumentStaffInstructorOrNull(underlyingDocument);
			if (underlyingInstructor == null)
				staffInstructor = null;
			else
				staffInstructor = model.findInstructorByID(underlyingInstructor.getID());
			
			staffInstructorLoaded = true;
		}
		return staffInstructor;
	}

	public void setStaffInstructor(Instructor newInstructor) {
		staffInstructor = newInstructor;
		staffInstructorLoaded = true;
	}

	public boolean staffInstructorIsSet() { return staffInstructorLoaded; }
	
}
