package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTable;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class CoursesView extends View {
	private GreetingServiceAsync greetingService;
	private EditableTable courseTable;

	public CoursesView(GreetingServiceAsync greetingService) {
		this.greetingService = greetingService;
		
		courseTable = EditableTableFactory.createCourses(new EditableTable.CancelHandler() {
			public void canceled() {
				populateCourses();
			}
		}, new EditableTable.SaveHandler() {
			public void saved(ArrayList<InstructorGWT> existingGWTs, ArrayList<InstructorGWT> deletedGWTs) {
				/* TODO */
				populateCourses();
			}
		});
		
		this.add(courseTable.getWidget());
	}
	
	public void populateCourses() {
		courseTable.clear();
		
		greetingService.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
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
	
	@Override
	public void beforeHide() {
		
	}

	@Override
	public void afterShow() {
		populateCourses();
	}

}
