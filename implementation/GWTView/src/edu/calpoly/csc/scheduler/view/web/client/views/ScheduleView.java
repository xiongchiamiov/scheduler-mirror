package edu.calpoly.csc.scheduler.view.web.client.views;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.ScheduleViewWidget;

public class ScheduleView extends View {
	private GreetingServiceAsync greetingService;

	public ScheduleView(GreetingServiceAsync greetingService) {
		this.greetingService = greetingService;
		ScheduleViewWidget schdView = new ScheduleViewWidget();
		this.add(schdView.getWidget(greetingService));
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
