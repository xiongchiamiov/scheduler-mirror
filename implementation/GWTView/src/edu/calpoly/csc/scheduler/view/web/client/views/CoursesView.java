package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.Table;
import edu.calpoly.csc.scheduler.view.web.client.table.TableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CoursesView extends ScrollPanel {
	private Panel container;
	private GreetingServiceAsync service;
	private String quarterID;
	private Table<CourseGWT> cTable;

	public CoursesView(Panel container, GreetingServiceAsync greetingService, String quarterID) {
		this.container = container;
		this.service = greetingService;
		this.quarterID = quarterID;
		
		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);
		
		cTable = TableFactory.course();
		vp.add(cTable.getWidget());
		populateCourses();
	}
	
	public void populateCourses() {
		cTable.clear();
		
		service.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<CourseGWT> result){
				if (result != null) {
					cTable.set(result);
				}
			}
		});
	}
}
