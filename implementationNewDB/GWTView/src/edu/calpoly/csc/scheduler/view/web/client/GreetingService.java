package edu.calpoly.csc.scheduler.view.web.client;



import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	// Managing User
	void login(String username);
	
	// Managing Schedules
	Map<String, UserDataGWT> getScheduleNames();
	Integer openNewSchedule(String newScheduleName);
	Pair<Integer, InstructorGWT> openExistingSchedule(int scheduleID);
	int copySchedule(int existingScheduleID, String scheduleName);
	int importFromCSV(String scheduleName, String value);
	void removeSchedule(String schedName);

	// Instructors
	void saveInstructor(InstructorGWT instructor);	
	List<InstructorGWT> getInstructors();
	List<InstructorGWT> saveInstructors(List<InstructorGWT> added, List<InstructorGWT> edited, List<InstructorGWT> removed);
	
	// Courses
	List<CourseGWT> saveCourses(List<CourseGWT> added, List<CourseGWT> edited, List<CourseGWT> removed);
	List<CourseGWT> getCourses();
	
	// Locations
	List<LocationGWT> getLocations();
	List<LocationGWT> saveLocations(List<LocationGWT> added, List<LocationGWT> edited, List<LocationGWT> removed);
	
	// Schedule Items
	List<ScheduleItemGWT> getGWTScheduleItems(List<CourseGWT> courses);
	List<ScheduleItemGWT> generateSchedule();	
	ScheduleItemList rescheduleCourse(ScheduleItemGWT scheduleItem, List<Integer> days, int startHour, boolean atHalfHour, boolean inSchedule);
	List<ScheduleItemGWT> removeScheduleItem(ScheduleItemGWT removed);
	void saveSchedule();
	List<ScheduleItemGWT> getSchedule();
	int exportCSV();
}
