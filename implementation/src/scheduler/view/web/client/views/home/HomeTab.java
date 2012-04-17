package scheduler.view.web.client.views.home;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;

import scheduler.view.web.client.Import;
import scheduler.view.web.shared.DocumentGWT;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class HomeTab extends Tab {
	public interface DocumentsStrategy {
		Collection<DocumentGWT> getAllOriginalDocuments();
		void trashDocuments(Collection<Integer> documentIDs);
		void mergeDocuments(Collection<Integer> documentIDs);
		void createNew();
		void openDocument(int id);
	}
	
	final DocumentsStrategy documents;
	final DataSource dataSource;
	final ListGrid aliveOriginalDocumentsGrid;
	
	public HomeTab(final DocumentsStrategy documents) {
		super("Home");
		
		this.documents = documents;
		
		VLayout homePane = new VLayout();
		setPane(homePane);
		
		setID("s_HomeTab");
		
		homePane.addMember(makeHomeTopButtons());
		
		// Documents List
		aliveOriginalDocumentsGrid = new ListGrid() {
			protected int rowCount = 0;
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				
				if (fieldName.equals("linkField")) {
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
							documents.openDocument(docID);
						}
					});
					label.setID("sc_document_"+this.rowCount);
					this.rowCount++;
					return label;
				}
				else {
					return null;
				}
			}
		};
		
		aliveOriginalDocumentsGrid.setShowRecordComponents(true);
		aliveOriginalDocumentsGrid.setShowRecordComponentsByCell(true);
		aliveOriginalDocumentsGrid.setWidth100();
		aliveOriginalDocumentsGrid.setAutoFitData(Autofit.VERTICAL);
		aliveOriginalDocumentsGrid.setShowAllRecords(true);
		aliveOriginalDocumentsGrid.setAutoFetchData(true);
		aliveOriginalDocumentsGrid.setCanEdit(false);
		dataSource = new DocumentsDataSource(new DocumentsDataSource.DocumentsStrategy() {
			public Collection<DocumentGWT> getAllDocuments() {
				Collection<DocumentGWT> allLiveOriginals = new LinkedList<DocumentGWT>();
				for (DocumentGWT document : documents.getAllOriginalDocuments())
					if (!document.isTrashed())
						allLiveOriginals.add(document);
				return allLiveOriginals;
			}
		});
		aliveOriginalDocumentsGrid.setDataSource(dataSource);
		aliveOriginalDocumentsGrid.setID("s_doclistTbl");
		
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
		
		aliveOriginalDocumentsGrid.setFields(idField, nameField);
		
		
		aliveOriginalDocumentsGrid.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace") || event.getKeyName().equals("Delete")) {
					if (com.google.gwt.user.client.Window
							.confirm("Are you sure you want to move this document to the trash?")) {
						Collection<Integer> selectedIDs = new TreeSet<Integer>();
						Record[] selectedRecords = aliveOriginalDocumentsGrid.getSelectedRecords();
						for (Record selectedRecord : selectedRecords)
							selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
						documents.trashDocuments(selectedIDs);
					}
				}
			}
		});
		
		homePane.addMember(aliveOriginalDocumentsGrid);
		
		homePane.addMember(makeHomeBottomButtons(aliveOriginalDocumentsGrid));
		
	}

	HLayout makeHomeBottomButtons(final ListGrid aliveOriginalDocumentsGrid) {
		HLayout bottomButtons = new HLayout();
		bottomButtons.setAlign(Alignment.CENTER);
		
		IButton deleteButton = new IButton("Delete Selected Documents", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Collection<Integer> selectedIDs = new TreeSet<Integer>();
				Record[] selectedRecords = aliveOriginalDocumentsGrid.getSelectedRecords();
				for (Record selectedRecord : selectedRecords)
					selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
				documents.trashDocuments(selectedIDs);
			}
		});
		deleteButton.setAutoWidth();
		deleteButton.setOverflow(Overflow.VISIBLE);
		deleteButton.setID("s_deleteBtn");
		bottomButtons.addMember(deleteButton);
		
		IButton mergeButton = new IButton("Merge Selected Documents", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Collection<Integer> selectedIDs = new TreeSet<Integer>();
				Record[] selectedRecords = aliveOriginalDocumentsGrid.getSelectedRecords();
				for (Record selectedRecord : selectedRecords)
					selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
				documents.mergeDocuments(selectedIDs);
			}
		});
		mergeButton.setAutoWidth();
		mergeButton.setOverflow(Overflow.VISIBLE);
		mergeButton.setID("s_mergeBtn");
		bottomButtons.addMember(mergeButton);
		
		return bottomButtons;
	}

	HLayout makeHomeTopButtons() {
		HLayout topButtons = new HLayout();
		topButtons.setAlign(Alignment.RIGHT);
		
		IButton createButton = new IButton("Create New Document", new ClickHandler() {
			public void onClick(ClickEvent event) {
				documents.createNew();
			}
		});
		
		
		createButton.setID("s_createBtn");
		createButton.setAutoWidth();
		createButton.setOverflow(Overflow.VISIBLE);
		topButtons.addMember(createButton);
		
		IButton importButton = new IButton("Import Document", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Import.showImport();
			}
		});
		importButton.setID("importButton");
		importButton.setAutoWidth();
		importButton.setOverflow(Overflow.VISIBLE);
		topButtons.addMember(importButton);
		
		return topButtons;
	}

	public void refreshDocuments() {
		System.out.println("HomeTab refreshDocuments()");
		aliveOriginalDocumentsGrid.invalidateCache(); // TODO: see if we can remove this
		aliveOriginalDocumentsGrid.fetchData();
	}
}
