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

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.SelectionAppearance;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
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
		topButtons.addMember(importButton);
		
		return topButtons;
	}
	
	HLayout makeTrashBottomButtons(final ListGrid deletedOriginalDocumentsGrid) {
		HLayout bottomButtons = new HLayout();

		IButton deleteButton = new IButton("Restore Selected Documents", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Collection<Integer> selectedIDs = new TreeSet<Integer>();
				Record[] selectedRecords = deletedOriginalDocumentsGrid.getSelectedRecords();
				for (Record selectedRecord : selectedRecords)
					selectedIDs.add(selectedRecord.getAttributeAsInt("id"));
				restoreSelectedDocuments(selectedIDs);
			}
		});
		deleteButton.setAutoWidth();
		deleteButton.setOverflow(Overflow.VISIBLE);
		bottomButtons.addMember(deleteButton);

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
		bottomButtons.addMember(instructorsButton);
		
		bottomButtons.addMember(new IButton("open (temporary)", new ClickHandler() {
			public void onClick(ClickEvent event) {
				int docID = aliveOriginalDocumentsGrid.getSelectedRecord().getAttributeAsInt("id");
				TabOpener.openDocInNewTab(username, documentsCache.getDocumentByID(docID));
			}
		}));
		
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
		
		tabSet.addTab(makeTrashTabAndPane());
		
		this.add(tabSet);
	}
	
	Tab makeHomeTabAndPane(String username) {
		final Tab homeTab = new Tab("Home");
		VLayout homePane = new VLayout();
		homeTab.setPane(homePane);

		homePane.addMember(makeHomeTopButtons(username));
		
		// Documents List
		final ListGrid aliveOriginalDocumentsGrid = new ListGrid();
		aliveOriginalDocumentsGrid.setWidth100();
		aliveOriginalDocumentsGrid.setAutoFitData(Autofit.VERTICAL);
		aliveOriginalDocumentsGrid.setShowAllRecords(true);
		aliveOriginalDocumentsGrid.setAutoFetchData(true);
		aliveOriginalDocumentsGrid.setCanEdit(false);
		aliveOriginalDocumentsGrid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
		aliveOriginalDocumentsGrid.setDataSource(new OriginalDocumentsCacheDataSource(documentsCache, OriginalDocumentsCacheDataSource.Mode.LIVE_DOCUMENTS_ONLY));
		
		ListGridField idField = new ListGridField("id");
		idField.setHidden(true);
		
		ListGridField nameField = new ListGridField("name", "Name");

		aliveOriginalDocumentsGrid.setFields(idField, nameField);
		
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
	
	Tab makeTrashTabAndPane() {
		final Tab trashTab = new Tab("Trash");
		VLayout trashPane = new VLayout();
		trashTab.setPane(trashPane);

		final ListGrid deletedOriginalDocumentsGrid = new ListGrid();
		deletedOriginalDocumentsGrid.setWidth100();
		deletedOriginalDocumentsGrid.setAutoFitData(Autofit.VERTICAL);
		deletedOriginalDocumentsGrid.setShowAllRecords(true);
		deletedOriginalDocumentsGrid.setAutoFetchData(true);
		deletedOriginalDocumentsGrid.setCanEdit(false);
		deletedOriginalDocumentsGrid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
		deletedOriginalDocumentsGrid.setDataSource(new OriginalDocumentsCacheDataSource(documentsCache, OriginalDocumentsCacheDataSource.Mode.DELETED_DOCUMENTS_ONLY));
		
		ListGridField idField = new ListGridField("id");
		idField.setHidden(true);
		
		ListGridField nameField = new ListGridField("name", "Name");

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
			document.setTrashed(true);
			documentsCache.updateDocument(document);
			
//			access a cached copy in the datasource, then tell datasource to send that for editing
//			in the meantime, move it to the trashed list
//			the trashed list should refresh based on the cached datasource
		}
	}
}
