package scheduler.model;

import java.util.Collection;
import java.util.Set;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBScheduleItem;

public class ScheduleItem extends ModelObject {
	private final Model model;
	
	IDBScheduleItem underlying;
	
	private boolean documentLoaded;
	private Document document;

	private boolean courseLoaded;
	private Course course;
	
	private boolean locationLoaded;
	private Location location;
	
	private boolean instructorLoaded;
	private Instructor instructor;

	private boolean lectureLoaded;
	private ScheduleItem lecture;

	
	ScheduleItem(Model model, IDBScheduleItem underlying) {
		this.model = model;
		this.underlying = underlying;

		if (!underlying.isTransient())
			assert(!model.itemCache.inCache(underlying)); // make sure its not in the cache yet (how could it be, we're not even done with the constructor)
	}

	
	// PERSISTENCE FUNCTIONS

	public ScheduleItem insert() throws DatabaseException {
		assert(documentLoaded);
		assert(courseLoaded);
		assert(locationLoaded);
		assert(instructorLoaded);
		
		preInsertOrUpdateSanityCheck();
		model.itemCache.insert(this);
		
		return this;
	}

	public void delete() throws DatabaseException {
		model.itemCache.delete(this);
	}

	public void update() throws DatabaseException {
		model.database.setScheduleItemCourse(underlying, course.underlyingCourse);
		model.database.setScheduleItemInstructor(underlying, instructor.underlyingInstructor);
		model.database.setScheduleItemLocation(underlying, location.underlyingLocation);

		IDBScheduleItem oldLecture = model.database.getScheduleItemLectureOrNull(underlying);
		if ((oldLecture == null) != (lecture == null)) {
			if (lecture == null)
				model.database.disassociateScheduleItemLab(model.database.getScheduleItemLectureOrNull(underlying), underlying);
			else
				model.database.associateScheduleItemLab(lecture.underlying, underlying);
		}
		
		preInsertOrUpdateSanityCheck();
		model.itemCache.update(this);
	}
	
	public ScheduleItem createTransientCopy() throws DatabaseException {
		ScheduleItem result = new ScheduleItem(model, model.database.assembleScheduleItemCopy(underlying));
		if (documentLoaded)
			result.setDocument(getDocument());
		if (instructorLoaded)
			result.setInstructor(getInstructor());
		if (locationLoaded)
			result.setLocation(getLocation());
		if (courseLoaded)
			result.setCourse(getCourse());
		if (lectureLoaded)
			result.setLecture(getLecture());
		return result;
	}
	
	

	// ENTITY ATTRIBUTES
	
	public Integer getID() { return underlying.getID(); }
	public int getSection() { return underlying.getSection(); }
	public void setSection(int section) { this.underlying.setSection(section); }
	public Set<Day> getDays() { return underlying.getDays(); }
	public void setDays(Set<Day> days) { underlying.setDays(days); }
	public int getStartHalfHour() { return underlying.getStartHalfHour(); }
	public void setStartHalfHour(int startHalfHour) { underlying.setStartHalfHour(startHalfHour); }
	public int getEndHalfHour() { return underlying.getEndHalfHour(); }
	public void setEndHalfHour(int endHalfHour) { underlying.setEndHalfHour(endHalfHour); }
	public boolean isPlaced() { return underlying.isPlaced(); }
	public void setIsPlaced(boolean placed) { underlying.setIsPlaced(placed); }
	public boolean isConflicted() { return underlying.isConflicted(); }
	public void setIsConflicted(boolean conflicted) { underlying.setIsConflicted(conflicted); }

	
	
	// ENTITY RELATIONS

	
	// Labs
	
	public Collection<ScheduleItem> getLabs() throws DatabaseException {
		return model.getLabScheduleItemsForScheduleItem(this);
	}
	
	public ScheduleItem getLecture() throws DatabaseException {
		if (!lectureLoaded) {
			assert(lecture == null);
			lecture = model.getScheduleItemLecture(this);
			lectureLoaded = true;
		}
		return lecture;
	}
	
	public void setLecture(ScheduleItem newLecture) {
		assert(newLecture == null || !newLecture.isTransient()); // You need to insert something before you can reference it
		lectureLoaded = true;
		lecture = newLecture;
	}


	// Schedule

	public Document getDocument() throws DatabaseException {
		if (!documentLoaded) {
			assert(document == null);
			document = model.findDocumentByID(model.database.getScheduleItemDocument(underlying).getID());
			documentLoaded = true;
		}
		return document;
	}

	public ScheduleItem setDocument(Document newDocument) {
		assert(!newDocument.isTransient()); // You need to insert something before you can reference it
		document = newDocument;
		documentLoaded = true;
		return this;
	}
	

	// Course

	public Course getCourse() throws DatabaseException {
		if (!courseLoaded) {
			course = model.findCourseByID(model.database.getScheduleItemCourse(underlying).getID());
			courseLoaded = true;
		}
		return course;
	}

	public void setCourse(Course newCourse) {
		assert(!newCourse.isTransient()); // You need to insert something before you can reference it
		course = newCourse;
		courseLoaded = true;
	}
	
	public boolean courseIsSet() { return courseLoaded; }
	

	// Location

	public Location getLocation() throws DatabaseException {
		if (!locationLoaded) {
			location = model.findLocationByID(model.database.getScheduleItemLocation(underlying).getID());
			locationLoaded = true;
		}
		return location;
	}

	public void setLocation(Location newLocation) {
		assert(!newLocation.isTransient()); // You need to insert something before you can reference it
		location = newLocation;
		locationLoaded = true;
	}

	public boolean locationIsSet() { return locationLoaded; }
	

	// Instructor

	public Instructor getInstructor() throws DatabaseException {
		if (!instructorLoaded) {
			instructor = model.findInstructorByID(model.database.getScheduleItemInstructor(underlying).getID());
			instructorLoaded = true;
		}
		return instructor;
	}

	public void setInstructor(Instructor newInstructor) {
		assert(!newInstructor.isTransient()); // You need to insert something before you can reference it
		instructor = newInstructor;
		instructorLoaded = true;
	}

	public boolean instructorIsSet() { return instructorLoaded; }
	
	/**
	    * Displays all this object's fields in a visually easy-to-read form.
	    */
	   public String toString ()
	   {
	      String r = "";
		try {
			r = (this.getCourse() + " - Section: " + this.getSection() + " - " +
			          this.getCourse().getType() + "\n" +
			          "Instructor:\t" + this.getInstructor() + "\n" +
			          "In:\t\t" + this.getLocation() + "\n" +
			          "On:\t\t" + this.getDays() + "\n" +
			          "Starts:\t\t" + this.getStartHalfHour() + "\n" +
			          "Ends:\t\t" + this.getEndHalfHour() + "\n");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		//TODO Add Labs into printing
	      /*if (!this.getLabIDs().isEmpty())
	      {
	         r += this.getLabIDs().toString();
	      }*/
	      return r;
	   }


	@Override
	public void preInsertOrUpdateSanityCheck() {
		assert getDays() != null : "days null";
		
		if (documentLoaded)
			assert document != null : "sched null";
		
		if (courseLoaded)
			assert course != null : "course null";
		
		if (locationLoaded)
			assert location != null : "location null";
		
		if (instructorLoaded)
			assert instructor != null : "instructor null";
		
		// lecture can be null
	}
}
