package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class ScheduleView extends ScrollPanel {
	private GreetingServiceAsync service;

	public ScheduleView(GreetingServiceAsync greetingService) {
		this.service = greetingService;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		final VerticalPanel panel = new VerticalPanel();

		service.resetSchedule(
				new AsyncCallback<Void>()
				{
				 public void onFailure(Throwable caught) 
				 {
				  Window.alert("Failed to reset schedule");
				 }
				 public void onSuccess(Void result) 
				 {
				  panel.add(new HTML("<h2>Fall Quarter 2010 Final Schedule</h2>"));
				  ScheduleView.this.add(panel);		
				 }				
				});
	}
}
