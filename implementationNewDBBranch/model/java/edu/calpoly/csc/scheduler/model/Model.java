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
import edu.calpoly.csc.scheduler.model.db.IDBOfferedDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDBTime;
import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Model {
	final IDatabase database;
	
	public Model() {
		this.database = new edu.calpoly.csc.scheduler.model.db.simple.Database();
	}
	
	public Model(IDatabase database) {
		this.database = database;
	}

	// USERS

	public User findUserByUsername(String username) throws NotFoundException {
		return new User(database, database.findUserByUsername(username));
	}

	public User createTransientUser(String username, boolean b) {
		return new User(database, database.assembleUser(username, b));
	}
	
	
	// DOCUMENTS
	
	public Document createTransientDocument(String name, int startHalfHour, int endHalfHour) {
		return new Document(database, database.assembleDocument(name, startHalfHour, endHalfHour));
	}
	
	public Document findDocumentByID(int documentID) throws NotFoundException {
		try {
			IDBDocument doc = database.findDocumentByID(documentID);
			return new Document(database, doc);
		}
		catch (NotFoundException e) {
			System.out.println("Couldnt find document ID " + documentID);
			throw e;
		}
	}

	public Collection<Document> findAllDocuments() {
		Collection<Document> result = new LinkedList<Document>();
		for (IDBDocument underlying : database.findAllDocuments()) {
			result.add(new Document(database, underlying));
		}
		return result;
	}

	public void disassociateWorkingCopyFromOriginal(Document workingCopyDocument, Document original) {
		database.disassociateWorkingCopyWithOriginal(workingCopyDocument.underlyingDocument, original.underlyingDocument);
	}

	public Document getWorkingCopyForOriginalDocumentOrNull(Document originalDocument) {
		IDBDocument underlying = database.getWorkingCopyForOriginalDocumentOrNull(originalDocument.underlyingDocument);
		if (underlying == null)
			return null;
		return new Document(database, underlying);
	}
	
	public Document copyDocument(Document existingDocument, String newName) {
		IDBDocument underlying = database.assembleDocument(newName, existingDocument.getStartHalfHour(), existingDocument.getEndHalfHour());
		database.insertDocument(underlying);
		Document newDocument = new Document(database, underlying);
		
		

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
		return new Document(database, underlying);
	}

	public boolean isOriginalDocument(Document doc) {
		return database.isOriginalDocument(doc.underlyingDocument);
	}

	
	// SCHEDULES
	
	public Schedule createTransientSchedule() {
		return new Schedule(this, database.assembleSchedule());
	}
		
	public Collection<Schedule> findAllSchedulesForDocument(Document containingDocument) {
		Collection<Schedule> result = new LinkedList<Schedule>();
		for (IDBSchedule underlyingSchedule : database.findAllSchedulesForDocument(containingDocument.underlyingDocument))
			result.add(new Schedule(this, underlyingSchedule));
		return result;
	}

	public Schedule findScheduleByID(int scheduleID) throws NotFoundException {
		IDBSchedule underlying = database.findScheduleByID(scheduleID);
		return new Schedule(this, underlying);
	}

	
	// INSTRUCTORS

	public Instructor createTransientInstructor(String firstName, String lastName, String username, String maxWTU, boolean isSchedulable) {
		IDBInstructor underlyingInstructor = database.assembleInstructor(firstName, lastName, username, maxWTU, isSchedulable);
		return new Instructor(this, underlyingInstructor);
	}

	public Collection<Instructor> findInstructorsForDocument(Document doc) {
		Collection<Instructor> result = new LinkedList<Instructor>();
		for (IDBInstructor underlying : database.findInstructorsForDocument(doc.underlyingDocument))
			result.add(new Instructor(this, underlying));
		return result;
	}

	public Instructor findInstructorByID(int instructorID) throws NotFoundException {
		IDBInstructor underlying = database.findInstructorByID(instructorID);
		return new Instructor(this, underlying);
	}

	

	// COURSES
	
	public Course createTransientCourse(String name, String catalogNumber, String department, String wtu, String scu, String numSections, String type, String maxEnrollment, String numHalfHoursPerWeek, boolean isSchedulable) {
		return new Course(this, database.assembleCourse(name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek, isSchedulable));
	}
	
	public Collection<Course> findCoursesForDocument(Document doc) {
		Collection<Course> result = new LinkedList<Course>();
		for (IDBCourse underlying : database.findCoursesForDocument(doc.underlyingDocument)) {
			result.add(new Course(this, underlying));
		}
		return result;
	}

	public Course findCourseByID(int courseID) throws NotFoundException {
		IDBCourse underlying = database.findCourseByID(courseID);

		return new Course(this, underlying);
	}
	

	// LOCATIONS
	
	public Location createTransientLocation(String room, String type, String maxOccupancy, boolean isSchedulable) {
		return new Location(this, database.assembleLocation(room, type, maxOccupancy, isSchedulable));
	}

	public Collection<Location> findLocationsForDocument(Document doc) {
		Collection<Location> result = new LinkedList<Location>();
		for (IDBLocation underlying : database.findLocationsForDocument(doc.underlyingDocument)) {
			System.out.println("find result underlying room: " + underlying.getRoom() + " id " + underlying.getID());
			result.add(new Location(this, underlying));
		}
		return result;
	}

	public Location findLocationByID(int locationID) throws NotFoundException {
		IDBLocation underlying = database.findLocationByID(locationID);
		return new Location(this, underlying);
	}
	
	

	// SCHEDULE ITEMS

	public Collection<ScheduleItem> findAllScheduleItemsForSchedule(Schedule schedule) {
		Collection<ScheduleItem> result = new LinkedList<ScheduleItem>();
		for (IDBScheduleItem underlying : database.findAllScheduleItemsForSchedule(schedule.underlyingSchedule))
			result.add(new ScheduleItem(this, underlying));
		return result;
	}

	public ScheduleItem assembleScheduleItem(int section,
			Set<Day> days, int startHalfHour, int endHalfHour,
			boolean isPlaced, boolean isConflicted) {
		IDBScheduleItem underlying = database.assembleScheduleItem(section, days, startHalfHour, endHalfHour, isPlaced, isConflicted);
		return new ScheduleItem(this, underlying);
	}

	public ScheduleItem findScheduleItemByID(int id) throws NotFoundException {
		IDBScheduleItem underlying = database.findScheduleItemByID(id);
		return new ScheduleItem(this, underlying);
	}

	public boolean isInserted(ScheduleItem scheduleItem) {
		return database.isInserted(scheduleItem.underlying);
	}

	

	
	

	public boolean isEmpty() {
		return database.isEmpty();
	}
}
