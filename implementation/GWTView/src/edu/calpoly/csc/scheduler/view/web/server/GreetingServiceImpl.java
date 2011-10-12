package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
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
		InstructorGWT i1 = new InstructorGWT("Gene", "Fisher", "123", "14", "210");
		
		results.add(i1);
		
		
		InstructorGWT i2 = new InstructorGWT("Aaron", "Keen", "409", "14", "230");
		
		results.add(i2);
		
		
		InstructorGWT i3 = new InstructorGWT("Clark", "Turner", "2", "14", "222");
		
		results.add(i3);

		return results;
	}
	
	
	public void saveInstructors(ArrayList<InstructorGWT> instructors,
			ArrayList<InstructorGWT> deleted) throws IllegalArgumentException {
		
		/** TODO */
	}

    /*
	 *@gwt.typeArgs <shared.gwtScheduleItem>
	 */
	public ArrayList<gwtScheduleItem> getGWTScheduleItems()
	{
	 ArrayList<gwtScheduleItem> items = new ArrayList<gwtScheduleItem>();
	 gwtScheduleItem i1 = new gwtScheduleItem("Gene Fisher", "CPE", 101, 1, "MWF",
	  new int[] {1, 3, 5}, 8, 10);
	 gwtScheduleItem i2 = new gwtScheduleItem("Gene Fisher", "CPE", 402, 1, "MWF",
	  new int[] {1, 3, 5}, 13, 17);
	 gwtScheduleItem i3 = new gwtScheduleItem("Clark Turner", "CPE", 300, 1, "TR",
	  new int[] {2, 4}, 15, 17);
	 gwtScheduleItem i4 = new gwtScheduleItem("Nancy Parham", "CPE", 141, 1, 
	  "MWF", new int[] {1, 3, 5}, 13, 14);
	 gwtScheduleItem i5 = new gwtScheduleItem("John Dalbey", "CPE", 308, 1, "MWF",
	  new int[] {1, 3, 5}, 13, 14);
	 gwtScheduleItem i6 = new gwtScheduleItem("John Clements", "CPE", 431, 1, 
	  "MWF", new int[] {1,3,5}, 16, 22);
	 gwtScheduleItem i7 = new gwtScheduleItem("Mei-Ling Liu", "CPE", 365, 1, 
	  "MWF", new int[] {1,3,5}, 13, 17);
	 items.add(i1);
	 items.add(i2);
	 items.add(i4);
	 items.add(i3);
	 items.add(i5);
	 items.add(i6);
	 items.add(i7);
	 return items;
	}

	@Override
	public ArrayList<LocationGWT> getLocationNames() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void saveLocations(ArrayList<LocationGWT> locations,
			ArrayList<LocationGWT> deleted) {
		// TODO Auto-generated method stub
		
	}
}
