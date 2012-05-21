package scheduler.view.web.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CachedService {
	int sessionID;
	public GreetingServiceAsync mService; // we need to make this private very soon
	public final String username;
	
	final boolean deferredSynchronizationEnabled;
	public final OriginalDocumentsCache originalDocuments;
	
	Map<Integer, CachedOpenWorkingCopyDocument> workingCopyDocumentsByOriginalDocumentRealID = new HashMap<Integer, CachedOpenWorkingCopyDocument>();

	boolean synchronizing = false;
	Collection<AsyncCallback<Void>> callbacksToCallAfterNextSynchronize = new LinkedList<AsyncCallback<Void>>();
	
	public CachedService(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, String username, ServerResourcesResponse<OriginalDocumentGWT> initialDocuments) {
		this.deferredSynchronizationEnabled = deferredSynchronizationEnabled;
		
		this.mService = service;
		this.sessionID = sessionID;
		this.username = username;
		
		originalDocuments = new OriginalDocumentsCache(deferredSynchronizationEnabled, service, sessionID, initialDocuments);

		(new Timer() {
			public void run() {
				forceSynchronizeOriginalDocuments(null);
			}
		}).scheduleRepeating(5000);
	}
	
	public void forceSynchronizeOriginalDocuments(AsyncCallback<Void> nextSyncCallback) {
		if (nextSyncCallback == null) {
			nextSyncCallback = new AsyncCallback<Void>() {
				public void onFailure(Throwable caught) {
					Window.alert("Failed to synchronize working document! " + caught.getMessage());
				}
				public void onSuccess(Void result) { }
			};
		}
		
		callbacksToCallAfterNextSynchronize.add(nextSyncCallback);
		
		if (!synchronizing) {
			synchronizing = true;
			
			final Collection<AsyncCallback<Void>> callbacksToCallAfterThisSynchronize = callbacksToCallAfterNextSynchronize;
			callbacksToCallAfterNextSynchronize = new LinkedList<AsyncCallback<Void>>();
			
			SynchronizeRequest<OriginalDocumentGWT> request = originalDocuments.startSynchronize();
			
			mService.synchronizeOriginalDocuments(sessionID, request, new AsyncCallback<SynchronizeResponse<OriginalDocumentGWT>>() {
				@Override
				public void onFailure(Throwable caught) {
					assert(synchronizing);
					synchronizing = false;
					for (AsyncCallback<Void> callback : callbacksToCallAfterThisSynchronize)
						callback.onFailure(caught);
				}
				
				@Override
				public void onSuccess(SynchronizeResponse<OriginalDocumentGWT> response) {
					assert(synchronizing);
					originalDocuments.finishSynchronize(response);
					synchronizing = false;
					for (AsyncCallback<Void> callback : callbacksToCallAfterThisSynchronize)
						callback.onSuccess(null);
					
					if (!callbacksToCallAfterNextSynchronize.isEmpty())
						forceSynchronizeOriginalDocuments(null);
				}
			});
		}
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
				
				for (InstructorGWT instructor : completeWorkingDocument.instructors.resourcesOnServer) {
					for (CourseGWT course : completeWorkingDocument.courses.resourcesOnServer)
						assert(instructor.getCoursePreferences().containsKey(course.getID()));
					assert(instructor.getCoursePreferences().size() == completeWorkingDocument.courses.resourcesOnServer.size());
				}
				
				CachedOpenWorkingCopyDocument openedDocument = new CachedOpenWorkingCopyDocument(deferredSynchronizationEnabled, mService, sessionID, completeWorkingDocument);
				
				openedDocument.getInstructors(false);
				
				workingCopyDocumentsByOriginalDocumentRealID.put(originalDocumentRealID, openedDocument);
				
				callback.onSuccess(openedDocument);
			}
		});
	}
}
