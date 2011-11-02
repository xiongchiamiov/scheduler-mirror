package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
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
	private Table<CourseGWT> cTable;

	public CoursesView(Panel container, GreetingServiceAsync greetingService) {
		this.container = container;
		this.service = greetingService;
		
		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);

		vp.add(new HTML("<h2>Fall Quarter 2010 Final Schedule Courses</h2>"));
		
		cTable = TableFactory.course(service);
		
		vp.add(cTable.getWidget());
		cTable.clear();

		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		service.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<CourseGWT> result){
				popup.hide();
				
				if (result != null) {
					cTable.set(result);
				}
			}
		});
	}
	
	public void populateCourses() {
	}
}
