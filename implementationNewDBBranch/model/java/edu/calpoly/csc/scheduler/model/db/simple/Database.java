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

public class Database implements IDatabase {
	class SimpleTable<T extends DBObject> {
		Map<Integer, T> objectsByID;
		
		public SimpleTable() {
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

	SimpleTable<DBUser> userTable;
	SimpleTable<DBDocument> documentTable;
	SimpleTable<DBSchedule> scheduleTable;
	SimpleTable<DBScheduleItem> scheduleItemTable;
	SimpleTable<DBCourse> courseTable;
	SimpleTable<DBLocation> locationTable;
	SimpleTable<DBInstructor> instructorTable;
	SimpleTable<DBTimePreference> timePreferenceTable;
	SimpleTable<DBCoursePreference> coursePreferenceTable;
	SimpleTable<DBEquipmentType> equipmentTypeTable;
	SimpleTable<DBProvidedEquipment> providedEquipmentTable;
	SimpleTable<DBUsedEquipment> usedEquipmentTable;
	SimpleTable<DBOfferedDayPattern> offeredDayPatternTable;
	
	public Database() {
		userTable = new SimpleTable<DBUser>();
		documentTable = new SimpleTable<DBDocument>();
		scheduleTable = new SimpleTable<DBSchedule>();
		scheduleItemTable = new SimpleTable<DBScheduleItem>();
		courseTable = new SimpleTable<DBCourse>();
		locationTable = new SimpleTable<DBLocation>();
		instructorTable = new SimpleTable<DBInstructor>();
		timePreferenceTable = new SimpleTable<DBTimePreference>();
		coursePreferenceTable = new SimpleTable<DBCoursePreference>();
		equipmentTypeTable = new SimpleTable<DBEquipmentType>();
		providedEquipmentTable = new SimpleTable<DBProvidedEquipment>();
		usedEquipmentTable = new SimpleTable<DBUsedEquipment>();
		offeredDayPatternTable = new SimpleTable<DBOfferedDayPattern>();
	}
	
	@Override
	public String generateUnusedUsername() {
		Set<String> usernames = new HashSet<String>();
		for (DBUser user : userTable.getAll())
			usernames.add(user.getUsername());
		
		for (int potentialUsernameSuffix = 0; ; potentialUsernameSuffix++) {
			String potentialUsername = "gen" + potentialUsernameSuffix;
			if (!usernames.contains(potentialUsername))
				return potentialUsername;
		}
	}

	@Override
	public IDBUser findUserByUsername(String username) throws NotFoundException {
		for (DBUser user : userTable.getAll())
			if (user.getUsername().equals(username))
				return user;
		throw new NotFoundException();
	}

	@Override
	public IDBUser insertUser(String username, boolean isAdmin) {
		return new DBUser(userTable.insert(new DBUser(null, username, isAdmin)));
	}

	@Override
	public void updateUser(IDBUser user) {
		userTable.update(new DBUser((DBUser)user));
	}

	@Override
	public void deleteUser(IDBUser user) {
		userTable.deleteByID(user.getID());
	}

	@Override
	public Collection<IDBDocument> findAllDocuments() {
		return new ArrayList<IDBDocument>(documentTable.getAll());
	}

	@Override
	public IDBDocument findDocumentByID(int id) throws NotFoundException {
		return new DBDocument(documentTable.findByID(id));
	}

	@Override
	public IDBDocument insertDocument(String name) {
		return new DBDocument(documentTable.insert(new DBDocument(null, name)));
	}

	@Override
	public void updateDocument(IDBDocument document) {
		documentTable.update(new DBDocument((DBDocument)document));
	}

	@Override
	public void deleteDocument(IDBDocument document) {
		documentTable.deleteByID(document.getID());
	}

	@Override
	public Collection<IDBSchedule> findAllSchedulesForDocument(IDBDocument document) {
		Collection<IDBSchedule> result = new LinkedList<IDBSchedule>();
		for (DBSchedule schedule : scheduleTable.getAll())
			if (schedule.documentID == document.getID())
				result.add(schedule);
		return result;
	}

	@Override
	public IDBSchedule findScheduleByID(int id) throws NotFoundException {
		return new DBSchedule(scheduleTable.findByID(id));
	}

	@Override
	public IDBSchedule insertSchedule(IDBDocument containingDocument) {
		return new DBSchedule(scheduleTable.insert(new DBSchedule(null, containingDocument.getID())));
	}

	@Override
	public void updateSchedule(IDBSchedule schedule) {
		scheduleTable.update(new DBSchedule((DBSchedule)schedule));
	}

	@Override
	public void deleteSchedule(IDBSchedule schedule) {
		scheduleTable.deleteByID(schedule.getID());
	}

	@Override
	public Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(IDBSchedule schedule) {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		for (DBScheduleItem scheduleItem : scheduleItemTable.getAll())
			if (scheduleItem.scheduleID == schedule.getID())
				result.add(scheduleItem);
		return result;
	}

	@Override
	public IDBScheduleItem findScheduleItemByID(int id) throws NotFoundException {
		return new DBScheduleItem(scheduleItemTable.findByID(id));
	}

	@Override
	public IDBScheduleItem insertScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, int section) {
		return new DBScheduleItem(scheduleItemTable.insert(new DBScheduleItem(null, schedule.getID(), course.getID(), instructor.getID(), location.getID(), section)));
	}

