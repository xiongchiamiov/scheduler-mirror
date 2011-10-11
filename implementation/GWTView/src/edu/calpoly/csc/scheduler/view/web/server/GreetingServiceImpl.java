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
	  items.add(new gwtScheduleItem("Gene Fisher", "CPE", 101, 1, "MWF", new int[] {1, 3, 5}, 8, 10));
	  items.add(new gwtScheduleItem("Gene Fisher", "CPE", 402, 1, "MWF", new int[] {1, 3, 5}, 14, 16));
	  items.add(new gwtScheduleItem("Clark Turner", "CPE", 300, 1, "TR", new int[] {2, 4}, 12, 14));
	  items.add(new gwtScheduleItem("Nancy Parham", "CPE", 141, 1, "MWF", new int[] {1, 3, 5}, 13, 14));
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
