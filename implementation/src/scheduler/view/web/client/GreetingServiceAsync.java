package scheduler.view.web.client;

import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;
import scheduler.view.web.shared.WorkingDocumentSynchronizeRequest;
import scheduler.view.web.shared.WorkingDocumentSynchronizeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void createAndOpenWorkingCopyForOriginalDocument(
			int sessionID,
			int originalDocumentID,
			boolean openExistingWorkingDocument, AsyncCallback<CompleteWorkingCopyDocumentGWT> callback);

	void synchronizeOriginalDocuments(
			int sessionID,
			SynchronizeRequest<OriginalDocumentGWT> request,
			AsyncCallback<SynchronizeResponse<OriginalDocumentGWT>> callback);

	void saveWorkingCopyToOriginalDocument(int sessionID, int documentID, AsyncCallback<Void> callback);

	void deleteWorkingCopyDocument(int sessionID, int documentID, AsyncCallback<Void> callback);

	void associateWorkingCopyWithNewOriginalDocument(
			int sessionID,
			int workingCopyID,
			String scheduleName,
			boolean allowOverwrite,
			AsyncCallback<Void> callback);

	void generateRestOfSchedule(
			int sessionID,
			int scheduleID,
			AsyncCallback<Void> asyncCallback);

	void getAllOriginalDocuments(int sessionID, AsyncCallback<ServerResourcesResponse<OriginalDocumentGWT>> callback);

	void loginAndGetAllOriginalDocuments(String username, AsyncCallback<LoginResponse> callback);

	void synchronizeWorkingDocument(
			int sessionID,
			int documentID,
			WorkingDocumentSynchronizeRequest request,
			AsyncCallback<WorkingDocumentSynchronizeResponse> callback);
}
