package edu.calpoly.csc.scheduler.model.db;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.simple.DBProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.simple.DBUsedEquipment;

public interface IDatabase {
	public class NotFoundException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	IDBUser findUserByUsername(String username) throws NotFoundException;
	String generateUnusedUsername();
	IDBUser insertUser(String username, boolean isAdmin);
	void updateUser(IDBUser user);
	void deleteUser(IDBUser user);
	
	Collection<IDBDocument> findAllDocuments();
	IDBDocument findDocumentByID(int id) throws NotFoundException;
	IDBDocument insertDocument(String name);
	void updateDocument(IDBDocument document);
	void deleteDocument(IDBDocument document);
	boolean documentIsWorkingCopy(IDBDocument document);
	IDBDocument getWorkingCopyForDocument(IDBDocument rawDocument) throws NotFoundException;
	IDBDocument getOriginalForDocument(IDBDocument document);

	Collection<IDBSchedule> findAllSchedulesForDocument(IDBDocument document);
	IDBSchedule findScheduleByID(int id) throws NotFoundException;
	IDBSchedule insertSchedule(IDBDocument containingDocument);
	void updateSchedule(IDBSchedule schedule);
	void deleteSchedule(IDBSchedule schedule);
	
	Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(IDBSchedule schedule);
	IDBScheduleItem findScheduleItemByID(int id) throws NotFoundException;
	IDBScheduleItem insertScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, int section);
	void updateScheduleItem(IDBScheduleItem schedule);
	void deleteScheduleItem(IDBScheduleItem schedule);
	
	Collection<IDBLocation> findLocationsForDocument(IDBDocument document);
	IDBLocation findLocationByID(int id) throws NotFoundException;
	IDBLocation insertLocation(IDBDocument containingDocument, String room,
			String type, String maxOccupancy);
	void updateLocation(IDBLocation location);
	void deleteLocation(IDBLocation location);
	
	Collection<IDBCourse> findCoursesForDocument(IDBDocument document);
	IDBCourse findCourseByID(int id) throws NotFoundException;
	IDBCourse insertCourse(IDBDocument containingDocument, String name, String catalogNumber, String department, String wtu, String scu, String numSections, String type, String maxEnrollment, String numHalfHoursPerWeek);
	void updateCourse(IDBCourse course);
	void deleteCourse(IDBCourse course);
	boolean labCourseIsTethered(IDBCourse labCourse);
	IDBCourse getLectureTetheredToLabCourse(IDBCourse labCourse) throws NotFoundException;
	Collection<IDBCourse> getLabsTetheredToLectureCourse(IDBCourse lectureCourse);

	Collection<IDBInstructor> findInstructorsForDocument(IDBDocument document);
	IDBInstructor findInstructorByID(int id) throws NotFoundException;
	IDBInstructor insertInstructor(IDBDocument containingDocument, String firstName, String lastName, String username, String maxWTU);
	void updateInstructor(IDBInstructor instructor);
	void deleteInstructor(IDBInstructor instructor);
	
	Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(IDBInstructor instructor);
	IDBTimePreference findTimePreferenceByID(int id) throws NotFoundException;
	IDBTimePreference findTimePreferenceForInstructorAndDayAndTime(IDBInstructor instructor, IDBTime time) throws NotFoundException;
	IDBTimePreference insertTimePreference(IDBInstructor ins, IDBTime time, int preference);
	void updateTimePreference(IDBTimePreference timePreference);
	void deleteTimePreference(IDBTimePreference timePreference);

	Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(IDBInstructor instructor);
	IDBCoursePreference findCoursePreferenceByID(int id) throws NotFoundException;
	IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(IDBInstructor instructor, IDBCourse course) throws NotFoundException;
	IDBCoursePreference insertCoursePreference(IDBInstructor instructor, IDBCourse course, int preference);
	void updateCoursePreference(IDBCoursePreference coursePreference);
	void deleteCoursePreference(IDBCoursePreference coursePreference);
	
	// Time
	IDBTime findTimeByDayAndHalfHour(int day, int halfHour);
	
	// Schedule Items
	IDBLocation getScheduleItemLocation(IDBScheduleItem item);
	IDBCourse getScheduleItemCourse(IDBScheduleItem item);
	IDBInstructor getScheduleItemInstructor(IDBScheduleItem item);
	Collection<IDBScheduleItem> findScheduleItemsBySchedule(IDBSchedule schedule);
	
	// Equipment Types
	IDBEquipmentType findEquipmentTypeByDescription(String equipmentTypeDescription) throws NotFoundException;
	
	// Used Equipment
	Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(IDBCourse course);
	void deleteUsedEquipment(IDBUsedEquipment usedEquipment);
	DBUsedEquipment insertUsedEquipment(IDBCourse course, IDBEquipmentType equipmentType);
	
	// Provided Equipment
	Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(IDBLocation location);
	void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment);
	DBProvidedEquipment insertProvidedEquipment(IDBLocation location, IDBEquipmentType findEquipmentTypeByDescription);
	
	// Day Patterns
	IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern) throws NotFoundException;
	IDBOfferedDayPattern insertOfferedDayPattern(IDBCourse underlying, IDBDayPattern findDayPatternByDays);
	Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(IDBCourse underlying);
	IDBDayPattern getDayPatternForOfferedDayPattern(IDBOfferedDayPattern offered);
	void deleteOfferedDayPattern(IDBOfferedDayPattern offered);
}
