package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Import {

	public static DialogBox importPopup;
	
	
	/**
	 * Imports schedule from file contents
	 * @param text the contents of the imported file
	 */
	public static void processText(String text){
		/** TODO */
	}
	
	
	/**
	 * Show the import dialog
	 */
	public static void showImport(){
		
		// create dialog
		FormPanel form = importForm();
		importPopup = new DialogBox();
		importPopup.setWidget(form);
		importPopup.setGlassEnabled(true);
		importPopup.setText("Import");
		importPopup.center();
	}
	
	
	/**
	 * Upload form for importing settings
	 * 
	 * @return
	 */
	private static FormPanel importForm() {

		return formPanel("schedule.csv", new ClickHandler() {
			public void onClick(ClickEvent event) {
				importPopup.hide();
			}
		}, new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {

				importPopup.hide();
				String result = event.getResults();

				// null check
				if (result != null) {
					processText(result);
				}

				else {
					Window.alert("Import was unsuccessful");
				}
			}
		});
	}

	/**
	 * Creates a form panel for uploading a file
	 * 
	 * @param fileName
	 *            name of the file to request
	 * @param cancelHandler
	 *            handler for the cancel button
	 * @param handler
	 *            handler to use after upload is complete
	 * @return completed form panel
	 */
	private static FormPanel formPanel(String fileName,
			ClickHandler cancelHandler, FormPanel.SubmitCompleteHandler handler) {

		// Create a FormPanel and point it at a service.
		final FormPanel form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "fileupload");

		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		// Create a panel to hold all of the form widgets.
		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);

		// instructions
		VerticalPanel labelPanel = new VerticalPanel();
		labelPanel.add(new Label("Upload the following file:"));
		labelPanel.add(new Label(fileName));

		panel.add(labelPanel);

		// Create a FileUpload widget.
		FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");
		panel.add(upload);

		// add buttons to panel
		HorizontalPanel savePanel = new HorizontalPanel();
		Grid grid = new Grid(1, 2);
		grid.setWidget(0, 0, new Button("Cancel", cancelHandler));
		grid.setWidget(0, 1, new Button("Submit", new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		}));
		savePanel.add(grid);

		// css
		savePanel.addStyleName("configureSaveCancelButtonPanel");

		panel.add(savePanel);

		form.addSubmitCompleteHandler(handler);

		panel.setSpacing(12);

		return form;
	}
}
