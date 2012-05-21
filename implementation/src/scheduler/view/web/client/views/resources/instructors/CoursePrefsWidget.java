package scheduler.view.web.client.views.resources.instructors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
	protected CachedOpenWorkingCopyDocument workingCopyDocument;
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
	 * @param workingCopyDocument
	 * @param documentID
	 * @param scheduleName
	 * @param instructor
	 */
	public CoursePrefsWidget(CachedOpenWorkingCopyDocument openDocument, InstructorGWT instructor)
	{
		this.workingCopyDocument = openDocument;
		instructor.verify();
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);
		
		this.table = new FlexTable();
		DOM.setElementAttribute(this.table.getElement(), "id", "coursePrefsTable");
		this.add(this.table);
	}

	/**
	 * this method should be called after instantiating the panel and
	 * after setParent. It sets up the UI and data for the course selection
	 */
	public void afterPush()
	{
		List<CourseGWT> result = new LinkedList<CourseGWT>(workingCopyDocument.getCourses());
		for (CourseGWT course : result)
			assert(workingCopyDocument.getInstructorByID(instructor.getID()).getCoursePreferences().containsKey(course.getID()));
		for (CourseGWT course : result)
			assert(instructor.getCoursePreferences().containsKey(course.getID()));

		workingCopyDocument.sanityCheck();
		
		HashMap<Integer, CourseGWT> newCoursesByID = new HashMap<Integer, CourseGWT>();
		for (CourseGWT course : result) {
			newCoursesByID.put(course.getID(), course);
			assert(instructor.getCoursePreferences().containsKey(course.getID()));
		}
		populateCourses(newCoursesByID);
	}
	
	void populateCourses(final Map<Integer, CourseGWT> newCoursesByID) {
		coursesByID = newCoursesByID;
		this.table.removeAllRows();
		
		HTML htmlCourse = new HTML("Course");
		htmlCourse.setStyleName("timePrefs");
		HTML htmlPreference = new HTML("Preference");
		htmlPreference.setStyleName("timePrefs");
		this.table.setWidget(0, 0, htmlCourse);
		this.table.setWidget(0, 1, htmlPreference);
		
		int row = 1;
		for (final CourseGWT course : coursesByID.values()) {
			String name = course.getCourseName() + " (" + course.getType() + ")";
			this.table.setWidget(row, 0, new HTML(name));
			
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
					assert(newCoursesByID.get(course.getID()).attributesEqual(course));
					setCoursePreference(course, list.getSelectedIndex());
					save();
				}
			});

			row++;
		}
		this.redoColors();
	}

	/**
	 * saves the data of the current instructor
	 */
	void save() {
		this.savedInstructor = new InstructorGWT(instructor);
		workingCopyDocument.editInstructor(instructor);
		
		workingCopyDocument.forceSynchronize(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error saving instructor: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				savedInstructor = new InstructorGWT(instructor);
				//instructor = new InstructorGWT(instructor);
				redoColors();
			}
		});
	}

	int getCoursePreference(InstructorGWT instructor, CourseGWT course) {
		assert (instructor.getCoursePreferences() != null);
		assert(instructor.getCoursePreferences().containsKey(course.getID()));
		return instructor.getCoursePreferences().get(course.getID());
	}

	void setCoursePreference(CourseGWT course, int newDesire) {
		for (Integer key : instructor.getCoursePreferences().keySet())
			assert (key != null);
		instructor.getCoursePreferences().put(course.getID(), newDesire);
		for (Integer key : instructor.getCoursePreferences().keySet())
			assert (key != null);
		for (Integer key : instructor.getCoursePreferences().keySet()) {
			assert(workingCopyDocument.getCourseByID(key) != null);
			assert(workingCopyDocument.courseWithLocalIDExistsOnServer(key));
		}
		redoColors();
	}

	/**
	 * refreshes the colors of the course list
	 */
	void redoColors() {
		if(coursesByID == null)
		{
			return;
		}
		for (CourseGWT course : coursesByID.values()) {
			ListBox list = listBoxesByCourseID.get(course.getID());
			if(list == null){return;}
			list.setStyleName(styleNames[3 - getCoursePreference(instructor,
					course)]);
			list.setItemSelected(getCoursePreference(instructor, course), true); 
		}
	}
	
	public void setDataSources(CachedOpenWorkingCopyDocument doc, InstructorGWT instructor)
	{
		this.workingCopyDocument = doc;
		instructor.verify();
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);

		this.afterPush();
	}
}