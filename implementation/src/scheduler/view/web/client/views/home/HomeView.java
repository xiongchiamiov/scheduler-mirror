package scheduler.view.web.client.views.home;

import java.util.Collection;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.CachedService;
import scheduler.view.web.client.MergeDialog;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.UpdateHeaderStrategy;
import scheduler.view.web.client.views.AdminScheduleNavView;
import scheduler.view.web.client.views.LoadingPopup;
import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.OriginalDocumentGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.tab.TabSet;

public class HomeView extends SimplePanel {
	CachedService service;
	final UpdateHeaderStrategy updateHeaderStrategy;
	HomeTab homeTab;
	TrashTab trashTab;
	
	VerticalPanel homeViewContents;
	AdminScheduleNavView navView;
	
	public HomeView(UpdateHeaderStrategy updateHeaderStrategy, final CachedService service) {
		this.updateHeaderStrategy = updateHeaderStrategy;
		this.service = service;
		
		homeViewContents = new VerticalPanel();
		
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
			public OriginalDocumentGWT getOriginalDocumentByID(int documentID) {
				return service.originalDocuments.getByID(documentID);
			}
			@Override
			public void createNew() {
				NewScheduleCreator.createNewSchedule(service, new NewScheduleCreator.OpenDocumentCallback() {
					@Override
					public void openDocument(int documentID) {
						HomeView.this.openDocument(documentID, false);
					}
				});
			}
			
			@Override
			public void openDocument(int originalDocumentID, boolean openExistingWorkingDocument) {
//				TabOpener.openDocInNewTab(username, service.originalDocuments.localIDToRealID(originalDocumentID), openExistingWorkingDocument);
				HomeView.this.openDocument(originalDocumentID, openExistingWorkingDocument);
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
//				TabOpener.openDocInNewTab(username, service.originalDocuments.localIDToRealID(originalDocumentID), openExistingWorkingDocument);
				HomeView.this.openDocument(originalDocumentID, openExistingWorkingDocument);
			}
			
			@Override
			public Collection<OriginalDocumentGWT> getAllOriginalDocuments() {
				return service.originalDocuments.getAll();
			}
			
			@Override
			public OriginalDocumentGWT getOriginalDocumentByID(int documentID) {
				return service.originalDocuments.getByID(documentID);
			}
		});
		tabSet.addTab(trashTab);
		
		homeViewContents.add(tabSet);
		
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
		
		setWidget(homeViewContents);
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
	
	private void openDocument(final int originalDocumentID, boolean openExistingWorkingDocument) {
		final LoadingPopup loadingPopup = new LoadingPopup();
		loadingPopup.show();
		
		service.openWorkingCopyForOriginalDocument(originalDocumentID, openExistingWorkingDocument, new AsyncCallback<CachedOpenWorkingCopyDocument>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get document!");
				loadingPopup.hide();
			}
			
			@Override
			public void onSuccess(CachedOpenWorkingCopyDocument workingCopyDocument) {
				updateHeaderStrategy.onOpenedDocument(service.originalDocuments.getByID(service.originalDocuments.realIDToLocalID(originalDocumentID)).getName());
				
				CloseStrategy closeStrategy = new CloseStrategy() {
					@Override
					public void closeDocument() {
						HomeView.this.closeDocument();
					}
				};
				
				OpenDocumentStrategy openDocumentStrategy = new OpenDocumentStrategy() {
					@Override
					public void openDocument(int documentID, boolean openExistingWorkingDocument) {
						if (HomeView.this.closeDocument())
							HomeView.this.openDocument(documentID, openExistingWorkingDocument);
					}
				};
				
				navView = new AdminScheduleNavView(service, updateHeaderStrategy, closeStrategy, openDocumentStrategy, workingCopyDocument);
				setWidget(navView);
				loadingPopup.hide();
			}
		});
	}
	
	protected boolean closeDocument() {
		if (navView.canClose()) {
			navView.close();
			clear();
			setWidget(homeViewContents); // instead of add
			return true;
		}
		return false;
	}

	boolean canClose() {
		if (navView != null) {
			return navView.canClose();
		}
		return true;
	}
	
	void close() {
		navView.close();

		this.clear();
	}
}
