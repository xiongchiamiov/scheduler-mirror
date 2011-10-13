package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.gwtScheduleItem;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void getInstructorNames(AsyncCallback<ArrayList<InstructorGWT>> callback)
			throws IllegalArgumentException;
	void saveInstructors(ArrayList<InstructorGWT> instructors, ArrayList<InstructorGWT> deleted, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void getLocationNames(AsyncCallback<ArrayList<LocationGWT>> callback)
			throws IllegalArgumentException;
	void saveLocations(ArrayList<LocationGWT> locations, ArrayList<LocationGWT> deleted, AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	
	void getCourses(AsyncCallback<ArrayList<CourseGWT>> callback)
			throws IllegalArgumentException;
	void saveCourses(ArrayList<CourseGWT> courses, ArrayList<CourseGWT> deleted, AsyncCallback<Void> callback)
			throws IllegalArgumentException;
	
    void getGWTScheduleItems(AsyncCallback<ArrayList<gwtScheduleItem>> scheduleItems)
    		throws IllegalArgumentException;
}
