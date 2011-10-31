package edu.calpoly.csc.scheduler.view.web.client;



import java.util.ArrayList;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	ArrayList<InstructorGWT> getInstructorNames() throws IllegalArgumentException;
	
	void saveInstructors(ArrayList<InstructorGWT> instructors) throws IllegalArgumentException;
	
	void selectSchedule(Integer scheduleID);
	
	Map<Integer, String> getScheduleNames(String username);
	
	ArrayList<ScheduleItemGWT> getGWTScheduleItems(ArrayList<CourseGWT> courses) throws IllegalArgumentException;
	
	ArrayList<ScheduleItemGWT> getTestGWTScheduleItems() throws IllegalArgumentException;

	ArrayList<LocationGWT> getLocationNames();

	void saveLocations(ArrayList<LocationGWT> locations);
	
	ArrayList<CourseGWT> getCourses();

	ArrayList<CourseGWT> getCannedCourses();

	void newSchedule(String schedName);

	void saveCourses(ArrayList<CourseGWT> locations);
	
	ArrayList<ScheduleItemGWT> generateSchedule();
	
	ArrayList<ScheduleItemGWT> rescheduleCourse(ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour);

	void saveInstructor(InstructorGWT instructor);
}
