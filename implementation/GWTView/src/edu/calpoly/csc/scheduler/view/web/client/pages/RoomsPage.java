package edu.calpoly.csc.scheduler.view.web.client.pages;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class RoomsPage extends View {
	private GreetingServiceAsync greetingService;

	public RoomsPage(GreetingServiceAsync greetingService) {
		this.greetingService = greetingService;
	}
	
	@Override
	public void beforeHide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterShow() {
		// TODO Auto-generated method stub

	}

}
