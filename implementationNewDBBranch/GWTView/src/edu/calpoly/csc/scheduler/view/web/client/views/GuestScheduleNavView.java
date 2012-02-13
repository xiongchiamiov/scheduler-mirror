package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;

public class GuestScheduleNavView extends SimplePanel implements IViewContents {
	GreetingServiceAsync service;
	String scheduleName;
	MenuBar MenuBar;
	MenuItem scheduleMenuItem;
	
	public GuestScheduleNavView(final GreetingServiceAsync service, final MenuBar MenuBar, final String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
		this.MenuBar = MenuBar;
	}
	
	@Override
	public void afterPush(final ViewFrame frame) {
		scheduleMenuItem = new MenuItem("Schedule", true, new Command() {
			@Override
			public void execute() {
				if (frame.canPopViewsAboveMe()) {
					frame.popFramesAboveMe();
					frame.frameViewAndPushAboveMe(new CalendarView(service, scheduleName));
				}
			}
		});
		
		scheduleMenuItem.getCommand().execute();
	}

	@Override
	public boolean canPop() { return true; }
	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}
