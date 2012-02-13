package edu.calpoly.csc.scheduler.model.db.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;
import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;
import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;
import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;
import edu.calpoly.csc.scheduler.model.db.IDBUser;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

class SimpleDB<T extends DBObject> {
	Map<Integer, T> objectsByID;
	
	public SimpleDB() {
		objectsByID = new HashMap<Integer, T>();
	}
	
	private int generateUnusedID() {
		for (int unusedID = 1; ; unusedID++)
			if (!objectsByID.containsKey(unusedID))
				return unusedID;
	}
	
	// Returns the given object, for convenience
	T insert(T newObject) {
		assert(newObject.id == null);
		
		int newID = generateUnusedID();
		newObject.id = newID;
		
		objectsByID.put(newObject.id, newObject);

		return newObject;
	}
	
	T findByID(int id) throws NotFoundException {
		T result = objectsByID.get(id);
		if (result == null)
			throw new NotFoundException();
		return result;
	}
	
	Collection<T> getAll() {
		return new ArrayList<T>(objectsByID.values());
	}
	
	void deleteByID(int id) {
		assert(objectsByID.containsKey(id));
		objectsByID.remove(id);
	}

	public void update(T object) {
		assert(objectsByID.containsKey(object.id));
		objectsByID.put(object.id, object);
	}
}

public class Database implements IDatabase {
	SimpleDB<DBUser> userDB;
	SimpleDB<DBDocument> documentDB;
	SimpleDB<DBSchedule> scheduleDB;
	SimpleDB<DBScheduleItem> scheduleItemDB;
	SimpleDB<DBCourse> courseDB;
	SimpleDB<DBLocation> locationDB;
	SimpleDB<DBInstructor> instructorDB;
	SimpleDB<DBTimePreference> timePreferenceDB;
	SimpleDB<DBCoursePreference> coursePreferenceDB;
	
	public Database() {
		userDB = new SimpleDB<DBUser>();
		documentDB = new SimpleDB<DBDocument>();
		scheduleDB = new SimpleDB<DBSchedule>();
		scheduleItemDB = new SimpleDB<DBScheduleItem>();
		courseDB = new SimpleDB<DBCourse>();
		locationDB = new SimpleDB<DBLocation>();
		instructorDB = new SimpleDB<DBInstructor>();
		timePreferenceDB = new SimpleDB<DBTimePreference>();
		coursePreferenceDB = new SimpleDB<DBCoursePreference>();
	}
	
	@Override
	public String generateUnusedUsername() {
		Set<String> usernames = new HashSet<String>();
		for (DBUser user : userDB.getAll())
			usernames.add(user.getUsername());
		
		for (int potentialUsernameSuffix = 0; ; potentialUsernameSuffix++) {
			String potentialUsername = "gen" + potentialUsernameSuffix;
			if (!usernames.contains(potentialUsername))
				return potentialUsername;
		}
	}

	@Override
	public IDBUser findUserByUsername(String username) throws NotFoundException {
		for (DBUser user : userDB.getAll())
			if (user.getUsername().equals(username))
				return user;
		throw new NotFoundException();
	}

	@Override
	public IDBUser insertUser(String username, boolean isAdmin) {
		return new DBUser(userDB.insert(new DBUser(null, username, isAdmin)));
	}

	@Override
	public void updateUser(IDBUser user) {
		userDB.update(new DBUser((DBUser)user));
	}

	@Override
	public void deleteUser(IDBUser user) {
		userDB.deleteByID(user.getID());
	}

	@Override
	public Collection<IDBDocument> findAllDocuments() {
		return new ArrayList<IDBDocument>(documentDB.getAll());
	}

	@Override
	public IDBDocument findDocumentByID(int id) throws NotFoundException {
		return new DBDocument(documentDB.findByID(id));
	}

	@Override
	public IDBDocument insertDocument(String name) {
		return new DBDocument(documentDB.insert(new DBDocument(null, name)));
	}

	@Override
	public void updateDocument(IDBDocument document) {
		documentDB.update(new DBDocument((DBDocument)document));
	}

