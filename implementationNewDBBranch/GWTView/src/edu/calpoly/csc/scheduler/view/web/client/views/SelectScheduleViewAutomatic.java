package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;

public class SelectScheduleViewAutomatic extends SelectScheduleView
{
   final int automaticOpenOriginalDocumentID;

   public SelectScheduleViewAutomatic(GreetingServiceAsync service, SimplePanel scheduleNameContainer, MenuBar menuBar, String username,
         int automaticOpenDocumentID)
   {
      super(service, scheduleNameContainer, menuBar, username);
      this.automaticOpenOriginalDocumentID = automaticOpenDocumentID;
   }

   @Override
   protected void doneAddingDocuments() {
	   System.out.println("done adding documents, there are " + allAvailableOriginalDocumentsByID.size() + ", opening " + automaticOpenOriginalDocumentID);
	   DocumentGWT doc = allAvailableOriginalDocumentsByID.get(automaticOpenOriginalDocumentID);
	   assert(doc != null);
	   

	   assert(allAvailableOriginalDocumentsByID.values().contains(doc));
	   
      if (myFrame.canPopViewsAboveMe())
      {
    	  service.createWorkingCopyForOriginalDocument(doc.getID(), new AsyncCallback<DocumentGWT>() {
			
			@Override
			public void onSuccess(DocumentGWT result) {
		         myFrame.popFramesAboveMe();
		         myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, SelectScheduleViewAutomatic.this, menuBar, username, result));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to open document: " + caught.getMessage());
			}
		});
      }
   }
}
