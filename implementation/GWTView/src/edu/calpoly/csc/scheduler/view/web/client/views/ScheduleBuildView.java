package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class ScheduleBuildView extends ScrollPanel {
	private GreetingServiceAsync service;

	public ScheduleBuildView(Panel container, GreetingServiceAsync greetingService, String quarterID) {
		this.service = greetingService;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");

		this.add(new HTML("My content goes here lololol"));
	}
}
