package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.Panel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class GuestScheduleNavView extends ScheduleNavView {
	GuestScheduleNavView(SelectScheduleView homeView, Panel container, final GreetingServiceAsync service, int selectedScheduleID, String scheduleName) {
		super(homeView, container, service, selectedScheduleID, scheduleName);
		
		addButton("Schedule", "topBarLink", true, new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new ScheduleView(service); }
		});
	}
}
