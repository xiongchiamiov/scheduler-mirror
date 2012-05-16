package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.CachedService;
import scheduler.view.web.client.views.View;
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
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;

public class InstructorsHomeView extends VerticalPanel implements View
{
	protected CachedService service;
	protected ArrayList<String> scheduleNames;
	protected HashMap<Integer, DocumentGWT> allAvailableOriginalDocumentsByID;
	protected FlexTable schedList = new FlexTable();
	protected InstructorGWT instructor;
	
	protected InstructorPreferencesView iipv = null;
	protected Window prefsWindow = null;
	
	public InstructorsHomeView(final CachedService service)
	{	
		this.service = service;
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
						 if(scheduleNames.size() == 0)
						 {
							schedList.setWidget(schedList.getRowCount(),
									0, new HTML("There are currently no documents for you"));
						 }
					 }
				  });
	}
	
	public void addNewDocument(final OriginalDocumentGWT doc)
	{
		// set the instructor

		int row = this.schedList.getRowCount();
		this.schedList.setWidget(row, 0, new HTML(doc.getName()));
		Button prefs = new Button("Preferences");
		this.schedList.getCellFormatter().addStyleName(row, 1, "rightness");
		prefs.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				service.openWorkingCopyForOriginalDocument(doc.getID(), false, new AsyncCallback<CachedOpenWorkingCopyDocument>() {
					public void onFailure(Throwable caught) {
						com.google.gwt.user.client.Window.alert("Failed to get instructors!");
					}
					public void onSuccess(CachedOpenWorkingCopyDocument result) {
						for (InstructorGWT i : result.getInstructors(true)) {
							if (i.getUsername().equals(service.username)) {
								setInstructor(i);
								preferencesButtonClicked(result);
								break;
							}
						}
					}
				});
				
			}
			
		});
		schedList.setWidget(row, 1, prefs);
	}
	
	public void preferencesButtonClicked(CachedOpenWorkingCopyDocument doc) {
		
		if(this.iipv == null)
		{
			this.prefsWindow = new Window();
			this.prefsWindow.setAutoSize(true);
			
			this.prefsWindow.setCanDragReposition(true);
			this.prefsWindow.setCanDragResize(true);

			this.prefsWindow.setSize("700px", "500px");
			
			this.iipv = new InstructorPreferencesView(doc, instructor);
			
			this.prefsWindow.addItem(iipv);
			this.prefsWindow.setAutoSize(true);
			
			this.iipv.setParent(prefsWindow);
			this.iipv.afterPush();
		}
		else
		{
			this.iipv.setDocument(doc);
			this.iipv.setInstructor(instructor);
		}
		
		this.prefsWindow.setTitle("Instructor Preferences - <i>"
				+ doc.getDocument().getName() + "</i>");
		
		this.prefsWindow.show();
	}
	
	/**
	 * sets the instructor who shows the document
	 * @param instructor
	 */
	public void setInstructor(InstructorGWT instructor1)
	{
		this.instructor = instructor1;
	}
	
//	public void openWindow(CachedOpenWorkingCopyDocument workingCopyDocument)
//	{
//		System.err.println("entered");
//
//		if(win1 == null)
//		{
//			win1 = new Window();
//			win2 = new Window();
//			win1.setTitle(workingCopyDocument.getDocument().getName() + " - Course Preferences");
//			win2.setTitle(workingCopyDocument.getDocument().getName() + " - Time Preferences");
//			win1.setAutoCenter(true);
//			win1.setSize("750px", "600px");
//			win2.setAutoCenter(true);
//			win2.setSize("750px", "600px");
//			
//		
//			courses = new InstructorPrefsWizardCourseView(workingCopyDocument, instructor);
//			times = new InstructorPrefsWizardTimeView(workingCopyDocument, instructor);
//			courses.addCloseClickHandler(new ClickHandler(){
//				@Override
//				public void onClick(ClickEvent event) {
//					win1.hide();
//				}
//			});
//			courses.addNextClickHandler(new ClickHandler(){
//				@Override
//				public void onClick(ClickEvent event) {
//					win1.hide();
//					win2.show();
//				}
//			});
//			times.addFinishClickHandler(new ClickHandler(){
//				@Override
//				public void onClick(ClickEvent event) {
//					win2.hide();
//				}
//			});
//			times.addBackClickHandler(new ClickHandler(){
//				@Override
//				public void onClick(ClickEvent event) {
//					win2.hide();
//					win1.show();
//				}
//			});
//			courses.setParent(win1);
//			courses.afterPush();
//			
//			win1.addItem(courses);
//			win2.addItem(times);
//		}
//		else
//		{
//			win1.setTitle(workingCopyDocument.getDocument().getName() + " - Course Preferences");
//			win2.setTitle(workingCopyDocument.getDocument().getName() + " - Time Preferences");
//			
//			System.err.println("sofosefojjnsojj");
//			
//			courses.setDocument(workingCopyDocument);
//			times.setDocument(workingCopyDocument);
//		}
//		System.err.println("wwwweweweewewewewqqqqqqqqqqqwqwqw");
//		win1.show();
//	}

	@Override
	public boolean canClose() {
		return true;
	}

	@Override
	public void close() {
		
	}

	@Override
	public Widget viewAsWidget() { return this; }
}
