package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.ScheduleViewWidget;

public class ScheduleView extends ScrollPanel {
	private GreetingServiceAsync service;

	public ScheduleView(Panel container, GreetingServiceAsync greetingService, String quarterID) {
		this.service = service;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");

		ScheduleViewWidget schdView = new ScheduleViewWidget();
		this.add(schdView.getWidget(service));
	}
}
