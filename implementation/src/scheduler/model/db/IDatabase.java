package scheduler.model.db;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import scheduler.model.Day;

// TODO: Auto-generated Javadoc
/**
 * The Interface IDatabase defines an API to be used by the Scheduler
 * for storing and retrieving of data. The IDatabase class is generic
 * and does not require specific implementations. It was used as the
 * interface for both a sqlite database and a serialized java database.
 * It defines all methods needed for the front-end scheduler to store
 * data in the database, and allows for communication between the Model
 * and the algorithm.
 */
public interface IDatabase {
	
	/**
	 * The Class NotFoundException for anytime an object is not found
	 * in the database.
	 */
	public class NotFoundException extends DatabaseException {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;
//		public NotFoundException(Throwable thr) {
//			super(thr);
//		}
		/**
 * Instantiates a new not found exception.
 *
 * @param message the message to go with the exception
 */
public NotFoundException(String message) {
			super(message);
		}
	}
	
	// Users
	/**
	 * Find user by username.
	 *
	 * @param username the username of the user
	 * @return the iDBUser
	 * @throws DatabaseException the database exception
	 */
	IDBUser findUserByUsername(String username) throws DatabaseException;
	
	/**
	 * Assemble user to be used by the model or database.
	 *
	 * @param username the username
	 * @param isAdmin whether a user is an admin
	 * @return the iDBUser
	 */
	IDBUser assembleUser(String username, boolean isAdmin);
	
	/**
	 * Insert user into the database.
	 *
	 * @param user the user to be inserted
	 * @throws DatabaseException the database exception
	 */
	void insertUser(IDBUser user) throws DatabaseException;
	
	/**
	 * Update user in the database.
	 *
	 * @param user the user to be updated
	 * @throws DatabaseException the database exception
	 */
	void updateUser(IDBUser user) throws DatabaseException;
	
	/**
	 * Delete user from the database.
	 *
	 * @param user the user to be deleted
	 * @throws DatabaseException the database exception
	 */
	void deleteUser(IDBUser user) throws DatabaseException;
	
	// Documents
	/**
	 * Find all documents.
	 *
	 * @return the collection of documents found in the database
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBDocument> findAllDocuments() throws DatabaseException;
	
	/**
	 * Find document by id.
	 *
	 * @param id the id of the desired document
	 * @return the iDBDocument found in the database
	 * @throws DatabaseException the database exception
	 */
	IDBDocument findDocumentByID(int id) throws DatabaseException;
	
	/**
	 * Insert document into the database.
	 *
	 * @param document the document to be inserted
	 * @throws DatabaseException the database exception
	 */
	void insertDocument(IDBDocument document) throws DatabaseException;
	
	/**
	 * Assemble document to be used by the model or database.
	 *
	 * @param name the name of the document
	 * @param startHalfHour the start half hour
	 * @param endHalfHour the end half hour
	 * @return the iDBDocument thats been assembled
	 * @throws DatabaseException the database exception
	 */
	IDBDocument assembleDocument(String name, int startHalfHour, int endHalfHour) throws DatabaseException;
	
	/**
	 * Update document in the database.
	 *
	 * @param document the document to be updated
	 * @throws DatabaseException the database exception
	 */
	void updateDocument(IDBDocument document) throws DatabaseException;
	
	/**
	 * Delete document from the database.
	 *
	 * @param document the document
	 * @throws DatabaseException the database exception
	 */
	void deleteDocument(IDBDocument document) throws DatabaseException;
//	IDBDocument findDocumentForSchedule(IDBSchedule schedule) throws DatabaseException;

	/**
 * Gets the document staff instructor or null from the database.
 *
 * @param underlyingDocument the underlying document
 * @return the document staff instructor or null if not found
 * @throws DatabaseException the database exception
 */
IDBInstructor getDocumentStaffInstructorOrNull(IDBDocument underlyingDocument) throws DatabaseException;
	
	/**
	 * Gets the document tba location or null.
	 *
	 * @param underlyingDocument the underlying document
	 * @return the document tba location or null
	 * @throws DatabaseException the database exception
	 */
	IDBLocation getDocumentTBALocationOrNull(IDBDocument underlyingDocument) throws DatabaseException;
	
