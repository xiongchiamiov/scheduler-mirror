package scheduler.view.web.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.Window;

public class ExportDialog {

	/**
	 * Displays a popup to export schedule.
	 */
	public static void displayExportPopup()
	{
		final Window window = new Window();
		window.setAutoSize(true);
		window.setTitle("Export As");
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
//		window.setID("s_exportPopup");
		
		VerticalPanel mainVerticalPanel = new VerticalPanel();
		DOM.setElementAttribute(mainVerticalPanel.getElement(), "id", "s_mainExportPanel");
		
		VerticalPanel verticalPanel = new VerticalPanel();
	    DOM.setElementAttribute(verticalPanel.getElement(), "id", "s_exportPanel");
	    
		mainVerticalPanel.add(verticalPanel);
		
		HorizontalPanel typeSelectorPanel = new HorizontalPanel();
	    
		DOM.setElementAttribute(typeSelectorPanel.getElement(), "id", "s_typeSelectorPanel");
		
		verticalPanel.add(typeSelectorPanel);
		
		final FocusPanel csvFocusPanel = new FocusPanel();
		csvFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO Save Selection
			}
		});
		
		DOM.setElementAttribute(csvFocusPanel.getElement(), "id", "s_csvFocusPanel");
		
		FocusPanel pdfFocusPanel = new FocusPanel();
		pdfFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO Save Selection
			}
		});
		
		DOM.setElementAttribute(pdfFocusPanel.getElement(), "id", "s_pdfFocusPanel");
		
		pdfFocusPanel.setStyleName("exportChoice");
		typeSelectorPanel.add(pdfFocusPanel);
		pdfFocusPanel.setSize("", "");
		
		VerticalPanel pdfPanel = new VerticalPanel();
		pdfFocusPanel.setWidget(pdfPanel);
		pdfPanel.setSize("100px", "100px");
		
		DOM.setElementAttribute(pdfPanel.getElement(), "id", "s_pdfPanel");
		
		Image PDFIcon = new Image("imgs/pdf-icon.png");
		pdfPanel.add(PDFIcon);
		PDFIcon.setSize("100px", "100px");
		
		DOM.setElementAttribute(PDFIcon.getElement(), "id", "s_pdfIcon");
		
		Label lblCSV = new Label("PDF");
		DOM.setElementAttribute(lblCSV.getElement(), "id", "s_csvLbl");
		
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
		DOM.setElementAttribute(CSVIcon.getElement(), "id", "s_csvIcon");
		
		csvPanel.add(CSVIcon);
		CSVIcon.setSize("100px", "100px");

		Label lblNewLabel = new Label("Excel (CSV)");
		DOM.setElementAttribute(lblNewLabel.getElement(), "id", "s_excelCSVLbl");
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
				window.destroy();
			}
		});
		
		DOM.setElementAttribute(cancelButton.getElement(), "id", "s_cancelExportBtn");
		
		final Button nextButton = new Button("Next", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				window.destroy();
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
				
				//DOM.setElementAttribute(verticalPanel.getElement(), "id", "s_exportPanel");
				
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
		
		window.addItem(mainVerticalPanel);
		mainVerticalPanel.setSize("103px", "23px");
		
	
		window.centerInPage();
		window.show();
	}
	
}
