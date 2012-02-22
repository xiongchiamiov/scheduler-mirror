package edu.calpoly.csc.scheduler.model.db.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Schedule;
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
		
		// Returns the new ID
		int insert(T newObject) {
			assert(newObject.id == null);
			
			newObject.id = generateUnusedID();
			
			objectsByID.put(newObject.id, newObject);

			System.out.println("Inserted " + newObject.getClass().getName() + " id " + newObject.id);
			
			return newObject.id;
		}
		
		T findByID(Integer id) throws NotFoundException {
			assert(id != null);
			System.out.println("Finding " + id);
			T result = objectsByID.get(id);
			if (result == null) {
				System.out.println("Couldn't find id " + id);
				throw new NotFoundException();
			}
			return result;
		}
		
		Collection<T> getAll() {
			return new ArrayList<T>(objectsByID.values());
		}
		
		void deleteByID(int id) {
			assert(objectsByID.containsKey(id));
			String className = objectsByID.get(id).getClass().getName();
			objectsByID.remove(id);
			System.out.println("Removed " + className + " id " + id);
		}
	
		public void update(T object) {
			assert(objectsByID.containsKey(object.id));
			objectsByID.put(object.id, object);
			System.out.println("Updated " + object.getClass().getName() + " id " + object.id);
		}

		public boolean isEmpty() { return objectsByID.isEmpty(); }
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
	public IDBUser assembleUser(String username, boolean isAdmin) {
		return new DBUser(null, username, isAdmin);
	}

	@Override
	public void insertUser(IDBUser rawUser) {
		DBUser user = (DBUser)rawUser;
		assert(user.id == null);
		user.id = userTable.insert(new DBUser(user));
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
	public IDBDocument assembleDocument(String name, int startHalfHour, int endHalfHour) {
		return new DBDocument(null, name, null, startHalfHour, endHalfHour);
	}

	@Override
	public void insertDocument(IDBDocument rawDocument) {
		DBDocument document = (DBDocument)rawDocument;
		assert(document.id == null);
		document.id = documentTable.insert(new DBDocument(document));
	}

	@Override
	public void updateDocument(IDBDocument document) {
		documentTable.update(new DBDocument((DBDocument)document));
	}

	@Override
	public void deleteDocument(IDBDocument document) {
		for (IDBSchedule schedule : this.findAllSchedulesForDocument(document))
			this.deleteSchedule(schedule);
		for (IDBInstructor instructor : this.findInstructorsForDocument(document))
			this.deleteInstructor(instructor);
		for (IDBCourse course : this.findCoursesForDocument(document))
			this.deleteCourse(course);
		for (IDBLocation location : this.findLocationsForDocument(document))
			this.deleteLocation(location);
		
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
	public IDBSchedule assembleSchedule(IDBDocument containingDocument) {
		return new DBSchedule(null, containingDocument.getID());
	}

	@Override
	public void insertSchedule(IDBSchedule rawSchedule) {
		DBSchedule schedule = (DBSchedule)rawSchedule;
		assert(schedule.id == null);
		schedule.id = scheduleTable.insert(new DBSchedule(schedule));
	}

	@Override
	public void updateSchedule(IDBSchedule schedule) {
		scheduleTable.update(new DBSchedule((DBSchedule)schedule));
	}

	@Override
	public void deleteSchedule(IDBSchedule schedule) {
		for (IDBScheduleItem item : this.findAllScheduleItemsForSchedule(schedule))
			this.deleteScheduleItem(item);
		
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
	public IDBScheduleItem assembleScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced, boolean isConflicted) {
		return new DBScheduleItem(null, schedule.getID(), course.getID(), instructor.getID(), location.getID(), section, days, startHalfHour, endHalfHour, isPlaced, isConflicted);
	}

	@Override
	public IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying) {
		return (IDBScheduleItem) ((DBScheduleItem)underlying).clone();
	}
	
	@Override
	public void insertScheduleItem(IDBScheduleItem rawItem) {
		DBScheduleItem item = (DBScheduleItem)rawItem;
		assert(item.id == null);
		item.id = scheduleItemTable.insert(new DBScheduleItem(item));
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
	public IDBLocation assembleLocation(IDBDocument containingDocument, String room, String type, String maxOccupancy) {
		return new DBLocation(null, containingDocument.getID(), room, type, maxOccupancy);
	}

	@Override
	public void insertLocation(IDBLocation rawLocation) {
		DBLocation location = (DBLocation)rawLocation;
		assert(location.id == null);
		location.id = locationTable.insert(new DBLocation(new DBLocation(location)));
	}

	@Override
	public void updateLocation(IDBLocation location) {
		locationTable.update(new DBLocation((DBLocation)location));
	}

	@Override
	public void deleteLocation(IDBLocation location) {
		for (IDBProvidedEquipment equip : this.findProvidedEquipmentByEquipmentForLocation(location).values())
			this.deleteProvidedEquipment(equip);
		
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
	public IDBDocument findDocumentForCourse(IDBCourse underlyingCourse) {
		try {
			return documentTable.findByID(((DBCourse)underlyingCourse).documentID);
		}
		catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public IDBCourse findCourseByID(int id) throws NotFoundException {
		return new DBCourse(courseTable.findByID(id));
	}

	@Override
	public void insertCourse(IDBCourse rawCourse) {
		DBCourse course = (DBCourse)rawCourse;
		assert(course.id == null);
		course.id = courseTable.insert(new DBCourse(new DBCourse(course)));
	}

	@Override
	public void updateCourse(IDBCourse course) {
		courseTable.update(new DBCourse((DBCourse)course));
	}

	@Override
	public void deleteCourse(IDBCourse course) {
		for (IDBUsedEquipment equip : this.findUsedEquipmentByEquipmentForCourse(course).values())
			this.deleteUsedEquipment(equip);
		for (IDBOfferedDayPattern pat : this.findOfferedDayPatternsForCourse(course))
			this.deleteOfferedDayPattern(pat);

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
	public IDBInstructor assembleInstructor(IDBDocument containingDocument, String firstName, String lastName,
			String username, String maxWTU) {
		return new DBInstructor(null, containingDocument.getID(), firstName, lastName, username, maxWTU);
	}

	@Override
	public void insertInstructor(IDBInstructor rawInstructor) {
		DBInstructor instructor = (DBInstructor)rawInstructor;
		assert(instructor.id == null);
		instructor.id = instructorTable.insert(new DBInstructor(instructor));
	}

	@Override
	public void updateInstructor(IDBInstructor instructor) {
		instructorTable.update(new DBInstructor((DBInstructor)instructor));
	}

	@Override
	public void deleteInstructor(IDBInstructor instructor) {
		for (IDBTimePreference timePref : this.findTimePreferencesByTimeForInstructor(instructor).values())
			this.deleteTimePreference(timePref);
		for (IDBCoursePreference coursePref : this.findCoursePreferencesByCourseForInstructor(instructor).values())
			this.deleteCoursePreference(coursePref);
		
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
	public IDBTimePreference findTimePreferenceForInstructorAndTime(IDBInstructor instructor, IDBTime time) throws NotFoundException {
		for (DBTimePreference timePref : timePreferenceTable.getAll())
			if (timePref.instructorID == instructor.getID() && sameTime(time, findTimeByID(timePref.timeID)))
				return timePref;
		throw new NotFoundException();
	}

	@Override
	public IDBTimePreference assembleTimePreference(IDBInstructor instructor, IDBTime time, int preference) {
		return new DBTimePreference(null, instructor.getID(), time.getID(), preference);
	}

	@Override
	public void insertTimePreference(IDBTimePreference rawTimePreference) {
		DBTimePreference timePreference = (DBTimePreference)rawTimePreference;
		assert(timePreference.id == null);
		timePreference.id = timePreferenceTable.insert(new DBTimePreference(timePreference));
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
		try {
			Map<IDBCourse, IDBCoursePreference> result = new HashMap<IDBCourse, IDBCoursePreference>();
			for (DBCoursePreference coursePref : coursePreferenceTable.getAll()) {
				if (coursePref.instructorID == instructor.getID()) {
					result.put(findCourseByID(coursePref.courseID), coursePref);
				}
			}
			return result;
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
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
	public IDBCoursePreference assembleCoursePreference(IDBInstructor instructor, IDBCourse course, int preference) {
		return new DBCoursePreference(null, instructor.getID(), course.getID(), preference);
	}

	@Override
	public void insertCoursePreference(IDBCoursePreference rawCoursePreference) {
		DBCoursePreference coursePreference = (DBCoursePreference)rawCoursePreference;
		assert(coursePreference.id == null);
		coursePreference.id = coursePreferenceTable.insert(new DBCoursePreference(coursePreference));
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
	public IDBDocument getWorkingCopyForOriginalDocumentOrNull(IDBDocument rawDocument) {
		assert(!documentIsWorkingCopy(rawDocument));
		DBDocument document = (DBDocument)rawDocument;
		
		for (DBDocument workingCopy : this.documentTable.getAll())
			if (documentIsWorkingCopy(workingCopy))
				if (workingCopy.originalID == document.id)
					return workingCopy;
		
		return null;
	}

	@Override
	public IDBDocument getOriginalForWorkingCopyDocument(IDBDocument rawDocument) throws NotFoundException {
		assert(documentTable != null);
		assert(rawDocument != null);
		assert(!documentIsWorkingCopy(rawDocument));
		DBDocument document = (DBDocument)rawDocument;
		
		assert(document.originalID != null);
		
		System.out.println("doc table " + documentTable + " doc " + document);
		return documentTable.findByID(document.originalID);
	}

	@Override
	public void associateLectureAndLab(IDBCourse rawLecture, IDBCourse rawLab) {
		DBCourse lab = (DBCourse)rawLab;
		DBCourse lecture = (DBCourse)rawLecture;
		
		assert(lab.lectureID == null);
		lab.lectureID = lecture.id;
	}
	
	@Override
	public IDBCourseAssociation getAssociationForLabOrNull(IDBCourse rawLabCourse) {
		DBCourse labCourse = (DBCourse)rawLabCourse;
		if (labCourse.lectureID == null)
			return null;
		return new DBCourseAssociation(labCourse.id, labCourse.lectureID, labCourse.tetheredToLecture);
	}
	
	@Override
	public IDBCourse getAssociationLab(IDBCourseAssociation rawAssoc) {
		DBCourseAssociation assoc = (DBCourseAssociation)rawAssoc;
		try {
			return findCourseByID(assoc.getLabID());
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public IDBCourse getAssociationLecture(IDBCourseAssociation rawAssoc) {
		DBCourseAssociation assoc = (DBCourseAssociation)rawAssoc;
		try {
			return findCourseByID(assoc.lectureID);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	@Override
	public Collection<IDBCourseAssociation> getAssociationsForLecture(IDBCourse rawLectureCourse) {
		DBCourse lectureCourse = (DBCourse)rawLectureCourse;
		assert(lectureCourse.lectureID == null);
		
		Collection<IDBCourseAssociation> result = new LinkedList<IDBCourseAssociation>();
		for (DBCourse labCourse : this.courseTable.getAll())
			if (labCourse.lectureID.equals(lectureCourse.id))
				result.add(new DBCourseAssociation(labCourse.id, lectureCourse.id, labCourse.tetheredToLecture));
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
		try {
			Map<IDBEquipmentType, IDBUsedEquipment> result = new HashMap<IDBEquipmentType, IDBUsedEquipment>();
			for (DBUsedEquipment used : usedEquipmentTable.getAll())
				if (used.courseID == course.getID())
					result.put(equipmentTypeTable.findByID(used.equipmentTypeID), used);
			return result;
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
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
	public DBUsedEquipment assembleUsedEquipment(IDBCourse course, IDBEquipmentType equipmentType) {
		return new DBUsedEquipment(null, course.getID(), equipmentType.getID());
	}
	
	@Override
	public void insertUsedEquipment(IDBUsedEquipment rawEquip) {
		DBUsedEquipment equip = (DBUsedEquipment)rawEquip;
		assert(equip.id == null);
		equip.id = usedEquipmentTable.insert(new DBUsedEquipment(equip));
	}

	@Override
	public Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(IDBLocation location) {
		try {
			Map<IDBEquipmentType, IDBProvidedEquipment> result = new HashMap<IDBEquipmentType, IDBProvidedEquipment>();
			for (DBProvidedEquipment provided : providedEquipmentTable.getAll())
				if (provided.locationID == location.getID())
					result.put(equipmentTypeTable.findByID(provided.equipmentTypeID), provided);
			return result;
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment) {
		providedEquipmentTable.deleteByID(providedEquipment.getID());
		DBProvidedEquipment derp = (DBProvidedEquipment)providedEquipment;
		derp.id = null;
	}

	@Override
	public IDBProvidedEquipment assembleProvidedEquipment(IDBLocation location, IDBEquipmentType equipmentType) {
		return new DBProvidedEquipment(null, location.getID(), equipmentType.getID());
	}
	
	@Override
	public void insertProvidedEquipment(IDBProvidedEquipment rawEquip) {
		DBProvidedEquipment equip = (DBProvidedEquipment)rawEquip;
		assert(equip.id == null);
		equip.id = providedEquipmentTable.insert(new DBProvidedEquipment(equip));
	}

	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern) throws NotFoundException {
		return new DBDayPattern(dayPattern);
	}

	@Override
	public IDBOfferedDayPattern assembleOfferedDayPattern(IDBCourse underlying, IDBDayPattern dayPattern) {
		return new DBOfferedDayPattern(null, underlying.getID(), dayPattern.getID());
	}
	
	@Override
	public void insertOfferedDayPattern(IDBOfferedDayPattern rawPattern) {
		DBOfferedDayPattern pattern = (DBOfferedDayPattern)rawPattern;
		assert(pattern.id == null);
		offeredDayPatternTable.insert(new DBOfferedDayPattern(pattern));
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
		((DBOfferedDayPattern)offered).id = null;
	}

	@Override
	public void associateWorkingCopyWithOriginal(IDBDocument rawWorkingCopy, IDBDocument rawOriginal) {
		DBDocument workingCopy = (DBDocument)rawWorkingCopy;
		DBDocument original = (DBDocument)rawOriginal;
		
		assert(workingCopy.originalID == null);
		workingCopy.originalID = original.id;
		System.out.println("set doc id " + workingCopy.id + " originalID to " + original.id);
	}

	@Override
	public void disassociateWorkingCopyWithOriginal(IDBDocument rawWorkingCopy, IDBDocument rawOriginal) {
		DBDocument workingCopy = (DBDocument)rawWorkingCopy;
		DBDocument original = (DBDocument)rawOriginal;
		
		assert(workingCopy.originalID == original.id);
		workingCopy.originalID = null;
	}

	@Override
	public boolean isOriginalDocument(IDBDocument doc) {
		return ((DBDocument)doc).originalID == null;
	}

	@Override
	public IDBDocument findDocumentForSchedule(IDBSchedule schedule) throws NotFoundException {
		return documentTable.findByID(((DBSchedule)schedule).documentID);
	}

	@Override
	public void setScheduleItemCourse(IDBScheduleItem rawItem, IDBCourse course) {
		((DBScheduleItem)rawItem).courseID = course.getID();
	}

	@Override
	public void setScheduleItemLocation(IDBScheduleItem rawItem, IDBLocation location) {
		((DBScheduleItem)rawItem).locationID = location.getID();
	}

	@Override
	public void setScheduleItemInstructor(IDBScheduleItem rawItem, IDBInstructor instructor) {
		((DBScheduleItem)rawItem).courseID = instructor.getID();
	}

	@Override
	public IDBCourse assembleCourse(IDBDocument containingDocument, String name,
			String catalogNumber, String department, String wtu, String scu,
			String numSections, String type, String maxEnrollment,
			String numHalfHoursPerWeek, boolean isSchedulable) {
		return new DBCourse(null, containingDocument.getID(), name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek, isSchedulable);
	}

	@Override
	public boolean isEmpty() {
		return userTable.isEmpty()
				&& documentTable.isEmpty()
				&& scheduleTable.isEmpty()
				&& courseTable.isEmpty()
				&& locationTable.isEmpty()
				&& instructorTable.isEmpty()
				&& timePreferenceTable.isEmpty()
				&& coursePreferenceTable.isEmpty()
				&& equipmentTypeTable.isEmpty()
				&& providedEquipmentTable.isEmpty()
				&& usedEquipmentTable.isEmpty()
				&& offeredDayPatternTable.isEmpty();
	}

	@Override
	public Collection<IDBEquipmentType> findAllEquipmentTypes() {
		Collection<IDBEquipmentType> result = new LinkedList<IDBEquipmentType>();
		for (DBEquipmentType derp : equipmentTypeTable.getAll())
			result.add(derp);
		return result;
	}

}
