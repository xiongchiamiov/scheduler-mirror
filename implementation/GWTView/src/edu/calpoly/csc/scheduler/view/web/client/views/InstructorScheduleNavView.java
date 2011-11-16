package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.views.ScheduleNavView.CreateViewCallback;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorScheduleNavView extends ScheduleNavView {
	InstructorScheduleNavView(SelectScheduleView homeView, Panel container, final GreetingServiceAsync service, int selectedScheduleID, String scheduleName, final InstructorGWT instructor) {
		super(homeView, container, service, selectedScheduleID, scheduleName);

		addButton("Preferences", "topBarLink first", new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new InstructorPreferencesView(service, instructor); }
		});

		addButton("Schedule", "topBarLink", new CreateViewCallback() {
			public IView<ScheduleNavView> createView() { return new ScheduleView(service); }
		});
	}
}
