package edu.calpoly.csc.scheduler.view.web.client.views;

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
	   DocumentGWT docToOpen = allAvailableOriginalDocumentsByID.get(automaticOpenOriginalDocumentID);
	   assert(docToOpen != null);
	   openOriginalDocument(docToOpen);
   }
}
