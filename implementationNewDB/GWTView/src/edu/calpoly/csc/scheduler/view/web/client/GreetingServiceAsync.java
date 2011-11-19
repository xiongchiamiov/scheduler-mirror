package edu.calpoly.csc.scheduler.view.web.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
	void login(String username, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void getScheduleNames(AsyncCallback<Map<String, UserDataGWT>> callback);
	
	void openNewSchedule(String newScheduleName, AsyncCallback<Integer> callback) throws IllegalArgumentException;
	void openExistingSchedule(
			int scheduleID,
			AsyncCallback<edu.calpoly.csc.scheduler.view.web.shared.Pair<Integer, InstructorGWT>> asyncCallback);
	void removeSchedule(String schedName, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getInstructors(AsyncCallback<List<InstructorGWT>> callback) throws IllegalArgumentException;
	void saveInstructor(InstructorGWT instructor, AsyncCallback<Void> asyncCallback);
	void saveInstructors(List<InstructorGWT> added, List<InstructorGWT> edited, List<InstructorGWT> removed, AsyncCallback<List<InstructorGWT>> asyncCallback) throws IllegalArgumentException;
	
	void getLocations(AsyncCallback<List<LocationGWT>> asyncCallback) throws IllegalArgumentException;
	
	void getCourses(AsyncCallback<List<CourseGWT>> asyncCallback) throws IllegalArgumentException;

    void getGWTScheduleItems(List<CourseGWT> courses,
			AsyncCallback<List<ScheduleItemGWT>> scheduleItems);
	void rescheduleCourse(ScheduleItemGWT scheduleItem,
			List<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule, AsyncCallback<ScheduleItemList> callback);
	void removeScheduleItem(ScheduleItemGWT removed,
			AsyncCallback<List<ScheduleItemGWT>> scheduleItems);
	void generateSchedule(
			AsyncCallback<List<ScheduleItemGWT>> scheduleItems);
	void getSchedule(AsyncCallback<List<ScheduleItemGWT>> asyncCallback);
	void saveSchedule(AsyncCallback<Void> asyncCallback);
	void copySchedule(int existingScheduleID, String scheduleName,
			AsyncCallback<Integer> asyncCallback);
	void importFromCSV(String scheduleName, String value,
			AsyncCallback<Integer> asyncCallback);
	void saveCourses(List<CourseGWT> added, List<CourseGWT> edited,
			List<CourseGWT> removed,
			AsyncCallback<List<CourseGWT>> callback);
	void saveLocations(List<LocationGWT> added,
			List<LocationGWT> edited, List<LocationGWT> removed,
			AsyncCallback<List<LocationGWT>> callback);
	void exportCSV(AsyncCallback<Integer> asyncCallback);
}
