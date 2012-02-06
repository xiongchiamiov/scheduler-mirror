package edu.calpoly.csc.scheduler.view.web.client.views.resources.courses;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class AssociationsCell extends OsmTable.EditingCell {
	public interface GetCoursesCallback {
		ArrayList<CourseGWT> getCourses();
	}

	final GetCoursesCallback getCourses;
	CourseGWT selectedCourse;
	
	boolean courseIsLecture;
	
	FocusPanel readingLabel;
	
	ListBox listBox; // null when not editing
	ArrayList<CourseGWT> courses; // null when not editing.
	
	public AssociationsCell(GetCoursesCallback getCourses) {
		this.getCourses = getCourses;

		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("(none)"));
		
		if (!courseIsLecture)
			add(readingLabel);
	}
	
	@Override
	public void enteredEditingMode() {
		if (!courseIsLecture) {
			assert(listBox == null);
			assert(courses == null);
			
			clear();
	
			courses = getCourses.getCourses();
			listBox = new ListBox();
			listBox.addItem("(none)", "0");
			for (CourseGWT course : courses) {
				listBox.addItem(courseString(course), course.getID().toString());
			}
			
			if (selectedCourse == null)
				listBox.setSelectedIndex(0);
			else
				listBox.setSelectedIndex(indexOfCourseWithID(selectedCourse.getID()));
			
			listBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					// TODO Auto-generated method stub
					assert(listBox.getSelectedIndex() >= 0);
					if (listBox.getSelectedIndex() == 0) {
						selectedCourse = null;
					}
					else {
						selectedCourse = getCourseWithID(Integer.parseInt(listBox.getValue(listBox.getSelectedIndex())));
						System.out.println("Setting selectedcourse to " + selectedCourse.getID());
					}					
				}
			});
			
			add(listBox);
		}
	}
	
	private int indexOfCourseWithID(int courseID) {
		for (int i = 0; i < courses.size(); i++)
			if (courses.get(i).getID() == courseID)
				return 1 + i;
		assert(false);
		return 0;
	}
	
	public void setSelectedCourse(CourseGWT newSelectedCourse) {
		selectedCourse = newSelectedCourse;
		
		if (!courseIsLecture) {
			if (isInEditingMode()) {
				if (selectedCourse == null)
					listBox.setSelectedIndex(0);
				else
					listBox.setSelectedIndex(indexOfCourseWithID(selectedCourse.getID()));
			}
			else {
				clear();
				readingLabel.clear();
				readingLabel.add(new HTML(courseString(selectedCourse)));
				add(readingLabel);
			}
		}
	}
	
	private static String courseString(CourseGWT course) {
		if (course == null)
			return "(none)";
		else
			return course.getDept() + " " + course.getCatalogNum();
	}
	
	private CourseGWT getCourseWithID(int id) {
		for (CourseGWT course : courses)
			if (course.getID() == id)
				return course;
		assert(false);
		return null;
	}

	@Override
	public void exitedEditingMode() {
		if (!courseIsLecture) {
			listBox = null;
			courses = null;
	
			clear();
			readingLabel.clear();
			readingLabel.add(new HTML(courseString(selectedCourse)));
			add(readingLabel);
		}
	}

//	@Override
//	public void focus() {
//		assert(editing);
//		listBox.setFocus(true);
//	}

	public int getSelectedCourseID() {
		System.out.println("reading selectedCourse id ");
		if (selectedCourse == null)
			return -1;
		return selectedCourse.getID();
	}
	
	public void setCourseIsLecture(boolean courseIsNowLecture) {
		if (courseIsLecture == courseIsNowLecture)
			return;
		
		if (isInEditingMode()) {
			assert(false); // implement
		}
		else {
			if (courseIsNowLecture) {
				clear();
			}
			else {
				add(readingLabel);
			}
		}
		
		courseIsLecture = courseIsNowLecture;
	}
}
