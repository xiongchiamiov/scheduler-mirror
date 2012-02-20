package edu.calpoly.csc.scheduler.view.web.client.views.resources.instructors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorPreferencesView extends VerticalPanel implements IViewContents {
	Panel container;
	GreetingServiceAsync service;
	int documentID;
	InstructorGWT instructor;
	InstructorGWT savedInstructor;
	Map<Integer, CourseGWT> coursesByID;
	
	Map<Integer, ListBox> listBoxesByCourseID = new HashMap<Integer, ListBox>();
	
	InstructorTimePreferencesWidget timePrefs;
	FlexTable coursePrefs;
	String[] styleNames = {"preferred", "acceptable", "notPreferred", "notQualified"};
	
	public InstructorPreferencesView(GreetingServiceAsync service, int documentID, String scheduleName, InstructorGWT instructor) {
		this.service = service;
		
		instructor.verify();
		
		this.documentID = documentID;
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);
	}

	@Override
	public void afterPush(ViewFrame frame) {
		this.setWidth("100%");
		this.setHeight("100%");
		FocusPanel fpanel = new FocusPanel();
		HTML instructorName = new HTML(instructor.getFirstName() + " " + instructor.getLastName());
		instructorName.setStyleName("bigBold");
		fpanel.add(instructorName);
		this.add(fpanel);
		
		timePrefs = new InstructorTimePreferencesWidget(service, new InstructorTimePreferencesWidget.Strategy() {
			public InstructorGWT getSavedInstructor() { return savedInstructor; }
			public InstructorGWT getInstructor() { return instructor; }
			public void autoSave() { save(); }
		});
		this.setSpacing(10);
		/*this.add(new Button("Save All Preferences", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		}));*/
		
		//this.add(new HTML("Time preferences are between 0 and 3.  0 means you cannot teach at that time, 3 means you really want to teach at that time."));
		
		this.add(timePrefs);
		
		coursePrefs = new FlexTable();
		this.add(coursePrefs);
		HTML htmlCourse = new HTML("Course");
		htmlCourse.setStyleName("timePrefs");
		HTML htmlPreference = new HTML("Preference");
		htmlPreference.setStyleName("timePrefs");
		coursePrefs.setWidget(0, 0, htmlCourse);
		coursePrefs.setWidget(0, 1, htmlPreference);
		
		service.getCoursesForDocument(documentID, new AsyncCallback<List<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get courses.");
			}
			
			public void onSuccess(List<CourseGWT> result) {
				HashMap<Integer, CourseGWT> newCoursesByID = new HashMap<Integer, CourseGWT>();
				for (CourseGWT course : result)
					newCoursesByID.put(course.getID(), course);
				populateCourses(newCoursesByID);
			}
		});
	}
	
	void populateCourses(Map<Integer, CourseGWT> newCoursesByID) {
		coursesByID = newCoursesByID;
		
		int row = 1;
		for (final CourseGWT course : coursesByID.values()) {
			coursePrefs.setWidget(row, 0, new HTML(course.getCourseName()));
			
			final ListBox list = new ListBox();
			listBoxesByCourseID.put(course.getID(), list);
			list.addItem("Not Qualified");
			list.addItem("Not Preferred");
			list.addItem("Acceptable");
			list.addItem("Preferred");
			coursePrefs.setWidget(row, 1, list);
			
			list.setSelectedIndex(getCoursePreference(instructor, course));
			list.setStyleName(styleNames[3 - getCoursePreference(instructor, course)]);
			
			list.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					setCoursePreference(course, list.getSelectedIndex());
					save();
				}
			});
			
			row++;
		}
	}
	
	void save() {
		//final LoadingPopup popup = new LoadingPopup();
		//popup.show();
		
		service.editInstructor(instructor, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				//popup.hide();
				Window.alert("Error saving instructor: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				//popup.hide();
				savedInstructor = instructor;
				instructor = new InstructorGWT(instructor);
				redoColors();
			}
		});
	}
	
	int getCoursePreference(InstructorGWT instructor, CourseGWT course) {
		assert(instructor.getCoursePreferences() != null);
		if (instructor.getCoursePreferences().get(course.getID()) == null)
			return 0;
		return instructor.getCoursePreferences().get(course.getID());
	}
	
	void setCoursePreference(CourseGWT course, int newDesire) {
		for (Integer key : instructor.getCoursePreferences().keySet())
			assert(key != null);
		instructor.getCoursePreferences().put(course.getID(), newDesire);
		for (Integer key : instructor.getCoursePreferences().keySet())
			assert(key != null);
		redoColors();
	}
	
	void redoColors() {
		timePrefs.redoColors();
		
		for (CourseGWT course : coursesByID.values()) {
			ListBox list = listBoxesByCourseID.get(course.getID());
			assert(list != null);
			list.setStyleName(styleNames[3 - getCoursePreference(instructor, course)]);
			/*if (getCoursePreference(instructor, course) != getCoursePreference(savedInstructor, course))
				list.addStyleName("changed");
			else
				list.removeStyleName("changed");*/
		}
	}

	@Override
	public boolean canPop() { return true; }
	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}
