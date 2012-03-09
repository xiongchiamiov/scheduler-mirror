package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;

public class SelectScheduleViewAutomatic extends SelectScheduleView
{
   final int automaticOpenDocumentID;

   public SelectScheduleViewAutomatic(GreetingServiceAsync service, SimplePanel scheduleNameContainer, MenuBar menuBar, String username,
         int automaticOpenDocumentID)
   {
      super(service, scheduleNameContainer, menuBar, username);
      this.automaticOpenDocumentID = automaticOpenDocumentID;
   }

   @Override
   protected void doneAddingDocuments() {
	   for (DocumentGWT document : availableDocuments) {
		   if (document.getID().equals(automaticOpenDocumentID)) {
			   openDocument(document);
			   return;
		   }
	   }
	   assert(false);
   }
}
