package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
	
	Map<CourseGWT, ListBox> listBoxesByCourse = new TreeMap<CourseGWT, ListBox>();
	
	InstructorTimePreferencesWidget timePrefs;
	FlexTable coursePrefs;
	
	public InstructorPreferencesView(Panel container, GreetingServiceAsync service, InstructorGWT instructor) {
		this.container = container;
		this.service = service;
		
		instructor.verify();
		
		this.instructor = instructor;
		this.savedInstructor = instructor.clone();
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		final VerticalPanel vp = new VerticalPanel();
		add(vp);
		
		timePrefs = new InstructorTimePreferencesWidget(service, new InstructorTimePreferencesWidget.Strategy() {
			public InstructorGWT getSavedInstructor() { return savedInstructor; }
			public InstructorGWT getInstructor() { return instructor; }
		});

		vp.add(new Button("Save All Preferences", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		}));
		
		vp.add(timePrefs);
		
		coursePrefs = new FlexTable();
		vp.add(coursePrefs);
		coursePrefs.setWidget(0, 0, new HTML("Course"));
		coursePrefs.setWidget(0, 1, new HTML("Preference"));
		
		service.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
			
			public void onSuccess(ArrayList<CourseGWT> result) {
				populateCourses(result);
			}
		});

		vp.add(new HTML("Which building do you prefer to be nearest to?"));
		final TextBox nearestBuilding = new TextBox();
		vp.add(nearestBuilding);
		nearestBuilding.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				nearestBuilding.addStyleName("changed");
			}
		});
		
		vp.add(new HTML("How far are you willing to walk to rooms?"));
		final TextBox maxDistance = new TextBox();
		vp.add(maxDistance);
		maxDistance.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				maxDistance.addStyleName("changed");
			}
		});
	}
	
	void populateCourses(List<CourseGWT> result) {
		int row = 1;
		for (final CourseGWT course : result) {
			coursePrefs.setWidget(row, 0, new HTML(course.getCourseName()));
			
			final ListBox list = new ListBox();
			listBoxesByCourse.put(course, list);
			list.addItem("Preferred", "0");
			list.addItem("Acceptable", "1");
			list.addItem("Not Preferred", "2");
			list.addItem("Not Qualified", "3");
			coursePrefs.setWidget(row, 1, list);
			
			list.setSelectedIndex(getCoursePreference(instructor, course));
			
			list.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					setCoursePreference(course, list.getSelectedIndex());
				}
			});
			
			row++;
		}
	}
	
	void save() {
		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		service.saveInstructor(instructor, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Error saving instructor: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				popup.hide();
				savedInstructor = instructor;
				instructor = instructor.clone();
				timePrefs.redoColors();
				redoColors();
			}
		});
	}
	
	int getCoursePreference(InstructorGWT instructor, CourseGWT course) {
		assert(instructor.getCoursePreferences() != null);
		if (instructor.getCoursePreferences().get(course) == null)
			return 0;
		return instructor.getCoursePreferences().get(course);
	}
	
	void setCoursePreference(CourseGWT course, int newDesire) {
		ListBox list = listBoxesByCourse.get(course);
		
		instructor.getCoursePreferences().put(course, newDesire);
		
		if (getCoursePreference(instructor, course) != getCoursePreference(savedInstructor, course))
			list.addStyleName("changed");
		else
			list.removeStyleName("changed");
	}
	
	void redoColors() {
		//TODO: implement
	}
}
