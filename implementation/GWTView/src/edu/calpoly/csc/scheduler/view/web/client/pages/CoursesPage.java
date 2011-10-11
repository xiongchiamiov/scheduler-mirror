package edu.calpoly.csc.scheduler.view.web.client.pages;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class CoursesPage extends View {
	private GreetingServiceAsync greetingService;

	public CoursesPage(GreetingServiceAsync greetingService) {
		this.greetingService = greetingService;
	}
	
	@Override
	public void beforeHide() {
		
	}

	@Override
	public void afterShow() {
		// TODO Auto-generated method stub

	}

}
