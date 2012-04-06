package scheduler.view.web.client.views.resources.instructors;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the second page of the preferences wprivate CoursePrefsWidget coursePrefs;izard for
 * the instructors, showing the time preferences
 * @author Carsten Pfeffer <pfeffer@tzi.de>
 */
public class InstructorPrefsWizardTimeView extends VerticalPanel{
	private TimePrefsWidget timePrefs;
	private Button back;
	private Button finish;
	
	/**
	 * The constructor sets up the UI and passes the
	 * data fetching parameters to the sub widgets
	 * @param service
	 * @param documentID
	 * @param scheduleName
	 * @param instructor
	 */
	public InstructorPrefsWizardTimeView(GreetingServiceAsync service,
			int documentID, InstructorGWT instructor)
	{
		this.setWidth("100%");
		this.setHeight("100%");
		this.timePrefs = new TimePrefsWidget(service, documentID, instructor);
		this.back = new Button("< back");
		this.finish = new Button("finish");
		
		this.add(this.timePrefs);
		
		HorizontalPanel buttons = new HorizontalPanel();		
		buttons.setWidth("100%");
		buttons.add(this.back);
		Label empty = new Label();
		empty.setWidth("10px");
		buttons.add(empty);
		buttons.add(this.finish);
		buttons.setCellHorizontalAlignment(this.back, ALIGN_RIGHT);
		buttons.setCellHorizontalAlignment(this.finish, ALIGN_LEFT);
		this.setCellVerticalAlignment(buttons, ALIGN_BOTTOM);
		this.add(buttons);
	}

	/**
	 * adds a ClickHandler to the back button
	 * @param handler
	 */
	public void addBackClickHandler(ClickHandler handler) {
		this.back.addClickHandler(handler);
	}
	
	/**
	 * adds a ClickHandler to the finish button
	 * @param handler
	 */
	public void addFinishClickHandler(ClickHandler handler) {
		this.finish.addClickHandler(handler);
	}
}
