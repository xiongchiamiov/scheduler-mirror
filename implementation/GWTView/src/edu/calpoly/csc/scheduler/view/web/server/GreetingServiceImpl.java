package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;
import edu.calpoly.csc.scheduler.model.schedule.*;
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
		return model.getDb().getScheduleID();
    }
    
    public void openExistingSchedule(int scheduleID) {
    	model.openExistingSchedule(scheduleID);
    }

	public ArrayList<InstructorGWT> getInstructors() throws IllegalArgumentException {
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		for (Instructor instructor : model.getDb().getInstructorDB().getData()) {
			System.out.println("Model returning instructor " + instructor.getFirstName() + " has prefs? " + hasPreferences(instructor));
			results.add(Conversion.toGWT(instructor));
		}
		System.out.println("Model returning " + results.size() + " instructors");
		return results;
	}
	
	public void saveInstructors(ArrayList<InstructorGWT> instructors) throws IllegalArgumentException {
		assert(model != null);
		
		InstructorDB idb = model.getDb().getInstructorDB();

		HashMap<String, Instructor> newInstructorsByUserID = new LinkedHashMap<String, Instructor>();

		for (InstructorGWT instructorGWT : instructors) {
			Instructor instructor = Conversion.fromGWT(instructorGWT);
			newInstructorsByUserID.put(instructor.getUserID(), instructor);
			idb.saveData(instructor);
		}
		
		for (Instructor instructor : model.getDb().getInstructorDB().getData())
			if (newInstructorsByUserID.get(instructor.getUserID()) == null)
				idb.removeData(instructor);
		
		assert(model.getDb().getInstructorDB().getData().size() == instructors.size());
	}

	public ArrayList<ScheduleItemGWT> generateSchedule() {
		assert(model != null);
		Database db = model.getDb();
		ArrayList<Instructor> instructors = db.getInstructorDB().getData();
		ArrayList<Course> courses = db.getCourseDB().getData();
		ArrayList<Location> locations = db.getLocationDB().getData();
		
		Schedule schedule = new Schedule();
		schedule.generate(new Vector<Course>(courses));
		
		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
		
	    for(ScheduleItem item : schedule.getItems())
	    	gwtItems.add(Conversion.toGWT(item));
	    
		return gwtItems;
	}
	
	public ArrayList<ScheduleItemGWT> getGWTScheduleItems(ArrayList<CourseGWT> courses)
	{
	 assert(model != null);
	 Database db = model.getDb();
	 CourseDB cdb = db.getCourseDB();
	 ArrayList<Instructor> instructors = db.getInstructorDB().getData();
     ArrayList<Location> locations = db.getLocationDB().getData();
     ArrayList<Course> modelCourses = new ArrayList<Course>();
     scheduleItems = new HashMap<String, ScheduleItem>();
     
     for(CourseGWT course : courses)
     {
      modelCourses.add(availableCourses.get(course.getDept() + course.getCatalogNum()));	 
     }
     if(schedule == null)
     {
      schedule = new Schedule(new Vector<Instructor>(instructors), new Vector<Location>(locations));
     }
	 schedule.generate(modelCourses);		
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
	 Database db = model.getDb();
	 int i;
	 ScheduleItem moved; 
	 String schdItemKey = scheduleItem.getDept() + 
			  scheduleItem.getCatalogNum() + 
			  scheduleItem.getSection();
	 
	 if(schedule == null)
	 {
	  schedule = new Schedule(db.getInstructorDB().getData(), 
			  db.getLocationDB().getData());
	 }
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
	 
	 course = availableCourses.get(scheduleItem.getDept() + 
			 scheduleItem.getCatalogNum()); 
	 daysInWeek = new Week(daysScheduled);
	 startTime = new Time(startHour, (atHalfHour? 30 : 0));
	 
	 if(inSchedule)
	 {
	  moved = scheduleItems.get(schdItemKey);
	  schedule.remove(moved);
	 }
	 schedule.genItem(course, daysInWeek, startTime);
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
		for (Location location : model.getDb().getLocationDB().getData())
			results.add(Conversion.toGWT(location));
		return results;
	}


	@Override
	public void saveLocations(ArrayList<LocationGWT> locations) {
		LocationDB ldb = model.getDb().getLocationDB();

		HashMap<String, Location> newLocationsByUserID = new LinkedHashMap<String, Location>();

		for (LocationGWT locationGWT : locations) {
			Location location = Conversion.fromGWT(locationGWT);
			newLocationsByUserID.put(location.getBuilding() + "-" + location.getRoom(), location);
			ldb.saveData(location);
		}
		
		for (Location location : model.getDb().getLocationDB().getData())
			if (newLocationsByUserID.get(location.getBuilding() + "-" + location.getRoom()) == null)
				ldb.removeData(location);

		assert(model.getDb().getLocationDB().getData().size() == locations.size());
	}
	
	@Override
	public ArrayList<CourseGWT> getCourses() {		
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		availableCourses = new HashMap<String, Course>();
		for (Course course : model.getDb().getCourseDB().getData())
		{
			course.setLength(6);
			availableCourses.put(course.getDept()+course.getCatalogNum(), course);
			results.add(Conversion.toGWT(course));
		}
		return results;
	}


	@Override
	public void saveCourses(ArrayList<CourseGWT> courses) {
		CourseDB cdb = model.getDb().getCourseDB();

		HashMap<String, Course> newLocationsByUserID = new LinkedHashMap<String, Course>();

		for (CourseGWT courseGWT : courses) {
			Course course = Conversion.fromGWT(courseGWT);
			newLocationsByUserID.put(course.getDept() + "-" + course.getCatalogNum(), course);
			cdb.saveData(course);
		}
		
		for (Course course : model.getDb().getCourseDB().getData())
			if (newLocationsByUserID.get(course.getDept() + "-" + course.getCatalogNum()) == null)
				cdb.removeData(course);

		
		assert(model.getDb().getCourseDB().getData().size() == courses.size());
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
		System.out.println("calling editdata. has prefs? " + hasPreferences(instructor));
		model.getDb().getInstructorDB().saveData(instructor);
	}
}