	@Override
	public void updateScheduleItem(IDBScheduleItem scheduleItem) {
		scheduleItemTable.update(new DBScheduleItem((DBScheduleItem)scheduleItem));
	}

	@Override
	public void deleteScheduleItem(IDBScheduleItem scheduleItem) {
		scheduleItemTable.deleteByID(scheduleItem.getID());
	}

	@Override
	public Collection<IDBLocation> findLocationsForDocument(IDBDocument document) {
		Collection<IDBLocation> result = new LinkedList<IDBLocation>();
		for (DBLocation location : locationTable.getAll())
			if (location.documentID == document.getID())
				result.add(location);
		return result;
	}

	@Override
	public IDBLocation findLocationByID(int id) throws NotFoundException {
		return new DBLocation(locationTable.findByID(id));
	}

	@Override
	public IDBLocation insertLocation(IDBDocument containingDocument, String room, String type, String maxOccupancy) {
		return new DBLocation(locationTable.insert(new DBLocation(null, containingDocument.getID(), room, type, maxOccupancy)));
	}

	@Override
	public void updateLocation(IDBLocation location) {
		locationTable.update(new DBLocation((DBLocation)location));
	}

	@Override
	public void deleteLocation(IDBLocation location) {
		locationTable.deleteByID(location.getID());
	}

	@Override
	public Collection<IDBCourse> findCoursesForDocument(IDBDocument document) {
		Collection<IDBCourse> result = new LinkedList<IDBCourse>();
		for (DBCourse course : courseTable.getAll())
			if (course.documentID == document.getID())
				result.add(course);
		return result;
	}

	@Override
	public IDBCourse findCourseByID(int id) throws NotFoundException {
		return new DBCourse(courseTable.findByID(id));
	}

	@Override
	public IDBCourse insertCourse(IDBDocument containingDocument, String name, String catalogNumber, String department, String wtu, String scu, String numSections, String type, String maxEnrollment, String numHalfHoursPerWeek) {
		return new DBCourse(courseTable.insert(new DBCourse(null, containingDocument.getID(), name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek)));
	}

	@Override
	public void updateCourse(IDBCourse course) {
		courseTable.update(new DBCourse((DBCourse)course));
	}

	@Override
	public void deleteCourse(IDBCourse course) {
		courseTable.deleteByID(course.getID());
	}

	@Override
	public Collection<IDBInstructor> findInstructorsForDocument(IDBDocument document) {
		Collection<IDBInstructor> result = new LinkedList<IDBInstructor>();
		for (DBInstructor instructor : instructorTable.getAll())
			if (instructor.documentID == document.getID())
				result.add(instructor);
		return result;
	}

	@Override
	public IDBInstructor findInstructorByID(int id) throws NotFoundException {
		return new DBInstructor(instructorTable.findByID(id));
	}

	@Override
	public IDBInstructor insertInstructor(IDBDocument containingDocument, String firstName, String lastName,
			String username, String maxWTU) {
		return new DBInstructor(instructorTable.insert(new DBInstructor(null, containingDocument.getID(), firstName, lastName, username, maxWTU)));
	}

	@Override
	public void updateInstructor(IDBInstructor instructor) {
		instructorTable.update(new DBInstructor((DBInstructor)instructor));
	}

	@Override
	public void deleteInstructor(IDBInstructor instructor) {
		instructorTable.deleteByID(instructor.getID());
	}

	@Override
	public Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(IDBInstructor instructor) {
		Map<IDBTime, IDBTimePreference> result = new HashMap<IDBTime, IDBTimePreference>();
		for (DBTimePreference timePref : timePreferenceTable.getAll())
			if (timePref.instructorID == instructor.getID())
				result.put(findTimeByID(timePref.timeID), timePref);
		return result;
	}

	@Override
	public IDBTimePreference findTimePreferenceByID(int id) throws NotFoundException {
		return new DBTimePreference(timePreferenceTable.findByID(id));
	}

	private boolean sameTime(IDBTime a, IDBTime b) {
		return a.getDay() == b.getDay() && a.getHalfHour() == b.getHalfHour();
	}
	
	
	@Override
	public IDBTime findTimeByDayAndHalfHour(int day, int halfHour) {
		return new DBTime(day, halfHour);
	}
	
