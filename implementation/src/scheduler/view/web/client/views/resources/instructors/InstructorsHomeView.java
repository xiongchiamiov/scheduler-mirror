package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.Window;

public class InstructorsHomeView extends VerticalPanel
{
	protected GreetingServiceAsync service;
	protected String username;
	protected ArrayList<String> scheduleNames;
	protected HashMap<Integer, DocumentGWT> allAvailableOriginalDocumentsByID;	
	protected FlexTable schedList = new FlexTable();
	
	public InstructorsHomeView(final GreetingServiceAsync service, String username)
	{	
		this.service = service;
		this.username = username;
		this.scheduleNames = new ArrayList<String>();
		
		// ------------------------------------
		
		this.setWidth("100%");
		this.schedList.setStyleName("otherCenterness");
		this.schedList.setWidth("80%");
		HTML schedule = new HTML("Scheduler\n\n\n");
		
		HTML mydocs = new HTML("My Scheduling Documents:");
		mydocs.setStyleName("centerness");
		
		schedule.addStyleName("editTableHeading");
		
		DOM.setElementAttribute(this.schedList.getElement(), "id", "schedList");
		this.setStyleName("centerness");
		this.add(schedule);
		this.add(mydocs);
		this.add(schedList);

		// ------------------------------------
		
		this.service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>(){
					 @Override
					 public void onFailure(Throwable caught)
					 {
						 com.google.gwt.user.client.Window.alert("There was an error getting the schedules: " + caught.getMessage());
					 }
					
					 @Override
					 public void onSuccess(Collection<DocumentGWT> result)
					 {
						 allAvailableOriginalDocumentsByID = new HashMap<Integer, DocumentGWT>();
					
						 for (DocumentGWT doc : result)
						 {
							 assert(doc.getID() != null);
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
	
	public void addNewDocument(final DocumentGWT doc)
	{
		int row = this.schedList.getRowCount();
		this.schedList.setWidget(row, 0, new HTML(doc.getName()));
		Button prefs = new Button("Preferences");
		prefs.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				final Window win1 = new Window();
				win1.setTitle("Course Preferences");
				win1.setAutoCenter(true);
				win1.setSize("750px", "600px");
				
				final Window win2 = new Window();
				win2.setTitle("Time Preferences");
				win2.setAutoCenter(true);
				win2.setSize("750px", "600px");

//===================================================================================
//				this is still a dummy and has to be fetched from the real login name:
//===================================================================================
				InstructorGWT instructor = new InstructorGWT(1, "foobar", "Hello",
						"World", "120", new int[DayGWT.values().length][48],
						new HashMap<Integer, Integer>(), true);
				
				final InstructorPrefsWizardCourseView courses =
						new InstructorPrefsWizardCourseView(service, doc.getID(), instructor);
				final InstructorPrefsWizardTimeView times =
						new InstructorPrefsWizardTimeView(service, doc.getID(), instructor);
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
			
		});
		schedList.setWidget(row, 1, prefs);
	}
	
}
