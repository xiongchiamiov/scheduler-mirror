package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class ScheduleNavView extends DockLayoutPanel {
	HomeView homeView;
	
	Panel container;
	GreetingServiceAsync service;
	Panel contentPanel;

	ScheduleNavView(HomeView homeView, Panel container, GreetingServiceAsync service) {
		super(Unit.EM);
		
		this.homeView = homeView;
		
		this.container = container;
		this.service = service;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		Widget leftPanel = createLeftPanel();
		addWest(leftPanel, 15);
		getWidgetContainerElement(leftPanel).addClassName("quarterViewMenu");
		
		add(contentPanel = new SimplePanel());
		contentPanel.add(new ScheduleView(contentPanel, service));
	}
	
	protected Widget createLeftPanel() {
		FlowPanel leftMenuVP = new FlowPanel();
		
		leftMenuVP.add(new HTML("Schedule Name Here"));
		
		leftMenuVP.add(HTMLUtilities.createLink("<b>Back to Select Quarter</b>", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new HomeView(container, service));
			}
		}));
		
		leftMenuVP.add(new HTML("<b>Schedule</b>"));
		leftMenuVP.add(HTMLUtilities.createLink("Build / View", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new ScheduleView(contentPanel, service));
			}
		}));
		
		leftMenuVP.add(new HTML("<b>Manage</b>"));
		leftMenuVP.add(HTMLUtilities.createLink("Instructors", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new InstructorsView(contentPanel, service));
			}
		}));
		leftMenuVP.add(HTMLUtilities.createLink("Locations", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new RoomsView(contentPanel, service));
			}
		}));
		leftMenuVP.add(HTMLUtilities.createLink("Courses", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new CoursesView(contentPanel, service));
			}
		}));
				
		return leftMenuVP;
	}
}