	@Override
	public void deleteDocument(IDBDocument document) {
		documentDB.deleteByID(document.getID());
	}

	@Override
	public Collection<IDBSchedule> findAllSchedulesForDocument(IDBDocument document) {
		Collection<IDBSchedule> result = new LinkedList<IDBSchedule>();
		for (DBSchedule schedule : scheduleDB.getAll())
			if (schedule.documentID == document.getID())
				result.add(schedule);
		return result;
	}

	@Override
	public IDBSchedule findScheduleByID(int id) throws NotFoundException {
		return new DBSchedule(scheduleDB.findByID(id));
	}

	@Override
	public IDBSchedule insertSchedule(IDBDocument containingDocument) {
		return new DBSchedule(scheduleDB.insert(new DBSchedule(null, containingDocument.getID())));
	}

	@Override
	public void updateSchedule(IDBSchedule schedule) {
		scheduleDB.update(new DBSchedule((DBSchedule)schedule));
	}

	@Override
	public void deleteSchedule(IDBSchedule schedule) {
		scheduleDB.deleteByID(schedule.getID());
	}

	@Override
	public Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(IDBSchedule schedule) {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		for (DBScheduleItem scheduleItem : scheduleItemDB.getAll())
			if (scheduleItem.scheduleID == schedule.getID())
				result.add(scheduleItem);
		return result;
	}

	@Override
	public IDBScheduleItem findScheduleItemByID(int id) throws NotFoundException {
		return new DBScheduleItem(scheduleItemDB.findByID(id));
	}

