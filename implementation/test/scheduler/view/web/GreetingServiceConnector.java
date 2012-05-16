package scheduler.view.web;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

import scheduler.view.web.client.GreetingService;
import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.server.GreetingServiceImpl;
import scheduler.view.web.shared.ClientChangesRequest;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SessionClosedFromInactivityExceptionGWT;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class GreetingServiceConnector implements GreetingServiceAsync {
	interface GreetingServiceRequestMonad {
		void executeOnServer(GreetingService service);
	}
	
	interface GreetingServiceResponseMonad {
		void executeOnClient();
	}

	class AsyncCallbackSuccessCaller<T> implements GreetingServiceResponseMonad {
		AsyncCallback<T> callback;
		T result;
		AsyncCallbackSuccessCaller(AsyncCallback<T> callback, T result) {
			this.callback = callback;
			this.result = result;
		}
		@Override
		public void executeOnClient() {
			this.callback.onSuccess(result);
		}
	}

	class AsyncCallbackFailureCaller<T> implements GreetingServiceResponseMonad {
		AsyncCallback<T> callback;
		Throwable result;
		AsyncCallbackFailureCaller(AsyncCallback<T> callback, Throwable result) {
			this.callback = callback;
			this.result = result;
			assert(result != null);
		}
		@Override
		public void executeOnClient() {
			this.callback.onFailure(result);
		}
	}
	
	GreetingServiceImpl service;
	
	Queue<GreetingServiceRequestMonad> requests = new ArrayDeque<GreetingServiceRequestMonad>();
	Queue<GreetingServiceResponseMonad> responses = new ArrayDeque<GreetingServiceResponseMonad>();
	
	GreetingServiceConnector(GreetingServiceImpl service) {
		this.service = service;
	}
	
	void serverReceiveRequesAndSendResponset() {
		requests.poll().executeOnServer(service);
	}
	
	void clientReceiveResponse() {
		responses.poll().executeOnClient();
	}
	
	@Override
	public void synchronizeOriginalDocuments(
			final int sessionID,
			final SynchronizeRequest<OriginalDocumentGWT> request,
			final AsyncCallback<SynchronizeResponse<OriginalDocumentGWT>> callback) {
		requests.add(new GreetingServiceRequestMonad() {
			public void executeOnServer(GreetingService service) {
				try {
					final SynchronizeResponse<OriginalDocumentGWT> result = service.synchronizeOriginalDocuments(sessionID, request);
					responses.add(new AsyncCallbackSuccessCaller<SynchronizeResponse<OriginalDocumentGWT>>(callback, result));
				}
				catch (SessionClosedFromInactivityExceptionGWT e) {
					responses.add(new AsyncCallbackFailureCaller<SynchronizeResponse<OriginalDocumentGWT>>(callback, e));
				}
			}
		});
	}

	@Override
	public void loginAndGetAllOriginalDocuments(String username, AsyncCallback<LoginResponse> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void generateRestOfSchedule(
			int sessionID,
			int scheduleID,
			AsyncCallback<Void> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getAllOriginalDocuments(
			int sessionID,
			AsyncCallback<ServerResourcesResponse<OriginalDocumentGWT>> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void createAndOpenWorkingCopyForOriginalDocument(
			int sessionID,
			int originalDocumentID,
			boolean openExistingWorkingDocument,
			AsyncCallback<CompleteWorkingCopyDocumentGWT> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveWorkingCopyToOriginalDocument(int sessionID, int documentID, AsyncCallback<Void> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteWorkingCopyDocument(int sessionID, int documentID, AsyncCallback<Void> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void associateWorkingCopyWithNewOriginalDocument(
			int sessionID,
			int workingCopyID,
			String scheduleName,
			boolean allowOverwrite,
			AsyncCallback<Void> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void synchronizeDocumentCourses(
			int sessionID,
			int documentID,
			SynchronizeRequest<CourseGWT> request,
			AsyncCallback<SynchronizeResponse<CourseGWT>> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void synchronizeDocumentInstructors(
			int sessionID,
			int documentID,
			SynchronizeRequest<InstructorGWT> request,
			AsyncCallback<SynchronizeResponse<InstructorGWT>> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void synchronizeDocumentLocations(
			int sessionID,
			int documentID,
			SynchronizeRequest<LocationGWT> request,
			AsyncCallback<SynchronizeResponse<LocationGWT>> callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void synchronizeDocumentScheduleItems(
			int sessionID,
			int documentID,
			SynchronizeRequest<ScheduleItemGWT> request,
			AsyncCallback<SynchronizeResponse<ScheduleItemGWT>> callback) {
		throw new UnsupportedOperationException();
	}
}
