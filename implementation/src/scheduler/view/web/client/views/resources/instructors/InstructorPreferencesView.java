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

				InstructorPreferencesView.this.timePrefs = new TimePrefsWidget(
						InstructorPreferencesView.this.openDocument, 
						InstructorPreferencesView.this.instructor);
				
				InstructorPreferencesView.this.setSpacing(20);

				InstructorPreferencesView.this.add(timePrefs);
				InstructorPreferencesView.this.setStyleName("preferencesPanel");
				
				InstructorPreferencesView.this.coursePrefs = new CoursePrefsWidget(
						InstructorPreferencesView.this.openDocument, 
						InstructorPreferencesView.this.instructor);
				InstructorPreferencesView.this.coursePrefs.setStyleName("otherCenterness");
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

	}
	
	private void setParent(com.smartgwt.client.widgets.Window parent) {
		this.parent = parent;
	}
	
	public boolean isReady()
	{
		return this.closebutton != null;
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
	
	public void synchronize(final CachedOpenWorkingCopyDocument doc,
                             final InstructorGWT instructor)
	{
		this.openDocument = doc;
		instructor.verify();
		this.instructor = instructor;
		this.openDocument.forceSynchronize(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				
				if((openDocument.getCourses().size() != 0)
				  ||(com.google.gwt.user.client.Window.confirm(
							"The database doesn't contain any course right now. "
							+ "Do you want to proceed?")))
				{
					InstructorPreferencesView.this.coursePrefs.setDataSources(doc, instructor);
					InstructorPreferencesView.this.timePrefs.setDataSources(doc, instructor);
					
					parent.show();
				}
				else
				{
					parent.hide();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("failed to synchronize document");
			}
		});
		parent.show();
	}
}
