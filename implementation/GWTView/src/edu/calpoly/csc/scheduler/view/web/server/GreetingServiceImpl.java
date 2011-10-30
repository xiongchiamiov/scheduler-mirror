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
    	if (model == null) {
    		model = new Model();
    	}
    	
    	return model.getSchedules(username);
    }
    
    public void selectSchedule(Integer scheduleID) {
		model.initDbs(scheduleID);
    }
    
	public ArrayList<InstructorGWT> getInstructorNames() throws IllegalArgumentException {
		assert(model != null);
		
		/** TODO */
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		
		ArrayList<Instructor> instructors = model.getDb().getInstructorDB().getData();
		
		System.out.println("Size of instructor list: " + instructors.size());
		for(int i = 0; i < instructors.size(); i++)
		    results.add(new InstructorGWT(
    				instructors.get(i).getFirstName(),
    				instructors.get(i).getLastName(),
    				instructors.get(i).getId(),
	                instructors.get(i).getMaxWTU(),
	                instructors.get(i).getBuilding(),
	                instructors.get(i).getRoomNumber(),
	                instructors.get(i).getDisability()));
		
		return results;
	}
	
	
	public void saveInstructors(ArrayList<InstructorGWT> instructors) throws IllegalArgumentException {
		assert(model != null);
		
		InstructorDB idb = model.getDb().getInstructorDB();
		
		idb.clearData();
		
		for (InstructorGWT instructor : instructors){
			instructor.verify();
			
			Instructor ins = new Instructor();
			ins.setQuarterId(instructor.getQuarterID());
			ins.setScheduleId(0);
			ins.setFirstName(instructor.getFirstName());
			ins.setLastName(instructor.getLastName());
			ins.setUserID(instructor.getUserID());
			ins.setMaxWtu(instructor.getMaxWtu());
			ins.setCurWtu(instructor.getCurWtu());
			ins.setOffice(new Location(instructor.getBuilding(), instructor.getRoomNumber()));
			ins.setFairness(instructor.getFairness());
			ins.setDisability(instructor.getDisabilities());
			ins.setGenerosity(instructor.getGenerosity());
			ins.setAvailability(new WeekAvail());
			ins.setCoursePreferences(new HashMap<Course,Integer>());
			ins.setTimePreferences(new HashMap<Day, LinkedHashMap<Time, TimePreference>>());
			ins.setItemsTaught(new Vector<ScheduleItem>());
			ins.verify();
			
			idb.addData(ins);
		}
		
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
	    {         
	    	gwtItems.add(convertScheduleItem(item));
	    }
	    
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
      gwtItems.add(convertScheduleItem(item));
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
		for(int i = 0; i < courses.size(); i++)
		{
			Course course = courses.get(i);
			CourseGWT newCourse = new CourseGWT();
			newCourse.setCatalogNum(course.getCatalogNum());
			newCourse.setCourseName(course.getName());
			newCourse.setDept(course.getDept());
			newCourse.setLab(null);
			newCourse.setDays(null);
			newCourse.setLength(course.getLength());
			newCourse.setLabPad(course.getLabPad());
			newCourse.setMaxEnroll(course.getEnrollment());
			newCourse.setNumSections(course.getNumOfSections());
			newCourse.setScu(course.getScu());
			newCourse.setType(course.getType().toString());
			newCourse.setWtu(course.getWtu());
			newCourse.setQuarterID(course.getQuarterId());
			//newCourse.setScheduleID(course.getScheduleId());
		    results.add(newCourse);
		}
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
      gwtItems.add(convertScheduleItem(item));
      scheduleItems.put(item.getCourse().getDept() + 
       item.getCourse().getCatalogNum() + 
       item.getSection(), item);
     } 
	 return gwtItems;
	}
	
	
	public ScheduleItemGWT convertScheduleItem(ScheduleItem schdItem)
	{
	 assert(model != null);
	 String instructor;
	 String courseDept;
	 int courseNum;
	 int section;
	 ArrayList<Integer> dayNums = new ArrayList<Integer>();
	 int startTimeHour;
	 int endTimeHour;
	 int startTimeMin;
	 int endTimeMin;
	 String location;
	 Vector<Day> schdDays;
	 
	 instructor = (schdItem.getInstructor() == null ? "" : schdItem.getInstructor().getName());
	 courseDept = schdItem.getCourse().getDept();
	 courseNum = schdItem.getCourse().getCatalogNum();
	 section = schdItem.getSection();
	 schdDays = schdItem.getDays().getDays();
	 for(Day d : schdDays)
	 {
	  dayNums.add(d.getNum()); 
	 }
	 startTimeHour = schdItem.getStart().getHour();
	 endTimeHour = schdItem.getEnd().getHour();
	 startTimeMin = schdItem.getStart().getMinute();
     endTimeMin = schdItem.getEnd().getMinute();
     location = (schdItem.getLocation() == null ? "" : schdItem.getLocation().toString());
     
     return new ScheduleItemGWT(instructor, courseDept, courseNum, section,
    		                    dayNums, startTimeHour, startTimeMin, 
    		                    endTimeHour, endTimeMin, location);
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
	 return convertScheduleItem(rescheduled);
	}
	
	@Override
	public ArrayList<LocationGWT> getLocationNames() {
		assert(model != null);
		/** TODO */
		// replace sample data with data from the db
		
		ArrayList<LocationGWT> results = new ArrayList<LocationGWT>();
		
		Database sqldb = model.getDb();

		LocationDB ldb = sqldb.getLocationDB();
		
		ArrayList<Location> locations = ldb.getData();
		System.out.println("Size of locations list: " + locations.size());
		for(int i = 0; i < locations.size(); i++)
		{
		    results.add(new LocationGWT(locations.get(i).getBuilding(), locations.get(i).getRoom(),
		                locations.get(i).getMaxOccupancy(), locations.get(i).getType(),
		                locations.get(i).isSmartRoom(), locations.get(i).hasLaptopConnectivity(),
		                locations.get(i).isADACompliant(), locations.get(i).hasOverhead()));
		}
		
		// dummy data
		LocationGWT l1 = new LocationGWT("14", "Frank E. Pilling", "256", "Lab", 32, "Computers", "Really comfortable chairs");
		
		//results.add(l1);
		
		
		LocationGWT l2 = new LocationGWT("22", "English", "212", "Lec", 38, "Desks", "Uncomfortable, wooden desk chairs");
		
		//results.add(l2);
		
		
		LocationGWT l3 = new LocationGWT("53", "Science North", "213", "Lec", 84, "", "Stadium seats");
		
		//results.add(l3);

		return results;
	}


	@Override
	public void saveLocations(ArrayList<LocationGWT> locations) {
		assert(model != null);
		// TODO Auto-generated method stub

		/** TODO */
		Database sqldb = model.getDb();

		LocationDB ldb = sqldb.getLocationDB();
		
		ldb.clearData();
		
		for (LocationGWT location : locations)
		{
			location.verify();
			
			Location loc = new Location();
			loc.setRoom(location.getRoom());
			loc.setAdaCompliant(location.isADACompliant());
			loc.setAvailability(new WeekAvail());
			loc.setBuilding(location.getBuilding());
			loc.setMaxOccupancy(location.getMaxOccupancy());
			loc.setProvidedEquipment(loc.new ProvidedEquipment());
			loc.setQuarterId(location.getQuarterID());
			loc.setRoom(location.getRoom());
			loc.setScheduleId(location.getScheduleID());
			loc.setType(location.getType());
			loc.verify();
			
			ldb.addData(loc);
		}
//			ldb.addData(new Location(location.getBuilding(), location.getRoom(), location.getMaxOccupancy(), location.getType(), false, false, false, false));
			//ldb.addData(new Location(location.getBuilding(), location.getRoom(), location.getMaxOccupancy(), location.getType(), location.isADACompliant(), location.isSmartRoom(), location.hasLaptopConnectivity(), location.hasOverhead()));
	}
	
	@Override
	public ArrayList<CourseGWT> getCourses() {

		assert(model != null);
		/** TODO */
		// replace sample data with data from the db
		
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		
		Database db = model.getDb();

		CourseDB cdb = db.getCourseDB();
		
		ArrayList<Course> courses = cdb.getData();
		System.out.println("Size of course list: " + courses.size());
		for(int i = 0; i < courses.size(); i++)
		{
			Course course = courses.get(i);
			CourseGWT newCourse = new CourseGWT();
			newCourse.setCatalogNum(course.getCatalogNum());
			newCourse.setCourseName(course.getName());
			newCourse.setDept(course.getDept());
			newCourse.setLab(null);
			newCourse.setDays(null);
			newCourse.setLength(course.getLength());
			newCourse.setLabPad(course.getLabPad());
			newCourse.setMaxEnroll(course.getEnrollment());
			newCourse.setNumSections(course.getNumOfSections());
			newCourse.setScu(course.getScu());
			newCourse.setType(course.getType().toString());
			newCourse.setWtu(course.getWtu());
			newCourse.setQuarterID(course.getQuarterId());
			newCourse.setScheduleID(course.getScheduleId());
		    results.add(newCourse);
		}
		
		// dummy data
		CourseGWT c1 = new CourseGWT("The beginning...", 101, "CPE", 4, 4, 6, "Lec", 30, "CPE101");
				
		
		CourseGWT c2 = new CourseGWT("Writing", 300, "CSC", 4, 4, 1, "Lec", 24, "");
				
		
		CourseGWT c3 = new CourseGWT("Scheduling", 402, "CSC", 4, 4, 1, "Lec", 20, "");
		
		return results;
	}


	@Override
	public void saveCourses(ArrayList<CourseGWT> courses) {
		// TODO Auto-generated method stub

		assert(model != null);
		/** TODO */
		Database sqldb = model.getDb();

		CourseDB cdb = sqldb.getCourseDB();
		
		cdb.clearData();
		
		for (CourseGWT course : courses) {
//			cdb.addData(new Course(course.getCourseName(), course.getDept(), course.getCatalogNum()));
//			cdb.addData(new Course((), course.getLabID(), course.getSmartroom(), course.getLaptop(), course.getOverhead(), 8, course.getCTPrefix(), course.getPrefix()));
			Course newCourse = new Course();
			newCourse.setName(course.getCourseName());
			newCourse.setCatalogNum(course.getCatalogNum());
			newCourse.setWtu(course.getWtu());
			newCourse.setScu(course.getScu());
			newCourse.setType(course.getType());
			newCourse.setEnrollment(course.getMaxEnroll());
			newCourse.setLab(null);
			newCourse.setLabPad(course.getLabPad());
			newCourse.setQuarterId(course.getQuarterID());
			newCourse.setScheduleId(course.getScheduleID());
			newCourse.setDept(course.getDept());
			newCourse.setLength(course.getLength());
			newCourse.setNumOfSections(course.getNumSections());
			newCourse.setDays(null);
			
			
			cdb.addData(newCourse);
			
			//assert(false); // implement the rest of them
		}
	}
}
