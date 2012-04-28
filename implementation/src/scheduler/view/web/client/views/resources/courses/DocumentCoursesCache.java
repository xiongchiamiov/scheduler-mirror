package scheduler.view.web.client.views.resources.courses;

import java.util.Collection;
import java.util.List;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.CourseGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;

// Called "Document"CoursesCache because it only deals with the courses for a given document.
public class DocumentCoursesCache extends ResourceCache<CourseGWT> {
	GreetingServiceAsync service;
	int documentID;
	
	public DocumentCoursesCache(GreetingServiceAsync service, int documentID) {
		super();
		this.service = service;
		this.documentID = documentID;
	}
	
	@Override
	protected void getInitialResourcesFromServer(AsyncCallback<List<CourseGWT>> callback) {
		service.getCoursesForDocument(documentID, callback);
	}

	@Override
	protected void sendActivityToServer(
			List<CourseGWT> addedResources,
			Collection<CourseGWT> editedResources,
			List<Integer> deletedResourcesIDs,
			AsyncCallback<List<Integer>> asyncCallback) {
		service.updateCourses(documentID, addedResources, editedResources, deletedResourcesIDs, asyncCallback);
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
