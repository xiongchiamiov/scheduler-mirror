package scheduler.view.web.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ExportDialog {

	/**
	 * Displays a popup to export schedule.
	 */
	public static void displayExportPopup()
	{	
		final DialogBox db = new DialogBox();
		VerticalPanel mainVerticalPanel = new VerticalPanel();
		
		VerticalPanel verticalPanel = new VerticalPanel();
		mainVerticalPanel.add(verticalPanel);
		
		HorizontalPanel typeSelectorPanel = new HorizontalPanel();
		verticalPanel.add(typeSelectorPanel);
		
		final FocusPanel csvFocusPanel = new FocusPanel();
		csvFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO Save Selection
			}
		});
		
		FocusPanel pdfFocusPanel = new FocusPanel();
		pdfFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO Save Selection
			}
		});
		
		pdfFocusPanel.setStyleName("exportChoice");
		typeSelectorPanel.add(pdfFocusPanel);
		pdfFocusPanel.setSize("", "");
		
		VerticalPanel pdfPanel = new VerticalPanel();
		pdfFocusPanel.setWidget(pdfPanel);
		pdfPanel.setSize("100px", "100px");
		
		Image PDFIcon = new Image("imgs/pdf-icon.png");
		pdfPanel.add(PDFIcon);
		PDFIcon.setSize("100px", "100px");
		
		Label lblCSV = new Label("PDF");
		pdfPanel.add(lblCSV);
		pdfPanel.setCellHorizontalAlignment(lblCSV, HasHorizontalAlignment.ALIGN_CENTER);
		csvFocusPanel.setStyleName("exportChoice");
		typeSelectorPanel.add(csvFocusPanel);
		typeSelectorPanel.setCellHorizontalAlignment(csvFocusPanel, HasHorizontalAlignment.ALIGN_CENTER);
		csvFocusPanel.setSize("", "");

		VerticalPanel csvPanel = new VerticalPanel();
		csvFocusPanel.setWidget(csvPanel);
		csvPanel.setSize("100px", "100px");

		Image CSVIcon = new Image("imgs/csv-icon.png");
		csvPanel.add(CSVIcon);
		CSVIcon.setSize("100px", "100px");

		Label lblNewLabel = new Label("Excel (CSV)");
		csvPanel.add(lblNewLabel);
		csvPanel.setCellHorizontalAlignment(lblNewLabel, HasHorizontalAlignment.ALIGN_CENTER);
		
		final HorizontalPanel Buttons = new HorizontalPanel();
		Buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		
		
		mainVerticalPanel.add(Buttons);
		Buttons.setWidth("102px");
		mainVerticalPanel.setCellHorizontalAlignment(Buttons, HasHorizontalAlignment.ALIGN_CENTER);
		
		final Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				db.hide();
			}
		});
		
		
		final Button nextButton = new Button("Next", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				db.hide();
				//TODO Add Selection between PDF and CSV options

				//Temporary dialogue box until CSV functionality is officially integrate
				
				final DialogBox TODOdb = new DialogBox();
				VerticalPanel TODOverticalPanel = new VerticalPanel();
				VerticalPanel TODOmainVerticalPanel = new VerticalPanel();

				TODOmainVerticalPanel.add(TODOverticalPanel);
				TODOmainVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				
				Label lblTODO = new Label("This feature is not yet implemented.");
				TODOmainVerticalPanel.add(lblTODO);
				
				TODOdb.setText("Not yet implemented");
				
				final Button TODOcancelButton = new Button("Cancel", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						TODOdb.hide();
					}
				});
				
				TODOmainVerticalPanel.add(TODOcancelButton);

				
				TODOdb.setWidget(TODOmainVerticalPanel);
				TODOdb.center();
				TODOdb.show();
			}
		});
		
		
		Buttons.add(cancelButton);
		Buttons.add(nextButton);
		nextButton.setWidth("65px");
		
		db.setText("Export As");
		db.setWidget(mainVerticalPanel);
		mainVerticalPanel.setSize("103px", "23px");
		
	
		db.center();
		db.show();

	}
	
}
