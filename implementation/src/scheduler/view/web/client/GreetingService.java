package scheduler.view.web.client;



import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.ExistingWorkingDocumentDoesntExistExceptionGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SessionClosedFromInactivityExceptionGWT;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

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

	SynchronizeResponse<CourseGWT> synchronizeDocumentCourses(
			int sessionID,
			int documentID,
			SynchronizeRequest<CourseGWT> request) throws SessionClosedFromInactivityExceptionGWT;

	SynchronizeResponse<InstructorGWT> synchronizeDocumentInstructors(
			int sessionID,
			int documentID,
			SynchronizeRequest<InstructorGWT> request) throws SessionClosedFromInactivityExceptionGWT;

	SynchronizeResponse<LocationGWT> synchronizeDocumentLocations(
			int sessionID,
			int documentID,
			SynchronizeRequest<LocationGWT> request) throws SessionClosedFromInactivityExceptionGWT;

//	SynchronizeResponse<ScheduleItemGWT> synchronizeDocumentScheduleItems(int documentID, SynchronizeRequest<ScheduleItemGWT> request);
	
	

	void generateRestOfSchedule(int sessionID, int scheduleID) throws CouldNotBeScheduledExceptionGWT, SessionClosedFromInactivityExceptionGWT;

	SynchronizeResponse<ScheduleItemGWT> synchronizeDocumentScheduleItems(
			int sessionID,
			int documentID,
			SynchronizeRequest<ScheduleItemGWT> request) throws SessionClosedFromInactivityExceptionGWT;

	LoginResponse loginAndGetAllOriginalDocuments(String username) throws InvalidLoginException;
	
	ServerResourcesResponse<OriginalDocumentGWT> getAllOriginalDocuments(int sessionID)
			throws SessionClosedFromInactivityExceptionGWT;
	
	SynchronizeResponse<OriginalDocumentGWT> synchronizeOriginalDocuments(
			int sessionID,
			SynchronizeRequest<OriginalDocumentGWT> request) throws SessionClosedFromInactivityExceptionGWT;
}
