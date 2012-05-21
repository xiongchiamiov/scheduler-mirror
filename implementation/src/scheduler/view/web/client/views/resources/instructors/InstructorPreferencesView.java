package scheduler.view.web.client.views.resources.instructors;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
	protected ClickHandler additionalCloseHandler = null;

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

	public void afterPush(com.smartgwt.client.widgets.Window parentWindow) {
		this.setParent(parentWindow);
		openDocument.forceSynchronize(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				HTML instructorName = new HTML("Instructor Time Preferences");
				instructorName.setStyleName("bigBold");
				DOM.setElementAttribute(instructorName.getElement(), "id", "instructorName");
				InstructorPreferencesView.this.add(instructorName);

				InstructorPreferencesView.this.timePrefs = new TimePrefsWidget(openDocument, InstructorPreferencesView.this.instructor);
				
				InstructorPreferencesView.this.setSpacing(20);

				InstructorPreferencesView.this.add(timePrefs);
				InstructorPreferencesView.this.setStyleName("preferencesPanel");
				
				InstructorPreferencesView.this.coursePrefs = new CoursePrefsWidget(openDocument, InstructorPreferencesView.this.instructor);
				InstructorPreferencesView.this.coursePrefs.setStyleName("otherCenterness");
				InstructorPreferencesView.this.coursePrefs.setParent(InstructorPreferencesView.this.parent);
				InstructorPreferencesView.this.coursePrefs.afterPush();

				HTML cprefs = new HTML("Instructor Course Preferences");
				cprefs.addStyleName("bigBold");
				InstructorPreferencesView.this.add(cprefs);

				InstructorPreferencesView.this.add(coursePrefs);
				
				closebutton = new Button("Close", new ClickHandler() {
					public void onClick(ClickEvent event) {
						parent.hide();
					}
				});
				
				
				if(additionalCloseHandler != null)
				{
					closebutton.addClickHandler(additionalCloseHandler);
					additionalCloseHandler = null;
				}
				DOM.setElementAttribute(closebutton.getElement(), "id", "s_prefCloseBtn");
				InstructorPreferencesView.this.add(closebutton);
				InstructorPreferencesView.this.setCellHorizontalAlignment(closebutton, ALIGN_RIGHT);
				parent.show();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("failed to synchronize document");
			}
		});
		this.add(new HTML("<span style=\"display: block; height: 20px;\"></span>"));
	}
	
	private void setParent(com.smartgwt.client.widgets.Window parent) {
		this.parent = parent;
		if(this.coursePrefs != null)
			this.coursePrefs.setParent(parent);
	}
	
	public boolean isReady()
	{
		return this.closebutton != null;
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
		if(this.closebutton == null)
		{
			this.additionalCloseHandler = handler;
		}
		else
		{
			this.closebutton.addClickHandler(handler);
		}
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