	/**
	 * Gets the document choose for me instructor or null.
	 *
	 * @param underlyingDocument the underlying document
	 * @return the document choose for me instructor or null
	 * @throws DatabaseException the database exception
	 */
	IDBInstructor getDocumentChooseForMeInstructorOrNull(IDBDocument underlyingDocument) throws DatabaseException;
	
	/**
	 * Gets the document choose for me location or null.
	 *
	 * @param underlyingDocument the underlying document
	 * @return the document choose for me location or null
	 * @throws DatabaseException the database exception
	 */
	IDBLocation getDocumentChooseForMeLocationOrNull(IDBDocument underlyingDocument) throws DatabaseException;
	
	/**
	 * Sets the document staff instructor or null.
	 *
	 * @param underlyingDocument the underlying document
	 * @param underlyingInstructor the underlying instructor
	 * @throws DatabaseException the database exception
	 */
	void setDocumentStaffInstructorOrNull(IDBDocument underlyingDocument, IDBInstructor underlyingInstructor) throws DatabaseException;
	
	/**
	 * Sets the document tba location or null.
	 *
	 * @param underlyingDocument the underlying document
	 * @param underlyingLocation the underlying location
	 * @throws DatabaseException the database exception
	 */
	void setDocumentTBALocationOrNull(IDBDocument underlyingDocument, IDBLocation underlyingLocation) throws DatabaseException;
	
	/**
	 * Sets the document choose for me instructor or null.
	 *
	 * @param underlyingDocument the underlying document
	 * @param underlyingInstructor the underlying instructor
	 * @throws DatabaseException the database exception
	 */
	void setDocumentChooseForMeInstructorOrNull(IDBDocument underlyingDocument, IDBInstructor underlyingInstructor) throws DatabaseException;
	
	/**
	 * Sets the document choose for me location or null.
	 *
	 * @param underlyingDocument the underlying document
	 * @param underlyingLocation the underlying location
	 * @throws DatabaseException the database exception
	 */
	void setDocumentChooseForMeLocationOrNull(IDBDocument underlyingDocument, IDBLocation underlyingLocation) throws DatabaseException;
	
	// Working Copy
	/**
	 * Checks if is original document.
	 *
	 * @param doc the doc
	 * @return true, if is original document
	 * @throws DatabaseException the database exception
	 */
	boolean isOriginalDocument(IDBDocument doc) throws DatabaseException;
	
	/**
	 * Document is working copy.
	 *
	 * @param document the document
	 * @return true, if successful
	 * @throws DatabaseException the database exception
	 */
	boolean documentIsWorkingCopy(IDBDocument document) throws DatabaseException;
	
	/**
	 * Gets the original for working copy document or null.
	 *
	 * @param rawDocument the raw document
	 * @return the original for working copy document or null
	 * @throws DatabaseException the database exception
	 */
	IDBDocument getOriginalForWorkingCopyDocumentOrNull(IDBDocument rawDocument) throws DatabaseException;
	
	/**
	 * Gets the working copy for original document or null.
	 *
	 * @param document the document
	 * @return the working copy for original document or null
	 * @throws DatabaseException the database exception
	 */
	IDBDocument getWorkingCopyForOriginalDocumentOrNull(IDBDocument document) throws DatabaseException;
	
	/**
	 * Associate working copy with original.
	 *
	 * @param underlyingDocument the underlying document
	 * @param underlyingDocument2 the underlying document2
	 * @throws DatabaseException the database exception
	 */
	void associateWorkingCopyWithOriginal(IDBDocument underlyingDocument, IDBDocument underlyingDocument2) throws DatabaseException;
	
	/**
	 * Disassociate working copy with original.
	 *
	 * @param underlyingDocument the underlying document
	 * @param underlyingDocument2 the underlying document2
	 * @throws DatabaseException the database exception
	 */
	void disassociateWorkingCopyWithOriginal(IDBDocument underlyingDocument, IDBDocument underlyingDocument2) throws DatabaseException;

	// Schedules
//	Collection<IDBSchedule> findAllSchedulesForDocument(IDBDocument document) throws DatabaseException;
//	IDBSchedule findScheduleByID(int id) throws DatabaseException;
//	IDBSchedule assembleSchedule() throws DatabaseException;
//	void insertSchedule(IDBDocument containingDocument, IDBSchedule schedule) throws DatabaseException;
//	void updateSchedule(IDBSchedule schedule) throws DatabaseException;
//	void deleteSchedule(IDBSchedule schedule) throws DatabaseException;
	
