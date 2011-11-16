package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.Panel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class AdminScheduleNavView extends ScheduleNavView {
	AdminScheduleNavView(SelectScheduleView homeView, Panel container, final GreetingServiceAsync service, int selectedScheduleID, final String scheduleName) {
		super(homeView, container, service, selectedScheduleID, scheduleName);

//		Put this back in by protob release
//		addButton("Configuration", "topBarLink first", new CreateViewCallback() {
//			public IView<ScheduleNavView> createView() { return new AdminConfigView(service, scheduleName); }
//		});
		
		addButton("Instructors", "topBarLink", new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new InstructorsView(service, scheduleName); }
		});
		
		addButton("Locations", "topBarLink", new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new LocationsView(service, scheduleName); }
		});
		
		addButton("Courses", "topBarLink", new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new CoursesView(service, scheduleName); }
		});

		addButton("Schedule", "topBarLink", new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new ScheduleView(service); }
		});
	}
}
