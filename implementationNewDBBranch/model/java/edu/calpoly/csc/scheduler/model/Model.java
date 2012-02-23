package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;
import edu.calpoly.csc.scheduler.model.db.IDBCourseAssociation;
import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;
import edu.calpoly.csc.scheduler.model.db.IDBDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDBEquipmentType;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;
import edu.calpoly.csc.scheduler.model.db.IDBObject;
import edu.calpoly.csc.scheduler.model.db.IDBOfferedDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDBTime;
import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;
import edu.calpoly.csc.scheduler.model.db.IDBUser;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Model {
	abstract static class Cache<DecoratedT extends Identified, UnderlyingT extends IDBObject> {
		HashMap<Integer, DecoratedT> cache = new HashMap<Integer, DecoratedT>();

		public DecoratedT decorateAndPutInCache(UnderlyingT underlying) {
			DecoratedT result = decorate(underlying);
			cache.put(underlying.getID(), result);
			return result;
		}
		
		public DecoratedT putIfNotPresentThenGetDecorated(UnderlyingT underlying) {
			DecoratedT result = cache.get(underlying.getID());
			if (result == null)
				result = decorateAndPutInCache(underlying);
			return result;
		}

		public DecoratedT findByID(int id) throws NotFoundException {
			DecoratedT result = cache.get(id);
			if (result == null)
				result = decorateAndPutInCache(loadFromDatabase(id));
			return result;
		}
		
		abstract DecoratedT decorate(UnderlyingT underlying);
		
		abstract UnderlyingT loadFromDatabase(int id) throws NotFoundException;
		
		void insert(DecoratedT obj) throws NotFoundException {
			insertIntoDatabase(obj);
			cache.put(obj.getID(), obj);
		}
		
		abstract void insertIntoDatabase(DecoratedT obj) throws NotFoundException;

		public void delete(DecoratedT obj) {
			cache.remove(obj.getID());
			removeFromDatabase(obj);
		}
		
		abstract void removeFromDatabase(DecoratedT obj);

		public boolean isInserted(DecoratedT obj) {
			try {
				findByID(obj.getID());
				return true;
			}
			catch (NotFoundException e) { return false; }
		}
	}
	
	
	final IDatabase database;
	
	public Model() {
		this.database = new edu.calpoly.csc.scheduler.model.db.simple.Database();
	}
	
	public Model(IDatabase database) {
		this.database = database;
	}

	
	
	
	// USERS
	
	Cache<User, IDBUser> userCache = new Cache<User, IDBUser>() {
		User decorate(IDBUser underlying) {
			return new User(database, underlying);
		}
		IDBUser loadFromDatabase(int id) throws NotFoundException {
			throw new UnsupportedOperationException();
		}
		void insertIntoDatabase(User obj) throws NotFoundException {
			database.insertUser(obj.underlyingUser);
		}
		void removeFromDatabase(User obj) {
			database.deleteUser(obj.underlyingUser);
		}
	};

	public User findUserByUsername(String username) throws NotFoundException {
		IDBUser underlyingUser = database.findUserByUsername(username);
		return userCache.putIfNotPresentThenGetDecorated(underlyingUser);
	}

	public User createTransientUser(String username, boolean b) {
		return new User(database, database.assembleUser(username, b));
	}
	
	
	
	// DOCUMENTS

	Cache<Document, IDBDocument> documentCache = new Cache<Document, IDBDocument>() {
		Document decorate(IDBDocument underlying) {
			return new Document(Model.this, underlying);
		}
		IDBDocument loadFromDatabase(int id) throws NotFoundException {
			return database.findDocumentByID(id);
		}
		void insertIntoDatabase(Document obj) throws NotFoundException {
			database.insertDocument(obj.underlyingDocument);
		}
		void removeFromDatabase(Document obj) {
			database.deleteDocument(obj.underlyingDocument);
		}
	};

	public Document createTransientDocument(String name, int startHalfHour, int endHalfHour) {
		return new Document(this, database.assembleDocument(name, startHalfHour, endHalfHour));
	}
	
	public Document findDocumentByID(int documentID) throws NotFoundException {
		return documentCache.findByID(documentID);
	}

	public Collection<Document> findAllDocuments() {
		Collection<Document> result = new LinkedList<Document>();
		for (IDBDocument underlying : database.findAllDocuments())
			result.add(documentCache.putIfNotPresentThenGetDecorated(underlying));
		return result;
	}

	public void disassociateWorkingCopyFromOriginal(Document workingCopyDocument, Document original) {
		database.disassociateWorkingCopyWithOriginal(workingCopyDocument.underlyingDocument, original.underlyingDocument);
	}

	public Document getWorkingCopyForOriginalDocumentOrNull(Document originalDocument) {
		IDBDocument underlying = database.getWorkingCopyForOriginalDocumentOrNull(originalDocument.underlyingDocument);
		if (underlying == null)
			return null;
		return new Document(this, underlying);
	}
	
	public Document copyDocument(Document existingDocument, String newName) {
		IDBDocument underlying = database.assembleDocument(newName, existingDocument.getStartHalfHour(), existingDocument.getEndHalfHour());
		database.insertDocument(underlying);
		Document newDocument = new Document(this, underlying);
		
		

		// Locations
		Map<Integer, IDBLocation> newDocumentLocationsByExistingDocumentLocationIDs = new HashMap<Integer, IDBLocation>();
		for (IDBLocation existingDocumentLocation : database.findLocationsForDocument(existingDocument.underlyingDocument)) {
			IDBLocation newDocumentLocation = database.assembleLocation(existingDocumentLocation.getRoom(), existingDocumentLocation.getType(), existingDocumentLocation.getMaxOccupancy(), existingDocumentLocation.isSchedulable());
			database.insertLocation(newDocument.underlyingDocument, newDocumentLocation);
			newDocumentLocationsByExistingDocumentLocationIDs.put(existingDocumentLocation.getID(), newDocumentLocation);

			for (IDBEquipmentType providedEquipment : database.findProvidedEquipmentByEquipmentForLocation(existingDocumentLocation).keySet()) {
				database.insertProvidedEquipment(newDocumentLocation, providedEquipment, database.assembleProvidedEquipment());
			}
		}

		// Courses
		Map<Integer, IDBCourse> newDocumentCoursesByExistingDocumentCourseIDs = new HashMap<Integer, IDBCourse>();
		for (IDBCourse existingDocumentCourse : database.findCoursesForDocument(existingDocument.underlyingDocument)) {
			IDBCourse newDocumentCourse = database.assembleCourse(existingDocumentCourse.getName(), existingDocumentCourse.getCalatogNumber(), existingDocumentCourse.getDepartment(), existingDocumentCourse.getWTU(), existingDocumentCourse.getSCU(), existingDocumentCourse.getNumSections(), existingDocumentCourse.getType(), existingDocumentCourse.getMaxEnrollment(), existingDocumentCourse.getNumHalfHoursPerWeek(), existingDocumentCourse.isSchedulable());
			database.insertCourse(newDocument.underlyingDocument, newDocumentCourse);
			newDocumentCoursesByExistingDocumentCourseIDs.put(existingDocumentCourse.getID(), newDocumentCourse);
			
			for (IDBOfferedDayPattern existingOfferedDayPattern : database.findOfferedDayPatternsForCourse(existingDocumentCourse)) {
				IDBDayPattern dayPattern = database.getDayPatternForOfferedDayPattern(existingOfferedDayPattern);
				database.insertOfferedDayPattern(newDocumentCourse, dayPattern, database.assembleOfferedDayPattern());
			}

			for (IDBEquipmentType usedEquipment : database.findUsedEquipmentByEquipmentForCourse(existingDocumentCourse).keySet()) {
				database.insertUsedEquipment(newDocumentCourse, usedEquipment, database.assembleUsedEquipment());
			}
		}
		
		// Course Associations
		for (IDBCourse existingDocumentCourse : database.findCoursesForDocument(existingDocument.underlyingDocument)) {
			IDBCourseAssociation assoc = database.getAssociationForLabOrNull(existingDocumentCourse);
			if (assoc == null)
				continue;
			IDBCourse existingDocumentLecture = database.getAssociationLecture(assoc);
			
			IDBCourse newDocumentCourse = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentCourse.getID());
			IDBCourse newDocumentLecture = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentLecture.getID());
			database.associateLectureAndLab(newDocumentLecture, newDocumentCourse);
		}

		// Instructors
		Map<Integer, IDBInstructor> newDocumentInstructorsByExistingDocumentInstructorIDs = new HashMap<Integer, IDBInstructor>();
		for (IDBInstructor existingDocumentInstructor : database.findInstructorsForDocument(existingDocument.underlyingDocument)) {
			IDBInstructor newDocumentInstructor = database.assembleInstructor(existingDocumentInstructor.getFirstName(), existingDocumentInstructor.getLastName(), existingDocumentInstructor.getUsername(), existingDocumentInstructor.getMaxWTU(), existingDocumentInstructor.isSchedulable());
			database.insertInstructor(newDocument.underlyingDocument, newDocumentInstructor);
			newDocumentInstructorsByExistingDocumentInstructorIDs.put(existingDocumentInstructor.getID(), newDocumentInstructor);
			
			for (Entry<IDBCourse, IDBCoursePreference> existingDocumentEntry : database.findCoursePreferencesByCourseForInstructor(existingDocumentInstructor).entrySet()) {
				IDBCourse existingDocumentCoursePreferenceCourse = existingDocumentEntry.getKey();
				IDBCoursePreference existingDocumentCoursePreference = existingDocumentEntry.getValue();
				IDBCourse newDocumentCourse = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentCoursePreferenceCourse.getID());
				IDBCoursePreference newDocumentCoursePreference = database.assembleCoursePreference(existingDocumentCoursePreference.getPreference());
				database.insertCoursePreference(newDocumentInstructor, newDocumentCourse, newDocumentCoursePreference);
			}
			
			for (Entry<IDBTime, IDBTimePreference> existingDocumentEntry : database.findTimePreferencesByTimeForInstructor(existingDocumentInstructor).entrySet()) {
				IDBTime time = existingDocumentEntry.getKey();
				IDBTimePreference existingDocumentTimePreference = existingDocumentEntry.getValue();
				IDBTimePreference newDocumentTimePreference = database.assembleTimePreference(existingDocumentTimePreference.getPreference());
				database.insertTimePreference(newDocumentInstructor, time, newDocumentTimePreference);
			}
		}
		
		// Schedules
		Map<Integer, IDBSchedule> newDocumentScheduleIDsByExistingDocumentScheduleIDs = new HashMap<Integer, IDBSchedule>();
		for (IDBSchedule existingDocumentSchedule : database.findAllSchedulesForDocument(existingDocument.underlyingDocument)) {
			IDBSchedule newDocumentSchedule = database.assembleSchedule();
			database.insertSchedule(newDocument.underlyingDocument, newDocumentSchedule);
			
			newDocumentScheduleIDsByExistingDocumentScheduleIDs.put(existingDocumentSchedule.getID(), newDocumentSchedule);
			
			// Schedule Items
			for (IDBScheduleItem existingDocumentScheduleItem : database.findAllScheduleItemsForSchedule(existingDocumentSchedule)) {
				IDBCourse existingDocumentCourse = database.getScheduleItemCourse(existingDocumentScheduleItem);
				IDBLocation existingDocumentLocation = database.getScheduleItemLocation(existingDocumentScheduleItem);
				IDBInstructor existingDocumentInstructor = database.getScheduleItemInstructor(existingDocumentScheduleItem);

				IDBCourse newDocumentCourse = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentCourse.getID());
				IDBLocation newDocumentLocation = newDocumentLocationsByExistingDocumentLocationIDs.get(existingDocumentLocation.getID());
				IDBInstructor newDocumentInstructor = newDocumentInstructorsByExistingDocumentInstructorIDs.get(existingDocumentInstructor.getID());
				
				database.insertScheduleItem(
						newDocumentSchedule,
						newDocumentCourse,
						newDocumentInstructor,
						newDocumentLocation,
						database.assembleScheduleItem(
								existingDocumentScheduleItem.getSection(),
								existingDocumentScheduleItem.getDays(),
								existingDocumentScheduleItem.getStartHalfHour(),
								existingDocumentScheduleItem.getEndHalfHour(),
								existingDocumentScheduleItem.isPlaced(),
								existingDocumentScheduleItem.isConflicted()));
			}
		}
		
		return newDocument;
	}

	public void associateWorkingCopyWithOriginal(Document workingCopyDocument, Document newOriginal) {
		database.associateWorkingCopyWithOriginal(workingCopyDocument.underlyingDocument, newOriginal.underlyingDocument);
	}

	public Document getOriginalForWorkingCopyDocument(Document workingCopyDocument) throws NotFoundException {
		IDBDocument underlying = database.getOriginalForWorkingCopyDocument(workingCopyDocument.underlyingDocument);
		return new Document(this, underlying);
	}

	public boolean isOriginalDocument(Document doc) {
		return database.isOriginalDocument(doc.underlyingDocument);
	}

	
	
	
	// SCHEDULES

	Cache<Schedule, IDBSchedule> scheduleCache = new Cache<Schedule, IDBSchedule>() {
		Schedule decorate(IDBSchedule underlying) {
			return new Schedule(Model.this, underlying);
		}
		IDBSchedule loadFromDatabase(int id) throws NotFoundException {
			return database.findScheduleByID(id);
		}
		void insertIntoDatabase(Schedule obj) throws NotFoundException {
			database.insertSchedule(obj.getDocument().underlyingDocument, obj.underlyingSchedule);
		}
		void removeFromDatabase(Schedule obj) {
			database.deleteSchedule(obj.underlyingSchedule);
		}
	};
	
	public Collection<Schedule> findSchedulesForDocument(Document doc) {
		Collection<Schedule> result = new LinkedList<Schedule>();
		for (IDBSchedule underlying : database.findAllSchedulesForDocument(doc.underlyingDocument))
			result.add(scheduleCache.putIfNotPresentThenGetDecorated(underlying));
		return result;
	}

	public Schedule findScheduleByID(int scheduleID) throws NotFoundException {
		return scheduleCache.findByID(scheduleID);
	}
	
	public Schedule createTransientSchedule() {
		return new Schedule(this, database.assembleSchedule());
	}
	
	
	
	
	// INSTRUCTORS

	Cache<Instructor, IDBInstructor> instructorCache = new Cache<Instructor, IDBInstructor>() {
		Instructor decorate(IDBInstructor underlying) {
			return new Instructor(Model.this, underlying);
		}
		IDBInstructor loadFromDatabase(int id) throws NotFoundException {
			return database.findInstructorByID(id);
		}
		void insertIntoDatabase(Instructor obj) throws NotFoundException {
			database.insertInstructor(obj.getDocument().underlyingDocument, obj.underlyingInstructor);
		}
		void removeFromDatabase(Instructor obj) {
			database.deleteInstructor(obj.underlyingInstructor);
		}
	};
	
	public Collection<Instructor> findInstructorsForDocument(Document doc) {
		Collection<Instructor> result = new LinkedList<Instructor>();
		for (IDBInstructor underlying : database.findInstructorsForDocument(doc.underlyingDocument))
			result.add(instructorCache.putIfNotPresentThenGetDecorated(underlying));
		return result;
	}

	public Instructor findInstructorByID(int instructorID) throws NotFoundException {
		return instructorCache.findByID(instructorID);
	}
	
	public Instructor createTransientInstructor(String firstName, String lastName, String username, String maxWTU, boolean isSchedulable) {
		IDBInstructor underlyingInstructor = database.assembleInstructor(firstName, lastName, username, maxWTU, isSchedulable);
		return new Instructor(this, underlyingInstructor);
	}


	

	// COURSES

	Cache<Course, IDBCourse> courseCache = new Cache<Course, IDBCourse>() {
		Course decorate(IDBCourse underlying) {
			return new Course(Model.this, underlying);
		}
		IDBCourse loadFromDatabase(int id) throws NotFoundException {
			return database.findCourseByID(id);
		}
		void insertIntoDatabase(Course obj) throws NotFoundException {
			database.insertCourse(obj.getDocument().underlyingDocument, obj.underlyingCourse);
		}
		void removeFromDatabase(Course obj) {
			database.deleteCourse(obj.underlyingCourse);
		}
	};
	
	public Course createTransientCourse(String name, String catalogNumber, String department, String wtu, String scu, String numSections, String type, String maxEnrollment, String numHalfHoursPerWeek, boolean isSchedulable) {
		return new Course(this, database.assembleCourse(name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek, isSchedulable));
	}
	
	public Collection<Course> findCoursesForDocument(Document doc) {
		Collection<Course> result = new LinkedList<Course>();
		for (IDBCourse underlying : database.findCoursesForDocument(doc.underlyingDocument))
			result.add(courseCache.putIfNotPresentThenGetDecorated(underlying));
		return result;
	}

	public Course findCourseByID(int courseID) throws NotFoundException {
		return courseCache.findByID(courseID);
	}
	
	
	

	// LOCATIONS
	
	Cache<Location, IDBLocation> locationCache = new Cache<Location, IDBLocation>() {
		Location decorate(IDBLocation underlying) {
			return new Location(Model.this, underlying);
		}
		IDBLocation loadFromDatabase(int id) throws NotFoundException {
			return database.findLocationByID(id);
		}
		void insertIntoDatabase(Location obj) throws NotFoundException {
			database.insertLocation(obj.getDocument().underlyingDocument, obj.underlyingLocation);
		}
		void removeFromDatabase(Location obj) {
			database.deleteLocation(obj.underlyingLocation);
		}
	};
	
	
	
	public Location createTransientLocation(String room, String type, String maxOccupancy, boolean isSchedulable) {
		return new Location(this, database.assembleLocation(room, type, maxOccupancy, isSchedulable));
	}

	public Collection<Location> findLocationsForDocument(Document doc) {
		Collection<Location> result = new LinkedList<Location>();
		for (IDBLocation underlying : database.findLocationsForDocument(doc.underlyingDocument))
			result.add(locationCache.putIfNotPresentThenGetDecorated(underlying));
		return result;
	}

	public Location findLocationByID(int locationID) throws NotFoundException {
		return locationCache.findByID(locationID);
	}
	
	

	// SCHEDULE ITEMS

	Cache<ScheduleItem, IDBScheduleItem> itemCache = new Cache<ScheduleItem, IDBScheduleItem>() {
		ScheduleItem decorate(IDBScheduleItem underlying) {
			return new ScheduleItem(Model.this, underlying);
		}
		IDBScheduleItem loadFromDatabase(int id) throws NotFoundException {
			return database.findScheduleItemByID(id);
		}
		void insertIntoDatabase(ScheduleItem obj) throws NotFoundException {
			database.insertScheduleItem(obj.getSchedule().underlyingSchedule, obj.getCourse().underlyingCourse, obj.getInstructor().underlyingInstructor, obj.getLocation().underlyingLocation, obj.underlying);
		}
		void removeFromDatabase(ScheduleItem obj) {
			database.deleteScheduleItem(obj.underlying);
		}
	};
	
	public Collection<ScheduleItem> findAllScheduleItemsForSchedule(Schedule schedule) {
		Collection<ScheduleItem> result = new LinkedList<ScheduleItem>();
		for (IDBScheduleItem underlying : database.findAllScheduleItemsForSchedule(schedule.underlyingSchedule))
			result.add(itemCache.putIfNotPresentThenGetDecorated(underlying));
		return result;
	}

	public ScheduleItem createTransientScheduleItem(int section,
			Set<Day> days, int startHalfHour, int endHalfHour,
			boolean isPlaced, boolean isConflicted) {
		IDBScheduleItem underlying = database.assembleScheduleItem(section, days, startHalfHour, endHalfHour, isPlaced, isConflicted);
		return new ScheduleItem(this, underlying);
	}

	public ScheduleItem findScheduleItemByID(int id) throws NotFoundException {
		return itemCache.findByID(id);
	}

	public boolean isInserted(ScheduleItem scheduleItem) {
		return itemCache.isInserted(scheduleItem);
	}

	

	
	

	public boolean isEmpty() {
		return database.isEmpty();
	}
}
