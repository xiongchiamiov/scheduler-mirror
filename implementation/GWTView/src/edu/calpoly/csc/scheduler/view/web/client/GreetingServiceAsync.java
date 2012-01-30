package edu.calpoly.csc.scheduler.view.web.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void login(String username, AsyncCallback<Void> callback);
	void getScheduleNames(AsyncCallback<Map<String, UserDataGWT>> callback);
	
	void openNewSchedule(String newScheduleName, AsyncCallback<Integer> callback) throws IllegalArgumentException;
	void openExistingSchedule(
			int scheduleID,
			AsyncCallback<edu.calpoly.csc.scheduler.view.web.shared.Pair<Integer, InstructorGWT>> asyncCallback);
	void removeSchedule(String schedName, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getInstructors(AsyncCallback<List<InstructorGWT>> callback) throws IllegalArgumentException;
	void saveInstructor(InstructorGWT instructor, AsyncCallback<Void> asyncCallback);
	
	void getLocations(AsyncCallback<List<LocationGWT>> asyncCallback) throws IllegalArgumentException;
	
	void getCourses(AsyncCallback<List<CourseGWT>> asyncCallback) throws IllegalArgumentException;

    void generateSchedule(List<CourseGWT> courses, HashMap<String, ScheduleItemGWT> schedItems, AsyncCallback<List<ScheduleItemGWT>> scheduleItems);
	void rescheduleCourse(ScheduleItemGWT scheduleItem,
			List<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule, HashMap<String, ScheduleItemGWT> scheduleItems, AsyncCallback<ScheduleItemList> callback);
	void removeScheduleItem(ScheduleItemGWT removed, HashMap<String, ScheduleItemGWT> schedItems,
			AsyncCallback<List<ScheduleItemGWT>> scheduleItems);
    void getSchedule(HashMap<String, ScheduleItemGWT> scheduleItems, AsyncCallback<List<ScheduleItemGWT>> asyncCallback);
	void saveSchedule(AsyncCallback<Void> hollaBack);
    void copySchedule(int existingScheduleID, String scheduleName,
			AsyncCallback<Integer> asyncCallback);
	void exportCSV(AsyncCallback<Integer> asyncCallback);
	
	void saveCurrentScheduleAs(String schedName, AsyncCallback<Void> callback);
	
	void addCourse(CourseGWT toAdd, AsyncCallback<CourseGWT> callback);
	void editCourse(CourseGWT toEdit, AsyncCallback<Void> callback);
	void removeCourse(CourseGWT toRemove, AsyncCallback<Void> callback);
	void addInstructor(InstructorGWT toAdd, AsyncCallback<InstructorGWT> callback);
	void editInstructor(InstructorGWT toEdit, AsyncCallback<Void> callback);
	void removeInstructor(InstructorGWT toRemove, AsyncCallback<Void> callback);
	void addLocation(LocationGWT toAdd, AsyncCallback<LocationGWT> callback);
	void editLocation(LocationGWT toEdit, AsyncCallback<Void> callback);
	void removeLocation(LocationGWT toRemove, AsyncCallback<Void> callback);
}
