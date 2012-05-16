package scheduler.view.web.client;

import java.util.HashMap;
import java.util.Map;

import scheduler.view.web.client.views.home.NewOriginalDocumentsCache;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ServerResourcesResponse;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CachedService {
	int sessionID;
	public GreetingServiceAsync mService; // we need to make this private very soon
	public final String username;
	
	final boolean deferredSynchronizationEnabled;
	public final NewOriginalDocumentsCache originalDocuments;
	
	Map<Integer, CachedOpenWorkingCopyDocument> workingCopyDocumentsByOriginalDocumentRealID = new HashMap<Integer, CachedOpenWorkingCopyDocument>();
	
	public CachedService(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, String username, ServerResourcesResponse<OriginalDocumentGWT> initialDocuments) {
		this.deferredSynchronizationEnabled = deferredSynchronizationEnabled;
		
		this.mService = service;
		this.sessionID = sessionID;
		this.username = username;
		
		originalDocuments = new NewOriginalDocumentsCache(deferredSynchronizationEnabled, service, sessionID, initialDocuments);

		(new Timer() {
			public void run() {
				forceSynchronize(null);
			}
		}).scheduleRepeating(5000);
	}
	
	public void forceSynchronize(AsyncCallback<Void> callback) {
		originalDocuments.forceSynchronize(callback);
		
		for (CachedOpenWorkingCopyDocument doc : workingCopyDocumentsByOriginalDocumentRealID.values())
			doc.forceSynchronize(callback);
	}
	
	public void openWorkingCopyForOriginalDocument(final int originalDocumentLocalID, boolean openExistingWorkingDocument, final AsyncCallback<CachedOpenWorkingCopyDocument> callback) {
		assert(!workingCopyDocumentsByOriginalDocumentRealID.containsKey(originalDocumentLocalID));
		
		assert(originalDocumentLocalID < 0);
		
		final Integer originalDocumentRealID = originalDocuments.localIDToRealID(originalDocumentLocalID);
		
		assert(originalDocumentRealID != null);
		assert(originalDocumentRealID > 0);
		
		mService.createAndOpenWorkingCopyForOriginalDocument(sessionID, originalDocumentRealID, openExistingWorkingDocument, new AsyncCallback<CompleteWorkingCopyDocumentGWT>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
			
			@Override
			public void onSuccess(CompleteWorkingCopyDocumentGWT completeWorkingDocument) {
				CachedOpenWorkingCopyDocument openedDocument = new CachedOpenWorkingCopyDocument(deferredSynchronizationEnabled, mService, sessionID, completeWorkingDocument);
				
				workingCopyDocumentsByOriginalDocumentRealID.put(originalDocumentRealID, openedDocument);
				
				callback.onSuccess(openedDocument);
			}
		});
	}
}
