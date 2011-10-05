package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {


	public ArrayList<String> getProfessorNames() throws IllegalArgumentException {
		
		/** TODO */
		// replace sample data with data from the db
		
//		new Scheduler();
//		assert(Scheduler.getLocalIDB() != null);
//		if (Scheduler.getLocalIDB().size() == 0)
//			Scheduler.getLocalIDB().add();
//		Instructor instructor = Scheduler.getLocalIDB().iterator().next();
//		assert(instructor != null);
		
		Instructor instructor = new Instructor("Evan", "IsXAwesome", "1337", 69, new Location(14, 235));

		ArrayList<String> results = new ArrayList<String>();
		results.add(instructor.getLastName() + ", " + instructor.getFirstName());
		return results;
	}
}
