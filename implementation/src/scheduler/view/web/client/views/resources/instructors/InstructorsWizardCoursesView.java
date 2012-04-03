package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import scheduler.view.web.shared.CourseGWT;
//import scheduler.view.web.shared.InstructorGWT;

/**
 * this class shows the course preferences for a user logged in as an instructor
 * @author carsten
 */
public class InstructorsWizardCoursesView extends VerticalPanel {
	private FlexTable coursePrefs;
	private Map<Integer, ListBox> listBoxesByCourseID = new HashMap<Integer, ListBox>();
//	private InstructorGWT instructor;
	
	public InstructorsWizardCoursesView()
	{
		// adjust the size to the parent window
		this.setWidth("100%");
		this.setHeight("100%");
		
		// create the headline
		Label headline = new HTML("Course Preferences");
		headline.setStyleName("bigBold");
		this.add(headline);
		
		// just temporary dummy data:
		ArrayList<CourseGWT> courses = new ArrayList<CourseGWT>();
		CourseGWT tmp = new CourseGWT();
		tmp.setCatalogNum("CSC 406");
		tmp.setCourseName("Software Deployment");
		courses.add(tmp);
		
		tmp = new CourseGWT();
		tmp.setCatalogNum("CSC 471");
		tmp.setCourseName("Introduction to Computer Graphics");
		courses.add(tmp);
		
		this.setCourses(courses);
	}
	
	protected void setCourses(List<CourseGWT> courses)
	{	
		// create a new table (the old one will be garbage collected)
		this.coursePrefs = new FlexTable();
				
		// add the headlines
		HTML htmlCourse = new HTML("Course");
		htmlCourse.setStyleName("timePrefs");
		HTML htmlPreference = new HTML("Preference");
		htmlPreference.setStyleName("timePrefs");
		this.coursePrefs.setWidget(0, 0, htmlCourse);
		this.coursePrefs.setWidget(0, 1, htmlPreference);
		
		int row = 1;
		for(final CourseGWT course : courses)
		{
			this.coursePrefs.setWidget(row, 0, new Label(course.getCatalogNum() + " - " + course.getCourseName()));
			
			final ListBox list = new ListBox();
			this.listBoxesByCourseID.put(course.getID(), list);
			list.addItem("Not Qualified");
			list.addItem("Not Preferred");
			list.addItem("Acceptable");
			list.addItem("Preferred");
			
//			list.setSelectedIndex(this.getCoursePreference(this.instructor, course));
//			
//			list.addChangeHandler(new ChangeHandler() {
//				@Override
//				public void onChange(ChangeEvent event) {
//					setCoursePreference(course, list.getSelectedIndex());
//					save();
//				}
//			});
			
			this.coursePrefs.setWidget(row, 1, list);
			
			row++;
		}
		
		this.add(this.coursePrefs);
	}
}
