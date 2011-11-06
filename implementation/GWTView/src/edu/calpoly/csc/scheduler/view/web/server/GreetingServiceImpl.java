package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.CouldNotBeScheduledException;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	private Model model;
    private Schedule schedule;
    private HashMap<String, Course> availableCourses;
    private HashMap<String, ScheduleItem> scheduleItems;
	
    public void login(String username) {
    	model = new Model(username);
    }
    
    public Map<String, Integer> getScheduleNames() {
    	return model.getSchedules();
    }
    
    public Integer openNewSchedule(String newScheduleName) {
		model.openNewSchedule(newScheduleName);
		return model.getScheduleID();
    }
    
    public void openExistingSchedule(int scheduleID) {
    	model.openExistingSchedule(scheduleID);
    }

	public ArrayList<InstructorGWT> getInstructors() throws IllegalArgumentException {
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		for (Instructor instructor : model.getInstructors()) {
			results.add(Conversion.toGWT(instructor));
		}
		return results;
	}
	
	public void saveInstructors(ArrayList<InstructorGWT> instructors) throws IllegalArgumentException {
		assert(model != null);

		HashMap<String, Instructor> newInstructorsByUserID = new LinkedHashMap<String, Instructor>();

		for (InstructorGWT instructorGWT : instructors) {
			Instructor instructor = Conversion.fromGWT(instructorGWT);
			newInstructorsByUserID.put(instructor.getUserID(), instructor);
			
			displayInstructorPrefs(instructor);
			
			model.saveInstructor(instructor);
		}
		
		for (Instructor instructor : model.getInstructors())
			if (newInstructorsByUserID.get(instructor.getUserID()) == null)
				model.removeInstructor(instructor);
		assert(model.getInstructors().size() == instructors.size());
	}
	
	private void displayInstructorPrefs(Instructor instructor) {
		System.out.println("Prefs for instructor " + instructor.getLastName());
		
		for (Day day : instructor.getTimePreferences().keySet())
			for (Time time : instructor.getTimePreferences().get(day).keySet())
				System.out.println("Day " + day.getNum() + " time " + time.getHour() + ":" + time.getMinute() + " is " + instructor.getTimePreferences().get(day).get(time).getDesire());
	}

	public ArrayList<ScheduleItemGWT> generateSchedule() {
		assert(model != null);
		
		Collection<Course> coursesToGenerate = model.getCourses();
		
		// TODO: fix this hack.
		for (Course course : coursesToGenerate) {
			assert(course.getDays().size() > 0);
			if (course.getLength() < course.getDays().size() * 2) {
				course.setLength(course.getDays().size() * 2);
				System.err.println("Warning: the course length was too low, automatically set it to " + course.getLength());
			}
		}
		
		for (Instructor instructor : model.getInstructors())
			System.out.println("outside, num instructor day prefs for " + instructor.getLastName() + ": " + instructor.getTimePreferences().size());
		
		Collection<ScheduleItem> scheduleItems = schedule.generate(coursesToGenerate);
		System.out.println("schedule items: " + schedule.getItems().size());

		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
	    for(ScheduleItem item : scheduleItems)
	    	gwtItems.add(Conversion.toGWT(item));
		return gwtItems;
	}
	
	public ArrayList<ScheduleItemGWT> getGWTScheduleItems(ArrayList<CourseGWT> courses)
	{
	 assert(model != null);
     scheduleItems = new HashMap<String, ScheduleItem>();
     
	 Collection<Course> coursesToGenerate = new LinkedList<Course>();
     for(CourseGWT course : courses)
    	 coursesToGenerate.add(availableCourses.get(course.getDept() + course.getCatalogNum()));

		// TODO: fix this hack.
		for (Course course : coursesToGenerate) {
			assert(course.getDays().size() > 0);
			if (course.getLength() < course.getDays().size() * 2) {
				course.setLength(course.getDays().size() * 2);
				System.err.println("Warning: the course length was too low, automatically set it to " + course.getLength());
			}
		}

		for (Instructor instructor : model.getInstructors())
			System.out.println("num instructor day prefs for " + instructor.getLastName() + ": " + instructor.getTimePreferences().size());
		
     schedule.generate(coursesToGenerate);
		
	 ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();

	 for(ScheduleItem item : schedule.getItems())
	 {         
	  gwtItems.add(Conversion.toGWT(item));
	  scheduleItems.put(item.getCourse().getDept() + 
	   item.getCourse().getCatalogNum() + 
	   item.getSection(), item);
	 } 
	 return gwtItems;
	}
	
	public ArrayList<ScheduleItemGWT> rescheduleCourse(ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour, boolean inSchedule)
	{
	 assert(model != null);
	 ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
	 Course course;
	 int numberOfDays = days.size();
	 Day[] daysScheduled = new Day[numberOfDays];
	 Week daysInWeek;
	 Time startTime;
	 int i;
	 ScheduleItem moved; 
	 String schdItemKey = scheduleItem.getDept() + 
			  scheduleItem.getCatalogNum() + 
			  scheduleItem.getSection();
	 
	 for(i = 0; i < numberOfDays; i++)
	 {
	  switch(days.get(i))
	  {
	   case 1 : daysScheduled[i] = (Day.MON); break;
	   case 2 : daysScheduled[i] = (Day.TUE); break;
	   case 3 : daysScheduled[i] = (Day.WED); break;
	   case 4 : daysScheduled[i] = (Day.THU); break;
	   case 5 : daysScheduled[i] = (Day.FRI); break;
	  }
	 }
	 
	 daysInWeek = new Week(daysScheduled);
	 startTime = new Time(startHour, (atHalfHour? 30 : 0));
	 
	 if(inSchedule)
	 {
	  moved = scheduleItems.get(schdItemKey);
	  try
	  {
	   schedule.move(moved, daysInWeek, startTime);
	  }
	  catch(CouldNotBeScheduledException e)
	  {
	   System.out.println("Could not be scheduled");
	   return null;
	  }
	 }
	 else
	 {
	  course = availableCourses.get(scheduleItem.getDept() + 
				 scheduleItem.getCatalogNum()); 
	  schedule.genItem(course, daysInWeek, startTime);
	 }
	 
	 scheduleItems = new HashMap<String, ScheduleItem>();
	 for(ScheduleItem item : schedule.getItems())
	 {         
	  gwtItems.add(Conversion.toGWT(item));
	  scheduleItems.put(item.getCourse().getDept() + 
	   item.getCourse().getCatalogNum() + 
	   item.getSection(), item);
	 } 
	 return gwtItems;
	}
	
	@Override
	public ArrayList<LocationGWT> getLocations() {
		ArrayList<LocationGWT> results = new ArrayList<LocationGWT>();
		for (Location location : model.getLocations())
			results.add(Conversion.toGWT(location));
		return results;
	}


	@Override
	public void saveLocations(ArrayList<LocationGWT> locations) {
		HashMap<String, Location> newLocationsByUserID = new LinkedHashMap<String, Location>();

		for (LocationGWT locationGWT : locations) {
			Location location = Conversion.fromGWT(locationGWT);
			newLocationsByUserID.put(location.getBuilding() + "-" + location.getRoom(), location);
			model.saveLocation(location);
		}
		
		for (Location location : model.getLocations())
			if (newLocationsByUserID.get(location.getBuilding() + "-" + location.getRoom()) == null)
				model.removeLocation(location);

		assert(model.getLocations().size() == locations.size());
	}
	
	@Override
	public ArrayList<CourseGWT> getCourses() {		
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		availableCourses = new HashMap<String, Course>();
		for (Course course : model.getCourses())
		{
			course.setLength(6);
			availableCourses.put(course.getDept()+course.getCatalogNum(), course);
			results.add(Conversion.toGWT(course));
		}
		return results;
	}


	@Override
	public void saveCourses(ArrayList<CourseGWT> courses) {
		HashMap<String, Course> newLocationsByUserID = new LinkedHashMap<String, Course>();

		for (CourseGWT courseGWT : courses) {
			Course course = Conversion.fromGWT(courseGWT);
			newLocationsByUserID.put(course.getDept() + "-" + course.getCatalogNum(), course);
			model.saveCourse(course);
		}
		
		for (Course course : model.getCourses())
			if (newLocationsByUserID.get(course.getDept() + "-" + course.getCatalogNum()) == null)
				model.removeCourse(course);

		
		assert(model.getCourses().size() == courses.size());
	}

	private boolean hasPreferences(Instructor instructor) {
		int totalDesire = 0;
		for (Day day : instructor.getTimePreferences().keySet()) {
			LinkedHashMap<Time, TimePreference> dayPrefs = instructor.getTimePreferences().get(day);
			for (Time time : dayPrefs.keySet()) {
				TimePreference timePrefs = dayPrefs.get(time);
				totalDesire += timePrefs.getDesire();
			}
		}
		return totalDesire > 0;
	}
	
	@Override
	public void saveInstructor(InstructorGWT instructorGWT) {
		Instructor instructor = Conversion.fromGWT(instructorGWT);
		
		displayInstructorPrefs(instructor);

		model.saveInstructor(instructor);
	}

	@Override
	public void resetSchedule() 
	{
	 schedule = new Schedule(model.getInstructors(), model.getLocations());
	 scheduleItems = new HashMap<String, ScheduleItem>();
	}
}
