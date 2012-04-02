package scheduler.view.web.client;

import java.util.Collection;

import scheduler.view.web.client.views.LoadingPopup;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

public class NewScheduleCreator {
	private interface NamedScheduleCallback {
		void namedSchedule(String scheduleName);
	}
	
	public static void createNewSchedule(final GreetingServiceAsync service, final String username)
	{
		displayNewSchedPopup("Create", new NamedScheduleCallback() {
			@Override
			public void namedSchedule(final String newDocumentName) {
				service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>() {
					public void onSuccess(Collection<DocumentGWT> existingDocuments) {
						boolean newDocumentNameExists = false;
						for (DocumentGWT existingDocument : existingDocuments) {
							if (existingDocument.getName().equals(newDocumentName)) {
								newDocumentNameExists = true;
								break;
							}
						}
						
						if (newDocumentNameExists == false) {
							final LoadingPopup popup = new LoadingPopup();
							popup.show();
							
							DOM.setElementAttribute(popup.getElement(), "id", "failSchedPopup");
							
							service.createOriginalDocument(newDocumentName, new AsyncCallback<DocumentGWT>()
							{
								
								@Override
								public void onSuccess(DocumentGWT newDocument)
								{
									popup.hide();
									// openDocument(result);
									assert (newDocumentName.equals(newDocument.getName()));
									DocumentTabOpener.openDocInNewTab(username, newDocument);
								}
								
								@Override
								public void onFailure(Throwable caught)
								{
									popup.hide();
									Window.alert("Failed to open new schedule in" + ": " + caught.getMessage());
								}
							});
						}
						else {
							Window.alert("Error: Schedule named " + newDocumentName
									+ " already exists. Please enter a different name.");
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to get list of existing documents!");
					}
				});
			}
		});
	}
	
	
	/**
	 * Displays a popup for specifying a new schedule.
	 * 
	 * @param buttonLabel
	 * @param callback
	 */
	private static void displayNewSchedPopup(String buttonLabel, final NamedScheduleCallback callback)
	{
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox(false);
		FlowPanel fp = new FlowPanel();
		final Button butt = new Button(buttonLabel, new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				db.hide();
				
				final String scheduleName = tb.getText();
				
				callback.namedSchedule(scheduleName);
			}
		});
		final Button cancelButton = new Button("Cancel", new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				db.hide();
			}
		});
		
		tb.addKeyPressHandler(new KeyPressHandler()
		{
			@Override
			public void onKeyPress(KeyPressEvent event)
			{
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					butt.click();
			}
		});
		
		db.setText("Name Schedule");
		fp.add(new HTML("<center>Specify a new schedule name.</center>"));
		fp.add(tb);
		fp.add(butt);
		fp.add(cancelButton);
		
		db.setWidget(fp);
		db.center();
	}
	
	// displayNewSchedPopup("Create", new NameScheduleCallback()
	// {
	// @Override
	// public void namedSchedule(final String name)
	// {
	// if (!scheduleNames.contains(name))
	// {
	// newDocName = name;
	// currentDocName = name;
	// final LoadingPopup popup = new LoadingPopup();
	// popup.show();
	//
	// DOM.setElementAttribute(popup.getElement(), "id", "failSchedPopup");
	//
	// service.createOriginalDocument(newDocName, new
	// AsyncCallback<DocumentGWT>()
	// {
	//
	// @Override
	// public void onSuccess(DocumentGWT result)
	// {
	// popup.hide();
	// // openDocument(result);
	// assert(newDocName.equals(result.getName()));
	// openDocInNewTab(result.getName(), result.getID());
	// }
	//
	// @Override
	// public void onFailure(Throwable caught)
	// {
	// popup.hide();
	// Window.alert("Failed to open new schedule in" + ": " +
	// caught.getMessage());
	// }
	// });
	// }
	// else
	// {
	// Window.alert("Error: Schedule named " + name +
	// " already exists. Please enter a different name.");
	// }
	// }
	// });
}
