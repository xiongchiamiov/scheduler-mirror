package scheduler.view.web.client.views.resources.instructors;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * This is the panel class for the amdministrator's view of the
 * time and course preferences of the instructors
 * @author unknown, modified by Carsten Pfeffer <pfeffer@tzi.de>
 */
public class InstructorPreferencesView extends VerticalPanel {
	protected CachedOpenWorkingCopyDocument openDocument;
	protected InstructorGWT instructor;
	
	protected TimePrefsWidget timePrefs;
	protected CoursePrefsWidget coursePrefs;
	protected com.smartgwt.client.widgets.Window parent = null;
	protected Button closebutton;

	/**
	 * The constructor sets up the UI and passes the
	 * data fetching parameters to the sub widgets
	 * @param service
	 * @param documentID
	 * @param instructor
	 * @param unsavedDocumentStrategy 
	 */
	public InstructorPreferencesView(CachedOpenWorkingCopyDocument openDocument, InstructorGWT instructor) {
		this.openDocument = openDocument;

		instructor.verify();

		this.instructor = instructor;
	}

	// @Override
	public void afterPush() {
		this.setWidth("100%");
		this.setHeight("100%");
		FocusPanel fpanel = new FocusPanel();
		HTML instructorName = new HTML("Instructor Time Preferences");
		fpanel.setStyleName("bigBold");
		instructorName.setStyleName("bigBold");
		DOM.setElementAttribute(instructorName.getElement(), "id", "instructorName");
		fpanel.add(instructorName);
		this.add(fpanel);

		this.timePrefs = new TimePrefsWidget(openDocument, this.instructor);
		
		this.setSpacing(20);

		this.add(timePrefs);
		this.setStyleName("centerness");

		
		this.coursePrefs = new CoursePrefsWidget(openDocument, this.instructor);
		this.coursePrefs.setStyleName("otherCenterness");
		this.coursePrefs.setParent(this.parent);
		this.coursePrefs.afterPush();

		HTML cprefs = new HTML("Instructor Course Preferences");
		cprefs.addStyleName("bigBold");
		this.add(cprefs);

		this.add(coursePrefs);
		
		closebutton = new Button("Close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				parent.hide();
			}
		});
		DOM.setElementAttribute(closebutton.getElement(), "id", "s_prefCloseBtn");
		
		this.add(closebutton);
	}
	
	public void setParent(com.smartgwt.client.widgets.Window parent) {
		this.parent = parent;
		if(this.coursePrefs != null)
			this.coursePrefs.setParent(parent);
	}
	
	
	public void setInstructor(InstructorGWT instructor) {
		
		instructor.verify();
		this.instructor = instructor;

		this.timePrefs.setInstructor(instructor);
		this.coursePrefs.setInstructor(instructor);
	}
	
	public void setDocument(CachedOpenWorkingCopyDocument doc)
	{
		this.openDocument = doc;
		this.timePrefs.setDocument(doc);
		this.coursePrefs.setDocument(doc);
	}

	// @Override
	public boolean canPop() {
		return true;
	}

	// @Override
	public void beforePop() {
	}

	// @Override
	public void beforeViewPushedAboveMe() {
	}

	// @Override
	public void afterViewPoppedFromAboveMe() {
	}

	// @Override
	public Widget getContents() {
		return this;
	}
	
	public void addCloseHandler(ClickHandler handler)
	{
		this.closebutton.addClickHandler(handler);
	}
	
	public CachedOpenWorkingCopyDocument getDocument()
	{
		return this.openDocument;
	}
	
	public void save()
	{
		this.timePrefs.save();
		this.coursePrefs.save();
	}
}
