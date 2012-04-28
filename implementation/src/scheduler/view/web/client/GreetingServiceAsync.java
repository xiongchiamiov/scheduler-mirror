package scheduler.view.web.client;

import java.util.Collection;
import java.util.List;

import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.ScheduleItemGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
	void associateWorkingCopyWithNewOriginalDocument(Integer workingCopyID,
			String scheduleName, boolean allowOverwrite,
			AsyncCallback<Void> callback);
	void findDocumentByID(int automaticOpenDocumentID, AsyncCallback<DocumentGWT> callback);
	void getAllOriginalDocuments(AsyncCallback<Collection<DocumentGWT>> callback);
	void removeOriginalDocument(Integer id, AsyncCallback<Void> asyncCallback);
	
	void updateCourses(
			int documentID,
			List<CourseGWT> addedResources,
			Collection<CourseGWT> editedResources,
			List<Integer> deletedResourcesIDs,
			AsyncCallback<List<Integer>> asyncCallback);
}
