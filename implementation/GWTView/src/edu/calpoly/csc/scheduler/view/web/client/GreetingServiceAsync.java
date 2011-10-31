package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;
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
	void getInstructorNames(AsyncCallback<ArrayList<InstructorGWT>> callback)
			throws IllegalArgumentException;
	void saveInstructors(ArrayList<InstructorGWT> instructors, AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	
	void selectSchedule(Integer scheduleID, AsyncCallback<Void> callback) throws IllegalArgumentException;
		

	void newSchedule(AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void getScheduleNames(String username, AsyncCallback<Map<Integer, String>> callback)
			throws IllegalArgumentException;

	void getLocationNames(AsyncCallback<ArrayList<LocationGWT>> callback)
			throws IllegalArgumentException;
	void saveLocations(ArrayList<LocationGWT> locations, AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	
	void getCourses(AsyncCallback<ArrayList<CourseGWT>> callback)
			throws IllegalArgumentException;
	void saveCourses(ArrayList<CourseGWT> courses, AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	
	void generateSchedule(AsyncCallback<ArrayList<ScheduleItemGWT>> scheduleItems)
    		throws IllegalArgumentException;
	
    void getGWTScheduleItems(ArrayList<CourseGWT> courses, 
    		AsyncCallback<ArrayList<ScheduleItemGWT>> scheduleItems)
    		throws IllegalArgumentException;

	void getCannedCourses(AsyncCallback<ArrayList<CourseGWT>> callback)
			throws IllegalArgumentException;
    
    void getTestGWTScheduleItems(AsyncCallback<ArrayList<ScheduleItemGWT>> scheduleItems)
    		throws IllegalArgumentException;
    
	void rescheduleCourse(ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour, AsyncCallback<ArrayList<ScheduleItemGWT>> callback)
	         throws IllegalArgumentException;
	void saveInstructor(InstructorGWT instructor, AsyncCallback<Void> asyncCallback);
}
