package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class SelectScheduleViewAutomatic extends SelectScheduleView {
	final int automaticOpenDocumentID;
	
	public SelectScheduleViewAutomatic(GreetingServiceAsync service,
			MenuBar menuBar, String username, int automaticOpenDocumentID) {
		super(service, menuBar, username);
		this.automaticOpenDocumentID = automaticOpenDocumentID;
	}
	
	@Override
	protected void doneAddingDocuments() {
		service.openExistingDocument(automaticOpenDocumentID, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) { }
			public void onSuccess(String name) {
				openLoadedSchedule(automaticOpenDocumentID, name);
			}
		});
	}
}
