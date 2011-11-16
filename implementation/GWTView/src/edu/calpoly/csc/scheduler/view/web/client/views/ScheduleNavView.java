package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
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
	
	Widget instructorsLink, coursesLink, locationsLink, scheduleLink, configLink;

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

		add(createTopPanel());
		
		add(contentPanel = new SimplePanel());
		
		switchToView(new ScheduleView(service));
	}
	
	
	protected Widget createTopPanel() {
		FlowPanel topPanel = new FlowPanel();
		topPanel.setWidth("100%");
		topPanel.addStyleName("topBarMenu");
		
		Widget scheduleSelectWidget = selectScheduleView.createMiniSelectWidget(selectedScheduleID);
		scheduleSelectWidget.addStyleName("topBarScheduleSelect");
		topPanel.add(scheduleSelectWidget);
		
		topPanel.add(instructorsLink = HTMLUtilities.createLink("Instructors", "topBarLink first", new ClickHandler() {
			public void onClick(ClickEvent events) {
				checkforUnsavedTable(new InstructorsView(contentPanel, service, scheduleName));
			}
		}));
		topPanel.add(locationsLink = HTMLUtilities.createLink("Locations", "topBarLink", new ClickHandler() {
			public void onClick(ClickEvent events) {
				checkforUnsavedTable(new LocationsView(service, scheduleName));
			}
		}));
		topPanel.add(coursesLink = HTMLUtilities.createLink("Courses", "topBarLink", new ClickHandler() {
			public void onClick(ClickEvent events) {
				checkforUnsavedTable(new CoursesView(service,scheduleName));
			}
		}));
		topPanel.add(scheduleLink = HTMLUtilities.createLink("Schedule", "topBarLink", new ClickHandler() {
			public void onClick(ClickEvent events) {
				checkforUnsavedTable(new ScheduleView(service));
			}
		}));		
		topPanel.add(configLink = HTMLUtilities.createLink("Configuration", "topBarLink", new ClickHandler() {
			public void onClick(ClickEvent events) {
				checkforUnsavedTable(new AdminConfigView(service, scheduleName));
			}
		}));
		return topPanel;
	}
	
	public void switchToView(Widget widget) {
		instructorsLink.removeStyleName("currentView");
		locationsLink.removeStyleName("currentView");
		coursesLink.removeStyleName("currentView");
		scheduleLink.removeStyleName("currentView");
		configLink.removeStyleName("currentView");
		
		if (widget instanceof InstructorsView) {
			instructorsLink.addStyleName("currentView");
		}
		else if (widget instanceof LocationsView) {
			locationsLink.addStyleName("currentView");
		}
		else if (widget instanceof CoursesView) {
			coursesLink.addStyleName("currentView");
		}
		else if (widget instanceof ScheduleView) {
			scheduleLink.addStyleName("currentView");
		}
		else if (widget instanceof AdminConfigView) {
			configLink.addStyleName("currentView");
		}
		else
			assert(false);
		
		contentPanel.clear();
		contentPanel.add(widget);
	}
	
	
	private void checkforUnsavedTable(Widget w){
		
		boolean isSaved = true;
		if(!InstructorsView.isSaved() || !LocationsView.isSaved() || !CoursesView.isSaved()){
			isSaved = false;
		}
		
		if(isSaved){
			switchToView(w);
		}
		
		else{
			boolean confirm = Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
			if(confirm){
				
				InstructorsView.clearChanges();
				LocationsView.clearChanges();
				CoursesView.clearChanges();
				AdminConfigView.clearChanges();
				
				switchToView(w);
			}
		}
	}
}
