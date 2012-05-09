package scheduler.model;

import java.util.Collection;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBDocument;
import scheduler.model.db.IDBInstructor;
import scheduler.model.db.IDBLocation;
import scheduler.model.db.IDatabase.NotFoundException;

public class Document extends ModelObject {
	private final Model model;

	private boolean tbaLocationLoaded;
	private Location tbaLocation;
	
	private boolean staffInstructorLoaded;
	private Instructor staffInstructor;
	
	private boolean chooseForMeInsLoaded;
	private Instructor chooseForMeInstructor;
	
	private boolean chooseForMeLocLoaded;
	private Location chooseForMeLocation;
	
	private boolean originalLoaded;
	private Document original;
	
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
		preInsertOrUpdateSanityCheck();
		model.documentCache.insert(this);
		return this;
	}

	public void update() throws DatabaseException {
		if (staffInstructorIsSet())
			model.database.setDocumentStaffInstructor(underlyingDocument, staffInstructor.underlyingInstructor);
		if (tbaLocationIsSet())
			model.database.setDocumentTBALocation(underlyingDocument, tbaLocation.underlyingLocation);
		if(chooseForMeInstructorIsSet())
			model.database.setDocumentChooseForMeInstructor(underlyingDocument, chooseForMeInstructor.underlyingInstructor);
		if(chooseForMeLocationIsSet())
			model.database.setDocumentChooseForMeLocation(underlyingDocument, chooseForMeLocation.underlyingLocation);
		disassociateWorkingCopy();
		if (originalLoaded && original != null)
			model.database.associateWorkingCopyWithOriginal(underlyingDocument, original.underlyingDocument);
		preInsertOrUpdateSanityCheck();
		model.documentCache.update(this);
	}
	
	public void delete() throws DatabaseException {
		for (Schedule schedule : getSchedules())
			schedule.delete();
		for (Location location : getLocations())
			location.delete();
		getTBALocation().delete();
		getChooseForMeLocation().delete();
		for (Instructor instructor : getInstructors())
			instructor.delete();
		getStaffInstructor().delete();
		getChooseForMeInstructor().delete();
		for (Course course : getCourses())
			course.delete();

		disassociateWorkingCopy();
		model.documentCache.delete(this);
	}
	
	private void disassociateWorkingCopy() throws DatabaseException {
		IDBDocument oldOriginal = model.database.getOriginalForWorkingCopyDocumentOrNull(underlyingDocument);
		if (oldOriginal != null)
			model.database.disassociateWorkingCopyWithOriginal(underlyingDocument, oldOriginal);
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
	

	// TBA Location

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
		assert(!newLocation.isTransient()); // You need to insert something before you can reference it
		tbaLocation = newLocation;
		tbaLocationLoaded = true;
	}

	public boolean tbaLocationIsSet() { return tbaLocationLoaded; }
	

	// Choose For Me Location
	
	public Location getChooseForMeLocation() throws DatabaseException {
		if(!chooseForMeLocLoaded) {
			IDBLocation underlyingLocation = model.database.getDocumentChooseForMeLocationOrNull(underlyingDocument);
			if(underlyingDocument == null)
				chooseForMeLocation = null;
			else
				chooseForMeLocation = model.findLocationByID(underlyingLocation.getID());
			chooseForMeLocLoaded = true;
		}
		return chooseForMeLocation;
	}
	
	public void setChooseForMeLocation(Location newLocation) {
		assert(!newLocation.isTransient()); // You need to insert something before you can reference it
		chooseForMeLocation = newLocation;
		chooseForMeLocLoaded = true;
	}
	
	public boolean chooseForMeLocationIsSet() {return chooseForMeLocLoaded; }
	
	// Staff Instructor

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
		assert(!newInstructor.isTransient()); // You need to insert something before you can reference it
		staffInstructor = newInstructor;
		staffInstructorLoaded = true;
	}

	public boolean staffInstructorIsSet() { return staffInstructorLoaded; }
	
	// Choose For Me Instructor

		public Instructor getChooseForMeInstructor() throws DatabaseException {
			if (!chooseForMeInsLoaded) {
				IDBInstructor underlyingInstructor = model.database.getDocumentChooseForMeInstructorOrNull(underlyingDocument);
				if (underlyingInstructor == null)
					chooseForMeInstructor = null;
				else
					chooseForMeInstructor = model.findInstructorByID(underlyingInstructor.getID());
				
				chooseForMeInsLoaded = true;
			}
			return chooseForMeInstructor;
		}

		public void setChooseForMeInstructor(Instructor newInstructor) {
			assert(!newInstructor.isTransient()); // You need to insert something before you can reference it
			chooseForMeInstructor = newInstructor;
			chooseForMeInsLoaded = true;
		}

		public boolean chooseForMeInstructorIsSet() { return chooseForMeInsLoaded; }
	
	// Working Copy
	

	public Document getOriginal() throws DatabaseException {
		assert(isWorkingCopy());
		if (!originalLoaded) {
			IDBDocument originalUnderlying = model.database.getOriginalForWorkingCopyDocumentOrNull(underlyingDocument);
			if (originalUnderlying == null)
				original = null;
			else
				original = model.findDocumentByID(originalUnderlying.getID());
			originalLoaded = true;
		}
		return original;
	}

	public void setOriginal(Document newDocument) {
		assert(newDocument == null || !newDocument.isTransient()); // You need to insert something before you can reference it
		original = newDocument;
		originalLoaded = true;
	}
	
	public boolean isWorkingCopy() throws DatabaseException {
		return !model.database.isOriginalDocument(underlyingDocument);
	}
	
	public Document getWorkingCopy() throws DatabaseException {
		assert(!isWorkingCopy());
		IDBDocument workingCopy = model.database.getWorkingCopyForOriginalDocumentOrNull(underlyingDocument);
		if (workingCopy == null)
			return null;
		return model.documentCache.decorateAndPutIfNotPresent(workingCopy);
	}


	@Override
	public void preInsertOrUpdateSanityCheck() {
		assert getName() != null : "name null";
		
		if (tbaLocationLoaded)
			assert tbaLocation != null : "tba loc null";
		
		if(chooseForMeLocLoaded)
			assert chooseForMeLocation != null : "choose for me loc null";
		
		if (staffInstructorLoaded)
			assert staffInstructor != null : "staff null";
		
		if(chooseForMeInsLoaded)
			assert chooseForMeInstructor != null : "choose for me instructor null";
		
		// original can be null
	}
}
