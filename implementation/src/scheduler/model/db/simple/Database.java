package scheduler.model.db.simple;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import scheduler.model.Day;
import scheduler.model.db.IDBCourse;
import scheduler.model.db.IDBCourseAssociation;
import scheduler.model.db.IDBCoursePreference;
import scheduler.model.db.IDBDayPattern;
import scheduler.model.db.IDBDocument;
import scheduler.model.db.IDBEquipmentType;
import scheduler.model.db.IDBInstructor;
import scheduler.model.db.IDBLocation;
import scheduler.model.db.IDBObject;
import scheduler.model.db.IDBOfferedDayPattern;
import scheduler.model.db.IDBProvidedEquipment;
import scheduler.model.db.IDBSchedule;
import scheduler.model.db.IDBScheduleItem;
import scheduler.model.db.IDBTime;
import scheduler.model.db.IDBTimePreference;
import scheduler.model.db.IDBUsedEquipment;
import scheduler.model.db.IDBUser;
import scheduler.model.db.IDatabase;

public class Database implements IDatabase {
	static class SimpleTable<T extends DBObject> implements Serializable {
		private static final long serialVersionUID = 1337L;
		
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
			
//			newObject.sanityCheck();
			
			newObject.id = generateUnusedID();
			
			objectsByID.put(newObject.id, newObject);

			System.out.println("Inserted " + newObject.getClass().getName() + " id " + newObject.id);
			
			return newObject.id;
		}
		
		T findByID(Integer id) throws NotFoundException {
			assert(id != null);
			T result = objectsByID.get(id);
			if (result == null) {
				System.out.println("Couldn't find id " + id);
				throw new NotFoundException();
			}

//			result.sanityCheck();
			return result;
		}
		
		Collection<T> getAll() {
			return new ArrayList<T>(objectsByID.values());
		}
		
		void deleteByID(int id) {
			assert(objectsByID.containsKey(id));
			String className = objectsByID.get(id).getClass().getName();
			objectsByID.remove(id);
		}
	
		public void update(T object) {
//			object.sanityCheck();
			assert(objectsByID.containsKey(object.id));
			objectsByID.put(object.id, object);
		}

		public boolean isEmpty() { return objectsByID.isEmpty(); }

