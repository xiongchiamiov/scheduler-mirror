package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class EditScheduleView extends ScrollPanel {
	Panel container;
	GreetingServiceAsync service;
	String quarterID;
	
	public EditScheduleView(Panel container, GreetingServiceAsync service, String quarterID) {
		this.container = container;
		this.service = service;
		this.quarterID = quarterID;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		add(new HTML("Lol this is EditScheduleView"));
	}
}
