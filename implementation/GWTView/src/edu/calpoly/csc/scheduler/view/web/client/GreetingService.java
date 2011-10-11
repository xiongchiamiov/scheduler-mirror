package edu.calpoly.csc.scheduler.view.web.client;



import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.gwtScheduleItem;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	ArrayList<InstructorGWT> getInstructorNames() throws IllegalArgumentException;
	
	void saveInstructors(ArrayList<InstructorGWT> instructors, ArrayList<InstructorGWT> deleted) throws IllegalArgumentException;
	ArrayList<gwtScheduleItem> getGWTScheduleItems() throws IllegalArgumentException;

	ArrayList<LocationGWT> getLocationNames();

	void saveLocations(ArrayList<LocationGWT> locations,
			ArrayList<LocationGWT> deleted);
}
