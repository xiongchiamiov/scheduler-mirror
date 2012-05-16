package scheduler.view.web.client.views.home;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import scheduler.view.web.shared.OriginalDocumentGWT;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class TrashTab extends Tab {
	public interface DocumentsStrategy {
		Collection<OriginalDocumentGWT> getAllOriginalDocuments();
		void restoreDocuments(Collection<Integer> documentIDs);
		void openDocument(int originalDocumentID, boolean openExistingWorkingDocument);
		OriginalDocumentGWT getOriginalDocumentByID(int documentID);
	}
	
	final DocumentsStrategy documents;
	final ListGrid deletedOriginalDocumentsGrid;
	
	public TrashTab(final DocumentsStrategy documents) {
		super("Trash");
		
		this.documents = documents;
		

		VLayout trashPane = new VLayout();
		setPane(trashPane);

		{
			Canvas spacer = new Canvas();
			spacer.setHeight(10);
			spacer.setWidth(10);
			trashPane.addMember(spacer);
		}
		
		deletedOriginalDocumentsGrid = new OriginalDocumentsListGrid(new OriginalDocumentsListGrid.DocumentsStrategy() {
			@Override
			public Collection<OriginalDocumentGWT> getAllDocuments() {
				Collection<OriginalDocumentGWT> allTrashedOriginals = new LinkedList<OriginalDocumentGWT>();
				for (OriginalDocumentGWT document : documents.getAllOriginalDocuments())
					if (document.isTrashed())
						allTrashedOriginals.add(document);
				return allTrashedOriginals;
			}
			@Override
			public OriginalDocumentGWT getDocumentByID(int documentID) {
				return documents.getOriginalDocumentByID(documentID);
			}
			@Override
			public void openDocument(int documentID, boolean openExistingWorkingDocument) {
				documents.openDocument(documentID, openExistingWorkingDocument);
			}
		});
		
//		deletedOriginalDocumentsGrid.setID("s_doclistTrashTbl");
		
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
		
		deletedOriginalDocumentsGrid.setFields(idField, nameField);
		
		deletedOriginalDocumentsGrid.setFields(idField, nameField);
		
		trashPane.addMember(deletedOriginalDocumentsGrid);

		Canvas label = new Canvas();
		label.setHeight(10);
		label.setWidth(10);
		trashPane.addMember(label);
		
		trashPane.addMember(makeTrashBottomButtons(deletedOriginalDocumentsGrid));
	}
	

	HLayout makeTrashBottomButtons(final ListGrid deletedOriginalDocumentsGrid) {
		HLayout bottomButtons = new HLayout();
		bottomButtons.setAlign(Alignment.CENTER);
		
		IButton restoreButton = new IButton("Restore Selected Documents", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Collection<Integer> selectedIDs = new TreeSet<Integer>();
				Record[] selectedRecords = deletedOriginalDocumentsGrid.getSelectedRecords();
				for (Record selectedRecord : selectedRecords)
					selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
				documents.restoreDocuments(selectedIDs);
			}
		});
		restoreButton.setAutoWidth();
		restoreButton.setOverflow(Overflow.VISIBLE);
//		restoreButton.setID("s_restoreBtn");
		bottomButtons.addMember(restoreButton);
		
		return bottomButtons;
	}
	
	public void refreshDocuments() {
		deletedOriginalDocumentsGrid.invalidateCache(); // TODO: see if we can remove this
		deletedOriginalDocumentsGrid.fetchData();
	}
	
}
