package scheduler.view.web.client.views.home;

import java.util.Collection;

import scheduler.view.web.client.CachedService;
import scheduler.view.web.client.MergeDialog;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.TabOpener;
import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.client.views.resources.instructors.InstructorsHomeView;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.OriginalDocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.tab.TabSet;

public class HomeView extends VerticalPanel {
	CachedService service;
	HomeTab homeTab;
	TrashTab trashTab;
	
	public HomeView(final CachedService service, SimplePanel parentPanel, final String username) {
		this.service = service;
		
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
			public void mergeDocuments(Collection<Integer> documentIDs) {
				// TODO: https://scheduler.atlassian.net/browse/SCHED-388 (Feed selected documents on home screen into merge dialog)
				MergeDialog.fileMergePressed(service);
			}
			
			@Override
			public Collection<OriginalDocumentGWT> getAllOriginalDocuments() {
				return service.originalDocuments.getAll();
			}
			
			@Override
			public void createNew() {
				NewScheduleCreator.createNewSchedule(service, username);
			}
			
			@Override
			public void openDocument(int originalDocumentID, boolean openExistingWorkingDocument) {
				TabOpener.openDocInNewTab(username, service.originalDocuments.localIDToRealID(originalDocumentID), openExistingWorkingDocument);
			}
		});
		tabSet.addTab(homeTab);
		
		trashTab = new TrashTab(new TrashTab.DocumentsStrategy() {
			@Override
			public void restoreDocuments(Collection<Integer> documentIDs) {
				HomeView.this.restoreDocuments(documentIDs);
			}

			@Override
			public void openDocument(int originalDocumentID, boolean openExistingWorkingDocument) {
				TabOpener.openDocInNewTab(username, service.originalDocuments.localIDToRealID(originalDocumentID), openExistingWorkingDocument);
			}
			
			@Override
			public Collection<OriginalDocumentGWT> getAllOriginalDocuments() {
				return service.originalDocuments.getAll();
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
		

		service.originalDocuments.addObserver(new ResourceCache.Observer<OriginalDocumentGWT>() {
			@Override
			public void afterSynchronize() { }
			@Override
			public void onAnyLocalChange() { }
			@Override
			public void onResourceAdded(OriginalDocumentGWT resource, boolean addedLocally) {
				System.out.println("HomeView.observer.added");
				homeTab.refreshDocuments();
				trashTab.refreshDocuments();
			}
			@Override
			public void onResourceEdited(OriginalDocumentGWT resource, boolean editedLocally) {
				System.out.println("HomeView.observer.edited");
				homeTab.refreshDocuments();
				trashTab.refreshDocuments();
			}
			@Override
			public void onResourceDeleted(int localID, boolean deletedLocally) {
				System.out.println("HomeView.observer.deleted");
				homeTab.refreshDocuments();
				trashTab.refreshDocuments();
			}
		});
	}
	
	private void restoreDocuments(Collection<Integer> selectedIDs) {
		for (Integer documentID : selectedIDs) {
			System.out.println("Trashing " + documentID);
			OriginalDocumentGWT document = service.originalDocuments.getByID(documentID);
			document.setTrashed(false);
			service.originalDocuments.edit(document);
		}
	}
	
	private void trashDocuments(Collection<Integer> selectedIDs) {
		for (Integer documentID : selectedIDs) {
			System.out.println("Trashing " + documentID);
			OriginalDocumentGWT document = service.originalDocuments.getByID(documentID);
			assert (document.isTrashed() == false);
			document.setTrashed(true);
			service.originalDocuments.edit(document);
			
			// access a cached copy in the datasource, then tell datasource to send
			// that for editing
			// in the meantime, move it to the trashed list
			// the trashed list should refresh based on the cached datasource
		}
	}
}
