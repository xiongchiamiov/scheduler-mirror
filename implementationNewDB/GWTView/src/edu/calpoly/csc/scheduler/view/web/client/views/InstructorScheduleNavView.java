package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.Panel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorScheduleNavView extends ScheduleNavView {
	InstructorScheduleNavView(SelectScheduleView homeView, Panel container, final GreetingServiceAsync service, int selectedScheduleID, String scheduleName, final InstructorGWT instructor) {
		super(homeView, container, service, selectedScheduleID, scheduleName);

		addButton("Preferences", "topBarLink first", true, new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new InstructorPreferencesView(service, instructor); }
		});

		addButton("Schedule", "topBarLink", false, new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new ScheduleView(service); }
		});
	}
}
