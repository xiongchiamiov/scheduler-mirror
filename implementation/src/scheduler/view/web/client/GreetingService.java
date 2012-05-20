package scheduler.view.web.client;



import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import scheduler.view.web.shared.ExistingWorkingDocumentDoesntExistExceptionGWT;
import scheduler.view.web.shared.GenerateException;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SessionClosedFromInactivityExceptionGWT;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;
import scheduler.view.web.shared.WorkingDocumentSynchronizeRequest;
import scheduler.view.web.shared.WorkingDocumentSynchronizeResponse;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	
	void saveWorkingCopyToOriginalDocument(int sessionID, int documentID) throws SessionClosedFromInactivityExceptionGWT;
	CompleteWorkingCopyDocumentGWT createAndOpenWorkingCopyForOriginalDocument(
			int sessionID,
			int originalDocumentID,
			boolean openExistingWorkingDocument) throws SessionClosedFromInactivityExceptionGWT, ExistingWorkingDocumentDoesntExistExceptionGWT;
	void deleteWorkingCopyDocument(int sessionID, int documentID) throws SessionClosedFromInactivityExceptionGWT;
	// This is what we think of as "save as":
	void associateWorkingCopyWithNewOriginalDocument(int sessionID, int workingCopyID, String scheduleName, boolean allowOverwrite) throws SessionClosedFromInactivityExceptionGWT;

	WorkingDocumentSynchronizeResponse synchronizeWorkingDocument(
			int sessionID,
			int documentID,
			WorkingDocumentSynchronizeRequest request) throws SessionClosedFromInactivityExceptionGWT;

	void generateRestOfSchedule(int sessionID, int scheduleID) throws CouldNotBeScheduledExceptionGWT, SessionClosedFromInactivityExceptionGWT, GenerateException;

	LoginResponse loginAndGetAllOriginalDocuments(String username) throws InvalidLoginException;
	
	ServerResourcesResponse<OriginalDocumentGWT> getAllOriginalDocuments(int sessionID)
			throws SessionClosedFromInactivityExceptionGWT;
	
	SynchronizeResponse<OriginalDocumentGWT> synchronizeOriginalDocuments(
			int sessionID,
			SynchronizeRequest<OriginalDocumentGWT> request) throws SessionClosedFromInactivityExceptionGWT;
}
