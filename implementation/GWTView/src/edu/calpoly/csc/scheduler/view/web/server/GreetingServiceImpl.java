package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.gwtScheduleItem;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {


	public ArrayList<InstructorGWT> getInstructorNames() throws IllegalArgumentException {
		
		/** TODO */
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		
		Database db = new Database();

		InstructorDB idb = db.getInstructorDB();
		
		ArrayList<Instructor> instructors = idb.getData();
		System.out.println("Size of instructor list: " + instructors.size());
		for(int i = 0; i < instructors.size(); i++)
		{
		    results.add(new InstructorGWT(instructors.get(i).getFirstName(), instructors.get(i).getLastName(),
		                ((Integer)instructors.get(i).getMaxWTU()), instructors.get(i).getOffice().getBuilding() + "-" + instructors.get(i).getOffice().getRoom()));
		}
		// replace sample data with data from the db
		
//		new Scheduler();
//		assert(Scheduler.getLocalIDB() != null);
//		if (Scheduler.getLocalIDB().size() == 0)
//			Scheduler.getLocalIDB().add();
//		Instructor instructor = Scheduler.getLocalIDB().iterator().next();
//		assert(instructor != null);
		
//		Instructor instructor = new Instructor("Evan", "IsXAwesome", "1337", 69, new Location(14, 235));

//		results.add(Conversion.toGWT(instructor));
		
		
		
		// dummy data
		InstructorGWT i1 = new InstructorGWT("Gene", "Fisher", 12, "14-210");
		
		results.add(i1);
		
		
		InstructorGWT i2 = new InstructorGWT("Aaron", "Keen", 8, "14-230");
		
		results.add(i2);
		
		
		InstructorGWT i3 = new InstructorGWT("Clark", "Turner", 16, "14-222");
		
		results.add(i3);

		return results;
	}
	
	
	public void saveInstructors(ArrayList<InstructorGWT> instructors) throws IllegalArgumentException {
		
		/** TODO */
	}

	public ArrayList<gwtScheduleItem> getGWTScheduleItems()
	{
		
		Database db = new Database();
		ArrayList<Instructor> instructors = db.getInstructorDB().getData();
		ArrayList<Course> courses = db.getCourseDB().getData();
		ArrayList<Location> locations = db.getLocationDB().getData();
		
		Schedule schedule = new Schedule();
		schedule.generate(new Vector<Course>(courses), new Vector<Instructor>(instructors), new Vector<Location>(locations));
		
		
		
		
	 ArrayList<gwtScheduleItem> gwtItems = new ArrayList<gwtScheduleItem>();
	
     for(ScheduleItem item : schedule.getScheduleItems())
     {         
      gwtItems.add(convertScheduleItem(item));
     }
	 
	 return gwtItems;
	}
	
	public gwtScheduleItem convertScheduleItem(ScheduleItem schdItem)
	{
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
	 
	 instructor = schdItem.getInstructor().getName();
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
     location = schdItem.getLocation().toString();
     
     return new gwtScheduleItem(instructor, courseDept, courseNum, section,
    		                    dayNums, startTimeHour, startTimeMin, 
    		                    endTimeHour, endTimeMin, location);
	}
	
	@Override
	public ArrayList<LocationGWT> getLocationNames() {
		/** TODO */
		// replace sample data with data from the db
		
		ArrayList<LocationGWT> results = new ArrayList<LocationGWT>();
		
		Database sqldb = new Database();

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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ArrayList<CourseGWT> getCourses() {

		/** TODO */
		// replace sample data with data from the db
		
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		
		Database db = new Database();

		CourseDB cdb = db.getCourseDB();
		
		ArrayList<Course> courses = cdb.getData();
		System.out.println("Size of course list: " + courses.size());
		for(int i = 0; i < courses.size(); i++)
		{
		    results.add(new CourseGWT(courses.get(i).getName(), courses.get(i).getCatalogNum(),
		                courses.get(i).getDept(), courses.get(i).getWtu(), courses.get(i).getScu(),
		                courses.get(i).getNumOfSections(), courses.get(i).getType().toString(), 
		                courses.get(i).getEnrollment(), null));
		}
		
		// dummy data
		CourseGWT c1 = new CourseGWT("The beginning...", 101, "CPE", 4, 4, 6, "Lec", 30, "CPE101");
		
		results.add(c1);
		
		
		CourseGWT c2 = new CourseGWT("Writing", 300, "CSC", 4, 4, 1, "Lec", 24, "");
		
		results.add(c2);
		
		
		CourseGWT c3 = new CourseGWT("Scheduling", 402, "CSC", 4, 4, 1, "Lec", 20, "");
		
		results.add(c3);

		return results;
	}


	@Override
	public void saveCourses(ArrayList<CourseGWT> courses) {
		// TODO Auto-generated method stub
		
	}
}
