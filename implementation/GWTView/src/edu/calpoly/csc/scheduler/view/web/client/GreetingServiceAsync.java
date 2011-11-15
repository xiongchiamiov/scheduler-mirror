package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void login(String username, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void getScheduleNames(AsyncCallback<Map<String, Integer>> callback) throws IllegalArgumentException;
	
	void openNewSchedule(String newScheduleName, AsyncCallback<Integer> callback) throws IllegalArgumentException;
	void openExistingSchedule(int scheduleID, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getInstructors(AsyncCallback<ArrayList<InstructorGWT>> callback) throws IllegalArgumentException;
	void saveInstructor(InstructorGWT instructor, AsyncCallback<Void> asyncCallback);
	void saveInstructors(ArrayList<InstructorGWT> instructors, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void saveInstructors(Collection<InstructorGWT> collection, AsyncCallback<Collection<InstructorGWT>> asyncCallback) throws IllegalArgumentException;
	void getInstructors2(AsyncCallback<Collection<InstructorGWT>> asyncCallback) throws IllegalArgumentException;
	
	void getLocations(AsyncCallback<Collection<LocationGWT>> asyncCallback) throws IllegalArgumentException;
	void saveLocations(Collection<LocationGWT> collection, AsyncCallback<Collection<LocationGWT>> asyncCallback) throws IllegalArgumentException;
	
	void getCourses(AsyncCallback<ArrayList<CourseGWT>> callback) throws IllegalArgumentException;
	void saveCourses(ArrayList<CourseGWT> courses, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getCourses2(AsyncCallback<Collection<CourseGWT>> asyncCallback) throws IllegalArgumentException;
	void saveCourses(Collection<CourseGWT> collection, AsyncCallback<Collection<CourseGWT>> asyncCallback) throws IllegalArgumentException;
	
    void getGWTScheduleItems(ArrayList<CourseGWT> courses, AsyncCallback<ArrayList<ScheduleItemGWT>> scheduleItems) throws IllegalArgumentException;
	void rescheduleCourse(ScheduleItemGWT scheduleItem, 
			ArrayList<Integer> days, int startHour, boolean atHalfHour, 
			boolean isScheduled, AsyncCallback<ScheduleItemList> callback) 
					throws IllegalArgumentException;
	void removeScheduleItem(ScheduleItemGWT removed, AsyncCallback<ArrayList<ScheduleItemGWT>> scheduleItems);
	void generateSchedule(AsyncCallback<ArrayList<ScheduleItemGWT>> scheduleItems) throws IllegalArgumentException;
	void getSchedule(AsyncCallback<ArrayList<ScheduleItemGWT>> asyncCallback);
	void saveSchedule(AsyncCallback<Void> asyncCallback);
	void copySchedule(int existingScheduleID, String scheduleName,
			AsyncCallback<Integer> asyncCallback);
}
