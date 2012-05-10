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
	
	final boolean deferredSynchronizationEnabled;
	public final NewOriginalDocumentsCache originalDocuments;
	
	Map<Integer, CachedOpenWorkingCopyDocument> workingCopyDocumentsByOriginalDocumentID = new HashMap<Integer, CachedOpenWorkingCopyDocument>();
	
	public CachedService(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, ServerResourcesResponse<OriginalDocumentGWT> initialDocuments) {
		this.deferredSynchronizationEnabled = deferredSynchronizationEnabled;
		
		this.mService = service;
		this.sessionID = sessionID;
		
		originalDocuments = new NewOriginalDocumentsCache(deferredSynchronizationEnabled, service, sessionID, initialDocuments);

		(new Timer() {
			public void run() {
				forceSynchronize(null);
			}
		}).scheduleRepeating(5000);
	}
	
	public void forceSynchronize(AsyncCallback<Void> callback) {
		originalDocuments.forceSynchronize(callback);
		
		for (CachedOpenWorkingCopyDocument doc : workingCopyDocumentsByOriginalDocumentID.values())
			doc.forceSynchronize(callback);
	}
	
	public void openWorkingCopyForOriginalDocument(final int originalDocumentID, final AsyncCallback<CachedOpenWorkingCopyDocument> callback) {
		assert(!workingCopyDocumentsByOriginalDocumentID.containsKey(originalDocumentID));
		
		mService.createAndOpenWorkingCopyForOriginalDocument(sessionID, originalDocumentID, new AsyncCallback<CompleteWorkingCopyDocumentGWT>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
			
			@Override
			public void onSuccess(CompleteWorkingCopyDocumentGWT completeWorkingDocument) {

				CachedOpenWorkingCopyDocument openedDocument = new CachedOpenWorkingCopyDocument(deferredSynchronizationEnabled, mService, sessionID, completeWorkingDocument);
				
				workingCopyDocumentsByOriginalDocumentID.put(originalDocumentID, openedDocument);
				
				callback.onSuccess(openedDocument);
			}
		});
	}
}
