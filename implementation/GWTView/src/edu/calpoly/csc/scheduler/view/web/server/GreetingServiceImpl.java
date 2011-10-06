package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {


	public ArrayList<InstructorGWT> getProfessorNames() throws IllegalArgumentException {
		
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
	
	
	public void saveProfessors(ArrayList<InstructorGWT> instructors,
			ArrayList<InstructorGWT> deleted) throws IllegalArgumentException {
		
		/** TODO */
	}
}
