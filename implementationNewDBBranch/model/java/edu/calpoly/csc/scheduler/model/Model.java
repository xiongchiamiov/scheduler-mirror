package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;
import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;
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

	public User insertUser(String username, boolean b) {
		return new User(database.insertUser(username, b));
	}

	public Document insertDocument(String name) {
		return new Document(database.insertDocument(name));
	}

	public Document findDocumentByID(int documentID) throws NotFoundException {
		return new Document(database.findDocumentByID(documentID));
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
	
	public Schedule insertSchedule(Document containingDocument, Collection<Schedule.Item> items) {
		IDBSchedule underlyingSchedule = database.insertSchedule(containingDocument.underlyingDocument);
		putScheduleItemsIntoDB(underlyingSchedule, items);
		return new Schedule(underlyingSchedule, items);
	}

	public void updateSchedule(Schedule sched) {
		putScheduleItemsIntoDB(sched.underlyingSchedule, sched.getItems());
		database.updateSchedule(sched.underlyingSchedule);
	}

	public void deleteSchedule(Schedule sched) {
		removeScheduleItemsFromDB(sched.underlyingSchedule);
		database.deleteSchedule(sched.underlyingSchedule);
		sched.underlyingSchedule = null;
	}
	
	public Collection<Schedule> findAllSchedulesForDocument(Document containingDocument) {
		Collection<Schedule> result = new LinkedList<Schedule>();
		for (IDBSchedule underlyingSchedule : database.findAllSchedulesForDocument(containingDocument.underlyingDocument))
			result.add(new Schedule(underlyingSchedule, getScheduleItemsFromDB(underlyingSchedule)));
		return result;
	}
	
	private Collection<Schedule.Item> getScheduleItemsFromDB(IDBSchedule schedule) {
		Collection<Schedule.Item> newItems = new LinkedList<Schedule.Item>();
		
		for (IDBScheduleItem item : database.findScheduleItemsBySchedule(schedule))
			newItems.add(new Schedule.Item(item.getSection(),
					database.getScheduleItemCourse(item).getID(),
					database.getScheduleItemLocation(item).getID(),
					database.getScheduleItemInstructor(item).getID()));
		
		return newItems;
	}

	private void removeScheduleItemsFromDB(IDBSchedule schedule) {
		for (IDBScheduleItem item : database.findScheduleItemsBySchedule(schedule))
			database.deleteScheduleItem(item);
	}
	
	private void putScheduleItemsIntoDB(IDBSchedule schedule, Collection<Schedule.Item> items) {
		for (Schedule.Item item : items) {
			try {
				IDBCourse course = database.findCourseByID(item.getCourseID());
				IDBLocation location = database.findLocationByID(item.getCourseID());
				IDBInstructor instructor = database.findInstructorByID(item.getCourseID());
				
				database.insertScheduleItem(schedule, course, instructor, location, item.getSection());
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}
	
	
	
	// INSTRUCTORS

	public Instructor insertInstructor(Document containingDocument, String firstName, String lastName, String username, String maxWTU, HashMap<Integer, HashMap<Integer, Integer>> timePreferences, HashMap<Integer, Integer> coursePreferences) {
		IDBInstructor underlyingInstructor = database.insertInstructor(containingDocument.underlyingDocument, firstName, lastName, username, maxWTU);
		putTimePreferencesIntoDB(underlyingInstructor, timePreferences);
		putCoursePreferencesIntoDB(underlyingInstructor, coursePreferences);
		try {
			return findInstructorByID(underlyingInstructor.getID());
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
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
	
	private HashMap<Integer, HashMap<Integer, Integer>> getTimePreferencesFromDB(IDBInstructor instructor) {
		HashMap<Integer, HashMap<Integer, Integer>> newTimePreferences = new HashMap<Integer, HashMap<Integer, Integer>>();
		
		for (Entry<IDBTime, IDBTimePreference> entry : database.findTimePreferencesByTimeForInstructor(instructor).entrySet()) {
			IDBTime time = entry.getKey();
			IDBTimePreference pref = entry.getValue();
			
			if (!newTimePreferences.containsKey(time.getDay()))
				newTimePreferences.put(time.getDay(), new HashMap<Integer, Integer>());
			assert(!newTimePreferences.get(time.getDay()).containsKey(time.getHalfHour()));
			newTimePreferences.get(time.getDay()).put(time.getHalfHour(), pref.getPreference());
		}
		
		return newTimePreferences;
	}

	private void removeTimePreferencesFromDB(IDBInstructor instructor) {
		for (IDBTimePreference timePref : database.findTimePreferencesByTimeForInstructor(instructor).values())
			database.deleteTimePreference(timePref);
	}

	private void putTimePreferencesIntoDB(IDBInstructor instructor, HashMap<Integer, HashMap<Integer, Integer>> timePreferences) {
		for (Entry<Integer, HashMap<Integer, Integer>> timePreferencesForDay : timePreferences.entrySet()) {
			int day = timePreferencesForDay.getKey();
			for (Entry<Integer, Integer> timePreferenceForTime : timePreferencesForDay.getValue().entrySet()) {
				int halfHour = timePreferenceForTime.getKey();
				IDBTime time = database.findTimeByDayAndHalfHour(day, halfHour); 
				
				int preference = timePreferenceForTime.getValue();
				database.insertTimePreference(instructor, time, preference);
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
				database.insertCoursePreference(instructor, database.findCourseByID(courseID), preference);
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}
	
	

	// COURSES
	
	public Course insertCourse(Document containingDocument, String name, String catalogNumber, String department, String wtu, String scu, String numSections, String type, String maxEnrollment, String numHalfHoursPerWeek, Collection<String> usedEquipmentDescriptions, Collection<Set<Integer>> dayPatterns) {
		IDBCourse underlying = database.insertCourse(containingDocument.underlyingDocument, name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek);
		putUsedEquipmentIntoDB(underlying, usedEquipmentDescriptions);
		putOfferedDayPatternsIntoDB(underlying, dayPatterns);
		return new Course(underlying, usedEquipmentDescriptions, dayPatterns);
	}

	public Collection<Course> findCoursesForDocument(Document doc) {
		Collection<Course> result = new LinkedList<Course>();
		for (IDBCourse underlying : database.findCoursesForDocument(doc.underlyingDocument))
			result.add(new Course(underlying, getUsedEquipmentForCourse(underlying), getOfferedDayPatternsForCourse(underlying)));
		return result;
	}

	public Course findCourseByID(int courseID) throws NotFoundException {
		IDBCourse underlying = database.findCourseByID(courseID);
		return new Course(underlying, getUsedEquipmentForCourse(underlying), getOfferedDayPatternsForCourse(underlying));
	}
	
	public void updateCourse(Course ins) {
		database.updateCourse(ins.underlyingCourse);
		putUsedEquipmentIntoDB(ins.underlyingCourse, ins.usedEquipment);
		putOfferedDayPatternsIntoDB(ins.underlyingCourse, ins.dayPatterns);
	}

	public void deleteCourse(Course ins) {
		removeUsedEquipmentForCourseFromDB(ins.underlyingCourse);
		removeOfferedDayPatternsForCourse(ins.underlyingCourse);
		database.deleteCourse(ins.underlyingCourse);
		ins.underlyingCourse = null;
	}

	private Collection<String> getUsedEquipmentForCourse(IDBCourse course) {
		Collection<String> newUsedEquipment = new LinkedList<String>();
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
				database.insertUsedEquipment(course, database.findEquipmentTypeByDescription(usedEquipmentDescription));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	private Collection<Set<Integer>> getOfferedDayPatternsForCourse(IDBCourse underlying) {
		Collection<Set<Integer>> result = new LinkedList<Set<Integer>>();
		for (IDBOfferedDayPattern offered : database.findOfferedDayPatternsForCourse(underlying))
			result.add(database.getDayPatternForOfferedDayPattern(offered).getDays());
		return result;
	}

	private void removeOfferedDayPatternsForCourse(IDBCourse course) {
		for (IDBOfferedDayPattern offered : database.findOfferedDayPatternsForCourse(course))
			database.deleteOfferedDayPattern(offered);
	}
	
	private void putOfferedDayPatternsIntoDB(IDBCourse underlying, Collection<Set<Integer>> dayPatterns) {
		for (Set<Integer> dayPattern : dayPatterns) {
			try {
				database.insertOfferedDayPattern(underlying, database.findDayPatternByDays(dayPattern));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	
	
	

	// LOCATIONS
	
	public Location insertLocation(Document containingDocument, String room, String type, String maxOccupancy, Collection<String> providedEquipmentDescriptions) {
		IDBLocation underlying = database.insertLocation(containingDocument.underlyingDocument, room, type, maxOccupancy);
		putProvidedEquipmentIntoDB(underlying, providedEquipmentDescriptions);
		return new Location(underlying, providedEquipmentDescriptions);
	}

	public Collection<Location> findLocationsForDocument(Document doc) {
		Collection<Location> result = new LinkedList<Location>();
		for (IDBLocation underlying : database.findLocationsForDocument(doc.underlyingDocument))
			result.add(new Location(underlying, getProvidedEquipmentForLocation(underlying)));
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

	private Collection<String> getProvidedEquipmentForLocation(IDBLocation location) {
		Collection<String> newProvidedEquipment = new LinkedList<String>();
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
				database.insertProvidedEquipment(location, database.findEquipmentTypeByDescription(providedEquipmentDescription));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}
}
