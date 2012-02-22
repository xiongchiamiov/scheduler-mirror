package edu.calpoly.csc.scheduler.model.db;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.db.simple.DBProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.simple.DBUsedEquipment;

public interface IDatabase {
	public class NotFoundException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	// Users
	IDBUser findUserByUsername(String username) throws NotFoundException;
	IDBUser assembleUser(String username, boolean isAdmin);
	void insertUser(IDBUser user);
	void updateUser(IDBUser user);
	void deleteUser(IDBUser user);
	
	// Documents
	Collection<IDBDocument> findAllDocuments();
	IDBDocument findDocumentByID(int id) throws NotFoundException;
	void insertDocument(IDBDocument document);
	IDBDocument assembleDocument(String name, int startHalfHour, int endHalfHour);
	void updateDocument(IDBDocument document);
	void deleteDocument(IDBDocument document);
	IDBDocument findDocumentForSchedule(IDBSchedule schedule) throws NotFoundException;
	
	// Working Copy
	boolean isOriginalDocument(IDBDocument doc);
	boolean documentIsWorkingCopy(IDBDocument document);
	IDBDocument getOriginalForWorkingCopyDocument(IDBDocument rawDocument) throws NotFoundException;
	IDBDocument getWorkingCopyForOriginalDocumentOrNull(IDBDocument document);
	void associateWorkingCopyWithOriginal(IDBDocument underlyingDocument, IDBDocument underlyingDocument2);
	void disassociateWorkingCopyWithOriginal(IDBDocument underlyingDocument, IDBDocument underlyingDocument2);

	// Schedules
	Collection<IDBSchedule> findAllSchedulesForDocument(IDBDocument document);
	IDBSchedule findScheduleByID(int id) throws NotFoundException;
	IDBSchedule assembleSchedule(IDBDocument containingDocument);
	void insertSchedule(IDBSchedule schedule);
	void updateSchedule(IDBSchedule schedule);
	void deleteSchedule(IDBSchedule schedule);
	
	// Schedule Items
	Collection<IDBScheduleItem> findScheduleItemsBySchedule(IDBSchedule schedule);
	Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(IDBSchedule schedule);
	IDBScheduleItem findScheduleItemByID(int id) throws NotFoundException;
	IDBScheduleItem assembleScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced, boolean isConflicted);
	void insertScheduleItem(IDBScheduleItem item);
	void updateScheduleItem(IDBScheduleItem schedule);
	void deleteScheduleItem(IDBScheduleItem schedule);
	IDBLocation getScheduleItemLocation(IDBScheduleItem item);
	IDBCourse getScheduleItemCourse(IDBScheduleItem item);
	IDBInstructor getScheduleItemInstructor(IDBScheduleItem item);
	void setScheduleItemCourse(IDBScheduleItem underlying, IDBCourse findCourseByID);
	void setScheduleItemLocation(IDBScheduleItem underlying, IDBLocation findLocationByID);
	void setScheduleItemInstructor(IDBScheduleItem underlying, IDBInstructor findInstructorByID);
	
	// Locations
	Collection<IDBLocation> findLocationsForDocument(IDBDocument document);
	IDBLocation findLocationByID(int id) throws NotFoundException;
	IDBLocation assembleLocation(IDBDocument containingDocument, String room,
			String type, String maxOccupancy);
	void insertLocation(IDBLocation location);
	void updateLocation(IDBLocation location);
	void deleteLocation(IDBLocation location);
	
	// Courses
	Collection<IDBCourse> findCoursesForDocument(IDBDocument document);
	IDBCourse findCourseByID(int id) throws NotFoundException;
	IDBCourse assembleCourse(IDBDocument underlyingDocument, String name,
			String catalogNumber, String department, String wtu, String scu,
			String numSections, String type, String maxEnrollment,
			String numHalfHoursPerWeek, boolean isSchedulable);
	void insertCourse(IDBCourse course);
	void updateCourse(IDBCourse course);
	void deleteCourse(IDBCourse course);
	IDBDocument findDocumentForCourse(IDBCourse underlyingCourse);

	// Tethering
	IDBCourseAssociation getAssociationForLabOrNull(IDBCourse underlying);
	Collection<IDBCourseAssociation> getAssociationsForLecture(IDBCourse lectureCourse);
	IDBCourse getAssociationLecture(IDBCourseAssociation association);
	IDBCourse getAssociationLab(IDBCourseAssociation association);
	void associateLectureAndLab(IDBCourse lecture, IDBCourse lab);

	// Instructors
	Collection<IDBInstructor> findInstructorsForDocument(IDBDocument document);
	IDBInstructor findInstructorByID(int id) throws NotFoundException;
	IDBInstructor assembleInstructor(IDBDocument containingDocument, String firstName, String lastName, String username, String maxWTU);
	void insertInstructor(IDBInstructor instructor);
	void updateInstructor(IDBInstructor instructor);
	void deleteInstructor(IDBInstructor instructor);
	
	// Time Preferences
	Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(IDBInstructor instructor);
	IDBTimePreference findTimePreferenceByID(int id) throws NotFoundException;
	IDBTimePreference findTimePreferenceForInstructorAndTime(IDBInstructor instructor, IDBTime time) throws NotFoundException;
	IDBTimePreference assembleTimePreference(IDBInstructor ins, IDBTime time, int preference);
	void insertTimePreference(IDBTimePreference timePreference);
	void updateTimePreference(IDBTimePreference timePreference);
	void deleteTimePreference(IDBTimePreference timePreference);

	// Course Preferences
	Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(IDBInstructor instructor);
	IDBCoursePreference findCoursePreferenceByID(int id) throws NotFoundException;
	IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(IDBInstructor instructor, IDBCourse course) throws NotFoundException;
	IDBCoursePreference assembleCoursePreference(IDBInstructor instructor, IDBCourse course, int preference);
	void insertCoursePreference(IDBCoursePreference coursePreference);
	void updateCoursePreference(IDBCoursePreference coursePreference);
	void deleteCoursePreference(IDBCoursePreference coursePreference);
	
	// Time
	IDBTime findTimeByDayAndHalfHour(int day, int halfHour);
	
	// Equipment Types
	IDBEquipmentType findEquipmentTypeByDescription(String equipmentTypeDescription) throws NotFoundException;
	Collection<IDBEquipmentType> findAllEquipmentTypes();
	
	// Used Equipment
	Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(IDBCourse course);
	void deleteUsedEquipment(IDBUsedEquipment usedEquipment);
	DBUsedEquipment assembleUsedEquipment(IDBCourse course, IDBEquipmentType equipmentType);
	void insertUsedEquipment(IDBUsedEquipment equip);
	
	// Provided Equipment
	Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(IDBLocation location);
	void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment);
	IDBProvidedEquipment assembleProvidedEquipment(IDBLocation location, IDBEquipmentType equipmentType);
	void insertProvidedEquipment(IDBProvidedEquipment equip);
	
	// Day Patterns
	IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern) throws NotFoundException;
	Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(IDBCourse underlying);
	IDBDayPattern getDayPatternForOfferedDayPattern(IDBOfferedDayPattern offered);
	void deleteOfferedDayPattern(IDBOfferedDayPattern offered);
	IDBOfferedDayPattern assembleOfferedDayPattern(IDBCourse underlying, IDBDayPattern dayPattern);
	void insertOfferedDayPattern(IDBOfferedDayPattern pattern);
	
	// For testing
	boolean isEmpty();
	
	IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying);
	IDBSchedule getScheduleItemSchedule(IDBScheduleItem underlying) throws NotFoundException;
}
