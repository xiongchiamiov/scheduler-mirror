package edu.calpoly.csc.scheduler.view.web.client;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	void login(String username);
	Map<String, Integer> getScheduleNames();
	
	Integer openNewSchedule(String newScheduleName);
	Pair<Integer, InstructorGWT> openExistingSchedule(int scheduleID);

	void saveInstructor(InstructorGWT instructor);	
	ArrayList<InstructorGWT> getInstructors() throws IllegalArgumentException;
	Collection<InstructorGWT> saveInstructors(Collection<InstructorGWT> collection);
	Collection<InstructorGWT> getInstructors2();
	
	ArrayList<ScheduleItemGWT> getGWTScheduleItems(ArrayList<CourseGWT> courses) throws IllegalArgumentException;
	ArrayList<ScheduleItemGWT> generateSchedule();	
	ScheduleItemList rescheduleCourse(ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule);
	ArrayList<ScheduleItemGWT> removeScheduleItem(ScheduleItemGWT removed);
	void saveSchedule();
	
	Collection<LocationGWT> getLocations();
	Collection<LocationGWT> saveLocations(Collection<LocationGWT> collection);
	
	ArrayList<CourseGWT> getCourses();
	void saveCourses(ArrayList<CourseGWT> locations);
	
	Collection<CourseGWT> getCourses2();
	Collection<CourseGWT> saveCourses(Collection<CourseGWT> collection);
	
	ArrayList<ScheduleItemGWT> getSchedule();
	int copySchedule(int existingScheduleID, String scheduleName);
	int importFromCSV(String scheduleName, String value);
	
	void removeSchedule(String schedName);
}
