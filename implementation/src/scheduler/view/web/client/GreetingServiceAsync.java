package scheduler.view.web.client;

import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

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

	void synchronizeDocumentCourses(
			int sessionID,
			int documentID,
			SynchronizeRequest<CourseGWT> request,
			AsyncCallback<SynchronizeResponse<CourseGWT>> callback);

	void synchronizeDocumentInstructors(
			int sessionID,
			int documentID,
			SynchronizeRequest<InstructorGWT> request,
			AsyncCallback<SynchronizeResponse<InstructorGWT>> callback);

	void synchronizeDocumentLocations(
			int sessionID,
			int documentID,
			SynchronizeRequest<LocationGWT> request,
			AsyncCallback<SynchronizeResponse<LocationGWT>> callback);

	void generateRestOfSchedule(
			int sessionID,
			int scheduleID,
			AsyncCallback<Void> asyncCallback);

	void getAllOriginalDocuments(int sessionID, AsyncCallback<ServerResourcesResponse<OriginalDocumentGWT>> callback);

	void synchronizeDocumentScheduleItems(
			int sessionID,
			int documentID,
			SynchronizeRequest<ScheduleItemGWT> request,
			AsyncCallback<SynchronizeResponse<ScheduleItemGWT>> callback);

	void loginAndGetAllOriginalDocuments(String username, AsyncCallback<LoginResponse> callback);
}
