package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorPreferencesView extends ScrollPanel {
	Panel container;
	GreetingServiceAsync service;
	InstructorGWT instructor;
	InstructorGWT savedInstructor;
	
	public InstructorPreferencesView(Panel container, GreetingServiceAsync service, InstructorGWT instructor) {
		this.container = container;
		this.service = service;
		this.instructor = instructor;
		this.savedInstructor = instructor.clone();
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		add(vp);
		
		final InstructorTimePreferencesWidget timePrefs = new InstructorTimePreferencesWidget(service, new InstructorTimePreferencesWidget.Strategy() {
			@Override
			public InstructorGWT getSavedInstructor() { return savedInstructor; }
			
			@Override
			public InstructorGWT getInstructor() { return instructor; }
		});

		vp.add(new Button("Save All Preferences", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.saveInstructor(instructor, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error saving instructor: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						savedInstructor = instructor;
						instructor = instructor.clone();
						timePrefs.redoColors();
					}
				});
			}
		}));
		
		vp.add(timePrefs);
		
		final FlexTable coursePrefs = new FlexTable();
		vp.add(coursePrefs);
		coursePrefs.setWidget(0, 0, new HTML("Course"));
		coursePrefs.setWidget(0, 1, new HTML("Preference"));
		
		service.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			
			public void onSuccess(ArrayList<CourseGWT> result) {
				int row = 1;
				for (CourseGWT course : result) {
					coursePrefs.setWidget(row, 0, new HTML(course.getCourseName()));
					
					ListBox list = new ListBox();
					list.addItem("Preferred", "0");
					list.addItem("Acceptable", "1");
					list.addItem("Not Preferred", "2");
					list.addItem("Not Qualified", "3");
					coursePrefs.setWidget(row, 1, list);
					
					row++;
				}
			}
		});

		vp.add(new HTML("Which building do you prefer to be nearest to?"));
		vp.add(new TextBox());
		vp.add(new HTML("How far are you willing to walk to rooms?"));
		vp.add(new TextBox());
	}
}
