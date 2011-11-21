package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;

public class AdminScheduleNavView extends SimplePanel implements IViewContents {
	GreetingServiceAsync service;
	MenuBar menuBar;
	String scheduleName;
	MenuItem instructorsMenuItem, locationsMenuItem, coursesMenuItem,
			scheduleMenuItem;

	public AdminScheduleNavView(final GreetingServiceAsync service,
			final MenuBar MenuBar, int userID, String username,
			Integer scheduleID, final String scheduleName) {
		this.service = service;
		this.menuBar = MenuBar;
		this.scheduleName = scheduleName;
	}

	@Override
	public boolean canPop() {
		return true;
	}

	@Override
	public void afterPush(final ViewFrame frame) {

		menuBar.addItem(instructorsMenuItem = new MenuItem("Instructors", true,
				new Command() {
					public void execute() {
						if (frame.canPopViewsAboveMe()) {
							frame.popFramesAboveMe();
							frame.frameViewAndPushAboveMe(new InstructorsView(
									service, scheduleName));
						}
					}
				}));

		menuBar.addItem(locationsMenuItem = new MenuItem("Locations", true,
				new Command() {
					public void execute() {
						if (frame.canPopViewsAboveMe()) {
							frame.popFramesAboveMe();
							frame.frameViewAndPushAboveMe(new LocationsView(
									service, scheduleName));
						}
					}
				}));

		menuBar.addItem(coursesMenuItem = new MenuItem("Courses", true,
				new Command() {
					public void execute() {
						if (frame.canPopViewsAboveMe()) {
							frame.popFramesAboveMe();
							frame.frameViewAndPushAboveMe(new CoursesView(
									service, scheduleName));
						}
					}
				}));

		menuBar.addItem(scheduleMenuItem = new MenuItem("Schedule", true,
				new Command() {
					public void execute() {
						if (frame.canPopViewsAboveMe()) {
							frame.popFramesAboveMe();
							frame.frameViewAndPushAboveMe(new ScheduleView(
									service, scheduleName));
						}
					}
				}));
	}

	@Override
	public void beforePop() {
		menuBar.removeItem(scheduleMenuItem);
		menuBar.removeItem(coursesMenuItem);
		menuBar.removeItem(locationsMenuItem);
		menuBar.removeItem(instructorsMenuItem);
	}

	@Override
	public void beforeViewPushedAboveMe() {
	}

	@Override
	public void afterViewPoppedFromAboveMe() {
	}

	@Override
	public Widget getContents() {
		return this;
	}
}
