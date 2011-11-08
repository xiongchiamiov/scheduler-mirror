package edu.calpoly.csc.scheduler.view.web.client;



import java.util.Collection;
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
	
	Integer openNewSchedule(String newScheduleName);
	void openExistingSchedule(int scheduleID);

	void saveInstructor(InstructorGWT instructor);	
	Collection<InstructorGWT> getInstructors() throws IllegalArgumentException;
	void saveInstructors(Collection<InstructorGWT> instructors) throws IllegalArgumentException;
	
	Collection<ScheduleItemGWT> getGWTScheduleItems(Collection<CourseGWT> courses) throws IllegalArgumentException;
	Collection<ScheduleItemGWT> generateSchedule();	
	Collection<ScheduleItemGWT> rescheduleCourse(ScheduleItemGWT scheduleItem,
			Collection<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule);
	
	Collection<LocationGWT> getLocations();
	Collection<LocationGWT> saveLocations(Collection<LocationGWT> locations);
	
	Collection<CourseGWT> getCourses();
	void saveCourses(Collection<CourseGWT> locations);
	
	void resetSchedule();
}
