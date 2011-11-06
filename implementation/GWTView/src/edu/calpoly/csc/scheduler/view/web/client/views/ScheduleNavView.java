package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class ScheduleNavView extends VerticalPanel {
	SelectScheduleView selectScheduleView;
	
	Panel container;
	GreetingServiceAsync service;
	Panel contentPanel;
	int selectedScheduleID;
	final String scheduleName;

	ScheduleNavView(SelectScheduleView homeView, Panel container, GreetingServiceAsync service, int selectedScheduleID, String scheduleName) {
		this.selectScheduleView = homeView;
		
		this.container = container;
		this.service = service;
		
		this.selectedScheduleID = selectedScheduleID;
		this.scheduleName = scheduleName;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");

		Widget topPanel = createTopPanel();
		add(topPanel);
		
		add(contentPanel = new SimplePanel());
		contentPanel.add(new ScheduleView(contentPanel, service));
	}
	
	
	protected Widget createTopPanel() {
		FlowPanel topPanel = new FlowPanel();
		topPanel.setWidth("100%");
		topPanel.addStyleName("topBarMenu");
		
		Widget scheduleSelectWidget = selectScheduleView.createMiniSelectWidget(selectedScheduleID);
		scheduleSelectWidget.addStyleName("topBarScheduleSelect");
		topPanel.add(scheduleSelectWidget);
		
		topPanel.add(HTMLUtilities.createLink("Instructors", "topBarLink first", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new InstructorsView(contentPanel, service, scheduleName));
			}
		}));
		topPanel.add(HTMLUtilities.createLink("Locations", "topBarLink", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new RoomsView(contentPanel, service, scheduleName));
			}
		}));
		topPanel.add(HTMLUtilities.createLink("Courses", "topBarLink", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new CoursesView(service,scheduleName));
			}
		}));
		topPanel.add(HTMLUtilities.createLink("Schedule", "topBarLink", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new ScheduleView(contentPanel, service));
			}
		}));
		return topPanel;
	}
	
	protected Widget createLeftPanel() {
		FlowPanel leftMenuVP = new FlowPanel();
		
		leftMenuVP.add(selectScheduleView.createMiniSelectWidget(selectedScheduleID));
		
		leftMenuVP.add(HTMLUtilities.createLink("<b>Back to Select Quarter</b>", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(selectScheduleView);
			}
		}));
		
		leftMenuVP.add(new HTML("<b>Schedule</b>"));
		leftMenuVP.add(HTMLUtilities.createLink("Build / View", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new ScheduleView(contentPanel, service));
			}
		}));
		/*
		leftMenuVP.add(new HTML("<b>Manage</b>"));
		leftMenuVP.add(HTMLUtilities.createLink("Instructors", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new InstructorsView(contentPanel, service, scheduleName));
			}
		}));
		leftMenuVP.add(HTMLUtilities.createLink("Locations", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new RoomsView(contentPanel, service, scheduleName));
			}
		}));
		leftMenuVP.add(HTMLUtilities.createLink("Courses", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new CoursesView(service, scheduleName));
			}
		}));
				*/
		return leftMenuVP;
	}
}
