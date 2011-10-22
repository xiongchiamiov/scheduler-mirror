package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class ScheduleBuildView extends ScrollPanel {
	private GreetingServiceAsync service;

	public ScheduleBuildView(Panel container, GreetingServiceAsync greetingService, String quarterID) {
		this.service = greetingService;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");

		
		DockPanel dp = new DockPanel();
		
		
		ListBox listBoxAvailableCourses = new ListBox();		
		dp.add(listBoxAvailableCourses);
		
		ListBox listBoxIncludedCourses = new ListBox();		
		dp.add(listBoxIncludedCourses);
		
		Button buttonGenerate = new Button();
		buttonGenerate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
			}
		});
		
		this.add(dp);
//		service.generateSchedule(new Callback() {
//			
//		});
	}
}
