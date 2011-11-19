package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.schedule.ScheduleViewWidget;

public class ScheduleView extends ScrollPanel implements IView<ScheduleNavView> {
	private GreetingServiceAsync service;

	@Override
	public Widget getViewWidget() { return this; }
	
	public ScheduleView(GreetingServiceAsync greetingService) {
		this.service = greetingService;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		final VerticalPanel panel = new VerticalPanel();
        //panel.add(new HTML("<h2>Fall Quarter 2010 Final Schedule</h2>"));
        ScheduleViewWidget schdView = new ScheduleViewWidget();
        panel.add(schdView.getWidget(service));
        ScheduleView.this.add(panel);
	}

	@Override
	public void willOpenView(ScheduleNavView container) { }

	@Override
	public boolean canCloseView() { return true; }
}
