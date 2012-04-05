package scheduler.view.web.client.views.resources.instructors;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the first page of the preferences wizard for
 * the instructors, showing the course preferences.
 * @author Carsten Pfeffer <pfeffer@tzi.de>
 */
public class InstructorPrefsWizardCourseView extends VerticalPanel {
	private CoursePrefsWidget coursePrefs;
	private Button close;
	private Button next;
	
	/**
	 * The constructor sets up the UI and passes the
	 * data fetching parameters to the sub widgets
	 * @param service
	 * @param documentID
	 * @param scheduleName
	 * @param instructor
	 */
	public InstructorPrefsWizardCourseView(GreetingServiceAsync service,
			int documentID, InstructorGWT instructor)
	{
		this.setWidth("100%");
		this.setHeight("100%");
		this.coursePrefs = new CoursePrefsWidget(service, documentID, instructor);
		this.close = new Button("close");
		this.next = new Button("next >");
		
		this.add(this.coursePrefs);
		
		HorizontalPanel buttons = new HorizontalPanel();		
		buttons.setWidth("100%");
		buttons.add(this.close);
		buttons.add(this.next);
		buttons.setCellHorizontalAlignment(this.close, ALIGN_LEFT);
		buttons.setCellHorizontalAlignment(this.next, ALIGN_RIGHT);
		this.setCellVerticalAlignment(buttons, ALIGN_BOTTOM);
		this.add(buttons);
	}

	/**
	 * adds a ClickHandler to the close button
	 * @param handler
	 */
	public void addCloseClickHandler(ClickHandler handler) {
		this.close.addClickHandler(handler);
	}
	
	/**
	 * adds a ClickHandler to the next button
	 * @param handler
	 */
	public void addNextClickHandler(ClickHandler handler) {
		this.next.addClickHandler(handler);
	}
}