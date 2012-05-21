package scheduler.view.web.client;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
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

	public static void displayExportPopup(final CachedService service,
			final CachedOpenWorkingCopyDocument document) {

		final Window window = new Window();
		window.setAutoSize(true);
		window.setTitle("Export As");
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		window.setID("s_exportPopup");

		VerticalPanel mainVerticalPanel = new VerticalPanel();
		DOM.setElementAttribute(mainVerticalPanel.getElement(), "id",
				"s_mainExportPanel");

		VerticalPanel verticalPanel = new VerticalPanel();
		DOM.setElementAttribute(verticalPanel.getElement(), "id",
				"s_exportPanel");

		mainVerticalPanel.add(verticalPanel);

		HorizontalPanel typeSelectorPanel = new HorizontalPanel();

		DOM.setElementAttribute(typeSelectorPanel.getElement(), "id",
				"s_typeSelectorPanel");

		verticalPanel.add(typeSelectorPanel);

		final FocusPanel csvFocusPanel = new FocusPanel();
		csvFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				window.setTitle("Export As CSV");
			}
		});

		DOM.setElementAttribute(csvFocusPanel.getElement(), "id",
				"s_csvFocusPanel");

		FocusPanel pdfFocusPanel = new FocusPanel();
		pdfFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				window.setTitle("Export As PDF");
			}
		});

		DOM.setElementAttribute(pdfFocusPanel.getElement(), "id",
				"s_pdfFocusPanel");

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
		pdfPanel.setCellHorizontalAlignment(lblCSV,
				HasHorizontalAlignment.ALIGN_CENTER);
		csvFocusPanel.setStyleName("exportChoice");
		typeSelectorPanel.add(csvFocusPanel);
		typeSelectorPanel.setCellHorizontalAlignment(csvFocusPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
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
		csvPanel.setCellHorizontalAlignment(lblNewLabel,
				HasHorizontalAlignment.ALIGN_CENTER);

		final HorizontalPanel Buttons = new HorizontalPanel();
		Buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		mainVerticalPanel.add(Buttons);
		Buttons.setWidth("102px");
		mainVerticalPanel.setCellHorizontalAlignment(Buttons,
				HasHorizontalAlignment.ALIGN_CENTER);

		final Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				window.destroy();
			}
		});

		DOM.setElementAttribute(cancelButton.getElement(), "id",
				"s_cancelExportBtn");

		final Button nextButton = new Button("Next", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (window.getTitle().equals("Export As CSV")) {
					service.exportCSV(document.getDocument().getRealID());
								
				} else if (window.getTitle().equals("Export As PDF")) {
					com.google.gwt.user.client.Window
							.alert("This feature is not yet implemented");
				} else {
					com.google.gwt.user.client.Window
							.alert("Please select an option");
				}
				window.destroy();
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