		public boolean contains(T raw) {
			return objectsByID.containsKey(raw.getID());
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
		return new DBDocument(null, name, null, startHalfHour, endHalfHour, null, null, false);
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
		assert(findAllSchedulesForDocument(document).isEmpty());
		assert(findCoursesForDocument(document).isEmpty());
		assert(findInstructorsForDocument(document).isEmpty());
		assert(findLocationsForDocument(document).isEmpty());
		
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
	public IDBSchedule assembleSchedule() {
		return new DBSchedule(null, null);
	}

	@Override
	public void insertSchedule(IDBDocument document, IDBSchedule rawSchedule) {
		DBSchedule schedule = (DBSchedule)rawSchedule;
		assert(schedule.id == null);
		schedule.documentID = document.getID();
		schedule.id = scheduleTable.insert(new DBSchedule(schedule));
	}

	@Override
	public void updateSchedule(IDBSchedule schedule) {
		scheduleTable.update(new DBSchedule((DBSchedule)schedule));
	}

	@Override
	public void deleteSchedule(IDBSchedule schedule) {
		assert(findAllScheduleItemsForSchedule(schedule).isEmpty());
		
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
	public IDBScheduleItem assembleScheduleItem(int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced, boolean isConflicted) {
		return new DBScheduleItem(null, null, null, null, null, section, days, startHalfHour, endHalfHour, isPlaced, isConflicted, null);
	}

	@Override
	public IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying) {
		return new DBScheduleItem((DBScheduleItem)underlying);
	}
	
	@Override
	public void insertScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, IDBScheduleItem rawItem) {
		DBScheduleItem item = (DBScheduleItem)rawItem;
		assert(item.id == null);
		item.scheduleID = schedule.getID();
		item.courseID = course.getID();
		item.instructorID = instructor.getID();
		item.locationID = location.getID();
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
	public IDBLocation assembleLocation(String room, String type, String maxOccupancy, boolean isSchedulable) {
		return new DBLocation(null, null, room, type, maxOccupancy, isSchedulable);
	}

	@Override
	public void insertLocation(IDBDocument containingDocument, IDBLocation rawLocation) {
		DBLocation location = (DBLocation)rawLocation;
		assert(location.id == null);
		location.documentID = containingDocument.getID();
		location.id = locationTable.insert(new DBLocation(new DBLocation(location)));
	}

	@Override
	public void updateLocation(IDBLocation location) {
		locationTable.update(new DBLocation((DBLocation)location));
	}

	@Override
	public void deleteLocation(IDBLocation location) {
		assert(findProvidedEquipmentByEquipmentForLocation(location).isEmpty());
		
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
	public IDBCourse assembleCourse(String name,
			String catalogNumber, String department, String wtu, String scu,
			String numSections, String type, String maxEnrollment,
			String numHalfHoursPerWeek, boolean isSchedulable) {
		return new DBCourse(null, null, name, catalogNumber, department, wtu, scu, numSections, type, maxEnrollment, numHalfHoursPerWeek, isSchedulable, null, false);
	}

	@Override
	public void insertCourse(IDBDocument containingDocument, IDBCourse rawCourse) {
		DBCourse course = (DBCourse)rawCourse;
		assert(course.id == null);
		course.documentID = containingDocument.getID();
		course.id = courseTable.insert(new DBCourse(course));
	}

	@Override
	public void updateCourse(IDBCourse course) {
		courseTable.update(new DBCourse((DBCourse)course));
	}

	@Override
	public void deleteCourse(IDBCourse course) {
		assert(findUsedEquipmentByEquipmentForCourse(course).isEmpty());
		assert(findOfferedDayPatternsForCourse(course).isEmpty());

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
	public IDBInstructor assembleInstructor(String firstName, String lastName,
			String username, String maxWTU, boolean isSchedulable) {
		return new DBInstructor(null, null, firstName, lastName, username, maxWTU, isSchedulable);
	}

	@Override
	public void insertInstructor(IDBDocument containingDocument, IDBInstructor rawInstructor) {
		DBInstructor instructor = (DBInstructor)rawInstructor;
		assert(instructor.id == null);
		instructor.documentID = containingDocument.getID();
		instructor.id = instructorTable.insert(new DBInstructor(instructor));
	}

	@Override
	public void updateInstructor(IDBInstructor instructor) {
		instructorTable.update(new DBInstructor((DBInstructor)instructor));
	}

	@Override
	public void deleteInstructor(IDBInstructor instructor) {
		assert(findTimePreferencesByTimeForInstructor(instructor).isEmpty());
		assert(findCoursePreferencesByCourseForInstructor(instructor).isEmpty());
		
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
//	
//	@Override
//	public IDBTimePreference findTimePreferenceForInstructorAndTime(IDBInstructor instructor, IDBTime time) throws NotFoundException {
//		for (DBTimePreference timePref : timePreferenceTable.getAll())
//			if (timePref.instructorID == instructor.getID() && sameTime(time, findTimeByID(timePref.timeID)))
//				return timePref;
//		throw new NotFoundException();
//	}

	@Override
	public IDBTimePreference assembleTimePreference(int preference) {
		return new DBTimePreference(null, null, null, preference);
	}

	@Override
	public void insertTimePreference(IDBInstructor instructor, IDBTime time, IDBTimePreference rawTimePreference) {
		DBTimePreference timePreference = (DBTimePreference)rawTimePreference;
		assert(timePreference.id == null);
		timePreference.instructorID = instructor.getID();
		timePreference.timeID = time.getID();
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
//
//	@Override
//	public IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(
//			IDBInstructor instructor, IDBCourse course) throws NotFoundException {
//		for (DBCoursePreference coursePref : coursePreferenceTable.getAll())
//			if (coursePref.instructorID == instructor.getID() && coursePref.courseID == course.getID())
//				return coursePref;
//		throw new NotFoundException();
//	}

	@Override
	public IDBCoursePreference assembleCoursePreference(int preference) {
		return new DBCoursePreference(null, null, null, preference);
	}

	@Override
	public void insertCoursePreference(IDBInstructor instructor, IDBCourse course, IDBCoursePreference rawCoursePreference) {
		DBCoursePreference coursePreference = (DBCoursePreference)rawCoursePreference;
		assert(coursePreference.id == null);
		coursePreference.instructorID = instructor.getID();
		coursePreference.courseID = course.getID();
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
		return document.originalID != null;
	}

	@Override
	public IDBDocument getWorkingCopyForOriginalDocumentOrNull(IDBDocument rawDocument) {
		assert(!documentIsWorkingCopy(rawDocument));
		DBDocument document = (DBDocument)rawDocument;
		
		for (DBDocument workingCopy : this.documentTable.getAll())
			if (documentIsWorkingCopy(workingCopy))
				if (workingCopy.originalID.equals(document.id))
					return workingCopy;
		
		return null;
	}

	@Override
	public IDBDocument getOriginalForWorkingCopyDocumentOrNull(IDBDocument rawDocument) throws NotFoundException {
		assert(documentTable != null);
		assert(rawDocument != null);
		DBDocument document = (DBDocument)rawDocument;
		
		if (document.originalID == null)
			return null;
		
		System.out.println("doc table " + documentTable + " doc " + document);
		return documentTable.findByID(document.originalID);
	}

	@Override
	public void associateLectureAndLab(IDBCourse rawLecture, IDBCourse rawLab) {
		DBCourse lab = (DBCourse)rawLab;
		DBCourse lecture = (DBCourse)rawLecture;
		
		assert(courseTable.contains(lecture));
		assert(courseTable.contains(lab));
		
		assert(lab.lectureID == null);
		lab.lectureID = lecture.id;
		System.out.println("setting association for lab " + rawLab + " to: " + lab.lectureID);
	}
	
	@Override
	public IDBCourseAssociation getAssociationForLabOrNull(IDBCourse rawLabCourse) {
		DBCourse labCourse = (DBCourse)rawLabCourse;
		System.out.println("getting association for lab " + labCourse + ": " + labCourse.lectureID);
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
	public DBUsedEquipment assembleUsedEquipment() {
		return new DBUsedEquipment(null, null, null);
	}
	
	@Override
	public void insertUsedEquipment(IDBCourse course, IDBEquipmentType equipmentType, IDBUsedEquipment rawEquip) {
		DBUsedEquipment equip = (DBUsedEquipment)rawEquip;
		assert(equip.id == null);
		equip.courseID = course.getID();
		equip.equipmentTypeID = equipmentType.getID();
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
	public IDBProvidedEquipment assembleProvidedEquipment() {
		return new DBProvidedEquipment(null, null, null);
	}
	
	@Override
	public void insertProvidedEquipment(IDBLocation location, IDBEquipmentType equipmentType, IDBProvidedEquipment rawEquip) {
		DBProvidedEquipment equip = (DBProvidedEquipment)rawEquip;
		assert(equip.id == null);
		equip.locationID = location.getID();
		equip.equipmentTypeID = equipmentType.getID();
		equip.id = providedEquipmentTable.insert(new DBProvidedEquipment(equip));
	}

	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern) throws NotFoundException {
		return new DBDayPattern(dayPattern);
	}

	@Override
	public IDBOfferedDayPattern assembleOfferedDayPattern() {
		return new DBOfferedDayPattern(null, null, null);
	}
	
	@Override
	public void insertOfferedDayPattern(IDBCourse underlying, IDBDayPattern dayPattern, IDBOfferedDayPattern rawPattern) {
		DBOfferedDayPattern pattern = (DBOfferedDayPattern)rawPattern;
		assert(pattern.id == null);
		pattern.courseID = underlying.getID();
		pattern.dayPatternID = dayPattern.getID();
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

	@Override
	public IDBSchedule getScheduleItemSchedule(IDBScheduleItem underlying) throws NotFoundException {
		return scheduleTable.findByID(((DBScheduleItem)underlying).scheduleID);
	}


	@Override
	public boolean isInserted(IDBScheduleItem underlying) {
		assert(false);
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBObject findDocumentForLocation(IDBLocation underlyingLocation) throws NotFoundException {
		return documentTable.findByID(((DBLocation)underlyingLocation).documentID);
	}


	@Override
	public IDBObject findDocumentForInstructor(IDBInstructor underlyingInstructor) throws NotFoundException {
		return documentTable.findByID(((DBInstructor)underlyingInstructor).documentID);
	}

	@Override
	public void writeState(ObjectOutputStream oos) throws IOException {
		oos.writeObject(userTable);
		oos.writeObject(documentTable);
		oos.writeObject(scheduleTable);
		oos.writeObject(scheduleItemTable);
		oos.writeObject(courseTable);
		oos.writeObject(locationTable);
		oos.writeObject(instructorTable);
		oos.writeObject(timePreferenceTable);
		oos.writeObject(coursePreferenceTable);
		oos.writeObject(equipmentTypeTable);
		oos.writeObject(providedEquipmentTable);
		oos.writeObject(usedEquipmentTable);
		oos.writeObject(offeredDayPatternTable);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void readState(ObjectInputStream ois) throws IOException {
		try {
			userTable = (SimpleTable<DBUser>)ois.readObject();
			documentTable = (SimpleTable<DBDocument>)ois.readObject();
			scheduleTable = (SimpleTable<DBSchedule>)ois.readObject();
			scheduleItemTable = (SimpleTable<DBScheduleItem>)ois.readObject();
			courseTable = (SimpleTable<DBCourse>)ois.readObject();
			locationTable = (SimpleTable<DBLocation>)ois.readObject();
			instructorTable = (SimpleTable<DBInstructor>)ois.readObject();
			timePreferenceTable = (SimpleTable<DBTimePreference>)ois.readObject();
			coursePreferenceTable = (SimpleTable<DBCoursePreference>)ois.readObject();
			equipmentTypeTable = (SimpleTable<DBEquipmentType>)ois.readObject();
			providedEquipmentTable = (SimpleTable<DBProvidedEquipment>)ois.readObject();
			usedEquipmentTable = (SimpleTable<DBUsedEquipment>)ois.readObject();
			offeredDayPatternTable = (SimpleTable<DBOfferedDayPattern>)ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}


	@Override
	public IDBInstructor getDocumentStaffInstructorOrNull(IDBDocument underlyingDocument) throws NotFoundException {
		Integer id = ((DBDocument)underlyingDocument).staffInstructorID;
		if (id == null)
			return null;
		return instructorTable.findByID(id);
	}


	@Override
	public IDBLocation getDocumentTBALocationOrNull(IDBDocument underlyingDocument) throws NotFoundException {
		Integer id = ((DBDocument)underlyingDocument).tbaLocationID;
		if (id == null)
			return null;
		return locationTable.findByID(id);
	}


	@Override
	public void setDocumentStaffInstructor(IDBDocument underlyingDocument, IDBInstructor underlyingInstructor) {
		((DBDocument)underlyingDocument).staffInstructorID = underlyingInstructor.getID();
	}


	@Override
	public void setDocumentTBALocation(IDBDocument underlyingDocument, IDBLocation underlyingLocation) {
		((DBDocument)underlyingDocument).tbaLocationID = underlyingLocation.getID();
	}


	@Override
	public void disassociateLectureAndLab(IDBCourse lecture, IDBCourse lab) {
		((DBCourse)lab).lectureID = null;
		((DBCourse)lab).tetheredToLecture = false;
	}


	@Override
	public Collection<IDBScheduleItem> findAllLabScheduleItemsForScheduleItem(IDBScheduleItem underlying) {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		for (DBScheduleItem item : this.scheduleItemTable.getAll())
			if (item.lectureScheduleItemID != null && item.lectureScheduleItemID.equals(underlying.getID()))
				result.add(item);
		return result;
	}


	@Override
	public void associateScheduleItemLab(IDBScheduleItem lecture, IDBScheduleItem ilab) {
		DBScheduleItem lab = (DBScheduleItem)ilab;
		
		assert(lab.lectureScheduleItemID == null);
		lab.lectureScheduleItemID = lecture.getID();
	}


	@Override
	public void disassociateScheduleItemLab(IDBScheduleItem lecture, IDBScheduleItem ilab) {
		DBScheduleItem lab = (DBScheduleItem)ilab;
		
		assert(lab.lectureScheduleItemID != null);
		lab.lectureScheduleItemID = null;
	}


	@Override
	public IDBScheduleItem getScheduleItemLectureOrNull(IDBScheduleItem ilab) throws NotFoundException {
		DBScheduleItem lab = (DBScheduleItem)ilab;
		
		if (lab.lectureScheduleItemID == null)
			return null;
		else
			return scheduleItemTable.findByID(lab.lectureScheduleItemID);
	}


	@Override
	public void insertEquipmentType(String string) {
		equipmentTypeTable.insert(new DBEquipmentType(null, string));
	}


	@Override
	public IDBDocument findDocumentByName(String scheduleName) {
		for (IDBDocument doc : documentTable.getAll())
			if (doc.getName().equals(scheduleName))
				return doc;
		
		return null;
	}
}
