package scheduler.view.web.client.views.home;

import java.util.Collection;

import scheduler.view.web.shared.OriginalDocumentGWT;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;

public class OriginalDocumentsListGrid extends ListGrid {
	interface DocumentsStrategy {
		void openDocument(int documentID, boolean openExistingWorkingDocument);
		OriginalDocumentGWT getDocumentByID(int documentID);
		Collection<OriginalDocumentGWT> getAllDocuments();
	}
	
	DocumentsStrategy documentsStrategy;
	
	public OriginalDocumentsListGrid(final DocumentsStrategy documentsStrategy) {
		this.documentsStrategy = documentsStrategy;
		
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setWidth100();
		setAutoFitData(Autofit.VERTICAL);
		setShowAllRecords(true);
		setAutoFetchData(true);
		setCanEdit(false);
		setDataSource(new OriginalDocumentsDataSource(new OriginalDocumentsDataSource.DocumentsStrategy() {
			@Override
			public Collection<OriginalDocumentGWT> getAllDocuments() {
				return documentsStrategy.getAllDocuments();
			}
		}));
		
		ListGridField idField = new ListGridField("id", "&nbsp;");
		
		idField.setCanEdit(false);
		idField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				return "\u22EE";
			}
		});
		idField.setWidth(20);
		idField.setAlign(Alignment.CENTER);
		
		ListGridField nameField = new ListGridField("linkField", "Document");
		
		setFields(idField, nameField);
	}

	@Override
	protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
		String fieldName = this.getFieldName(colNum);
		
		if (fieldName.equals("linkField")) {
			HLayout layout = new HLayout();
			layout.setOverflow(Overflow.VISIBLE);
			layout.setAutoHeight();
			
			{
				Label label = new Label(record.getAttribute("name"));
				
				// for some reason two separate calls didnt work here, it wouldnt pick up the first one. -eo
				label.setStyleName("inAppLink homeDocumentLink");
	
				label.setOverflow(Overflow.VISIBLE);
				label.setAutoWidth();
				label.setAutoHeight();
				label.setWrap(false);
				label.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						int docID = record.getAttributeAsInt("id");
						documentsStrategy.openDocument(docID, false);
					}
				});
				
				layout.addMember(label);
			}
	
			{
				final OriginalDocumentGWT document = documentsStrategy.getDocumentByID(record.getAttributeAsInt("id"));
				
				String workingChangesSummary = document.getWorkingChangesSummary();
				if (workingChangesSummary == null)
					workingChangesSummary = "";
				
				Label workingLabel = new Label(" (being edited)");
	
				workingLabel.setStyleName("inAppLink");
	
				workingLabel.setOverflow(Overflow.VISIBLE);
				workingLabel.setAutoWidth();
				workingLabel.setAutoHeight();
				workingLabel.setWrap(false);
				workingLabel.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						documentsStrategy.openDocument(document.getID(), true);
					}
				});
				
				layout.addMember(workingLabel);
			}
			
			return layout;
		}
		else {
			return null;
		}
	}
}