	// Schedule Items
	/**
	 * Find schedule items by document.
	 *
	 * @param schedule the schedule
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBScheduleItem> findScheduleItemsByDocument(IDBDocument schedule) throws DatabaseException;
	
	/**
	 * Find all schedule items for document.
	 *
	 * @param schedule the schedule
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBScheduleItem> findAllScheduleItemsForDocument(IDBDocument schedule) throws DatabaseException;
	
	/**
	 * Find schedule item by id.
	 *
	 * @param id the id
	 * @return the iDB schedule item
	 * @throws DatabaseException the database exception
	 */
	IDBScheduleItem findScheduleItemByID(int id) throws DatabaseException;
	
	/**
	 * Assemble schedule item.
	 *
	 * @param section the section
	 * @param days the days
	 * @param startHalfHour the start half hour
	 * @param endHalfHour the end half hour
	 * @param isPlaced the is placed
	 * @param isConflicted the is conflicted
	 * @return the iDB schedule item
	 * @throws DatabaseException the database exception
	 */
	IDBScheduleItem assembleScheduleItem(int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced, boolean isConflicted) throws DatabaseException;
	
	/**
	 * Insert schedule item.
	 *
	 * @param document the document
	 * @param course the course
	 * @param instructor the instructor
	 * @param location the location
	 * @param item the item
	 * @throws DatabaseException the database exception
	 */
	void insertScheduleItem(IDBDocument document, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, IDBScheduleItem item) throws DatabaseException;
	
	/**
	 * Update schedule item.
	 *
	 * @param schedule the schedule
	 * @throws DatabaseException the database exception
	 */
	void updateScheduleItem(IDBScheduleItem schedule) throws DatabaseException;
	
	/**
	 * Delete schedule item.
	 *
	 * @param schedule the schedule
	 * @throws DatabaseException the database exception
	 */
	void deleteScheduleItem(IDBScheduleItem schedule) throws DatabaseException;
	
	/**
	 * Gets the schedule item location.
	 *
	 * @param item the item
	 * @return the schedule item location
	 * @throws DatabaseException the database exception
	 */
	IDBLocation getScheduleItemLocation(IDBScheduleItem item) throws DatabaseException;
	
	/**
	 * Gets the schedule item course.
	 *
	 * @param item the item
	 * @return the schedule item course
	 * @throws DatabaseException the database exception
	 */
	IDBCourse getScheduleItemCourse(IDBScheduleItem item) throws DatabaseException;
	
	/**
	 * Gets the schedule item instructor.
	 *
	 * @param item the item
	 * @return the schedule item instructor
	 * @throws DatabaseException the database exception
	 */
	IDBInstructor getScheduleItemInstructor(IDBScheduleItem item) throws DatabaseException;
	
	/**
	 * Sets the schedule item course.
	 *
	 * @param underlying the underlying
	 * @param findCourseByID the find course by id
	 * @throws DatabaseException the database exception
	 */
	void setScheduleItemCourse(IDBScheduleItem underlying, IDBCourse findCourseByID) throws DatabaseException;
	
	/**
	 * Sets the schedule item location.
	 *
	 * @param underlying the underlying
	 * @param findLocationByID the find location by id
	 * @throws DatabaseException the database exception
	 */
	void setScheduleItemLocation(IDBScheduleItem underlying, IDBLocation findLocationByID) throws DatabaseException;
	
	/**
	 * Sets the schedule item instructor.
	 *
	 * @param underlying the underlying
	 * @param findInstructorByID the find instructor by id
	 * @throws DatabaseException the database exception
	 */
	void setScheduleItemInstructor(IDBScheduleItem underlying, IDBInstructor findInstructorByID) throws DatabaseException;
	
	/**
	 * Find all lab schedule items for schedule item.
	 *
	 * @param underlying the underlying
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBScheduleItem> findAllLabScheduleItemsForScheduleItem(IDBScheduleItem underlying) throws DatabaseException;
	
	/**
	 * Associate schedule item lab.
	 *
	 * @param lecture the lecture
	 * @param lab the lab
	 * @throws DatabaseException the database exception
	 */
	void associateScheduleItemLab(IDBScheduleItem lecture, IDBScheduleItem lab) throws DatabaseException;
	
