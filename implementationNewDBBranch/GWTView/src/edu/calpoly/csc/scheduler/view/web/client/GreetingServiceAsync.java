package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

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
	void saveWorkingCopyToNewOriginalDocument(DocumentGWT existingDocument,
			String scheduleName, boolean allowOverwrite,
			AsyncCallback<DocumentGWT> asyncCallback);
	
	@Deprecated
	void getSchedule(HashMap<String, OldScheduleItemGWT> mSchedItems,
			AsyncCallback<List<OldScheduleItemGWT>> asyncCallback);
	@Deprecated
	void generateSchedule(List<CourseGWT> mAllCourses,
			HashMap<String, OldScheduleItemGWT> mSchedItems,
			AsyncCallback<List<OldScheduleItemGWT>> asyncCallback);
	@Deprecated
	void rescheduleCourse(OldScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule,
			HashMap<String, OldScheduleItemGWT> mSchedItems,
			AsyncCallback<ScheduleItemList> asyncCallback);
	void removeScheduleItem(int scheduleID, OldScheduleItemGWT oldItemOldGWT,
			AsyncCallback<Void> asyncCallback);
	
	
	/**
	 * Gets all the schedule items for a given document id.
	 * @param scheduleID
	 * @param callback
	 */
	void getScheduleItems(
			int documentID,
			AsyncCallback<Collection<OldScheduleItemGWT>> callback);
	
	void insertScheduleItem(
			int documentID,
			OldScheduleItemGWT scheduleItem,
			AsyncCallback<Void> callback);
	
	void updateScheduleItem(
			int documentID,
			OldScheduleItemGWT oldItemOldGWT,
			OldScheduleItemGWT newItemOldGWT,
			AsyncCallback<Void> callback);
	
	void generateRestOfSchedule(
			int documentID,
			AsyncCallback<Collection<OldScheduleItemGWT>> callback);
	
	void removeScheduleItem(
			OldScheduleItemGWT removed,
			HashMap<String, OldScheduleItemGWT> mSchedItems,
			AsyncCallback<List<OldScheduleItemGWT>> callback);
}
