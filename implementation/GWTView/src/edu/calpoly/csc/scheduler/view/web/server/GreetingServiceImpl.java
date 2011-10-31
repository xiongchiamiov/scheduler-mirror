package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;
import edu.calpoly.csc.scheduler.model.schedule.CouldNotBeScheduledException;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private Model model;
	
    private Schedule schedule;
    private HashMap<String, Course> cannedCourses;
    private HashMap<String, ScheduleItem> scheduleItems;
	
    public Map<Integer, String> getScheduleNames(String username) {
    	if (model == null)
    		model = new Model();
    	return model.getSchedules(username);
    }
    
    public void selectSchedule(Integer scheduleID) {
		model.initDbs(scheduleID);
    }
    
	public ArrayList<InstructorGWT> getInstructorNames() throws IllegalArgumentException {
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		for (Instructor instructor : model.getDb().getInstructorDB().getData())
			results.add(Conversion.toGWT(instructor));
		return results;
	}
	
	
	public void saveInstructors(ArrayList<InstructorGWT> instructors) throws IllegalArgumentException {
		assert(model != null);
		
		InstructorDB idb = model.getDb().getInstructorDB();
		
		idb.clearData();
		for (InstructorGWT instructor : instructors)
			idb.addData(Conversion.fromGWT(instructor));
		
		assert(model.getDb().getInstructorDB().getData().size() == instructors.size());
	}

	public void newSchedule() {
		selectSchedule(1337);
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
	
	public ArrayList<ScheduleItemGWT> getTestGWTScheduleItems()
	{
		assert(model != null);
		
	 Vector<ScheduleItem> modelItems = new Vector<ScheduleItem>();
	 ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
	 schedule = new Schedule();
	 
	 Week mwf = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
	 Week tr = new Week(new Day[]{Day.TUE, Day.THU});
	 Time ts1 = new Time(8, 10);
	 Time te1 = new Time(10, 0);
	 Time ts2 = new Time(9, 10);
	 Time te2 = new Time(10, 0);
	 Time ts3 = new Time(10, 10);
	 Time te3 = new Time(12, 0);
	 Time ts4 = new Time(14, 10);
	 Time te4 = new Time(16, 0);
	 Time ts5 = new Time(14, 10);
	 Time te5 = new Time(17, 0);
	 Time ts6 = new Time(15, 10);
	 Time te6 = new Time(19, 0);
	 
	 Course c1 = new Course("", "CPE", 101);
	 c1.setDept("CPE");
	 Course c2 = new Course("", "CPE", 102);
     c2.setDept("CPE");
     Course c3 = new Course("", "CPE", 103);
     c3.setDept("CPE");
     Course c4 = new Course("", "CPE", 104);
     c4.setDept("CPE");
     Course c5 = new Course("", "CPE", 105);
     c5.setDept("CPE");
     Course c6 = new Course("", "CPE", 106);
     c6.setDept("CPE");
     
	 Location office = new Location(1, 2);
     Instructor i1 = new Instructor("Gene", "Fisher", "1", 12, office);
     Instructor i2 = new Instructor("Clark", "Turner", "2", 12, office);
     Instructor i3 = new Instructor("John", "Dalbey", "3", 12, office);
     Instructor i4 = new Instructor("John", "Clements", "4", 12, office);
     Instructor i5 = new Instructor("Franz", "Kurfess", "5", 12, office);
     Instructor i6 = new Instructor("Mei-Ling", "Liu", "6", 12, office);
     
     Location l1 = new Location(14, 256);
     Location l2 = new Location(14, 255);
     Location l3 = new Location(1, 3);
     Location l4 = new Location(15, 10);
     Location l5 = new Location(50, 100);
     Location l6 = new Location(3, 14);
     
     modelItems.add(new ScheduleItem(i1, c1, l1, 1, mwf, ts1, te1));
     modelItems.add(new ScheduleItem(i2, c2, l2, 1, mwf, ts2, te2));
     modelItems.add(new ScheduleItem(i3, c3, l3, 1, tr, ts3, te3));
     modelItems.add(new ScheduleItem(i4, c4, l4, 1, mwf, ts4, te4));
     modelItems.add(new ScheduleItem(i5, c5, l5, 1, mwf, ts5, te5));
     modelItems.add(new ScheduleItem(i6, c6, l6, 1, mwf, ts6, te6));
     
     for(ScheduleItem item : modelItems)
     {         
      gwtItems.add(Conversion.toGWT(item));
     }
	 
	 return gwtItems;
	}
		
	public void setCannedCourses()
	{
		assert(model != null);
		cannedCourses = new HashMap<String, Course>();
		Week mwf = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
		 Week tr = new Week(new Day[]{Day.TUE, Day.THU});
		Course c1 = new Course("one oh one", "CPE", 101);
		 c1.setDept("CPE");
		 c1.setLength(6);
		 c1.setNumOfSections(1);
		 c1.setDays(mwf);
		 Course c2 = new Course("one oh two", "CPE", 102);
	     c2.setDept("CPE");
		 c2.setLength(6);
		 c2.setNumOfSections(1);
		 c2.setDays(mwf);
	     Course c3 = new Course("one oh three", "CPE", 103);
	     c3.setDept("CPE");
		 c3.setLength(6);
		 c3.setNumOfSections(1);
		 c3.setDays(mwf);
         Course c4 = new Course("one oh four", "CPE", 104);
	     c4.setDept("CPE");
		 c4.setLength(6);
		 c4.setNumOfSections(1);
		 c4.setDays(mwf);
         Course c5 = new Course("one oh five", "CPE", 105);
	     c5.setDept("CPE");
		 c5.setLength(6);
		 c5.setNumOfSections(1);
		 c5.setDays(mwf);
	     Course c6 = new Course("one oh six", "CPE", 106);
	     c6.setDept("CPE");
		 c6.setLength(6);
		 c6.setNumOfSections(1);
		 c6.setDays(mwf);
        cannedCourses.put(c1.getDept()+c1.getCatalogNum(), c1);
		cannedCourses.put(c2.getDept()+c2.getCatalogNum(), c2);
		cannedCourses.put(c3.getDept()+c3.getCatalogNum(), c3);
		cannedCourses.put(c4.getDept()+c4.getCatalogNum(), c4);
		cannedCourses.put(c5.getDept()+c5.getCatalogNum(), c5);
		cannedCourses.put(c6.getDept()+c6.getCatalogNum(), c6);	
	}
	
	public ArrayList<CourseGWT> getCannedCourses()
	{
	 setCannedCourses();
	 
	 ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		
		ArrayList<Course> courses = new ArrayList<Course>(cannedCourses.values());
		System.out.println("Size of course list: " + courses.size());
		for (Course course : courses)
		    results.add(Conversion.toGWT(course));
		return results;
	}
	
	public ArrayList<ScheduleItemGWT> getGWTScheduleItems(ArrayList<CourseGWT> courses)
	{
	 assert(model != null);
	 Database db = model.getDb();
	 CourseDB cdb = db.getCourseDB();
	 ArrayList<Instructor> instructors = db.getInstructorDB().getData();
     ArrayList<Location> locations = db.getLocationDB().getData();
     ArrayList<Course> modelCourses = new ArrayList<Course>();//new ArrayList<Course>();
     scheduleItems = new HashMap<String, ScheduleItem>();
     
     for(CourseGWT course : courses)
     {
      modelCourses.add(cannedCourses.get(course.getDept() + course.getCatalogNum()));	 
     }
     schedule = new Schedule(new Vector<Instructor>(), new Vector<Location>());
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
	
	
	public ScheduleItemGWT rescheduleCourse(ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour)
	{
	 assert(model != null);
	 ScheduleItem rescheduled;
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
	 
	 course = cannedCourses.get(scheduleItem.getDept() + scheduleItem.getCatalogNum()); 
	 daysInWeek = new Week(daysScheduled);
	 startTime = new Time(startHour, (atHalfHour? 30 : 0));
	 
	 moved = scheduleItems.get(schdItemKey);
	 schedule.remove(moved);
	 try
	 {
	  rescheduled = schedule.makeItem(course, daysInWeek, startTime);
	  schedule.add(rescheduled);
	 }
	 catch(CouldNotBeScheduledException e)
	 {
	  try
	  {
	   schedule.add(moved);
	  }
	  catch(CouldNotBeScheduledException cnbse)
	  {
	   System.out.println("Could not put old item back in");
	  }
	  return null;
	 }
	 scheduleItems.remove(schdItemKey);
	 scheduleItems.put(schdItemKey, rescheduled);
	 return Conversion.toGWT(rescheduled);
	}
	
	@Override
	public ArrayList<LocationGWT> getLocationNames() {
		ArrayList<LocationGWT> results = new ArrayList<LocationGWT>();
		for (Location location : model.getDb().getLocationDB().getData())
			results.add(Conversion.toGWT(location));
		return results;
	}


	@Override
	public void saveLocations(ArrayList<LocationGWT> locations) {
		LocationDB ldb = model.getDb().getLocationDB();
		ldb.clearData();
		for (LocationGWT location : locations)
			ldb.addData(Conversion.fromGWT(location));
	}
	
	@Override
	public ArrayList<CourseGWT> getCourses() {		
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		for (Course course : model.getDb().getCourseDB().getData())
			results.add(Conversion.toGWT(course));
		return results;
	}


	@Override
	public void saveCourses(ArrayList<CourseGWT> courses) {
		CourseDB cdb = model.getDb().getCourseDB();
		cdb.clearData();
		for (CourseGWT course : courses)
			cdb.addData(Conversion.fromGWT(course));
	}

	@Override
	public void saveInstructor(InstructorGWT instructorGWT) {
		Instructor instructor = Conversion.fromGWT(instructorGWT);
		System.out.println("implement saving instructor!");
//		model.saveInstructor(instructor);
	}
}