	/**
	 * Disassociate schedule item lab.
	 *
	 * @param lecture the lecture
	 * @param lab the lab
	 * @throws DatabaseException the database exception
	 */
	void disassociateScheduleItemLab(IDBScheduleItem lecture, IDBScheduleItem lab) throws DatabaseException;
	
	// Locations
	/**
	 * Find locations for document.
	 *
	 * @param document the document
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBLocation> findLocationsForDocument(IDBDocument document) throws DatabaseException;
	
	/**
	 * Find location by id.
	 *
	 * @param id the id
	 * @return the iDB location
	 * @throws DatabaseException the database exception
	 */
	IDBLocation findLocationByID(int id) throws DatabaseException;
	
	/**
	 * Assemble location.
	 *
	 * @param room the room
	 * @param type the type
	 * @param maxOccupancy the max occupancy
	 * @param isSchedulable the is schedulable
	 * @return the iDB location
	 * @throws DatabaseException the database exception
	 */
	IDBLocation assembleLocation(String room,
			String type, String maxOccupancy, boolean isSchedulable) throws DatabaseException;
	
	/**
	 * Insert location.
	 *
	 * @param containingDocument the containing document
	 * @param location the location
	 * @throws DatabaseException the database exception
	 */
	void insertLocation(IDBDocument containingDocument, IDBLocation location) throws DatabaseException;
	
	/**
	 * Update location.
	 *
	 * @param location the location
	 * @throws DatabaseException the database exception
	 */
	void updateLocation(IDBLocation location) throws DatabaseException;
	
	/**
	 * Delete location.
	 *
	 * @param location the location
	 * @throws DatabaseException the database exception
	 */
	void deleteLocation(IDBLocation location) throws DatabaseException;
	
	// Courses
	/**
	 * Find courses for document.
	 *
	 * @param document the document
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBCourse> findCoursesForDocument(IDBDocument document) throws DatabaseException;
	
	/**
	 * Find course by id.
	 *
	 * @param id the id
	 * @return the iDB course
	 * @throws DatabaseException the database exception
	 */
	IDBCourse findCourseByID(int id) throws DatabaseException;
	
	/**
	 * Assemble course.
	 *
	 * @param name the name
	 * @param catalogNumber the catalog number
	 * @param department the department
	 * @param wtu the wtu
	 * @param scu the scu
	 * @param numSections the num sections
	 * @param type the type
	 * @param maxEnrollment the max enrollment
	 * @param numHalfHoursPerWeek the num half hours per week
	 * @param isSchedulable the is schedulable
	 * @return the iDB course
	 * @throws DatabaseException the database exception
	 */
	IDBCourse assembleCourse(String name,
			String catalogNumber, String department, String wtu, String scu,
			String numSections, String type, String maxEnrollment,
			String numHalfHoursPerWeek, boolean isSchedulable) throws DatabaseException;
	
	/**
	 * Insert course.
	 *
	 * @param underlyingDocument the underlying document
	 * @param course the course
	 * @throws DatabaseException the database exception
	 */
	void insertCourse(IDBDocument underlyingDocument, IDBCourse course) throws DatabaseException;
	
	/**
	 * Update course.
	 *
	 * @param course the course
	 * @throws DatabaseException the database exception
	 */
	void updateCourse(IDBCourse course) throws DatabaseException;
	
	/**
	 * Delete course.
	 *
	 * @param course the course
	 * @throws DatabaseException the database exception
	 */
	void deleteCourse(IDBCourse course) throws DatabaseException;
	
	/**
	 * Find document for course.
	 *
	 * @param underlyingCourse the underlying course
	 * @return the iDB document
	 * @throws DatabaseException the database exception
	 */
	IDBDocument findDocumentForCourse(IDBCourse underlyingCourse) throws DatabaseException;

	// Tethering
	/**
	 * Gets the association for lab or null.
	 *
	 * @param underlying the underlying
	 * @return the association for lab or null
	 * @throws DatabaseException the database exception
	 */
	IDBCourseAssociation getAssociationForLabOrNull(IDBCourse underlying) throws DatabaseException;
	