	@Override
	public IDBScheduleItem insertScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, int section) {
		return new DBScheduleItem(scheduleItemDB.insert(new DBScheduleItem(null, schedule.getID(), course.getID(), instructor.getID(), location.getID(), section)));
	}

	@Override
	public void updateScheduleItem(IDBScheduleItem scheduleItem) {
		scheduleItemDB.update(new DBScheduleItem((DBScheduleItem)scheduleItem));
	}

	@Override
	public void deleteScheduleItem(IDBScheduleItem scheduleItem) {
		scheduleItemDB.deleteByID(scheduleItem.getID());
	}

	@Override
	public Collection<IDBLocation> findLocationsForDocument(IDBDocument document) {
		Collection<IDBLocation> result = new LinkedList<IDBLocation>();
		for (DBLocation location : locationDB.getAll())
			if (location.documentID == document.getID())
				result.add(location);
		return result;
	}

	@Override
	public IDBLocation findLocationByID(int id) throws NotFoundException {
		return new DBLocation(locationDB.findByID(id));
	}

	@Override
	public IDBLocation insertLocation(IDBDocument containingDocument, String room, String type, String maxOccupancy) {
		return new DBLocation(locationDB.insert(new DBLocation(null, containingDocument.getID(), room, type, maxOccupancy)));
	}

	@Override
	public void updateLocation(IDBLocation location) {
		locationDB.update(new DBLocation((DBLocation)location));
	}

	@Override
	public void deleteLocation(IDBLocation location) {
		locationDB.deleteByID(location.getID());
	}

	@Override
	public Collection<IDBCourse> findCoursesForDocument(IDBDocument document) {
		Collection<IDBCourse> result = new LinkedList<IDBCourse>();
		for (DBCourse course : courseDB.getAll())
			if (course.documentID == document.getID())
				result.add(course);
		return result;
	}

	@Override
	public IDBCourse findCourseByID(int id) throws NotFoundException {
		return new DBCourse(courseDB.findByID(id));
	}

	@Override
	public IDBCourse insertCourse(IDBDocument containingDocument,
			String name, String catalogNumber,
			String department, String wtu, String scu, String numSections,
			String type, String maxEnrollment, String numHalfHoursPerWeek) {
		return new DBCourse(courseDB.insert(new DBCourse(null, containingDocument.getID(), name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek)));
	}

	@Override
	public void updateCourse(IDBCourse course) {
		courseDB.update(new DBCourse((DBCourse)course));
	}

	@Override
	public void deleteCourse(IDBCourse course) {
		courseDB.deleteByID(course.getID());
	}

	@Override
	public Collection<IDBInstructor> findInstructorsForDocument(IDBDocument document) {
		Collection<IDBInstructor> result = new LinkedList<IDBInstructor>();
		for (DBInstructor instructor : instructorDB.getAll())
			if (instructor.documentID == document.getID())
				result.add(instructor);
		return result;
	}

	@Override
	public IDBInstructor findInstructorByID(int id) throws NotFoundException {
		return new DBInstructor(instructorDB.findByID(id));
	}

	@Override
	public IDBInstructor insertInstructor(IDBDocument containingDocument, String firstName, String lastName,
			String username, String maxWTU) {
		return new DBInstructor(instructorDB.insert(new DBInstructor(null, containingDocument.getID(), firstName, lastName, username, maxWTU)));
	}

	@Override
	public void updateInstructor(IDBInstructor instructor) {
		instructorDB.update(new DBInstructor((DBInstructor)instructor));
	}

	@Override
	public void deleteInstructor(IDBInstructor instructor) {
		instructorDB.deleteByID(instructor.getID());
	}

	@Override
	public Collection<IDBTimePreference> findTimePreferencesForInstructor(IDBInstructor instructor) {
		Collection<IDBTimePreference> result = new LinkedList<IDBTimePreference>();
		for (DBTimePreference timePref : timePreferenceDB.getAll())
			if (timePref.instructorID == instructor.getID())
				result.add(timePref);
		return result;
	}

	@Override
	public IDBTimePreference findTimePreferenceByID(int id) throws NotFoundException {
		return new DBTimePreference(timePreferenceDB.findByID(id));
	}

	@Override
	public IDBTimePreference findTimePreferenceForInstructorAndDayAndTime(
			IDBInstructor instructor, int day, int minute) throws NotFoundException {
		for (DBTimePreference timePref : timePreferenceDB.getAll())
			if (timePref.instructorID == instructor.getID() && timePref.day == day && timePref.minute == minute)
				return timePref;
		throw new NotFoundException();
	}

	@Override
	public IDBTimePreference insertTimePreference(IDBInstructor instructor,
			int day, int minute, int preference) {
		return new DBTimePreference(timePreferenceDB.insert(new DBTimePreference(null, instructor.getID(), day, minute, preference)));
	}

	@Override
	public void updateTimePreference(IDBTimePreference timePreference) {
		timePreferenceDB.update(new DBTimePreference((DBTimePreference)timePreference));
	}

	@Override
	public void deleteTimePreference(IDBTimePreference timePreference) {
		timePreferenceDB.deleteByID(timePreference.getID());
	}

	@Override
	public Collection<IDBCoursePreference> findCoursePreferencesForInstructor(IDBInstructor instructor) {
		Collection<IDBCoursePreference> result = new LinkedList<IDBCoursePreference>();
		for (DBCoursePreference coursePref : coursePreferenceDB.getAll())
			if (coursePref.instructorID == instructor.getID())
				result.add(coursePref);
		return result;
	}

	@Override
	public IDBCoursePreference findCoursePreferenceByID(int id) throws NotFoundException {
		return new DBCoursePreference(coursePreferenceDB.findByID(id));
	}

	@Override
	public IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(
			IDBInstructor instructor, IDBCourse course) throws NotFoundException {
		for (DBCoursePreference coursePref : coursePreferenceDB.getAll())
			if (coursePref.instructorID == instructor.getID() && coursePref.courseID == course.getID())
				return coursePref;
		throw new NotFoundException();
	}

	@Override
	public IDBCoursePreference insertCoursePreference(IDBInstructor instructor,
			IDBCourse course, int preference) {
		return new DBCoursePreference(coursePreferenceDB.insert(new DBCoursePreference(null, instructor.getID(), course.getID(), preference)));
	}

	@Override
	public void updateCoursePreference(IDBCoursePreference coursePreference) {
		coursePreferenceDB.update(new DBCoursePreference((DBCoursePreference)coursePreference));
	}

	@Override
	public void deleteCoursePreference(IDBCoursePreference coursePreference) {
		coursePreferenceDB.deleteByID(coursePreference.getID());
	}
}
