package scheduler.view.web.client.views.home;

import java.util.Collection;
import java.util.TreeSet;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.Import;
import scheduler.view.web.client.MergeDialog;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.NewScheduleCreator.CreatedScheduleCallback;
import scheduler.view.web.client.TabOpener;
import scheduler.view.web.client.views.home.OriginalDocumentsCache.Observer;
import scheduler.view.web.client.views.resources.instructors.InstructorsHomeView;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
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
import com.smartgwt.client.widgets.tab.TabSet;

public class HomeView extends VerticalPanel {
	OriginalDocumentsCache documentsCache;
	GreetingServiceAsync service;
	
	HLayout makeHomeTopButtons(final String username) {
		HLayout topButtons = new HLayout();
		topButtons.setAlign(Alignment.RIGHT);
		
		IButton createButton = new IButton("Create New Document", new ClickHandler() {
			public void onClick(ClickEvent event) {
				NewScheduleCreator.createNewSchedule(service, username, new CreatedScheduleCallback() {
					public void createdSchedule() {
						System.out.println("created schedule");
						documentsCache.updateFromServer();
					}
				});
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
		importButton.setAutoWidth();
		importButton.setOverflow(Overflow.VISIBLE);
        importButton.setID("s_ImportBtn");
		topButtons.addMember(importButton);

		return topButtons;
	}
	
	HLayout makeTrashBottomButtons(final ListGrid deletedOriginalDocumentsGrid) {
		HLayout bottomButtons = new HLayout();

		IButton restoreButton = new IButton("Restore Selected Documents", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Collection<Integer> selectedIDs = new TreeSet<Integer>();
				Record[] selectedRecords = deletedOriginalDocumentsGrid.getSelectedRecords();
				for (Record selectedRecord : selectedRecords)
					selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
				restoreSelectedDocuments(selectedIDs);
			}
		});
		restoreButton.setAutoWidth();
		restoreButton.setOverflow(Overflow.VISIBLE);
		restoreButton.setID("s_restoreBtn");
		bottomButtons.addMember(restoreButton);

		return bottomButtons;
	}
	
	HLayout makeHomeBottomButtons(final ListGrid aliveOriginalDocumentsGrid, final String username) {
		HLayout bottomButtons = new HLayout();
		bottomButtons.setAlign(Alignment.CENTER);
		
		IButton deleteButton = new IButton("Delete Selected Documents", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Collection<Integer> selectedIDs = new TreeSet<Integer>();
				Record[] selectedRecords = aliveOriginalDocumentsGrid.getSelectedRecords();
				for (Record selectedRecord : selectedRecords)
					selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
				trashSelectedDocuments(selectedIDs);
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
				mergeSelectedDocuments(selectedIDs);
			}
		});
		mergeButton.setAutoWidth();
		mergeButton.setOverflow(Overflow.VISIBLE);
		mergeButton.setID("s_mergeBtn");
		bottomButtons.addMember(mergeButton);

		IButton instructorsButton = new IButton("Instructors Home View (temporary)", new ClickHandler() {
			public void onClick(ClickEvent event) {
	        	 com.smartgwt.client.widgets.Window instructorWindow = new com.smartgwt.client.widgets.Window();
	        	 InstructorsHomeView homeView = new InstructorsHomeView(service, username);
	        	 instructorWindow.addItem(homeView);
	        	 homeView.setHorizontalAlignment(ALIGN_CENTER);
	        	 instructorWindow.setSize("500px", "500px");
	        	 instructorWindow.show();
			}
		});
		instructorsButton.setAutoWidth();
		instructorsButton.setOverflow(Overflow.VISIBLE);
		
		instructorsButton.setID("s_instructorsTab");
		
