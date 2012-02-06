package edu.calpoly.csc.scheduler.view.web.client;



import java.util.HashMap;
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
	void login(String username);
	
	// Managing Schedules
	Map<String, UserDataGWT> getScheduleNames();
	Integer openNewSchedule(String newScheduleName);
	Pair<Integer, InstructorGWT> openExistingSchedule(int scheduleID);
	int copySchedule(int existingScheduleID, String scheduleName);
	void removeSchedule(String schedName);

	// Instructors
	void saveInstructor(InstructorGWT instructor);	
	List<InstructorGWT> getInstructors();
	
	List<CourseGWT> getCourses();
	
	// Locations
	List<LocationGWT> getLocations();
	
	// Schedule Items
	List<ScheduleItemGWT> generateSchedule(List<CourseGWT> courses, HashMap<String, ScheduleItemGWT> scheduleItems);
	ScheduleItemList rescheduleCourse(ScheduleItemGWT scheduleItem, List<Integer> days, int startHour, boolean atHalfHour, boolean inSchedule, HashMap<String, ScheduleItemGWT> scheduleItems);
	List<ScheduleItemGWT> removeScheduleItem(ScheduleItemGWT removed, HashMap<String, ScheduleItemGWT> scheduleItems);
	List<ScheduleItemGWT> getSchedule(HashMap<String, ScheduleItemGWT> scheduleItems);
	void saveSchedule();
	int exportCSV();
	
	void saveCurrentScheduleAs(String schedName);

	CourseGWT addCourse(CourseGWT toAdd);

	void editCourse(CourseGWT toEdit);

	void removeCourse(Integer toRemoveID);

	InstructorGWT addInstructor(InstructorGWT toAdd);

	void editInstructor(InstructorGWT toEdit);

	void removeInstructor(Integer realInstructorID);

	LocationGWT addLocation(LocationGWT toAdd);

	void editLocation(LocationGWT toEdit);

	void removeLocation(Integer toRemoveID);
}
