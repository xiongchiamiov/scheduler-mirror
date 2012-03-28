package scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayCombinationGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.OldScheduleItemGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ScheduleItemList;

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
	void createOriginalDocument(String newDocName, AsyncCallback<DocumentGWT> asyncCallback);
	void saveWorkingCopyToOriginalDocument(Integer id, AsyncCallback<Void> asyncCallback);
	void createWorkingCopyForOriginalDocument(Integer originalDocumentID, AsyncCallback<DocumentGWT> callback);
	void deleteWorkingCopyDocument(Integer documentID, AsyncCallback<Void> asyncCallback);
	
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
	@Deprecated
	void removeScheduleItem(OldScheduleItemGWT removed,
			HashMap<String, OldScheduleItemGWT> mSchedItems,
			AsyncCallback<List<OldScheduleItemGWT>> callback);
	
	
	
	/**
	 * Gets all the schedule items for a given document id.
	 * This function uses the deprecated type OldScheduleItemGWT, which soon
	 * isn't going to be supported. The new function is getScheduleItems below,
	 * which returns the new ScheduleItemGWT. This function is here as an
	 * intermediate, so you can first refactor your code to use the new calls,
	 * debug it, make it work, then later you can refactor your code to use
	 * the new ScheduleItemGWT.
	 * @param documentID
	 * @param callback
	 */
	void intermediateGetScheduleItems(
			int documentID,
			AsyncCallback<Collection<OldScheduleItemGWT>> callback);
	
	/**
	 * Inserts a schedule item into the document's schedule.
	 * This function uses the deprecated type OldScheduleItemGWT, which soon
	 * isn't going to be supported. The new function is insertScheduleItem below,
	 * which takes the new ScheduleItemGWT. This function is here as an
	 * intermediate, so you can first refactor your code to use the new calls,
	 * debug it, make it work, then later you can refactor your code to use
	 * the new ScheduleItemGWT.
	 * @param documentID
	 * @param scheduleItem
	 * @param callback
	 */
	void intermediateInsertScheduleItem(
			int documentID,
			OldScheduleItemGWT scheduleItem,
			AsyncCallback<Void> callback);
	
	/**
	 * Update's a document's schedule's schedule item.
	 * This function uses the deprecated type OldScheduleItemGWT, which soon
	 * isn't going to be supported. The new function is updateScheduleItem below,
	 * which takes the new ScheduleItemGWT. This function is here as an
	 * intermediate, so you can first refactor your code to use the new calls,
	 * debug it, make it work, then later you can refactor your code to use
	 * the new ScheduleItemGWT.
	 * @param documentID
	 * @param oldItemOldGWT The old item. This will be replaced by the new item.
	 * @param newItemOldGWT The new item
	 * @param callback
	 */
	void intermediateUpdateScheduleItem(
			int documentID,
			OldScheduleItemGWT oldItemOldGWT,
			OldScheduleItemGWT newItemOldGWT,
			AsyncCallback<Void> callback);
	
	/**
	 * Generates schedule items for all of the schedulable unscheduled courses.
	 * This function uses the deprecated type OldScheduleItemGWT, which soon
	 * isn't going to be supported. The new function is generateRestOfSchedule below,
	 * which returns the new ScheduleItemGWT. This function is here as an
	 * intermediate, so you can first refactor your code to use the new calls,
	 * debug it, make it work, then later you can refactor your code to use
	 * the new ScheduleItemGWT.
	 * @param documentID
	 * @param callback
	 */
	void intermediateGenerateRestOfSchedule(
			int documentID,
			AsyncCallback<Collection<OldScheduleItemGWT>> callback);
	
	/**
	 * Removes a schedule items from a document's schedule.
	 * @param documentID
	 * @param oldItemOldGWT
	 * @param asyncCallback
	 */
	void intermediateRemoveScheduleItem(int documentID,
			OldScheduleItemGWT oldItemOldGWT,
			AsyncCallback<Void> asyncCallback);
	
	
	
	
	/**
	 * Inserts a new schedule item into the schedule with the given ID. The 
	 * schedule item should have an id of -1.
	 * @param scheduleID
	 * @param scheduleItem this item's ID must be negative.
	 * @param callback
	 * @return The new schedule item. Should contain all the same information
	 * as scheduleItem but with the ID set.
	 */
	void insertScheduleItem(int scheduleID, ScheduleItemGWT scheduleItem,
			AsyncCallback<Collection<ScheduleItemGWT>> callback);
	
	/**
	 * Generates schedule items for all unscheduled schedulable courses.
	 * @param scheduleID
	 * @param callback
	 */
	void generateRestOfSchedule(int scheduleID,
			AsyncCallback<Collection<ScheduleItemGWT>> callback);
	
	/**
	 * Updates the schedule item with the same item ID to the new values.
	 * @param itemGWT
	 * @param callback
	 */
	void updateScheduleItem(ScheduleItemGWT itemGWT,
			AsyncCallback<Collection<ScheduleItemGWT>> callback);
	
	/**
	 * Gets all schedule items in the schedule with id scheduleID.
	 * @param scheduleID
	 * @param callback
	 */
	void getScheduleItems(int scheduleID,
			AsyncCallback<Collection<ScheduleItemGWT>> callback);
	
	/**
	 * Removes the schedule item with the given id.
	 * @param itemGWT
	 * @param callback
	 */
	void newRemoveScheduleItem(ScheduleItemGWT itemGWT,
			AsyncCallback<Collection<ScheduleItemGWT>> callback);
	void updateDocument(DocumentGWT document, AsyncCallback<Void> callback);
	void moveWorkingCopyToNewOriginalDocument(Integer workingCopyID,
			String scheduleName, boolean allowOverwrite,
			AsyncCallback<Void> callback);
	void findDocumentByID(int automaticOpenDocumentID, AsyncCallback<DocumentGWT> callback);
	void getAllOriginalDocuments(AsyncCallback<Collection<DocumentGWT>> callback);
}