		return bottomButtons;
	}
	
	public HomeView(final GreetingServiceAsync service, SimplePanel parentPanel, final String username) {
		this.service = service;
		
		this.documentsCache = new OriginalDocumentsCache(service);
		
		TabSet tabSet = new TabSet();
		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarThickness(25);
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight(25);
		tabSet.setOverflow(Overflow.VISIBLE);
		tabSet.setPaneContainerOverflow(Overflow.VISIBLE);

		tabSet.addTab(makeHomeTabAndPane(username));
		
		tabSet.addTab(makeTrashTabAndPane(username));
		
		this.add(tabSet);
	}
	
	Tab makeHomeTabAndPane(final String username) {
		final Tab homeTab = new Tab("Home");
		VLayout homePane = new VLayout();
		homeTab.setPane(homePane);
		
		homeTab.setID("s_HomeTab");

		homePane.addMember(makeHomeTopButtons(username));
		
		// Documents List
		final ListGrid aliveOriginalDocumentsGrid = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				
				if (fieldName.equals("linkField")) {
					Label label = new Label(record.getAttribute("name"));
					label.addStyleName("inAppLink homeDocumentLink"); // for some reason two separate calls didn't work here, it wouldn't pick up the first one. - eo
					label.setAutoWidth();
					label.setOverflow(Overflow.VISIBLE);
					label.setAutoHeight();
					label.setOverflow(Overflow.VISIBLE);
					label.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							int docID = record.getAttributeAsInt("id");
							TabOpener.openDocInNewTab(username, documentsCache.getDocumentByID(docID));
						}
					});
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
		aliveOriginalDocumentsGrid.setDataSource(new OriginalDocumentsCacheDataSource(documentsCache, OriginalDocumentsCacheDataSource.Mode.LIVE_DOCUMENTS_ONLY));
		aliveOriginalDocumentsGrid.setID("s_doclistTbl");
		
		(new Timer() {
			public void run() {
	      	documentsCache.updateFromServer();
				aliveOriginalDocumentsGrid.invalidateCache();
				aliveOriginalDocumentsGrid.fetchData();
			}
		}).scheduleRepeating(5000);

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
					if (com.google.gwt.user.client.Window.confirm("Are you sure you want to move this document to the trash?")) {
						Collection<Integer> selectedIDs = new TreeSet<Integer>();
						Record[] selectedRecords = aliveOriginalDocumentsGrid.getSelectedRecords();
						for (Record selectedRecord : selectedRecords)
							selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
						trashSelectedDocuments(selectedIDs);
					}
				}
			}
		});
		
		homePane.addMember(aliveOriginalDocumentsGrid);
		
		homePane.addMember(makeHomeBottomButtons(aliveOriginalDocumentsGrid, username));

		this.documentsCache.addObserver(new Observer() {
			public void afterDocumentAdded(DocumentGWT document) {
				System.out.println("added " + document.isTrashed());
				aliveOriginalDocumentsGrid.invalidateCache();
				aliveOriginalDocumentsGrid.fetchData();
			}
			public void afterDocumentEdited(DocumentGWT oldDocument, DocumentGWT newDocument) {
				System.out.println("edited " + oldDocument.isTrashed() + " to " + newDocument.isTrashed());
				aliveOriginalDocumentsGrid.invalidateCache();
				aliveOriginalDocumentsGrid.fetchData();
			}
			public void beforeDocumentRemoved(DocumentGWT document) {
				aliveOriginalDocumentsGrid.invalidateCache();
				aliveOriginalDocumentsGrid.fetchData();
			}
		});
		
		return homeTab;
	}
	
	Tab makeTrashTabAndPane(final String username) {
		final Tab trashTab = new Tab("Trash");
		VLayout trashPane = new VLayout();
		trashTab.setPane(trashPane);

		// Documents List
		final ListGrid deletedOriginalDocumentsGrid = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				
				if (fieldName.equals("linkField")) {
					Label label = new Label(record.getAttribute("name"));
					label.addStyleName("inAppLink homeDocumentLink"); // for some reason two separate calls didn't work here, it wouldn't pick up the first one. - eo
					label.setAutoWidth();
					label.setOverflow(Overflow.VISIBLE);
					label.setAutoHeight();
					label.setOverflow(Overflow.VISIBLE);
					label.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							int docID = record.getAttributeAsInt("id");
							TabOpener.openDocInNewTab(username, documentsCache.getDocumentByID(docID));
						}
					});
					return label;
				}
				else {
					return null;
				}
			}
		};

		deletedOriginalDocumentsGrid.setShowRecordComponents(true);          
		deletedOriginalDocumentsGrid.setShowRecordComponentsByCell(true);  
      deletedOriginalDocumentsGrid.setWidth100();
		deletedOriginalDocumentsGrid.setAutoFitData(Autofit.VERTICAL);
		deletedOriginalDocumentsGrid.setShowAllRecords(true);
		deletedOriginalDocumentsGrid.setAutoFetchData(true);
		deletedOriginalDocumentsGrid.setCanEdit(false);
		deletedOriginalDocumentsGrid.setDataSource(new OriginalDocumentsCacheDataSource(documentsCache, OriginalDocumentsCacheDataSource.Mode.DELETED_DOCUMENTS_ONLY));
		deletedOriginalDocumentsGrid.setID("s_doclistTrashTbl");

		(new Timer() {
	      public void run() {
	      	documentsCache.updateFromServer();
	      	deletedOriginalDocumentsGrid.invalidateCache();
	      	deletedOriginalDocumentsGrid.fetchData();
	      }
	    }).scheduleRepeating(5000);

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

		this.documentsCache.addObserver(new Observer() {
			public void afterDocumentAdded(DocumentGWT document) {
				deletedOriginalDocumentsGrid.invalidateCache();
				deletedOriginalDocumentsGrid.fetchData();
			}
			public void afterDocumentEdited(DocumentGWT oldDocument, DocumentGWT newDocument) {
				deletedOriginalDocumentsGrid.invalidateCache();
				deletedOriginalDocumentsGrid.fetchData();
			}
			public void beforeDocumentRemoved(DocumentGWT document) {
				deletedOriginalDocumentsGrid.invalidateCache();
				deletedOriginalDocumentsGrid.fetchData();
			}
		});
		
		trashPane.addMember(makeTrashBottomButtons(deletedOriginalDocumentsGrid));
		
		return trashTab;
	}

	protected void restoreSelectedDocuments(Collection<Integer> selectedIDs) {
		for (Integer documentID : selectedIDs) {
			System.out.println("Trashing " + documentID);
			DocumentGWT document = documentsCache.getDocumentByID(documentID);
			document.setTrashed(false);
			documentsCache.updateDocument(document);
		}
	}

	protected void mergeSelectedDocuments(Collection<Integer> selectedIDs) {
		MergeDialog.fileMergePressed(service);
	}

	protected void trashSelectedDocuments(Collection<Integer> selectedIDs) {
		for (Integer documentID : selectedIDs) {
			System.out.println("Trashing " + documentID);
			DocumentGWT document = documentsCache.getDocumentByID(documentID);
			assert(document.isTrashed() == false);
			document.setTrashed(true);
			documentsCache.updateDocument(document);
			
//			access a cached copy in the datasource, then tell datasource to send that for editing
//			in the meantime, move it to the trashed list
//			the trashed list should refresh based on the cached datasource
		}
	}
}
