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
		if (staffInstructorIsSet()) {
			if (staffInstructor == null)
				model.database.setDocumentStaffInstructorOrNull(underlyingDocument, null);
			else
				model.database.setDocumentStaffInstructorOrNull(underlyingDocument, staffInstructor.underlyingInstructor);
		}
		
		if (tbaLocationIsSet()) {
			if (tbaLocation == null)
				model.database.setDocumentTBALocationOrNull(underlyingDocument, null);
			else
				model.database.setDocumentTBALocationOrNull(underlyingDocument, tbaLocation.underlyingLocation);
		}
		
		if(chooseForMeInstructorIsSet()) {
			if (chooseForMeInstructor == null)
				model.database.setDocumentChooseForMeInstructorOrNull(underlyingDocument, null);
			else
				model.database.setDocumentChooseForMeInstructorOrNull(underlyingDocument, chooseForMeInstructor.underlyingInstructor);
		}
		
		if(chooseForMeLocationIsSet()) {
			if (chooseForMeLocation == null)
				model.database.setDocumentChooseForMeLocationOrNull(underlyingDocument, null);
			else
				model.database.setDocumentChooseForMeLocationOrNull(underlyingDocument, chooseForMeLocation.underlyingLocation);
		}
		
		disassociateWorkingCopy();
		
		if (originalLoaded && original != null)
			model.database.associateWorkingCopyWithOriginal(underlyingDocument, original.underlyingDocument);
		
		preInsertOrUpdateSanityCheck();
		
		model.documentCache.update(this);
	}
	
	public void deleteContents(boolean excludeSpecialCaseInstructorsAndLocationsFromDeletion) throws DatabaseException {
		if (!excludeSpecialCaseInstructorsAndLocationsFromDeletion) {
			setStaffInstructor(null);
			setChooseForMeInstructor(null);
			setTBALocation(null);
			setChooseForMeLocation(null);
		}
		
		for (ScheduleItem scheduleItem : getScheduleItems())
			scheduleItem.delete();
		for (Location location : getLocations(excludeSpecialCaseInstructorsAndLocationsFromDeletion))
			location.delete();
		for (Instructor instructor : getInstructors(excludeSpecialCaseInstructorsAndLocationsFromDeletion))
			instructor.delete();
		for (Course course : getCourses())
			course.delete();
		
		update();
	}
	
	public void delete() throws DatabaseException {
		deleteContents(false);

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

	public Collection<ScheduleItem> getScheduleItems() throws DatabaseException{
		return model.findAllScheduleItemsForDocument(this);
	}

	public Collection<Instructor> getInstructors(boolean excludeSpecialCaseInstructors) throws DatabaseException{
		return model.findInstructorsForDocument(this, excludeSpecialCaseInstructors);
	}

	public Collection<Course> getCourses() throws DatabaseException {
		return model.findCoursesForDocument(this);
	}
	
	public Collection<Location> getLocations(boolean excludeSpecialCaseLocations) throws DatabaseException {
		return model.findLocationsForDocument(this, excludeSpecialCaseLocations);
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
		if (newLocation != null) {
			assert(!newLocation.isTransient()); // You need to insert something before you can reference it
		}
		tbaLocation = newLocation;
		tbaLocationLoaded = true;
	}

	public boolean tbaLocationIsSet() { return tbaLocationLoaded; }
	

	// Choose For Me Location
	
	public Location getChooseForMeLocation() throws DatabaseException {
		if(!chooseForMeLocLoaded) {
			IDBLocation underlyingLocation = model.database.getDocumentChooseForMeLocationOrNull(underlyingDocument);
			if(underlyingLocation == null)
				chooseForMeLocation = null;
			else
				chooseForMeLocation = model.findLocationByID(underlyingLocation.getID());
			chooseForMeLocLoaded = true;
		}
		return chooseForMeLocation;
	}
	
	public void setChooseForMeLocation(Location newLocation) {
		if (newLocation != null) {
			assert(!newLocation.isTransient()); // You need to insert something before you can reference it
		}
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
		if (newInstructor != null) {
			assert(!newInstructor.isTransient()); // You need to insert something before you can reference it
		}
		
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
			if (newInstructor != null) {
				assert(!newInstructor.isTransient()); // You need to insert something before you can reference it
			}
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
	
	public Document getWorkingCopyOrNull() throws DatabaseException {
		assert(!isWorkingCopy());
		IDBDocument workingCopy = model.database.getWorkingCopyForOriginalDocumentOrNull(underlyingDocument);
		if (workingCopy == null)
			return null;
		return model.documentCache.decorateAndPutIfNotPresent(workingCopy);
	}


	@Override
	public void preInsertOrUpdateSanityCheck() {
		assert getName() != null : "name null";
		
		// tba can be null
		
		// cfm location can be null
		
		// cfm instructor can be null
		
		// original can be null
	}


	public void invalidateLoaded() {
		tbaLocationLoaded = false;
		tbaLocation = null;
		
		staffInstructorLoaded = false;
		staffInstructor = null;
		
		chooseForMeInsLoaded = false;
		chooseForMeInstructor = null;
		
		chooseForMeLocLoaded = false;
		chooseForMeLocation = null;
		
		originalLoaded = false;
		original = null;		
	}
}
