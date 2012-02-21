package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;
import edu.calpoly.csc.scheduler.model.db.IDBCourseAssociation;
import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;
import edu.calpoly.csc.scheduler.model.db.IDBDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDBEquipmentType;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;
import edu.calpoly.csc.scheduler.model.db.IDBOfferedDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDBTime;
import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;
import edu.calpoly.csc.scheduler.model.db.IDBUsedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDBUser;
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

	public String generateUnprovidedUsername() { return database.generateUnusedUsername(); }

	public User assembleUser(String username, boolean b) {
		return new User(database.assembleUser(username, b));
	}
	
	public void insertUser(User user) {
		database.insertUser(user.underlyingUser);
	}

	public Document assembleDocument(String name, int startHalfHour, int endHalfHour) {
		return new Document(database.assembleDocument(name, startHalfHour, endHalfHour));
	}
	
	public Document insertDocument(Document document) {
		database.insertDocument(document.underlyingDocument);
		return document;
	}

	public Document findDocumentByID(int documentID) throws NotFoundException {
		try {
			return new Document(database.findDocumentByID(documentID));
		}
		catch (NotFoundException e) {
			System.out.println("Couldnt find document ID " + documentID);
			throw e;
		}
	}

	public void updateDocument(Document document) {
		database.updateDocument(document.underlyingDocument);
	}

	public void deleteDocument(Document document) {
		database.deleteDocument(document.underlyingDocument);
		document.underlyingDocument = null;
	}

	public Collection<Document> findAllDocuments() {
		Collection<Document> result = new LinkedList<Document>();
		for (IDBDocument underlying : database.findAllDocuments())
			result.add(new Document(underlying));
		return result;
	}
	
	
	// SCHEDULES
	
	public Schedule insertSchedule(Document containingDocument) {
		return new Schedule(database.assembleSchedule(containingDocument.underlyingDocument));
	}
	
	public void insertSchedule(Schedule schedule) {
		database.insertSchedule(schedule.underlyingSchedule);
	}

	public void updateSchedule(Schedule sched) {
		database.updateSchedule(sched.underlyingSchedule);
	}

	public void deleteSchedule(Schedule sched) {
		database.deleteSchedule(sched.underlyingSchedule);
		sched.underlyingSchedule = null;
	}
	
	public Collection<Schedule> findAllSchedulesForDocument(Document containingDocument) {
		Collection<Schedule> result = new LinkedList<Schedule>();
		for (IDBSchedule underlyingSchedule : database.findAllSchedulesForDocument(containingDocument.underlyingDocument))
			result.add(new Schedule(underlyingSchedule));
		return result;
	}

	public Collection<ScheduleItem> findAllScheduleItemsForSchedule(Schedule schedule) {
		Collection<ScheduleItem> result = new LinkedList<ScheduleItem>();
		for (IDBScheduleItem underlying : database.findAllScheduleItemsForSchedule(schedule.underlyingSchedule))
			result.add(new ScheduleItem(underlying, database.getScheduleItemCourse(underlying).getID(), database.getScheduleItemLocation(underlying).getID(), database.getScheduleItemInstructor(underlying).getID()));
		return result;
	}
	
	// INSTRUCTORS

	public Instructor assembleInstructor(Document containingDocument, String firstName, String lastName, String username, String maxWTU, HashMap<Day, HashMap<Integer, Integer>> timePreferences, HashMap<Integer, Integer> coursePreferences) {
		IDBInstructor underlyingInstructor = database.assembleInstructor(containingDocument.underlyingDocument, firstName, lastName, username, maxWTU);
		return new Instructor(underlyingInstructor, timePreferences, coursePreferences);
	}

	public Instructor insertInstructor(Instructor instructor) {
		database.insertInstructor(instructor.underlyingInstructor);
		putTimePreferencesIntoDB(instructor.underlyingInstructor, instructor.timePreferences);
		putCoursePreferencesIntoDB(instructor.underlyingInstructor, instructor.coursePreferences);
		return instructor;
	}

	public Collection<Instructor> findInstructorsForDocument(Document doc) {
		Collection<Instructor> result = new LinkedList<Instructor>();
		for (IDBInstructor underlying : database.findInstructorsForDocument(doc.underlyingDocument))
			result.add(new Instructor(underlying, getTimePreferencesFromDB(underlying), getCoursePreferencesFromDB(underlying)));
		return result;
	}

	public Instructor findInstructorByID(int instructorID) throws NotFoundException {
		IDBInstructor underlying = database.findInstructorByID(instructorID);
		return new Instructor(underlying, getTimePreferencesFromDB(underlying), getCoursePreferencesFromDB(underlying));
	}
	
	public void updateInstructor(Instructor ins) {
		putTimePreferencesIntoDB(ins.underlyingInstructor, ins.timePreferences);
		putCoursePreferencesIntoDB(ins.underlyingInstructor, ins.coursePreferences);
		database.updateInstructor(ins.underlyingInstructor);
	}

	public void deleteInstructor(Instructor ins) {
		removeTimePreferencesFromDB(ins.underlyingInstructor);
		removeCoursePreferencesFromDB(ins.underlyingInstructor);
		database.deleteInstructor(ins.underlyingInstructor);
		ins.underlyingInstructor = null;
	}
	
	private HashMap<Day, HashMap<Integer, Integer>> getTimePreferencesFromDB(IDBInstructor instructor) {
		HashMap<Day, HashMap<Integer, Integer>> newTimePreferences = new HashMap<Day, HashMap<Integer, Integer>>();
		
		for (Entry<IDBTime, IDBTimePreference> entry : database.findTimePreferencesByTimeForInstructor(instructor).entrySet()) {
			IDBTime time = entry.getKey();
			IDBTimePreference pref = entry.getValue();
			Day day = Day.values()[time.getDay()];
			int halfHour = time.getHalfHour();
			
			if (!newTimePreferences.containsKey(day))
				newTimePreferences.put(day, new HashMap<Integer, Integer>());
			assert(!newTimePreferences.get(day).containsKey(halfHour));
			newTimePreferences.get(day).put(halfHour, pref.getPreference());
		}
		
		return newTimePreferences;
	}

	private void removeTimePreferencesFromDB(IDBInstructor instructor) {
		for (IDBTimePreference timePref : database.findTimePreferencesByTimeForInstructor(instructor).values())
			database.deleteTimePreference(timePref);
	}

	private void putTimePreferencesIntoDB(IDBInstructor instructor, HashMap<Day, HashMap<Integer, Integer>> timePreferences) {
		for (Entry<Day, HashMap<Integer, Integer>> timePreferencesForDay : timePreferences.entrySet()) {
			int day = timePreferencesForDay.getKey().ordinal();
			for (Entry<Integer, Integer> timePreferenceForTime : timePreferencesForDay.getValue().entrySet()) {
				int halfHour = timePreferenceForTime.getKey();
				IDBTime time = database.findTimeByDayAndHalfHour(day, halfHour); 
				
				int preference = timePreferenceForTime.getValue();
				database.insertTimePreference(database.assembleTimePreference(instructor, time, preference));
			}
		}
	}

	private HashMap<Integer, Integer> getCoursePreferencesFromDB(IDBInstructor instructor) {
		HashMap<Integer, Integer> newCoursePreferences = new HashMap<Integer, Integer>();
		
		for (Entry<IDBCourse, IDBCoursePreference> entry : database.findCoursePreferencesByCourseForInstructor(instructor).entrySet())
			newCoursePreferences.put(entry.getKey().getID(), entry.getValue().getPreference());
		
		return newCoursePreferences;
	}

	private void removeCoursePreferencesFromDB(IDBInstructor instructor) {
		for (IDBCoursePreference coursePref : database.findCoursePreferencesByCourseForInstructor(instructor).values())
			database.deleteCoursePreference(coursePref);
	}
	
	private void putCoursePreferencesIntoDB(IDBInstructor instructor, HashMap<Integer, Integer> coursePreferences) {
		for (Entry<Integer, Integer> coursePreference : coursePreferences.entrySet()) {
			int courseID = coursePreference.getKey();
			int preference = coursePreference.getValue();
			
			try {
				database.insertCoursePreference(database.assembleCoursePreference(instructor, database.findCourseByID(courseID), preference));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}
	
	

	// COURSES
	
	public Course assembleCourse(Document containingDocument, String name, String catalogNumber, String department, String wtu, String scu, String numSections, String type, String maxEnrollment, String numHalfHoursPerWeek, Set<String> usedEquipmentDescriptions, Collection<Set<Day>> dayPatterns, boolean isSchedulable) {
		return new Course(database.assembleCourse(containingDocument.underlyingDocument, name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek, isSchedulable), usedEquipmentDescriptions, dayPatterns, -1, false);
	}
	
	public Course insertCourse(Course course) {
		database.insertCourse(course.underlyingCourse);
		putUsedEquipmentIntoDB(course.underlyingCourse, course.getUsedEquipment());
		putOfferedDayPatternsIntoDB(course.underlyingCourse, course.getDayPatterns());
		return course;
	}

	public Collection<Course> findCoursesForDocument(Document doc) {
		Collection<Course> result = new LinkedList<Course>();
		for (IDBCourse underlying : database.findCoursesForDocument(doc.underlyingDocument)) {
			int lectureID = -1;
			boolean tethered = false;
			if (underlying.getType().equals("LAB")) {
				IDBCourseAssociation assoc = database.getAssociationForLabOrNull(underlying);
				if (assoc != null) {
					assert(database.getAssociationLab(assoc).getID() == underlying.getID());
					lectureID = database.getAssociationLecture(assoc).getID();
					tethered = assoc.isTethered();
				}
			}
			result.add(new Course(underlying, getUsedEquipmentForCourse(underlying), getOfferedDayPatternsForCourse(underlying), lectureID, tethered));
		}
		return result;
	}

	public Course findCourseByID(int courseID) throws NotFoundException {
		IDBCourse underlying = database.findCourseByID(courseID);

		int lectureID = -1;
		boolean tethered = false;
		if (underlying.getType().equals("LAB")) {
			IDBCourseAssociation assoc = database.getAssociationForLabOrNull(underlying);
			if (assoc != null) {
				assert(database.getAssociationLab(assoc).getID() == underlying.getID());
				lectureID = database.getAssociationLecture(assoc).getID();
				tethered = assoc.isTethered();
			}
		}
		
		return new Course(underlying, getUsedEquipmentForCourse(underlying), getOfferedDayPatternsForCourse(underlying), lectureID, tethered);
	}
	
	public void updateCourse(Course ins) {
		database.updateCourse(ins.underlyingCourse);
		putUsedEquipmentIntoDB(ins.underlyingCourse, ins.usedEquipment);
		putOfferedDayPatternsIntoDB(ins.underlyingCourse, ins.dayPatterns);
	}

	public void deleteCourse(Course course) {
		removeUsedEquipmentForCourseFromDB(course.underlyingCourse);
		removeOfferedDayPatternsForCourse(course.underlyingCourse);
		database.deleteCourse(course.underlyingCourse);
		course.underlyingCourse = null;
	}
//
//   public void saveCurrentScheduleAs(String schedulename)
//   {
//      Schedule current = getSchedule();
//      // Rename current schedule and change dbid so that a new one is added and
//      // not the current one edited instead
//      current.setName(schedulename);
//      current.setScheduleDBId(-1);
//      current.setDbid(-1);
//      db.getScheduleDB().saveData(current);
//      db.setScheduleID(db.getScheduleDB().getScheduleDBID());
//   }

	private Set<String> getUsedEquipmentForCourse(IDBCourse course) {
		Set<String> newUsedEquipment = new HashSet<String>();
		for (IDBEquipmentType equipment : database.findUsedEquipmentByEquipmentForCourse(course).keySet())
			newUsedEquipment.add(equipment.getDescription());
		return newUsedEquipment;
	}

	private void removeUsedEquipmentForCourseFromDB(IDBCourse course) {
		for (IDBUsedEquipment usedEquipment : database.findUsedEquipmentByEquipmentForCourse(course).values())
			database.deleteUsedEquipment(usedEquipment);
	}
	
	private void putUsedEquipmentIntoDB(IDBCourse course, Collection<String> usedEquipmentDescriptions) {
		for (String usedEquipmentDescription : usedEquipmentDescriptions) {
			try {
				database.insertUsedEquipment(database.assembleUsedEquipment(course, database.findEquipmentTypeByDescription(usedEquipmentDescription)));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	private static Set<Day> daysFromIntegers(Set<Integer> integers) {
		Set<Day> result = new TreeSet<Day>();
		for (Integer integer : integers)
			result.add(Day.values()[integer]);
		return result;
	}
	
	private static Set<Integer> daysToIntegers(Set<Day> days) {
		Set<Integer> result = new TreeSet<Integer>();
		for (Day day : days)
			result.add(day.ordinal());
		return result;
	}
	
	private Collection<Set<Day>> getOfferedDayPatternsForCourse(IDBCourse underlying) {
		Collection<Set<Day>> result = new LinkedList<Set<Day>>();
		for (IDBOfferedDayPattern offered : database.findOfferedDayPatternsForCourse(underlying)) {
			result.add(daysFromIntegers(database.getDayPatternForOfferedDayPattern(offered).getDays()));
		}
		return result;
	}

	private void removeOfferedDayPatternsForCourse(IDBCourse course) {
		for (IDBOfferedDayPattern offered : database.findOfferedDayPatternsForCourse(course))
			database.deleteOfferedDayPattern(offered);
	}
	
	private void putOfferedDayPatternsIntoDB(IDBCourse underlying, Collection<Set<Day>> dayPatterns) {
		for (Set<Day> dayPattern : dayPatterns) {
			try {
				Set<Integer> integers = daysToIntegers(dayPattern);
				database.insertOfferedDayPattern(database.assembleOfferedDayPattern(underlying, database.findDayPatternByDays(integers)));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	
	
	
	
	
	

	// LOCATIONS
	
	public Location assembleLocation(Document containingDocument, String room, String type, String maxOccupancy, Set<String> providedEquipmentDescriptions) {
		return new Location(database.assembleLocation(containingDocument.underlyingDocument, room, type, maxOccupancy), providedEquipmentDescriptions);
	}
	
	public Location insertLocation(Location location) {
		database.insertLocation(location.underlyingLocation);
		putProvidedEquipmentIntoDB(location.underlyingLocation, location.providedEquipment);
		return location;
	}

	public Collection<Location> findLocationsForDocument(Document doc) {
		Collection<Location> result = new LinkedList<Location>();
		for (IDBLocation underlying : database.findLocationsForDocument(doc.underlyingDocument)) {
			System.out.println("find result underlying room: " + underlying.getRoom() + " id " + underlying.getID());
			result.add(new Location(underlying, getProvidedEquipmentForLocation(underlying)));
		}
		return result;
	}

	public Location findLocationByID(int locationID) throws NotFoundException {
		IDBLocation underlying = database.findLocationByID(locationID);
		return new Location(underlying, getProvidedEquipmentForLocation(underlying));
	}
	
	public void updateLocation(Location ins) {
		database.updateLocation(ins.underlyingLocation);
	}

	public void deleteLocation(Location ins) {
		removeProvidedEquipmentForLocationFromDB(ins.underlyingLocation);
		database.deleteLocation(ins.underlyingLocation);
		ins.underlyingLocation = null;
	}

	private Set<String> getProvidedEquipmentForLocation(IDBLocation location) {
		Set<String> newProvidedEquipment = new HashSet<String>();
		for (IDBEquipmentType equipment : database.findProvidedEquipmentByEquipmentForLocation(location).keySet())
			newProvidedEquipment.add(equipment.getDescription());
		return newProvidedEquipment;
	}

	private void removeProvidedEquipmentForLocationFromDB(IDBLocation location) {
		for (IDBProvidedEquipment providedEquipment : database.findProvidedEquipmentByEquipmentForLocation(location).values())
			database.deleteProvidedEquipment(providedEquipment);
	}
	
	private void putProvidedEquipmentIntoDB(IDBLocation location, Collection<String> providedEquipmentDescriptions) {
		for (String providedEquipmentDescription : providedEquipmentDescriptions) {
			try {
				database.insertProvidedEquipment(database.assembleProvidedEquipment(location, database.findEquipmentTypeByDescription(providedEquipmentDescription)));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	public Document findDocumentForCourse(Course course) {
		return new Document(database.findDocumentForCourse(course.underlyingCourse));
	}

	public User findUserByUsername(String username) throws NotFoundException {
		return new User(database.findUserByUsername(username));
	}

	public User assembleUser(String username) {
		IDBUser underlying = database.assembleUser(username, true);
		database.insertUser(underlying);
		return new User(underlying);
	}

	public void disassociateWorkingCopyFromOriginal(Document workingCopyDocument, Document original) {
		database.disassociateWorkingCopyWithOriginal(workingCopyDocument.underlyingDocument, original.underlyingDocument);
	}

	public Document getWorkingCopyForOriginalDocumentOrNull(Document originalDocument) {
		IDBDocument underlying = database.getWorkingCopyForOriginalDocumentOrNull(originalDocument.underlyingDocument);
		if (underlying == null)
			return null;
		return new Document(underlying);
	}
	
	public Document copyDocument(Document existingDocument, String newName) {
		IDBDocument underlying = database.assembleDocument(newName, existingDocument.getStartHalfHour(), existingDocument.getEndHalfHour());
		database.insertDocument(underlying);
		Document newDocument = new Document(underlying);

		// Locations
		Map<Integer, IDBLocation> newDocumentLocationsByExistingDocumentLocationIDs = new HashMap<Integer, IDBLocation>();
		for (IDBLocation existingDocumentLocation : database.findLocationsForDocument(existingDocument.underlyingDocument)) {
			IDBLocation newDocumentLocation = database.assembleLocation(newDocument.underlyingDocument, existingDocumentLocation.getRoom(), existingDocumentLocation.getType(), existingDocumentLocation.getMaxOccupancy());
			database.insertLocation(newDocumentLocation);
			newDocumentLocationsByExistingDocumentLocationIDs.put(existingDocumentLocation.getID(), newDocumentLocation);

			for (IDBEquipmentType providedEquipment : database.findProvidedEquipmentByEquipmentForLocation(existingDocumentLocation).keySet()) {
				database.insertProvidedEquipment(database.assembleProvidedEquipment(newDocumentLocation, providedEquipment));
			}
		}

		// Courses
		Map<Integer, IDBCourse> newDocumentCoursesByExistingDocumentCourseIDs = new HashMap<Integer, IDBCourse>();
		for (IDBCourse existingDocumentCourse : database.findCoursesForDocument(existingDocument.underlyingDocument)) {
			IDBCourse newDocumentCourse = database.assembleCourse(newDocument.underlyingDocument, existingDocumentCourse.getName(), existingDocumentCourse.getCalatogNumber(), existingDocumentCourse.getDepartment(), existingDocumentCourse.getWTU(), existingDocumentCourse.getSCU(), existingDocumentCourse.getNumSections(), existingDocumentCourse.getType(), existingDocumentCourse.getMaxEnrollment(), existingDocumentCourse.getNumHalfHoursPerWeek(), existingDocumentCourse.isSchedulable());
			database.insertCourse(newDocumentCourse);
			newDocumentCoursesByExistingDocumentCourseIDs.put(existingDocumentCourse.getID(), newDocumentCourse);
			
			for (IDBOfferedDayPattern existingOfferedDayPattern : database.findOfferedDayPatternsForCourse(existingDocumentCourse)) {
				IDBDayPattern dayPattern = database.getDayPatternForOfferedDayPattern(existingOfferedDayPattern);
				database.insertOfferedDayPattern(database.assembleOfferedDayPattern(newDocumentCourse, dayPattern));
			}

			for (IDBEquipmentType usedEquipment : database.findUsedEquipmentByEquipmentForCourse(existingDocumentCourse).keySet()) {
				database.insertUsedEquipment(database.assembleUsedEquipment(newDocumentCourse, usedEquipment));
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
			IDBInstructor newDocumentInstructor = database.assembleInstructor(newDocument.underlyingDocument, existingDocumentInstructor.getFirstName(), existingDocumentInstructor.getLastName(), existingDocumentInstructor.getUsername(), existingDocumentInstructor.getMaxWTU());
			database.insertInstructor(newDocumentInstructor);
			newDocumentInstructorsByExistingDocumentInstructorIDs.put(existingDocumentInstructor.getID(), newDocumentInstructor);
			
			for (Entry<IDBCourse, IDBCoursePreference> existingDocumentEntry : database.findCoursePreferencesByCourseForInstructor(existingDocumentInstructor).entrySet()) {
				IDBCourse existingDocumentCoursePreferenceCourse = existingDocumentEntry.getKey();
				IDBCoursePreference existingDocumentCoursePreference = existingDocumentEntry.getValue();
				IDBCourse newDocumentCourse = newDocumentCoursesByExistingDocumentCourseIDs.get(existingDocumentCoursePreferenceCourse.getID());
				IDBCoursePreference newDocumentCoursePreference = database.assembleCoursePreference(newDocumentInstructor, newDocumentCourse, existingDocumentCoursePreference.getPreference());
				database.insertCoursePreference(newDocumentCoursePreference);
			}
			
			for (Entry<IDBTime, IDBTimePreference> existingDocumentEntry : database.findTimePreferencesByTimeForInstructor(existingDocumentInstructor).entrySet()) {
				IDBTime time = existingDocumentEntry.getKey();
				IDBTimePreference existingDocumentTimePreference = existingDocumentEntry.getValue();
				IDBTimePreference newDocumentTimePreference = database.assembleTimePreference(newDocumentInstructor, time, existingDocumentTimePreference.getPreference());
				database.insertTimePreference(newDocumentTimePreference);
			}
		}
		
		// Schedules
		Map<Integer, IDBSchedule> newDocumentScheduleIDsByExistingDocumentScheduleIDs = new HashMap<Integer, IDBSchedule>();
		for (IDBSchedule existingDocumentSchedule : database.findAllSchedulesForDocument(existingDocument.underlyingDocument)) {
			IDBSchedule newDocumentSchedule = database.assembleSchedule(newDocument.underlyingDocument);
			database.insertSchedule(newDocumentSchedule);
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
						database.assembleScheduleItem(
								newDocumentSchedule,
								newDocumentCourse,
								newDocumentInstructor,
								newDocumentLocation,
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
		return new Document(database.getOriginalForWorkingCopyDocument(workingCopyDocument.underlyingDocument));
	}

	public boolean isOriginalDocument(Document doc) {
		return database.isOriginalDocument(doc.underlyingDocument);
	}

	public Schedule findScheduleByID(int scheduleID) throws NotFoundException {
		IDBSchedule underlying = database.findScheduleByID(scheduleID);
		return new Schedule(underlying);
	}

	public Course getScheduleItemCourse(ScheduleItem item) throws NotFoundException {
		return this.findCourseByID(item.getCourseID()); 
	}

	public Location getScheduleItemLocation(ScheduleItem item) throws NotFoundException {
		return this.findLocationByID(item.getLocationID()); 
	}

	public Instructor getScheduleItemInstructor(ScheduleItem item) throws NotFoundException {
		return this.findInstructorByID(item.getInstructorID()); 
	}

	public Document getDocumentForSchedule(Schedule schedule) throws NotFoundException {
		return new Document(database.findDocumentForSchedule(schedule.underlyingSchedule));
	}

	public ScheduleItem assembleScheduleItem(Schedule schedule, Course course,
			Instructor instructor, Location location, int section,
			Set<Day> days, int startHalfHour, int endHalfHour,
			boolean isPlaced, boolean isConflicted) {
		IDBScheduleItem underlying = database.assembleScheduleItem(schedule.underlyingSchedule, course.underlyingCourse, instructor.underlyingInstructor, location.underlyingLocation, section, days, startHalfHour, endHalfHour, isPlaced, isConflicted);
		return new ScheduleItem(underlying, course.getID(), location.getID(), instructor.getID());
	}

	public void insertScheduleItem(ScheduleItem item) {
		database.insertScheduleItem(item.underlying);
	}

	public void deleteScheduleItem(ScheduleItem item) {
		database.deleteScheduleItem(item.underlying);
	}

	public ScheduleItem findScheduleItemByID(int id) throws NotFoundException {
		IDBScheduleItem underlying = database.findScheduleItemByID(id);
		return new ScheduleItem(
				underlying,
				database.getScheduleItemCourse(underlying).getID(),
				database.getScheduleItemLocation(underlying).getID(),
				database.getScheduleItemInstructor(underlying).getID());
	}

	public void updateScheduleItem(ScheduleItem item) throws NotFoundException {
		database.setScheduleItemCourse(item.underlying, database.findCourseByID(item.getCourseID()));
		database.setScheduleItemInstructor(item.underlying, database.findInstructorByID(item.getInstructorID()));
		database.setScheduleItemLocation(item.underlying, database.findLocationByID(item.getLocationID()));
		database.updateScheduleItem(item.underlying);
	}
}
