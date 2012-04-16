package scheduler.view.web.client.views.resources.instructors;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the first page of the preferences wizard for
 * the instructors, showing the course preferences.
 * @author Carsten Pfeffer <pfeffer@tzi.de>
 */
public class InstructorPrefsWizardCourseView extends VerticalPanel {
	protected CoursePrefsWidget coursePrefs;
	protected Button close;
	protected Button next;
	
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
		this.setCellHorizontalAlignment(this.coursePrefs, ALIGN_CENTER);
		this.coursePrefs.setStyleName("otherCenterness");
		this.close = new Button("close");
		this.next = new Button("next >");
		
		DOM.setElementAttribute(this.close.getElement(), "id", "wizCloseButton");
		DOM.setElementAttribute(this.next.getElement(), "id", "wizNextButton");
		DOM.setElementAttribute(this.coursePrefs.getElement(), "id", "wizCoursePrefs");
		
		this.add(this.coursePrefs);
		
		HorizontalPanel buttons = new HorizontalPanel();		
		buttons.setWidth("100%");
		buttons.add(this.close);
		Label empty = new Label();
		empty.setWidth("10px");
		buttons.add(empty);
		buttons.add(this.next);
		buttons.setCellHorizontalAlignment(this.close, ALIGN_RIGHT);
		buttons.setCellHorizontalAlignment(this.next, ALIGN_LEFT);
		this.add(buttons);
		this.setCellVerticalAlignment(buttons, ALIGN_BOTTOM);
	}
	
	/**
	 * set the parent window, so that it can be closed when an error occurs
	 * @param parent
	 */
	public void setParent(com.smartgwt.client.widgets.Window parent) {
		this.coursePrefs.setParent(parent);
	}
	
	/**
	 * this method should be called after instantiating the panel and
	 * after setParent. It sets up the UI and data for the course selection
	 */
	public void afterPush()
	{
		this.coursePrefs.afterPush();
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