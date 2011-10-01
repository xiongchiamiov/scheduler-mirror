package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void getProfessorNames(AsyncCallback<ArrayList<String>> callback)
			throws IllegalArgumentException;
}
