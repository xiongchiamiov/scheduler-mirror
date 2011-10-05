package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void getProfessorNames(AsyncCallback<ArrayList<InstructorGWT>> callback)
			throws IllegalArgumentException;
}
