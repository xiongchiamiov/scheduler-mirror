package scheduler.view.web.client.views.home;

import java.util.Collection;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.MergeDialog;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.TabOpener;
import scheduler.view.web.client.views.home.OriginalDocumentsCache.Observer;
import scheduler.view.web.client.views.resources.instructors.InstructorsHomeView;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.tab.TabSet;

public class HomeView extends VerticalPanel {
	GreetingServiceAsync service;
	OriginalDocumentsCache documentsCache;
	HomeTab homeTab;
	TrashTab trashTab;
	
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
		
		homeTab = new HomeTab(new HomeTab.DocumentsStrategy() {
			@Override
			public void trashDocuments(Collection<Integer> documentIDs) {
				HomeView.this.trashDocuments(documentIDs);
			}
			
			@Override
			public void openDocument(int id) {
				TabOpener.openDocInNewTab(username, documentsCache.getDocumentByID(id));
			}
			
			@Override
			public void mergeDocuments(Collection<Integer> documentIDs) {
				// TODO: https://scheduler.atlassian.net/browse/SCHED-388 (Feed selected documents on home screen into merge dialog)
				MergeDialog.fileMergePressed(service);
			}
			
			@Override
			public Collection<DocumentGWT> getAllOriginalDocuments() {
				return documentsCache.getAllDocuments();
			}
			
			@Override
			public void createNew() {
				NewScheduleCreator.createNewSchedule(service, username, new NewScheduleCreator.CreatedScheduleCallback() {
					public void createdSchedule() {
						System.out.println("created schedule");
						documentsCache.updateFromServer();
					}
				});
			}
		});
		tabSet.addTab(homeTab);
		
		trashTab = new TrashTab(new TrashTab.DocumentsStrategy() {
			@Override
			public void restoreDocuments(Collection<Integer> documentIDs) {
				HomeView.this.restoreDocuments(documentIDs);
			}
			
			@Override
			public void openDocument(int id) {
				TabOpener.openDocInNewTab(username, documentsCache.getDocumentByID(id));
			}
			
			@Override
			public Collection<DocumentGWT> getAllOriginalDocuments() {
				return documentsCache.getAllDocuments();
			}
		});
		tabSet.addTab(trashTab);
		
		this.add(tabSet);
		

		Button instructorsButton = new Button("Instructors Home View (temporary)", new ClickHandler() {
			public void onClick(ClickEvent event) {
				com.smartgwt.client.widgets.Window instructorWindow = new com.smartgwt.client.widgets.Window();
				InstructorsHomeView homeView = new InstructorsHomeView(service, username);
				instructorWindow.addItem(homeView);
				homeView.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				instructorWindow.setSize("500px", "500px");
				instructorWindow.show();
			}
		});
		instructorsButton.getElement().setId("s_instructorsTab");
		
		this.add(instructorsButton);
		

		this.documentsCache.addObserver(new Observer() {
			public void afterDocumentAdded(DocumentGWT document) {
				System.out.println("homeview added " + document.isTrashed());
				homeTab.refreshDocuments();
				trashTab.refreshDocuments();
			}
			public void afterDocumentEdited(DocumentGWT oldDocument, DocumentGWT newDocument) {
				System.out.println("homeview edited " + oldDocument.isTrashed() + " to " + newDocument.isTrashed());
				homeTab.refreshDocuments();
				trashTab.refreshDocuments();
			}
			public void beforeDocumentRemoved(DocumentGWT document) {
				homeTab.refreshDocuments();
				trashTab.refreshDocuments();
			}
		});
		
		(new Timer() {
			public void run() {
				documentsCache.updateFromServer();
			}
		}).scheduleRepeating(5000);
	}
	
	private void restoreDocuments(Collection<Integer> selectedIDs) {
		for (Integer documentID : selectedIDs) {
			System.out.println("Trashing " + documentID);
			DocumentGWT document = documentsCache.getDocumentByID(documentID);
			document.setTrashed(false);
			documentsCache.updateDocument(document);
		}
	}
	
	private void trashDocuments(Collection<Integer> selectedIDs) {
		for (Integer documentID : selectedIDs) {
			System.out.println("Trashing " + documentID);
			DocumentGWT document = documentsCache.getDocumentByID(documentID);
			assert (document.isTrashed() == false);
			document.setTrashed(true);
			documentsCache.updateDocument(document);
			
			// access a cached copy in the datasource, then tell datasource to send
			// that for editing
			// in the meantime, move it to the trashed list
			// the trashed list should refresh based on the cached datasource
		}
	}
}
