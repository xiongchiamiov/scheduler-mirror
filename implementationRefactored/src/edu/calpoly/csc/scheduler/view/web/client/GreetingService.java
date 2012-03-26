package edu.calpoly.csc.scheduler.view.web.client;



import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
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

	CourseGWT addCourseToDocument(int documentID, CourseGWT realCourse);

	void editCourse(CourseGWT realCourse);

	List<CourseGWT> getCoursesForDocument(int documentID);

	void removeCourse(Integer realCourseID);

	List<InstructorGWT> getInstructorsForDocument(int documentID);

	void removeInstructor(Integer realInstructorID);

	void editInstructor(InstructorGWT realInstructor);

	InstructorGWT addInstructorToDocument(int documentID,
			InstructorGWT realInstructor);

	LocationGWT addLocationToDocument(int documentID, LocationGWT location);

	void editLocation(LocationGWT source);

	List<LocationGWT> getLocationsForDocument(int documentID);

	void removeLocation(Integer locationID);

	Integer login(String username) throws InvalidLoginException;

	DocumentGWT createOriginalDocument(String newDocName);
	
	void updateDocument(DocumentGWT document);

	Collection<DocumentGWT> getAllOriginalDocuments();

	void saveWorkingCopyToOriginalDocument(Integer id);

	DocumentGWT createWorkingCopyForOriginalDocument(Integer originalDocumentID);

	void deleteWorkingCopyDocument(Integer documentID);
	
	void moveWorkingCopyToNewOriginalDocument(
			Integer workingCopyID, String scheduleName,
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

	public void intermediateInsertScheduleItem(int documentID, OldScheduleItemGWT scheduleItem);
	public Collection<OldScheduleItemGWT> intermediateGenerateRestOfSchedule(int scheduleID);
	void intermediateUpdateScheduleItem(int documentID, OldScheduleItemGWT oldItemOldGWT,
			OldScheduleItemGWT newItemOldGWT);
	void intermediateRemoveScheduleItem(int documentID, OldScheduleItemGWT oldItemOldGWT);
	Collection<OldScheduleItemGWT> intermediateGetScheduleItems(int documentID);
	

	Collection<ScheduleItemGWT> insertScheduleItem(int scheduleID,
			ScheduleItemGWT scheduleItem);
	public Collection<ScheduleItemGWT> generateRestOfSchedule(int scheduleID) throws CouldNotBeScheduledExceptionGWT;
	Collection<ScheduleItemGWT> updateScheduleItem(ScheduleItemGWT itemGWT);
	Collection<ScheduleItemGWT> newRemoveScheduleItem(ScheduleItemGWT itemGWT);
	Collection<ScheduleItemGWT> getScheduleItems(int scheduleID);

	DocumentGWT findDocumentByID(int automaticOpenDocumentID);
}