	/**
	 * Gets the associations for lecture.
	 *
	 * @param lectureCourse the lecture course
	 * @return the associations for lecture
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBCourseAssociation> getAssociationsForLecture(IDBCourse lectureCourse) throws DatabaseException;
	
	/**
	 * Gets the association lecture.
	 *
	 * @param association the association
	 * @return the association lecture
	 * @throws DatabaseException the database exception
	 */
	IDBCourse getAssociationLecture(IDBCourseAssociation association) throws DatabaseException;
	
	/**
	 * Gets the association lab.
	 *
	 * @param association the association
	 * @return the association lab
	 * @throws DatabaseException the database exception
	 */
	IDBCourse getAssociationLab(IDBCourseAssociation association) throws DatabaseException;
	
	/**
	 * Associate lecture and lab.
	 *
	 * @param lecture the lecture
	 * @param lab the lab
	 * @param tethered the tethered
	 * @throws DatabaseException the database exception
	 */
	void associateLectureAndLab(IDBCourse lecture, IDBCourse lab, boolean tethered) throws DatabaseException;
	
	/**
	 * Disassociate lecture and lab.
	 *
	 * @param lecture the lecture
	 * @param lab the lab
	 * @throws DatabaseException the database exception
	 */
	void disassociateLectureAndLab(IDBCourse lecture, IDBCourse lab) throws DatabaseException;

	// Instructors
	/**
	 * Find instructors for document.
	 *
	 * @param document the document
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBInstructor> findInstructorsForDocument(IDBDocument document) throws DatabaseException;
	
	/**
	 * Find instructor by id.
	 *
	 * @param id the id
	 * @return the iDB instructor
	 * @throws DatabaseException the database exception
	 */
	IDBInstructor findInstructorByID(int id) throws DatabaseException;
	
	/**
	 * Assemble instructor.
	 *
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param username the username
	 * @param maxWTU the max wtu
	 * @param isSchedulable the is schedulable
	 * @return the iDB instructor
	 * @throws DatabaseException the database exception
	 */
	IDBInstructor assembleInstructor(String firstName, String lastName, String username, String maxWTU, boolean isSchedulable) throws DatabaseException;
	
	/**
	 * Insert instructor.
	 *
	 * @param containingDocument the containing document
	 * @param instructor the instructor
	 * @throws DatabaseException the database exception
	 */
	void insertInstructor(IDBDocument containingDocument, IDBInstructor instructor) throws DatabaseException;
	
	/**
	 * Update instructor.
	 *
	 * @param instructor the instructor
	 * @throws DatabaseException the database exception
	 */
	void updateInstructor(IDBInstructor instructor) throws DatabaseException;
	
	/**
	 * Delete instructor.
	 *
	 * @param instructor the instructor
	 * @throws DatabaseException the database exception
	 */
	void deleteInstructor(IDBInstructor instructor) throws DatabaseException;
	
	// Time Preferences
	/**
	 * Find time preferences by time for instructor.
	 *
	 * @param instructor the instructor
	 * @return the map
	 * @throws DatabaseException the database exception
	 */
	Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(IDBInstructor instructor) throws DatabaseException;
	
	/**
	 * Find time preference by id.
	 *
	 * @param id the id
	 * @return the iDB time preference
	 * @throws DatabaseException the database exception
	 */
	IDBTimePreference findTimePreferenceByID(int id) throws DatabaseException;
	
	/**
	 * Assemble time preference.
	 *
	 * @param preference the preference
	 * @return the iDB time preference
	 * @throws DatabaseException the database exception
	 */
	IDBTimePreference assembleTimePreference(int preference) throws DatabaseException;
	
	/**
	 * Insert time preference.
	 *
	 * @param ins the ins
	 * @param time the time
	 * @param timePreference the time preference
	 * @throws DatabaseException the database exception
	 */
	void insertTimePreference(IDBInstructor ins, IDBTime time, IDBTimePreference timePreference) throws DatabaseException;
	
	/**
	 * Update time preference.
	 *
	 * @param timePreference the time preference
	 * @throws DatabaseException the database exception
	 */
	void updateTimePreference(IDBTimePreference timePreference) throws DatabaseException;
	
	/**
	 * Delete time preference.
	 *
	 * @param timePreference the time preference
	 * @throws DatabaseException the database exception
	 */
	void deleteTimePreference(IDBTimePreference timePreference) throws DatabaseException;

	// Course Preferences
	/**
	 * Find course preferences by course for instructor.
	 *
	 * @param instructor the instructor
	 * @return the map
	 * @throws DatabaseException the database exception
	 */
	Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(IDBInstructor instructor) throws DatabaseException;
	
