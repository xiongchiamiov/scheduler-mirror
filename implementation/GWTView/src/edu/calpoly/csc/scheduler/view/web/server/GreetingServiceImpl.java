package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
		// replace sample data with data from the db
		
//		new Scheduler();
//		assert(Scheduler.getLocalIDB() != null);
//		if (Scheduler.getLocalIDB().size() == 0)
//			Scheduler.getLocalIDB().add();
//		Instructor instructor = Scheduler.getLocalIDB().iterator().next();
//		assert(instructor != null);
		
//		Instructor instructor = new Instructor("Evan", "IsXAwesome", "1337", 69, new Location(14, 235));

		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
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

    /*
	 *@gwt.typeArgs <shared.gwtScheduleItem>
	 */
	public ArrayList<gwtScheduleItem> getGWTScheduleItems()
	{
	 ArrayList<gwtScheduleItem> items = new ArrayList<gwtScheduleItem>();
	 gwtScheduleItem i1 = new gwtScheduleItem("Gene Fisher", "CPE", 101, 1, "MWF",
	  new int[] {1, 3, 5}, 8, 10, 10, 0, "14-250");
 	 gwtScheduleItem i2 = new gwtScheduleItem("Gene Fisher", "CPE", 402, 1, "MWF",
	  new int[] {1, 3, 5}, 13, 30, 17, 30, "14-256");
	 gwtScheduleItem i3 = new gwtScheduleItem("Clark Turner", "CPE", 300, 1, "TR",
	  new int[] {2, 4}, 15, 10, 17, 0, "14-256");
	 gwtScheduleItem i4 = new gwtScheduleItem("Nancy Parham", "CPE", 141, 1, 
	  "MWF", new int[] {1, 3, 5}, 13, 10, 14, 0, "100-1234");
	 gwtScheduleItem i5 = new gwtScheduleItem("John Dalbey", "CPE", 308, 1, "MWF",
	  new int[] {1, 3, 5}, 13, 59, 14, 01, "01-001");
	 gwtScheduleItem i6 = new gwtScheduleItem("John Clements", "CPE", 431, 1, 
	  "MWF", new int[] {1,3,5}, 16, 10, 22, 0, "12-34");
	 gwtScheduleItem i7 = new gwtScheduleItem("Mei-Ling Liu", "CPE", 365, 1, 
	  "MWF", new int[] {1,3,5}, 13, 1, 17, 31, "50-100");
	 gwtScheduleItem i8 = new gwtScheduleItem("Kurt Mammen", "CPE", 101, 2, "TR",
	  new int[] {2,4}, 17, 0, 19, 0, "03-14");
			 
	 items.add(i1);
	 items.add(i2);
	 items.add(i4);
	 items.add(i3);
	 items.add(i5);
	 items.add(i6);
	 items.add(i7);
	 //items.add(i8);
	 return items;
	}

	@Override
	public ArrayList<LocationGWT> getLocationNames() {
		/** TODO */
		// replace sample data with data from the db
		
		ArrayList<LocationGWT> results = new ArrayList<LocationGWT>();
		
		// dummy data
		LocationGWT l1 = new LocationGWT("14", "Frank E. Pilling", "256", "Lab", 32, "Computers", "Really comfortable chairs");
		
		results.add(l1);
		
		
		LocationGWT l2 = new LocationGWT("22", "English", "212", "Lec", 38, "Desks", "Uncomfortable, wooden desk chairs");
		
		results.add(l2);
		
		
		LocationGWT l3 = new LocationGWT("53", "Science North", "213", "Lec", 84, "", "Stadium seats");
		
		results.add(l3);

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
