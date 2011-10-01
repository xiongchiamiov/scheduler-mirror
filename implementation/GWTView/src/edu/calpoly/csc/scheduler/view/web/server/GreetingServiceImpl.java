package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.Iterator;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {


	public ArrayList<String> getProfessorNames() throws IllegalArgumentException {
		
		/** TODO */
		// replace sample data with data from the db
		
		new Scheduler();
		assert(Scheduler.getLocalIDB() != null);
		if (Scheduler.getLocalIDB().size() < 4)
			Scheduler.getLocalIDB().add(new Instructor("Evan", "IsAwesome", "1337", 69, new Location(14, 235)));

		ArrayList<String> results = new ArrayList<String>();
		
		for (Iterator<Instructor> i = Scheduler.getLocalIDB().iterator(); i.hasNext(); ) {
			Instructor instructor = i.next();
			results.add(instructor.getLastName() + ", " + instructor.getFirstName());
		}
		
		return results;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
