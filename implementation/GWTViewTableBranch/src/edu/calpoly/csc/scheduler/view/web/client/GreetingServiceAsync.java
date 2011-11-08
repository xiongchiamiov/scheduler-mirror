package edu.calpoly.csc.scheduler.view.web.client;

import java.util.Collection;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void login(String username, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void getScheduleNames(AsyncCallback<Map<String, Integer>> callback) throws IllegalArgumentException;
	
	void openNewSchedule(String newScheduleName, AsyncCallback<Integer> callback) throws IllegalArgumentException;
	void openExistingSchedule(int scheduleID, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getInstructors(AsyncCallback<Collection<InstructorGWT>> callback) throws IllegalArgumentException;
	void saveInstructor(InstructorGWT instructor, AsyncCallback<Void> asyncCallback);
	void saveInstructors(Collection<InstructorGWT> instructors, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getLocations(AsyncCallback<Collection<LocationGWT>> callback) throws IllegalArgumentException;
	void saveLocations(Collection<LocationGWT> locations, AsyncCallback<Collection<LocationGWT>> callback) throws IllegalArgumentException;
	
	void getCourses(AsyncCallback<Collection<CourseGWT>> callback) throws IllegalArgumentException;
	void saveCourses(Collection<CourseGWT> courses, AsyncCallback<Void> callback) throws IllegalArgumentException;

    void getGWTScheduleItems(Collection<CourseGWT> courses, AsyncCallback<Collection<ScheduleItemGWT>> scheduleItems) throws IllegalArgumentException;
	void rescheduleCourse(ScheduleItemGWT scheduleItem, 
			Collection<Integer> days, int startHour, boolean atHalfHour, 
			boolean isScheduled, 
			AsyncCallback<Collection<ScheduleItemGWT>> callback) 
					throws IllegalArgumentException;
	void generateSchedule(AsyncCallback<Collection<ScheduleItemGWT>> scheduleItems) throws IllegalArgumentException;
	void resetSchedule(AsyncCallback<Void> asyncCallback);
}
