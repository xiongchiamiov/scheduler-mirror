package scheduler.view.web.client.views.resources.instructors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is a widget to show and modify the course
 * preferences of an instructor
 * @author Carsten Pfeffer <pfeffer@tzi.de>
 */
public class CoursePrefsWidget extends VerticalPanel
{
	protected GreetingServiceAsync service;
	protected int documentID;
	protected InstructorGWT instructor;
	protected InstructorGWT savedInstructor;
	
	protected FlexTable table;
	protected com.smartgwt.client.widgets.Window parent = null;
	protected String[] styleNames = { "preferred", "acceptable", "notPreferred",
			"notQualified" };
	protected Map<Integer, ListBox> listBoxesByCourseID = new HashMap<Integer, ListBox>();
	protected Map<Integer, CourseGWT> coursesByID;
	
	/**
	 * The following parameters are needed to get and save the course preferences
	 * @param service
	 * @param documentID
	 * @param scheduleName
	 * @param instructor
	 */
	public CoursePrefsWidget(GreetingServiceAsync service,
			int documentID, InstructorGWT instructor)
	{
		this.service = service;
		instructor.verify();
		this.documentID = documentID;
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);
	}
	
	/**
	 * this method should be called after instantiating the panel and
	 * after setParent. It sets up the UI and data for the course selection
	 */
	public void afterPush()
	{
		this.table = new FlexTable();
		DOM.setElementAttribute(this.table.getElement(), "id", "coursePrefsTable");
		this.add(this.table);

		HTML htmlCourse = new HTML("Course");
		htmlCourse.setStyleName("timePrefs");
		HTML htmlPreference = new HTML("Preference");
		htmlPreference.setStyleName("timePrefs");
		this.table.setWidget(0, 0, htmlCourse);
		this.table.setWidget(0, 1, htmlPreference);

		final com.smartgwt.client.widgets.Window parent = this.parent;

		service.getCoursesForDocument(documentID,
				new AsyncCallback<List<CourseGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to get courses.");
					}

					public void onSuccess(List<CourseGWT> result) {
						if (result.size() == 0) {
							System.out
									.println("The size of the course list >>is<< zero. It should NOT open preferences");
							final NoCourseDialog dlg = new NoCourseDialog(
									"No courses in database",
									"The database doesn't contain any course right now. "
											+ "Do you want to proceed?");
							dlg.addClickNoHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									dlg.hide();
									if (parent != null) {
										parent.hide();
									}
								}
							});

							dlg.addClickYesHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									if (parent != null) {
										parent.show();
									}
									dlg.hide();
								}
							});
							dlg.show();
						} else {
							System.out
									.println("The size of the course list is not zero. It should open preferences");

							HashMap<Integer, CourseGWT> newCoursesByID = new HashMap<Integer, CourseGWT>();
							for (CourseGWT course : result)
								newCoursesByID.put(course.getID(), course);
							populateCourses(newCoursesByID);
						}
					}
				});
	}
	
	void populateCourses(Map<Integer, CourseGWT> newCoursesByID) {
		coursesByID = newCoursesByID;

		int row = 1;
		for (final CourseGWT course : coursesByID.values()) {
			this.table.setWidget(row, 0, new HTML(course.getCourseName()));
			System.out.println("Stupid course name: " + course.getCourseName());
			final ListBox list = new ListBox();
			listBoxesByCourseID.put(course.getID(), list);
			list.addItem("Not Qualified");
			list.addItem("Not Preferred");
			list.addItem("Acceptable");
			list.addItem("Preferred");
			this.table.setWidget(row, 1, list);

			list.setSelectedIndex(getCoursePreference(instructor, course));
			list.setStyleName(styleNames[3 - getCoursePreference(instructor,
					course)]);

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
	
	/**
	 * set the parent window, so that it can be closed when an error occurs
	 * @param parent
	 */
	public void setParent(com.smartgwt.client.widgets.Window parent) {
		this.parent = parent;
	}

	/**
	 * saves the data of the current instructor
	 */
	void save() {
		service.editInstructor(instructor, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				// popup.hide();
				Window.alert("Error saving instructor: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				// popup.hide();
				savedInstructor = instructor;
				instructor = new InstructorGWT(instructor);
				redoColors();
			}
		});
	}

	int getCoursePreference(InstructorGWT instructor, CourseGWT course) {
		assert (instructor.getCoursePreferences() != null);
		if (instructor.getCoursePreferences().get(course.getID()) == null)
			return 0;
		return instructor.getCoursePreferences().get(course.getID());
	}

	void setCoursePreference(CourseGWT course, int newDesire) {
		for (Integer key : instructor.getCoursePreferences().keySet())
			assert (key != null);
		instructor.getCoursePreferences().put(course.getID(), newDesire);
		for (Integer key : instructor.getCoursePreferences().keySet())
			assert (key != null);
		redoColors();
	}

	/**
	 * refreshes the colors of the course list
	 */
	void redoColors() {

		for (CourseGWT course : coursesByID.values()) {
			ListBox list = listBoxesByCourseID.get(course.getID());
			assert (list != null);
			list.setStyleName(styleNames[3 - getCoursePreference(instructor,
					course)]);
			/*
			 * if (getCoursePreference(instructor, course) !=
			 * getCoursePreference(savedInstructor, course))
			 * list.addStyleName("changed"); else
			 * list.removeStyleName("changed");
			 */
		}
	}
	
	public void setInstructor(InstructorGWT instructor)
	{
		instructor.verify();
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);

		service.getCoursesForDocument(documentID,
				new AsyncCallback<List<CourseGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to get courses.");
					}

					public void onSuccess(List<CourseGWT> result) {
						if (result.size() == 0) {
							System.out
									.println("The size of the course list >>is<< zero. It should NOT open preferences");
							final NoCourseDialog dlg = new NoCourseDialog(
									"No courses in database",
									"The database doesn't contain any course right now. "
											+ "Do you want to proceed?");
							dlg.addClickNoHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									dlg.hide();
									if (parent != null) {
										parent.hide();
									}
								}
							});

							dlg.addClickYesHandler(new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									if (parent != null) {
										parent.show();
									}
									dlg.hide();
								}
							});
							dlg.show();
						} else {
							System.out
									.println("The size of the course list is not zero. It should open preferences");

							HashMap<Integer, CourseGWT> newCoursesByID = new HashMap<Integer, CourseGWT>();
							for (CourseGWT course : result)
								newCoursesByID.put(course.getID(), course);
							populateCourses(newCoursesByID);
						}
					}
				});
	}
}