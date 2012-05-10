package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.CachedService;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.OriginalDocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;

public class InstructorsHomeView extends VerticalPanel
{
	protected CachedService service;
	protected String username;
	protected ArrayList<String> scheduleNames;
	protected HashMap<Integer, DocumentGWT> allAvailableOriginalDocumentsByID;	
	protected FlexTable schedList = new FlexTable();
	protected InstructorGWT instructor;
	
	public InstructorsHomeView(final CachedService service, final String username)
	{	
		this.service = service;
		this.username = username;
		this.scheduleNames = new ArrayList<String>();
		this.setStyleName("centerness");
		
		// ------------------------------------
		
		this.setWidth("50%");
		this.schedList.setStyleName("otherCenterness");
		this.schedList.setWidth("100%");
		HTML schedule = new HTML("Scheduler<br/>\n\n\n");
		
		HTML mydocs = new HTML("My Scheduling Documents:<br/>");
		mydocs.setStyleName("centerness");
		
		schedule.addStyleName("editTableHeading");
		
		DOM.setElementAttribute(this.schedList.getElement(), "id", "schedList");
		this.setStyleName("centerness");
		this.add(schedule);
		this.add(mydocs);
		Label spacer = new Label();
		spacer.setHeight("10px");
		this.add(spacer);
		this.add(schedList);

		// ------------------------------------
		
		this.service.forceSynchronize(new AsyncCallback<Void>(){
					 @Override
					 public void onFailure(Throwable caught)
					 {
						 com.google.gwt.user.client.Window.alert("There was an error getting the schedules: " + caught.getMessage());
					 }
					
					 @Override
					 public void onSuccess(Void v)
					 {
						 Collection<OriginalDocumentGWT> result = service.originalDocuments.getAll();
						 
						 allAvailableOriginalDocumentsByID = new HashMap<Integer, DocumentGWT>();
					
						 for (final OriginalDocumentGWT doc : result)
						 {
								allAvailableOriginalDocumentsByID.put(doc.getID(), doc);
								 
								if (!doc.isTrashed())
								{
									addNewDocument(doc);
									scheduleNames.add(doc.getName());
								}
						 }
					 }
				  });
	}
	
	public void addNewDocument(final OriginalDocumentGWT doc)
	{
		// set the instructor
		final String username = this.username;

		int row = this.schedList.getRowCount();
		this.schedList.setWidget(row, 0, new HTML(doc.getName()));
		Button prefs = new Button("Preferences");
		this.schedList.getCellFormatter().addStyleName(row, 1, "rightness");
		prefs.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				service.openWorkingCopyForOriginalDocument(doc.getID(), new AsyncCallback<CachedOpenWorkingCopyDocument>() {
					public void onFailure(Throwable caught) {
						com.google.gwt.user.client.Window.alert("Failed to get instructors!");
					}
					public void onSuccess(CachedOpenWorkingCopyDocument result) {
						
						for (InstructorGWT i : result.getInstructors(true)) {
							if (i.getUsername().equals(username)) {
								System.out.println(i.getUsername()+", "+username);
								if(instructor == null)
								{
									System.out.println("SSSHHHHIIIITTTTTT");
									setInstructor(result, i);
								}
								break;
							}
						}
					}
				});
				
			}
			
		});
		schedList.setWidget(row, 1, prefs);
		
		//this.instructor = null; // dirty hack to use an attribute,
								// but otherwise we wouldn't be able to access this variable
		
	}
	
	/**
	 * sets the instructor who shows the document
	 * @param instructor
	 */
	public void setInstructor(CachedOpenWorkingCopyDocument workingCopyDocument, InstructorGWT instructor1)
	{
		//System.out.println("Instructor shit: "+instructor.getUsername());
		this.instructor = instructor1;
		
		final Window win1 = new Window();
		win1.setTitle(workingCopyDocument.getDocument().getName() + " - Course Preferences");
		win1.setAutoCenter(true);
		win1.setSize("750px", "600px");
		
		final Window win2 = new Window();
		win2.setTitle(workingCopyDocument.getDocument().getName() + " - Time Preferences");
		win2.setAutoCenter(true);
		win2.setSize("750px", "600px");

//===================================================================================
//		this is still a dummy and has to be fetched from the real login name:
//===================================================================================
//		final InstructorGWT instructor  = new InstructorGWT(1, "foobar", "Hello",
//				"World", "120", new int[DayGWT.values().length][48],
//				new HashMap<Integer, Integer>(), true);
				
		final InstructorPrefsWizardCourseView courses =
				new InstructorPrefsWizardCourseView(workingCopyDocument, instructor);
		final InstructorPrefsWizardTimeView times =
				new InstructorPrefsWizardTimeView(workingCopyDocument, instructor);
		courses.addCloseClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				win1.hide();
			}
		});
		courses.addNextClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				win1.hide();
				win2.show();
			}
		});
		times.addFinishClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				win2.hide();
			}
		});
		times.addBackClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				win2.hide();
				win1.show();
			}
		});
		courses.setParent(win1);
		courses.afterPush();
		
		win1.addItem(courses);
		win2.addItem(times);
		win1.show();
	}
}
