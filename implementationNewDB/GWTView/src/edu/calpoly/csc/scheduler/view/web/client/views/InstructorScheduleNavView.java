package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorScheduleNavView extends SimplePanel implements IViewContents {
	GreetingServiceAsync service;
	String scheduleName;
	MenuBar menuBar;
	InstructorGWT instructor;
	
	public InstructorScheduleNavView(GreetingServiceAsync service, MenuBar MenuBar, String scheduleName, InstructorGWT instructor) {
		this.menuBar = MenuBar;
		this.service = service;
		this.scheduleName = scheduleName;
		this.instructor = instructor;
	}

	@Override
	public boolean canPop() { return true; }

	@Override
	public void afterPush(final ViewFrame frame) {
		menuBar.addItem(new MenuItem("Preferences", true, new Command() {
			public void execute() {
				if (frame.canPopViewsAboveMe()) {
					frame.popFramesAboveMe();
					frame.frameViewAndPushAboveMe(new InstructorPreferencesView(service, scheduleName, instructor));
				}
			}
		}));
		
		menuBar.addItem(new MenuItem("Schedule", true, new Command() {
			public void execute() {
				if (frame.canPopViewsAboveMe()) {
					frame.popFramesAboveMe();
					frame.frameViewAndPushAboveMe(new ScheduleView(service, scheduleName));
				}
			}
		}));
	}

	@Override
	public void beforePop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeViewPushedAboveMe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterViewPoppedFromAboveMe() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Widget getContents() {
		// TODO Auto-generated method stub
		return null;
	}
}
