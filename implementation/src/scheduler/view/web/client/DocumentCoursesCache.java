package scheduler.view.web.client;

import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

// Called "Document"CoursesCache because it only deals with the courses for a given document.
public class DocumentCoursesCache extends ResourceCache<CourseGWT> {
	GreetingServiceAsync service;
	int sessionID;
	int workingDocumentRealID;
	
	public DocumentCoursesCache(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, int workingDocumentRealID, ServerResourcesResponse<CourseGWT> initialCourses) {
		super("doc" + workingDocumentRealID + "courses", deferredSynchronizationEnabled, initialCourses);
		this.service = service;
		this.sessionID = sessionID;
		this.workingDocumentRealID = workingDocumentRealID;
	}
	@Override
	protected boolean resourceChanged(CourseGWT oldResource, CourseGWT newResource) {
		return !oldResource.attributesEqual(newResource);
	}
	
	@Override
	protected void synchronizeWithServer(
			SynchronizeRequest<CourseGWT> request,
			AsyncCallback<SynchronizeResponse<CourseGWT>> callback) {
		service.synchronizeDocumentCourses(sessionID, workingDocumentRealID, request, callback);
	}
	
	@Override
	protected CourseGWT cloneResource(CourseGWT source) {
		return new CourseGWT(source);
	}
	
	@Override
	protected CourseGWT localToReal(CourseGWT localResource) {
		CourseGWT realCourse = new CourseGWT(localResource);
		realCourse.setID(localIDToRealID(realCourse.getID()));
		if (realCourse.getLectureID() != -1)
			realCourse.setLectureID(localIDToRealID(realCourse.getLectureID()));
		return realCourse;
	}

	@Override
	protected CourseGWT realToLocal(CourseGWT realResource) {
		CourseGWT localCourse = new CourseGWT(realResource);
		localCourse.setID(realIDToLocalID(localCourse.getID()));
		if (localCourse.getLectureID() != -1)
			localCourse.setLectureID(realIDToLocalID(localCourse.getLectureID()));
		return localCourse;
	}
}
