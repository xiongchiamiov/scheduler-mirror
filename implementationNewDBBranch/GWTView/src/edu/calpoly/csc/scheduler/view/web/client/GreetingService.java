package edu.calpoly.csc.scheduler.view.web.client;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.NotFoundExceptionGWT;
import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
//	void login(String username);
//	
//	// Managing Schedules
//	Map<String, UserDataGWT> getScheduleNames();
//	Integer openNewSchedule(String newScheduleName);
//	String openExistingSchedule(int scheduleID);
//	void removeSchedule(String schedName);
//
//	// Instructors
//	void saveInstructor(InstructorGWT instructor);	
//	List<InstructorGWT> getInstructors();
//	
//	List<CourseGWT> getCourses();
//	
//	// Locations
//	List<LocationGWT> getLocations();
//	
//	// Schedule Items
//	List<ScheduleItemGWT> generateSchedule(List<CourseGWT> courses, HashMap<String, ScheduleItemGWT> scheduleItems);
//	ScheduleItemList rescheduleCourse(ScheduleItemGWT scheduleItem, List<Integer> days, int startHour, boolean atHalfHour, boolean inSchedule, HashMap<String, ScheduleItemGWT> scheduleItems);
//	List<ScheduleItemGWT> removeScheduleItem(ScheduleItemGWT removed, HashMap<String, ScheduleItemGWT> scheduleItems);
//	List<ScheduleItemGWT> getSchedule(HashMap<String, ScheduleItemGWT> scheduleItems);
//	void saveSchedule();
//	int exportCSV();
//
//	CourseGWT addCourse(CourseGWT toAdd);
//
//	void editCourse(CourseGWT toEdit);
//
//	void removeCourse(Integer toRemoveID);
//
//	InstructorGWT addInstructor(InstructorGWT toAdd);
//
//	void editInstructor(InstructorGWT toEdit);
//
//	void removeInstructor(Integer realInstructorID);
//
//	LocationGWT addLocation(LocationGWT toAdd);
//
//	void editLocation(LocationGWT toEdit);
//
//	void removeLocation(Integer toRemoveID);
//
//	Integer saveCurrentScheduleAsAndOpen(String scheduleName, boolean allowOverwrite);

	CourseGWT addCourseToDocument(int documentID, CourseGWT realCourse) throws NotFoundExceptionGWT;

	void editCourse(CourseGWT realCourse) throws NotFoundExceptionGWT;

	List<CourseGWT> getCoursesForDocument(int documentID) throws NotFoundExceptionGWT;

	void removeCourse(Integer realCourseID) throws NotFoundExceptionGWT;

	List<InstructorGWT> getInstructorsForDocument(int documentID) throws NotFoundExceptionGWT;

	void removeInstructor(Integer realInstructorID) throws NotFoundExceptionGWT;

	void editInstructor(InstructorGWT realInstructor) throws NotFoundExceptionGWT;

	InstructorGWT addInstructorToDocument(int documentID,
			InstructorGWT realInstructor) throws NotFoundExceptionGWT;

	LocationGWT addLocationToDocument(int documentID, LocationGWT location)
			throws NotFoundExceptionGWT;

	void editLocation(LocationGWT source) throws NotFoundExceptionGWT;

	List<LocationGWT> getLocationsForDocument(int documentID)
			throws NotFoundExceptionGWT;

	void removeLocation(Integer locationID) throws NotFoundExceptionGWT;

	Integer login(String username) throws InvalidLoginException;

	DocumentGWT createDocument(String newDocName);

	Collection<DocumentGWT> getAllOriginalDocumentsByID();

	void saveWorkingCopyToOriginalDocument(Integer id) throws NotFoundExceptionGWT;

	DocumentGWT createWorkingCopyForOriginalDocument(Integer originalDocumentID) throws NotFoundExceptionGWT;

	void deleteWorkingCopyDocument(Integer documentID) throws NotFoundExceptionGWT;
	
	DocumentGWT saveWorkingCopyToNewOriginalDocument(
			DocumentGWT existingDocument, String scheduleName,
			boolean allowOverwrite);

	@Deprecated
	List<OldScheduleItemGWT> generateSchedule(List<CourseGWT> mAllCourses,
			HashMap<String, OldScheduleItemGWT> mSchedItems);

	@Deprecated
	List<OldScheduleItemGWT> getSchedule(
			HashMap<String, OldScheduleItemGWT> mSchedItems);

	@Deprecated
	ScheduleItemList rescheduleCourse(OldScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule, HashMap<String, OldScheduleItemGWT> mSchedItems);

	@Deprecated
	List<OldScheduleItemGWT> removeScheduleItem(OldScheduleItemGWT removed,
			HashMap<String, OldScheduleItemGWT> mSchedItems);
}