	/**
	 * Find course preference by id.
	 *
	 * @param id the id
	 * @return the iDB course preference
	 * @throws DatabaseException the database exception
	 */
	IDBCoursePreference findCoursePreferenceByID(int id) throws DatabaseException;
	
	/**
	 * Assemble course preference.
	 *
	 * @param preference the preference
	 * @return the iDB course preference
	 * @throws DatabaseException the database exception
	 */
	IDBCoursePreference assembleCoursePreference(int preference) throws DatabaseException;
	
	/**
	 * Insert course preference.
	 *
	 * @param instructor the instructor
	 * @param course the course
	 * @param coursePreference the course preference
	 * @throws DatabaseException the database exception
	 */
	void insertCoursePreference(IDBInstructor instructor, IDBCourse course, IDBCoursePreference coursePreference) throws DatabaseException;
	
	/**
	 * Update course preference.
	 *
	 * @param coursePreference the course preference
	 * @throws DatabaseException the database exception
	 */
	void updateCoursePreference(IDBCoursePreference coursePreference) throws DatabaseException;
	
	/**
	 * Delete course preference.
	 *
	 * @param coursePreference the course preference
	 * @throws DatabaseException the database exception
	 */
	void deleteCoursePreference(IDBCoursePreference coursePreference) throws DatabaseException;
	
	// Time
	/**
	 * Find time by day and half hour.
	 *
	 * @param day the day
	 * @param halfHour the half hour
	 * @return the iDB time
	 * @throws DatabaseException the database exception
	 */
	IDBTime findTimeByDayAndHalfHour(int day, int halfHour) throws DatabaseException;
	
	// Equipment Types
	/**
	 * Find equipment type by description.
	 *
	 * @param equipmentTypeDescription the equipment type description
	 * @return the iDB equipment type
	 * @throws DatabaseException the database exception
	 */
	IDBEquipmentType findEquipmentTypeByDescription(String equipmentTypeDescription) throws DatabaseException;
	
	/**
	 * Find all equipment types.
	 *
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBEquipmentType> findAllEquipmentTypes() throws DatabaseException;
	
	/**
	 * Insert equipment type.
	 *
	 * @param string the string
	 * @throws DatabaseException the database exception
	 */
	void insertEquipmentType(String string) throws DatabaseException;
	
	// Used Equipment
	/**
	 * Find used equipment by equipment for course.
	 *
	 * @param course the course
	 * @return the map
	 * @throws DatabaseException the database exception
	 */
	Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(IDBCourse course) throws DatabaseException;
	
	/**
	 * Delete used equipment.
	 *
	 * @param usedEquipment the used equipment
	 * @throws DatabaseException the database exception
	 */
	void deleteUsedEquipment(IDBUsedEquipment usedEquipment) throws DatabaseException;
	
	/**
	 * Assemble used equipment.
	 *
	 * @return the iDB used equipment
	 * @throws DatabaseException the database exception
	 */
	IDBUsedEquipment assembleUsedEquipment() throws DatabaseException;
	
	/**
	 * Insert used equipment.
	 *
	 * @param course the course
	 * @param equipmentType the equipment type
	 * @param equip the equip
	 * @throws DatabaseException the database exception
	 */
	void insertUsedEquipment(IDBCourse course, IDBEquipmentType equipmentType, IDBUsedEquipment equip) throws DatabaseException;
	
	// Provided Equipment
	/**
	 * Find provided equipment by equipment for location.
	 *
	 * @param location the location
	 * @return the map
	 * @throws DatabaseException the database exception
	 */
	Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(IDBLocation location) throws DatabaseException;
	
