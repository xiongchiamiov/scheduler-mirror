package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.pages.CoursesPage;
import edu.calpoly.csc.scheduler.view.web.client.pages.InstructorsPage;
import edu.calpoly.csc.scheduler.view.web.client.pages.View;
import edu.calpoly.csc.scheduler.view.web.client.pages.RoomsPage;
import edu.calpoly.csc.scheduler.view.web.client.pages.SchedulePage;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTView implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private static final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	VerticalPanel bodyPanel;
	
	Button instructorsViewButton, roomsViewButton, coursesViewButton, scheduleViewButton;

	InstructorsPage instructorsPage;
	RoomsPage roomsPage;
	CoursesPage coursesPage;
	SchedulePage viewSchedulePage;
	View currentPage;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		instructorsViewButton = new Button("Instructors", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (instructorsPage == null)
					instructorsPage = new InstructorsPage(greetingService);
				showPage(instructorsPage);
			}
		});

		roomsViewButton = new Button("Rooms", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (roomsPage == null)
					roomsPage = new RoomsPage(greetingService);
				showPage(roomsPage);
			}
		});

		coursesViewButton = new Button("Courses", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (coursesPage == null)
					coursesPage = new CoursesPage(greetingService);
				showPage(coursesPage);
			}
		});

		scheduleViewButton = new Button("Schedule", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (viewSchedulePage == null)
					viewSchedulePage = new SchedulePage(greetingService);
				showPage(viewSchedulePage);
			}
		});

		RootPanel.get().add(instructorsViewButton);
		RootPanel.get().add(roomsViewButton);
		RootPanel.get().add(coursesViewButton);
		RootPanel.get().add(scheduleViewButton);

		bodyPanel = new VerticalPanel();
		RootPanel.get().add(bodyPanel);
		
		instructorsViewButton.click();
	}
	
	public void showPage(View newPage) {
		if (currentPage != null) {
			bodyPanel.clear();
			currentPage.beforeHide();
			currentPage = null;
		}
		
		assert(newPage != null);
		
		currentPage = newPage;
		bodyPanel.add(currentPage);
		currentPage.afterShow();
	}
}
