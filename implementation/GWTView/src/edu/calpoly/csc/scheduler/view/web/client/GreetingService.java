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
	void login(String username);
	Map<String, Integer> getScheduleNames();
	
	void openNewSchedule(String newScheduleName);
	void openExistingSchedule(String scheduleID);

	void saveInstructor(InstructorGWT instructor);	
	ArrayList<InstructorGWT> getInstructors() throws IllegalArgumentException;
	void saveInstructors(ArrayList<InstructorGWT> instructors) throws IllegalArgumentException;
	
	ArrayList<ScheduleItemGWT> getGWTScheduleItems(ArrayList<CourseGWT> courses) throws IllegalArgumentException;
	ArrayList<ScheduleItemGWT> generateSchedule();	
	ArrayList<ScheduleItemGWT> rescheduleCourse(ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule);
	
	ArrayList<LocationGWT> getLocations();
	void saveLocations(ArrayList<LocationGWT> locations);
	
	ArrayList<CourseGWT> getCourses();
	void saveCourses(ArrayList<CourseGWT> locations);
}
