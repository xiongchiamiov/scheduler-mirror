package edu.calpoly.csc.scheduler.view.web.client;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void getCoursesForDocument(int documentID, AsyncCallback<List<CourseGWT>> asyncCallback);
	void removeCourse(Integer realCourseID, AsyncCallback<Void> asyncCallback);
	void editCourse(CourseGWT realCourse, AsyncCallback<Void> asyncCallback);
	void addCourseToDocument(int documentID, CourseGWT realCourse, AsyncCallback<CourseGWT> asyncCallback);

	void getInstructorsForDocument(int documentID, AsyncCallback<List<InstructorGWT>> asyncCallback);
	void removeInstructor(Integer realInstructorID, AsyncCallback<Void> asyncCallback);
	void editInstructor(InstructorGWT realInstructor, AsyncCallback<Void> asyncCallback);
	void addInstructorToDocument(int documentID, InstructorGWT realInstructor, AsyncCallback<InstructorGWT> asyncCallback);
	void addLocationToDocument(int documentID, LocationGWT location, AsyncCallback<LocationGWT> callback);
	void editLocation(LocationGWT source, AsyncCallback<Void> callback);
	void getLocationsForDocument(int documentID, AsyncCallback<List<LocationGWT>> callback);
	void removeLocation(Integer locationID, AsyncCallback<Void> callback);
	void login(String username, AsyncCallback<Integer> asyncCallback);
	void createDocument(String newDocName, AsyncCallback<DocumentGWT> asyncCallback);
	void saveWorkingCopyToOriginalDocument(Integer id, AsyncCallback<Void> asyncCallback);
	void createWorkingCopyForOriginalDocument(Integer originalDocumentID, AsyncCallback<DocumentGWT> callback);
	void getAllOriginalDocumentsByID(
			AsyncCallback<Collection<DocumentGWT>> callback);
	void deleteWorkingCopyDocument(Integer documentID, AsyncCallback<Void> asyncCallback);
	void getScheduleItemsForSchedule(int documentID,
			AsyncCallback<Collection<ScheduleItemGWT>> callback);
	void generateAndAddScheduleItems(int scheduleID,
			Set<Integer> courseIDsToSchedule,
			Set<Integer> availableLocationIDs,
			Set<Integer> availableInstructorIDs,
			AsyncCallback<Collection<ScheduleItemGWT>> callback);
	void addScheduleItem(int scheduleID, int courseID, int locationID,
			int instructorID, DayCombinationGWT days, int startHalfHour,
			int endHalfHour, int section,
			AsyncCallback<ScheduleItemGWT> callback);
	void updateScheduleItem(ScheduleItemGWT scheduleItem,
			AsyncCallback<Void> callback);
	void removeScheduleItem(int scheduleItemID, AsyncCallback<Void> callback);
	void saveWorkingCopyToNewOriginalDocument(DocumentGWT existingDocument,
			String scheduleName, boolean allowOverwrite,
			AsyncCallback<DocumentGWT> asyncCallback);
	
//	void login(String username, AsyncCallback<Void> callback);
//	void getScheduleNames(AsyncCallback<Map<String, UserDataGWT>> callback);
//	
//	void openNewSchedule(String newScheduleName, AsyncCallback<Integer> callback) throws IllegalArgumentException;
//	void openExistingSchedule(
//			int scheduleID,
//			AsyncCallback<String> asyncCallback);
//	void removeSchedule(String schedName, AsyncCallback<Void> callback) throws IllegalArgumentException;
//
//	void getInstructors(AsyncCallback<List<InstructorGWT>> callback) throws IllegalArgumentException;
//	void saveInstructor(InstructorGWT instructor, AsyncCallback<Void> asyncCallback);
//	
//	void getLocations(AsyncCallback<List<LocationGWT>> asyncCallback) throws IllegalArgumentException;
//	
//	void getCourses(AsyncCallback<List<CourseGWT>> asyncCallback) throws IllegalArgumentException;
//
//    void generateSchedule(List<CourseGWT> courses, HashMap<String, ScheduleItemGWT> schedItems, AsyncCallback<List<ScheduleItemGWT>> scheduleItems);
//	void rescheduleCourse(ScheduleItemGWT scheduleItem,
//			List<Integer> days, int startHour, boolean atHalfHour,
//			boolean inSchedule, HashMap<String, ScheduleItemGWT> scheduleItems, AsyncCallback<ScheduleItemList> callback);
//	void removeScheduleItem(ScheduleItemGWT removed, HashMap<String, ScheduleItemGWT> schedItems,
//			AsyncCallback<List<ScheduleItemGWT>> scheduleItems);
//    void getSchedule(HashMap<String, ScheduleItemGWT> scheduleItems, AsyncCallback<List<ScheduleItemGWT>> asyncCallback);
//	void saveSchedule(AsyncCallback<Void> hollaBack);
//	void exportCSV(AsyncCallback<Integer> asyncCallback);
//	
////	void saveCurrentScheduleAs(String schedName, AsyncCallback<Void> callback);
//	
//	void addCourse(CourseGWT toAdd, AsyncCallback<CourseGWT> callback);
//	void editCourse(CourseGWT toEdit, AsyncCallback<Void> callback);
//	void removeCourse(Integer toRemoveID, AsyncCallback<Void> callback);
//	void addInstructor(InstructorGWT toAdd, AsyncCallback<InstructorGWT> callback);
//	void editInstructor(InstructorGWT toEdit, AsyncCallback<Void> callback);
//	void removeInstructor(Integer realInstructorID, AsyncCallback<Void> callback);
//	void addLocation(LocationGWT toAdd, AsyncCallback<LocationGWT> callback);
//	void editLocation(LocationGWT toEdit, AsyncCallback<Void> callback);
//	void removeLocation(Integer toRemoveID, AsyncCallback<Void> callback);
//	void saveCurrentScheduleAsAndOpen(String scheduleName, boolean allowOverwrite,
//			AsyncCallback<Integer> asyncCallback);
}
