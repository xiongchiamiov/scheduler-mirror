package edu.calpoly.csc.scheduler.model.db;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.db.simple.DBUsedEquipment;

public interface IDatabase {
	public class NotFoundException extends DatabaseException {
		private static final long serialVersionUID = 1L;
		public NotFoundException(Throwable thr) {
			super(thr);
		}
		public NotFoundException() {
		}
	}
	
	// Users
	IDBUser findUserByUsername(String username) throws DatabaseException;
	IDBUser assembleUser(String username, boolean isAdmin);
	void insertUser(IDBUser user) throws DatabaseException;
	void updateUser(IDBUser user) throws DatabaseException;
	void deleteUser(IDBUser user) throws DatabaseException;
	
	// Documents
	Collection<IDBDocument> findAllDocuments() throws DatabaseException;
	IDBDocument findDocumentByID(int id) throws DatabaseException;
	void insertDocument(IDBDocument document) throws DatabaseException;
	IDBDocument assembleDocument(String name, int startHalfHour, int endHalfHour) throws DatabaseException;
	void updateDocument(IDBDocument document) throws DatabaseException;
	void deleteDocument(IDBDocument document) throws DatabaseException;
	IDBDocument findDocumentForSchedule(IDBSchedule schedule) throws DatabaseException;
	
	// Working Copy
	boolean isOriginalDocument(IDBDocument doc) throws DatabaseException;
	boolean documentIsWorkingCopy(IDBDocument document) throws DatabaseException;
	IDBDocument getOriginalForWorkingCopyDocument(IDBDocument rawDocument) throws DatabaseException;
	IDBDocument getWorkingCopyForOriginalDocumentOrNull(IDBDocument document) throws DatabaseException;
	void associateWorkingCopyWithOriginal(IDBDocument underlyingDocument, IDBDocument underlyingDocument2) throws DatabaseException;
	void disassociateWorkingCopyWithOriginal(IDBDocument underlyingDocument, IDBDocument underlyingDocument2) throws DatabaseException;

	// Schedules
	Collection<IDBSchedule> findAllSchedulesForDocument(IDBDocument document) throws DatabaseException;
	IDBSchedule findScheduleByID(int id) throws DatabaseException;
	IDBSchedule assembleSchedule() throws DatabaseException;
	void insertSchedule(IDBDocument containingDocument, IDBSchedule schedule) throws DatabaseException;
	void updateSchedule(IDBSchedule schedule) throws DatabaseException;
	void deleteSchedule(IDBSchedule schedule) throws DatabaseException;
	