	public IDBTime findTimeByID(int id) {
		return new DBTime(id);
	}
	
	@Override
	public IDBTimePreference findTimePreferenceForInstructorAndDayAndTime(IDBInstructor instructor, IDBTime time) throws NotFoundException {
		for (DBTimePreference timePref : timePreferenceTable.getAll())
			if (timePref.instructorID == instructor.getID() && sameTime(time, findTimeByID(timePref.timeID)))
				return timePref;
		throw new NotFoundException();
	}

	@Override
	public IDBTimePreference insertTimePreference(IDBInstructor instructor, IDBTime time, int preference) {
		return new DBTimePreference(timePreferenceTable.insert(new DBTimePreference(null, instructor.getID(), time.getID(), preference)));
	}

	@Override
	public void updateTimePreference(IDBTimePreference timePreference) {
		timePreferenceTable.update(new DBTimePreference((DBTimePreference)timePreference));
	}

	@Override
	public void deleteTimePreference(IDBTimePreference timePreference) {
		timePreferenceTable.deleteByID(timePreference.getID());
	}

	@Override
	public Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(IDBInstructor instructor) {
		Map<IDBCourse, IDBCoursePreference> result = new HashMap<IDBCourse, IDBCoursePreference>();
		for (DBCoursePreference coursePref : coursePreferenceTable.getAll()) {
			if (coursePref.instructorID == instructor.getID()) {
				try {
					result.put(findCourseByID(coursePref.courseID), coursePref);
				} catch (NotFoundException e) {
					throw new AssertionError(e);
				}
			}
		}
		return result;
	}

	@Override
	public IDBCoursePreference findCoursePreferenceByID(int id) throws NotFoundException {
		return new DBCoursePreference(coursePreferenceTable.findByID(id));
	}

	@Override
	public IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(
			IDBInstructor instructor, IDBCourse course) throws NotFoundException {
		for (DBCoursePreference coursePref : coursePreferenceTable.getAll())
			if (coursePref.instructorID == instructor.getID() && coursePref.courseID == course.getID())
				return coursePref;
		throw new NotFoundException();
	}

	@Override
	public IDBCoursePreference insertCoursePreference(IDBInstructor instructor,
			IDBCourse course, int preference) {
		return new DBCoursePreference(coursePreferenceTable.insert(new DBCoursePreference(null, instructor.getID(), course.getID(), preference)));
	}

	@Override
	public void updateCoursePreference(IDBCoursePreference coursePreference) {
		coursePreferenceTable.update(new DBCoursePreference((DBCoursePreference)coursePreference));
	}

	@Override
	public void deleteCoursePreference(IDBCoursePreference coursePreference) {
		coursePreferenceTable.deleteByID(coursePreference.getID());
	}

	@Override
	public boolean documentIsWorkingCopy(IDBDocument rawDocument) {
		DBDocument document = (DBDocument)rawDocument;
		return document.originalID == document.id;
	}

	@Override
	public IDBDocument getWorkingCopyForDocument(IDBDocument rawDocument) throws NotFoundException {
		assert(!documentIsWorkingCopy(rawDocument));
		DBDocument document = (DBDocument)rawDocument;
		
		for (DBDocument workingCopy : this.documentTable.getAll())
			if (documentIsWorkingCopy(workingCopy))
				if (workingCopy.originalID == document.id)
					return workingCopy;
		
		throw new NotFoundException();
	}

	@Override
	public IDBDocument getOriginalForDocument(IDBDocument rawDocument) {
		assert(!documentIsWorkingCopy(rawDocument));
		DBDocument document = (DBDocument)rawDocument;
		
		try {
			return documentTable.findByID(document.originalID);
		}
		catch (NotFoundException e) {
			assert(false);
			return null;
		}
	}

	@Override
	public boolean labCourseIsTethered(IDBCourse rawLabCourse) {
		DBCourse labCourse = (DBCourse)rawLabCourse;
		return labCourse.lectureID != 0;
	}

	@Override
	public IDBCourse getLectureTetheredToLabCourse(IDBCourse rawLabCourse) throws NotFoundException {
		assert(labCourseIsTethered(rawLabCourse));
		DBCourse labCourse = (DBCourse)rawLabCourse;
		return courseTable.findByID(labCourse.lectureID);
	}

	@Override
	public Collection<IDBCourse> getLabsTetheredToLectureCourse(IDBCourse rawLectureCourse) {
		DBCourse lectureCourse = (DBCourse)rawLectureCourse;
		assert(lectureCourse.lectureID == -1);
		
		Collection<IDBCourse> result = new LinkedList<IDBCourse>();
		for (DBCourse labCourse : this.courseTable.getAll())
			if (labCourse.lectureID == lectureCourse.id)
				result.add(labCourse);
		return result;
	}

