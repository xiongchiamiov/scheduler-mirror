package scheduler.view.web.client;

import java.util.HashMap;

import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.ServerResourcesResponse;

// Called "Document"InstructorsCache because it only deals with the Instructors for a given document.
public class DocumentInstructorsCache extends ResourceCache<InstructorGWT> {
	GreetingServiceAsync service;
	int sessionID;
	int workingDocumentRealID;
	
	final DocumentCoursesCache coursesCache;
	
	public DocumentInstructorsCache(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, int workingDocumentRealID, DocumentCoursesCache coursesCache, ServerResourcesResponse<InstructorGWT> initialInstructors) {
		super("doc" + workingDocumentRealID + "instructors", deferredSynchronizationEnabled, initialInstructors);
		this.service = service;
		this.sessionID = sessionID;
		this.workingDocumentRealID = workingDocumentRealID;
		this.coursesCache = coursesCache;
	}

	@Override
	protected InstructorGWT cloneResource(InstructorGWT source) {
		return new InstructorGWT(source);
	}
	
	@Override
	protected InstructorGWT localToReal(InstructorGWT localResource) {
		InstructorGWT realInstructor = new InstructorGWT(localResource);
		realInstructor.setID(localIDToRealID(realInstructor.getID()));

		HashMap<Integer, Integer> realCoursePreferences = new HashMap<Integer, Integer>();
		for (java.util.Map.Entry<Integer, Integer> courseLocalIDAndPreference : localResource.getCoursePreferences().entrySet()) {
			int courseLocalID = courseLocalIDAndPreference.getKey();
			int courseRealID = coursesCache.localIDToRealID(courseLocalID);
			realCoursePreferences.put(courseRealID, courseLocalIDAndPreference.getValue());
		}
		realInstructor.setCoursePreferences(realCoursePreferences);
		
		return realInstructor;
	}

	@Override
	protected InstructorGWT realToLocal(InstructorGWT realResource) {
		InstructorGWT localInstructor = new InstructorGWT(realResource);
		localInstructor.setID(realIDToLocalID(localInstructor.getID()));
		
		HashMap<Integer, Integer> localCoursePreferences = new HashMap<Integer, Integer>();
		for (java.util.Map.Entry<Integer, Integer> courseRealIDAndPreference : realResource.getCoursePreferences().entrySet()) {
			int courseRealID = courseRealIDAndPreference.getKey();
			System.out.println("instructor " + realResource.getID() + " " + realResource.getUsername() + " has for course real id " + courseRealID + " pref " + courseRealIDAndPreference.getValue());
			int courseLocalID = coursesCache.realIDToLocalID(courseRealID);
			localCoursePreferences.put(courseLocalID, courseRealIDAndPreference.getValue());
		}
		localInstructor.setCoursePreferences(localCoursePreferences);
		
		return localInstructor;
	}

	@Override
	protected boolean resourceChanged(InstructorGWT oldResource, InstructorGWT newResource) {
		return !oldResource.attributesEqual(newResource);
	}
}