	// Schedule Items
	Collection<IDBScheduleItem> findScheduleItemsBySchedule(IDBSchedule schedule) throws DatabaseException;
	Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(IDBSchedule schedule) throws DatabaseException;
	IDBScheduleItem findScheduleItemByID(int id) throws DatabaseException;
	IDBScheduleItem assembleScheduleItem(int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced, boolean isConflicted) throws DatabaseException;
	void insertScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, IDBScheduleItem item) throws DatabaseException;
	void updateScheduleItem(IDBScheduleItem schedule) throws DatabaseException;
	void deleteScheduleItem(IDBScheduleItem schedule) throws DatabaseException;
	IDBLocation getScheduleItemLocation(IDBScheduleItem item) throws DatabaseException;
	IDBCourse getScheduleItemCourse(IDBScheduleItem item) throws DatabaseException;
	IDBInstructor getScheduleItemInstructor(IDBScheduleItem item) throws DatabaseException;
	void setScheduleItemCourse(IDBScheduleItem underlying, IDBCourse findCourseByID) throws DatabaseException;
	void setScheduleItemLocation(IDBScheduleItem underlying, IDBLocation findLocationByID) throws DatabaseException;
	void setScheduleItemInstructor(IDBScheduleItem underlying, IDBInstructor findInstructorByID) throws DatabaseException;
	
	// Locations
	Collection<IDBLocation> findLocationsForDocument(IDBDocument document) throws DatabaseException;
	IDBLocation findLocationByID(int id) throws DatabaseException;
	IDBLocation assembleLocation(String room,
			String type, String maxOccupancy, boolean isSchedulable) throws DatabaseException;
	void insertLocation(IDBDocument containingDocument, IDBLocation location) throws DatabaseException;
	void updateLocation(IDBLocation location) throws DatabaseException;
	void deleteLocation(IDBLocation location) throws DatabaseException;
	
	// Courses
	Collection<IDBCourse> findCoursesForDocument(IDBDocument document) throws DatabaseException;
	IDBCourse findCourseByID(int id) throws DatabaseException;
	IDBCourse assembleCourse(String name,
			String catalogNumber, String department, String wtu, String scu,
			String numSections, String type, String maxEnrollment,
			String numHalfHoursPerWeek, boolean isSchedulable) throws DatabaseException;
	void insertCourse(IDBDocument underlyingDocument, IDBCourse course) throws DatabaseException;
	void updateCourse(IDBCourse course) throws DatabaseException;
	void deleteCourse(IDBCourse course) throws DatabaseException;
	IDBDocument findDocumentForCourse(IDBCourse underlyingCourse) throws DatabaseException;

	// Tethering
	IDBCourseAssociation getAssociationForLabOrNull(IDBCourse underlying) throws DatabaseException;
	Collection<IDBCourseAssociation> getAssociationsForLecture(IDBCourse lectureCourse) throws DatabaseException;
	IDBCourse getAssociationLecture(IDBCourseAssociation association) throws DatabaseException;
	IDBCourse getAssociationLab(IDBCourseAssociation association) throws DatabaseException;
	void associateLectureAndLab(IDBCourse lecture, IDBCourse lab) throws DatabaseException;
	void disassociateLectureAndLab(IDBCourse lecture, IDBCourse lab);

	// Instructors
	Collection<IDBInstructor> findInstructorsForDocument(IDBDocument document) throws DatabaseException;
	IDBInstructor findInstructorByID(int id) throws DatabaseException;
	IDBInstructor assembleInstructor(String firstName, String lastName, String username, String maxWTU, boolean isSchedulable) throws DatabaseException;
	void insertInstructor(IDBDocument containingDocument, IDBInstructor instructor) throws DatabaseException;
	void updateInstructor(IDBInstructor instructor) throws DatabaseException;
	void deleteInstructor(IDBInstructor instructor) throws DatabaseException;
	
	// Time Preferences
	Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(IDBInstructor instructor) throws DatabaseException;
	IDBTimePreference findTimePreferenceByID(int id) throws DatabaseException;
	IDBTimePreference findTimePreferenceForInstructorAndTime(IDBInstructor instructor, IDBTime time) throws DatabaseException;
	IDBTimePreference assembleTimePreference(int preference) throws DatabaseException;
	void insertTimePreference(IDBInstructor ins, IDBTime time, IDBTimePreference timePreference) throws DatabaseException;
	void updateTimePreference(IDBTimePreference timePreference) throws DatabaseException;
	void deleteTimePreference(IDBTimePreference timePreference) throws DatabaseException;

	// Course Preferences
	Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(IDBInstructor instructor) throws DatabaseException;
	IDBCoursePreference findCoursePreferenceByID(int id) throws DatabaseException;
	IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(IDBInstructor instructor, IDBCourse course) throws DatabaseException;
	IDBCoursePreference assembleCoursePreference(int preference) throws DatabaseException;
	void insertCoursePreference(IDBInstructor instructor, IDBCourse course, IDBCoursePreference coursePreference) throws DatabaseException;
	void updateCoursePreference(IDBCoursePreference coursePreference) throws DatabaseException;
	void deleteCoursePreference(IDBCoursePreference coursePreference) throws DatabaseException;
	
	// Time
	IDBTime findTimeByDayAndHalfHour(int day, int halfHour) throws DatabaseException;
	
	// Equipment Types
	IDBEquipmentType findEquipmentTypeByDescription(String equipmentTypeDescription) throws DatabaseException;
	Collection<IDBEquipmentType> findAllEquipmentTypes() throws DatabaseException;
	
	// Used Equipment
	Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(IDBCourse course) throws DatabaseException;
	void deleteUsedEquipment(IDBUsedEquipment usedEquipment) throws DatabaseException;
	DBUsedEquipment assembleUsedEquipment() throws DatabaseException;
	void insertUsedEquipment(IDBCourse course, IDBEquipmentType equipmentType, IDBUsedEquipment equip) throws DatabaseException;
	
	// Provided Equipment
	Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(IDBLocation location) throws DatabaseException;
	void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment) throws DatabaseException;
	IDBProvidedEquipment assembleProvidedEquipment() throws DatabaseException;
	void insertProvidedEquipment(IDBLocation location, IDBEquipmentType equipmentType, IDBProvidedEquipment equip) throws DatabaseException;
	
	// Day Patterns
	IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern) throws DatabaseException;
	Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(IDBCourse underlying) throws DatabaseException;
	IDBDayPattern getDayPatternForOfferedDayPattern(IDBOfferedDayPattern offered) throws DatabaseException;
	void deleteOfferedDayPattern(IDBOfferedDayPattern offered) throws DatabaseException;
	IDBOfferedDayPattern assembleOfferedDayPattern() throws DatabaseException;
	void insertOfferedDayPattern(IDBCourse underlying, IDBDayPattern dayPattern, IDBOfferedDayPattern pattern) throws DatabaseException;
	
	// For testing
	boolean isEmpty();
	
	IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying) throws DatabaseException;
	IDBSchedule getScheduleItemSchedule(IDBScheduleItem underlying) throws DatabaseException;
	boolean isInserted(IDBScheduleItem underlying) throws DatabaseException;
	IDBObject findDocumentForLocation(IDBLocation underlyingLocation) throws DatabaseException;
	IDBObject findDocumentForInstructor(IDBInstructor underlyingInstructor) throws DatabaseException;

	void writeState(ObjectOutputStream oos) throws IOException;
	void readState(ObjectInputStream ois) throws IOException;
	
	IDBInstructor getDocumentStaffInstructor(IDBDocument underlyingDocument) throws NotFoundException;
	IDBLocation getDocumentTBALocation(IDBDocument underlyingDocument) throws NotFoundException;
	void setDocumentStaffInstructor(IDBDocument underlyingDocument, IDBInstructor underlyingInstructor);
	void setDocumentTBALocation(IDBDocument underlyingDocument, IDBLocation underlyingLocation);
}