	/**
	 * Delete provided equipment.
	 *
	 * @param providedEquipment the provided equipment
	 * @throws DatabaseException the database exception
	 */
	void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment) throws DatabaseException;
	
	/**
	 * Assemble provided equipment.
	 *
	 * @return the iDB provided equipment
	 * @throws DatabaseException the database exception
	 */
	IDBProvidedEquipment assembleProvidedEquipment() throws DatabaseException;
	
	/**
	 * Insert provided equipment.
	 *
	 * @param location the location
	 * @param equipmentType the equipment type
	 * @param equip the equip
	 * @throws DatabaseException the database exception
	 */
	void insertProvidedEquipment(IDBLocation location, IDBEquipmentType equipmentType, IDBProvidedEquipment equip) throws DatabaseException;
	
	// Day Patterns
	/**
	 * Find day pattern by days.
	 *
	 * @param dayPattern the day pattern
	 * @return the iDB day pattern
	 * @throws DatabaseException the database exception
	 */
	IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern) throws DatabaseException;
	
	/**
	 * Find offered day patterns for course.
	 *
	 * @param underlying the underlying
	 * @return the collection
	 * @throws DatabaseException the database exception
	 */
	Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(IDBCourse underlying) throws DatabaseException;
	
	/**
	 * Gets the day pattern for offered day pattern.
	 *
	 * @param offered the offered
	 * @return the day pattern for offered day pattern
	 * @throws DatabaseException the database exception
	 */
	IDBDayPattern getDayPatternForOfferedDayPattern(IDBOfferedDayPattern offered) throws DatabaseException;
	
	/**
	 * Delete offered day pattern.
	 *
	 * @param offered the offered
	 * @throws DatabaseException the database exception
	 */
	void deleteOfferedDayPattern(IDBOfferedDayPattern offered) throws DatabaseException;
	
	/**
	 * Assemble offered day pattern.
	 *
	 * @return the iDB offered day pattern
	 * @throws DatabaseException the database exception
	 */
	IDBOfferedDayPattern assembleOfferedDayPattern() throws DatabaseException;
	
	/**
	 * Insert offered day pattern.
	 *
	 * @param underlying the underlying course
	 * @param dayPattern the day pattern
	 * @param pattern the offered pattern
	 * @throws DatabaseException the database exception
	 */
	void insertOfferedDayPattern(IDBCourse underlying, IDBDayPattern dayPattern, IDBOfferedDayPattern pattern) throws DatabaseException;
	
	// For testing
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 * @throws DatabaseException the database exception
	 */
	boolean isEmpty() throws DatabaseException;
	
	/**
	 * Assemble schedule item copy.
	 *
	 * @param underlying the underlying scheduleItem
	 * @return the iDB schedule item
	 * @throws DatabaseException the database exception
	 */
	IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying) throws DatabaseException;
	
	/**
	 * Gets the schedule item document.
	 *
	 * @param underlying the underlying scheduleItem
	 * @return the document
	 * @throws DatabaseException the database exception
	 */
	IDBDocument getScheduleItemDocument(IDBScheduleItem underlying) throws DatabaseException;
	
	/**
	 * Checks if the item is inserted.
	 *
	 * @param underlying the underlying scheduleitem
	 * @return true, if is inserted
	 * @throws DatabaseException the database exception
	 */
	boolean isInserted(IDBScheduleItem underlying) throws DatabaseException;
	
	/**
	 * Find document for location.
	 *
	 * @param underlyingLocation the underlying location
	 * @return the document
	 * @throws DatabaseException the database exception
	 */
	IDBObject findDocumentForLocation(IDBLocation underlyingLocation) throws DatabaseException;
	
	/**
	 * Find document for instructor.
	 *
	 * @param underlyingInstructor the underlying instructor
	 * @return the document
	 * @throws DatabaseException the database exception
	 */
	IDBObject findDocumentForInstructor(IDBInstructor underlyingInstructor) throws DatabaseException;

	/**
	 * Write state of the database to the stream.
	 *
	 * @param oos the stream to be written to
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void writeState(ObjectOutputStream oos) throws IOException;
	
	/**
	 * Read state of the database.
	 *
	 * @param ois the input stream of the database
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	void readState(ObjectInputStream ois) throws IOException;
	
	/**
	 * Gets the schedule item lecture or null.
	 *
	 * @param underlying the underlying scheduleItem
	 * @return the schedule item lecture or null
	 * @throws DatabaseException the database exception
	 */
	IDBScheduleItem getScheduleItemLectureOrNull(IDBScheduleItem underlying) throws DatabaseException;
	
	/**
	 * Find document by name.
	 *
	 * @param scheduleName the schedule name
	 * @return the iDB document
	 * @throws DatabaseException the database exception
	 */
	IDBDocument findDocumentByName(String scheduleName) throws DatabaseException;
	
	/**
	 * Close database.
	 */
	void closeDatabase();
}
