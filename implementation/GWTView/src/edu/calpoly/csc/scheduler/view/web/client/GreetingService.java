package edu.calpoly.csc.scheduler.view.web.client;



import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	ArrayList<InstructorGWT> getProfessorNames() throws IllegalArgumentException;
	
	void saveProfessors(ArrayList<InstructorGWT> instructors, ArrayList<InstructorGWT> deleted) throws IllegalArgumentException;
}