	@Override
	public IDBLocation getScheduleItemLocation(IDBScheduleItem rawItem) {
		DBScheduleItem item = (DBScheduleItem)rawItem;
		try {
			return findLocationByID(item.locationID);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public IDBCourse getScheduleItemCourse(IDBScheduleItem rawItem) {
		DBScheduleItem item = (DBScheduleItem)rawItem;
		try {
			return findCourseByID(item.courseID);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public IDBInstructor getScheduleItemInstructor(IDBScheduleItem rawItem) {
		DBScheduleItem item = (DBScheduleItem)rawItem;
		try {
			return findInstructorByID(item.instructorID);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public Collection<IDBScheduleItem> findScheduleItemsBySchedule(IDBSchedule schedule) {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		for (DBScheduleItem item : scheduleItemTable.getAll())
			if (item.scheduleID == schedule.getID())
				result.add(item);
		return result;
	}

	@Override
	public Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(IDBCourse course) {
		Map<IDBEquipmentType, IDBUsedEquipment> result = new HashMap<IDBEquipmentType, IDBUsedEquipment>();
		for (DBUsedEquipment used : usedEquipmentTable.getAll()) {
			if (used.courseID == course.getID()) {
				try {
					result.put(equipmentTypeTable.findByID(used.equipmentTypeID), used);
				} catch (NotFoundException e) {
					throw new AssertionError(e);
				}
			}
		}
		return result;
	}

	@Override
	public void deleteUsedEquipment(IDBUsedEquipment usedEquipment) {
		usedEquipmentTable.deleteByID(usedEquipment.getID());
	}

	@Override
	public IDBEquipmentType findEquipmentTypeByDescription(String equipmentTypeDescription) throws NotFoundException {
		for (DBEquipmentType type : equipmentTypeTable.getAll())
			if (type.description.equals(equipmentTypeDescription))
				return type;
		throw new NotFoundException();
	}

	@Override
	public DBUsedEquipment insertUsedEquipment(IDBCourse course, IDBEquipmentType equipmentType) {
		return new DBUsedEquipment(usedEquipmentTable.insert(new DBUsedEquipment(null, course.getID(), equipmentType.getID())));
	}

	@Override
	public Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(IDBLocation location) {
		Map<IDBEquipmentType, IDBProvidedEquipment> result = new HashMap<IDBEquipmentType, IDBProvidedEquipment>();
		for (DBProvidedEquipment provided : providedEquipmentTable.getAll()) {
			if (provided.locationID == location.getID()) {
				try {
					result.put(equipmentTypeTable.findByID(provided.equipmentTypeID), provided);
				} catch (NotFoundException e) {
					throw new AssertionError(e);
				}
			}
		}
		return result;
	}

	@Override
	public void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment) {
		providedEquipmentTable.deleteByID(providedEquipment.getID());
		DBProvidedEquipment derp = (DBProvidedEquipment)providedEquipment;
		derp.id = -1;
	}

	@Override
	public DBProvidedEquipment insertProvidedEquipment(IDBLocation location, IDBEquipmentType equipmentType) {
		return new DBProvidedEquipment(providedEquipmentTable.insert(new DBProvidedEquipment(null, location.getID(), equipmentType.getID())));
	}

	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern) throws NotFoundException {
		return new DBDayPattern(dayPattern);
	}

	@Override
	public IDBOfferedDayPattern insertOfferedDayPattern(IDBCourse underlying, IDBDayPattern dayPattern) {
		return new DBOfferedDayPattern(offeredDayPatternTable.insert(new DBOfferedDayPattern(null, underlying.getID(), dayPattern.getID())));
	}

	@Override
	public Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(IDBCourse underlying) {
		Collection<IDBOfferedDayPattern> result = new LinkedList<IDBOfferedDayPattern>();
		for (DBOfferedDayPattern offering : offeredDayPatternTable.getAll())
			if (offering.courseID == underlying.getID())
				result.add(offering);
		return result;
	}

	@Override
	public IDBDayPattern getDayPatternForOfferedDayPattern(IDBOfferedDayPattern rawOffered) {
		DBOfferedDayPattern offered = (DBOfferedDayPattern)rawOffered;
		return findDayPatternByID(offered.dayPatternID);
	}

	public IDBDayPattern findDayPatternByID(int dayPatternID) {
		return new DBDayPattern(dayPatternID);
	}

	@Override
	public void deleteOfferedDayPattern(IDBOfferedDayPattern offered) {
		offeredDayPatternTable.deleteByID(offered.getID());
		((DBOfferedDayPattern)offered).id = -1;
	}
}
