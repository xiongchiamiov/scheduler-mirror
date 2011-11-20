package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.schedule.ScheduleViewWidget;

public class ScheduleView extends VerticalPanel implements IViewContents {
	private GreetingServiceAsync service;
	private String scheduleName;

	public ScheduleView(GreetingServiceAsync greetingService, String scheduleName) {
		this.service = greetingService;
		this.scheduleName = scheduleName;
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.setWidth("100%");
		this.setHeight("100%");
		
        this.add(new HTML("<h2>" + scheduleName + "</h2>"));
        
        ScheduleViewWidget schdView = new ScheduleViewWidget();
        
        this.add(schdView.getWidget(service));
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
