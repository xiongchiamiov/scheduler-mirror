package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleBuildView extends ScrollPanel {
	private GreetingServiceAsync service;

	private ListBox listBoxAvailableCourses = new ListBox();
	private ListBox listBoxIncludedCourses = new ListBox();
	private Button buttonGenerate = new Button();
	private FlexTable flexTableCalendar = new FlexTable();
	
	public ScheduleBuildView(Panel container, GreetingServiceAsync greetingService) {
		this.service = greetingService;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		// fill all of available space
		setWidth("100%");
		setHeight("100%");
		
		// make sure panel is clear of items
		this.clear();
		
		VerticalPanel vp = new VerticalPanel();									
		HorizontalPanel hp = new HorizontalPanel();		
		
		hp.add(listBoxAvailableCourses);
		hp.add(flexTableCalendar);
		hp.add(listBoxIncludedCourses);
			
		buttonGenerate.setText("Generate Schedule");
		buttonGenerate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				
				service.generateSchedule(new AsyncCallback<ArrayList<ScheduleItemGWT>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO something bad happened
						Window.alert("Generate schedule failed.");
					}

					@Override
					public void onSuccess(ArrayList<ScheduleItemGWT> result) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		});
				
		vp.add(hp);
		vp.add(buttonGenerate);
		
		this.add(vp);

		service.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO something bad happened
				Window.alert("Could not get available courses from database.");
			}

			@Override
			public void onSuccess(ArrayList<CourseGWT> result) {
				// fill listbox with courses
				for (int i = 0; i < result.size(); i++) {					
					listBoxAvailableCourses.insertItem(result.get(i).getCourseName(), i);				
				}
				
			}
			
		});
	}
}
