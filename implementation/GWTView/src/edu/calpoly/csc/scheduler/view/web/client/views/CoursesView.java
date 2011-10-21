package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTable;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CoursesView extends ScrollPanel {
	private Panel container;
	private GreetingServiceAsync service;
	private EditableTable courseTable;
	private String quarterID;

	public CoursesView(Panel container, GreetingServiceAsync greetingService, String quarterID) {
		this.container = container;
		this.service = greetingService;
		this.quarterID = quarterID;
		
		setWidth("100%");
		setHeight("100%");
		
		courseTable = EditableTableFactory.createCourses();
		this.add(courseTable.getWidget());
		populateCourses();
	}
	
	public void populateCourses() {
		courseTable.clear();
		
		service.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<CourseGWT> result){
				if (result != null) {
					for (CourseGWT s : result) {
						courseTable.add(new EditableTableEntry(s));
					}
				}
			}
		});
	}
}
