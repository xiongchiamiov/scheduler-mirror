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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;


public class NewScheduleCreator {
	public interface CreatedScheduleCallback {
		void createdSchedule();
	}
	
	private interface NamedScheduleCallback {
		void namedSchedule(String scheduleName);
	}
	
	public static void createNewSchedule(final GreetingServiceAsync service, final String username, final CreatedScheduleCallback callback) {
		displayNewSchedPopup("Create", new NamedScheduleCallback() {
			@Override
			public void namedSchedule(final String newDocumentName) {
				service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>() {
					public void onSuccess(Collection<DocumentGWT> existingDocuments) {
						boolean newDocumentNameExists = false;
						for (DocumentGWT existingDocument : existingDocuments) {
							if (!existingDocument.isTrashed() && existingDocument.getName().equals(newDocumentName)) {
								newDocumentNameExists = true;
								break;
							}
						}
						
						if (newDocumentNameExists == false) {
							final LoadingPopup popup = new LoadingPopup();
							DOM.setElementAttribute(popup.getElement(), "id", "s_loadPop");
							popup.show();
							
							DOM.setElementAttribute(popup.getElement(), "id", "failSchedPopup");
							
							service.createOriginalDocument(newDocumentName, new AsyncCallback<DocumentGWT>() {
								public void onSuccess(DocumentGWT newDocument) {
									popup.hide();
									assert (newDocumentName.equals(newDocument.getName()));
									TabOpener.openDocInNewTab(username, newDocument);
									callback.createdSchedule();
								}
								
								public void onFailure(Throwable caught) {
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
		
		DOM.setElementAttribute(tb.getElement(), "id", "s_createBox");

		final com.smartgwt.client.widgets.Window window = new com.smartgwt.client.widgets.Window();
//		window.setID("s_NameTxt"); Don't put IDs on smartgwt widgets, it makes it add the window to a global list for later recalling
		window.setAutoSize(true);
		window.setTitle("Name Schedule");
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
	
		//DOM.setElementAttribute(window.getElement(), "id", "s_NameTxt");
		
		
		FlowPanel fp = new FlowPanel();
		final Button create = new Button(buttonLabel, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				window.destroy();
				final String scheduleName = tb.getText();
				callback.namedSchedule(scheduleName);
			}
		});
		
		DOM.setElementAttribute(create.getElement(), "id", "s_createNamedDocBtn");
		
		final Button cancelButton = new Button("Cancel", new ClickHandler() {
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		});
		
		DOM.setElementAttribute(cancelButton.getElement(), "id", "s_cancelNamedDocBtn");
		
		tb.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					create.click();
			}
		});
		
		fp.add(new HTML("<center>Specify a new schedule name.</center>"));
		fp.add(tb);
		fp.add(create);
		fp.add(cancelButton);
		DOM.setElementAttribute(fp.getElement(), "id", "s_subheaderPop");
		window.addItem(fp);

		window.centerInPage();
		window.show();
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
