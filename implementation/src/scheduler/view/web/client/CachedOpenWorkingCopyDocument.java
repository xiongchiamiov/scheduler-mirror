package scheduler.view.web.client;

import java.util.Collection;
import java.util.LinkedList;

import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.WorkingDocumentGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CachedOpenWorkingCopyDocument {
	public interface Observer {
		void onAnyLocalChange();
	}

	interface GetDocumentStrategy {
		DocumentGWT getDocument();
	}
	
	GreetingServiceAsync service;
	final int sessionID;
	private final WorkingDocumentGWT realWorkingDocument;
	private final WorkingDocumentGWT localWorkingDocument;
	private final DocumentInstructorsCache instructors;
	private final DocumentCoursesCache courses;
	private final DocumentLocationsCache locations;
	private final DocumentScheduleItemsCache scheduleItems;
	
	CachedOpenWorkingCopyDocument(
			boolean deferredSynchronizationEnabled,
			GreetingServiceAsync service,
			int sessionID,
			CompleteWorkingCopyDocumentGWT completeDocument) {
		
		this.service = service;
		this.sessionID = sessionID;
		
		this.realWorkingDocument = completeDocument.realWorkingDocument;
		
		this.localWorkingDocument = new WorkingDocumentGWT(this.realWorkingDocument);
		this.localWorkingDocument.setStaffInstructorID(null);
		this.localWorkingDocument.setTBALocationID(null);
		this.localWorkingDocument.setChooseForMeInstructorID(null);
		this.localWorkingDocument.setChooseForMeLocationID(null);
		
		this.courses = new DocumentCoursesCache(
				deferredSynchronizationEnabled,
				service, sessionID, realWorkingDocument.getRealID(),
				completeDocument.courses);
		
		this.instructors = new DocumentInstructorsCache(
				deferredSynchronizationEnabled,
				service, sessionID, realWorkingDocument.getRealID(),
				this.courses,
				completeDocument.instructors);
		this.localWorkingDocument.setStaffInstructorID(this.instructors.realIDToLocalID(this.realWorkingDocument.getStaffInstructorID()));
		this.localWorkingDocument.setChooseForMeInstructorID(this.instructors.realIDToLocalID(this.realWorkingDocument.getChooseForMeInstructorID()));
		
		this.locations = new DocumentLocationsCache(
				deferredSynchronizationEnabled,
				service, sessionID, realWorkingDocument.getRealID(),
				completeDocument.locations);
		this.localWorkingDocument.setTBALocationID(this.locations.realIDToLocalID(this.realWorkingDocument.getTBALocationID()));
		this.localWorkingDocument.setChooseForMeLocationID(this.locations.realIDToLocalID(this.realWorkingDocument.getChooseForMeLocationID()));
		
		this.scheduleItems = new DocumentScheduleItemsCache(
				deferredSynchronizationEnabled,
				service, sessionID, realWorkingDocument.getRealID(),
				realWorkingDocument, localWorkingDocument,
				this.courses, this.instructors, this.locations,
				completeDocument.scheduleItems);
	}
	
	public WorkingDocumentGWT getDocument() {
		return new WorkingDocumentGWT(localWorkingDocument); // Cloning for defense. Makes you wish java had const.
	}

	public void associateAndCopyToDifferentOriginalDocument(
			DocumentGWT existingOriginalDocumentByThatName,
			AsyncCallback<Void> callback) {
		assert(false); // implement
	}

	public void copyIntoAssociatedOriginalDocument(AsyncCallback<Void> callback) {
		assert(service != null);
		assert(localWorkingDocument != null);
		service.saveWorkingCopyToOriginalDocument(sessionID, localWorkingDocument.getRealID(), callback);
	}
	
	public void forceSynchronize(final AsyncCallback<Void> originalCallback) {
		AsyncCallback<Void> callbackToGiveToCaches = null;
		
		if (originalCallback != null) {
			AsyncCallback<Void> combineCallback = new AsyncCallback<Void>() {
				int requestsComplete = 0;
				boolean notifiedOfFailure = false;
				
				@Override
				public void onSuccess(Void result) {
					requestsComplete++;
					
					if (requestsComplete == 4)
						originalCallback.onSuccess(null);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					if (!notifiedOfFailure) {
						originalCallback.onFailure(caught);
						notifiedOfFailure = true;
					}
				}
			};
			
			callbackToGiveToCaches = combineCallback;
		}
		
		final AsyncCallback<Void> finalCallbackToGiveToCaches = callbackToGiveToCaches;

		courses.forceSynchronize(finalCallbackToGiveToCaches);
		instructors.forceSynchronize(finalCallbackToGiveToCaches);
		locations.forceSynchronize(finalCallbackToGiveToCaches);
		scheduleItems.forceSynchronize(finalCallbackToGiveToCaches);
	}

	public void generateRestOfSchedule(AsyncCallback<Void> callback) {
		service.generateRestOfSchedule(sessionID, realWorkingDocument.getRealID(), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void v) { }
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to generate schedule!" + caught);
			}
		});
		
		scheduleItems.forceSynchronize(callback);
	}

	public Collection<CourseGWT> getCourses() {
		return courses.getAll();
	}

	public void addCourse(CourseGWT newCourse) {
		courses.add(newCourse);
	}

	public void editCourse(CourseGWT course) {
		courses.edit(course);
	}

	public void deleteCourse(Integer id) {
		courses.delete(id);
	}

	public void editInstructor(InstructorGWT instructor) {
		instructors.edit(instructor);
	}

	public Collection<LocationGWT> getLocations(boolean excludeSpecialCaseLocations) {
		if (excludeSpecialCaseLocations) {
			Collection<LocationGWT> locationsExcludingSpecialCases = new LinkedList<LocationGWT>();
			for (LocationGWT location : locations.getAll()) {
				if (location.getID().equals(localWorkingDocument.getTBALocationID()))
					continue;
				if (location.getID().equals(localWorkingDocument.getChooseForMeLocationID()))
					continue;
				locationsExcludingSpecialCases.add(location);
			}
			return locationsExcludingSpecialCases;
		}
		else {
			return locations.getAll();
		}
	}

	public Collection<InstructorGWT> getInstructors(boolean excludeSpecialCaseInstructors) {
		if (excludeSpecialCaseInstructors) {
			Collection<InstructorGWT> instructorsExcludingSpecialCases = new LinkedList<InstructorGWT>();
			for (InstructorGWT location : instructors.getAll()) {
				if (location.getID().equals(localWorkingDocument.getStaffInstructorID()))
					continue;
				if (location.getID().equals(localWorkingDocument.getChooseForMeInstructorID()))
					continue;
				instructorsExcludingSpecialCases.add(location);
			}
			return instructorsExcludingSpecialCases;
		}
		else {
			return instructors.getAll();
		}
	}

	public void editScheduleItem(ScheduleItemGWT item) {
		scheduleItems.edit(item);
	}

	public void addScheduleItem(ScheduleItemGWT item) {
		scheduleItems.add(item);
	}

	public Collection<ScheduleItemGWT> getScheduleItems() {
		return scheduleItems.getAll();
	}

	public void deleteScheduleItem(Integer id) {
		scheduleItems.delete(id);
	}

	public InstructorGWT getInstructorByID(int instructorID) {
		return instructors.getByID(instructorID);
	}

	public CourseGWT getCourseByID(int id) {
		return courses.getByID(id);
	}
	
	public void deleteInstructor(Integer id) {
		instructors.delete(id);
	}

	public void addInstructor(InstructorGWT newInstructor) {
		instructors.add(newInstructor);
	}

	public void addLocation(LocationGWT newLocation) {
		locations.add(newLocation);
	}

	public void editLocation(LocationGWT location) {
		locations.edit(location);
	}

	public void deleteLocation(Integer id) {
		locations.delete(id);
	}
	
	public void addCourseObserver(ResourceCache.Observer<CourseGWT> obs) {
		courses.addObserver(obs);
	}

	public void addObserver(final Observer observer) {
		courses.addObserver(new ResourceCache.Observer<CourseGWT>() {
			@Override
			public void afterSynchronize() { }
			@Override
			public void onAnyLocalChange() { observer.onAnyLocalChange(); }
			@Override
			public void onResourceAdded(CourseGWT resource, boolean addedLocally) { }
			@Override
			public void onResourceEdited(CourseGWT resource, boolean editedLocally) { }
			@Override
			public void onResourceDeleted(int localID, boolean deletedLocally) { }
		});

		locations.addObserver(new ResourceCache.Observer<LocationGWT>() {
			@Override
			public void afterSynchronize() { }
			@Override
			public void onAnyLocalChange() { observer.onAnyLocalChange(); }
			@Override
			public void onResourceAdded(LocationGWT resource, boolean addedLocally) { }
			@Override
			public void onResourceEdited(LocationGWT resource, boolean editedLocally) { }
			@Override
			public void onResourceDeleted(int localID, boolean deletedLocally) { }
		});

		instructors.addObserver(new ResourceCache.Observer<InstructorGWT>() {
			@Override
			public void afterSynchronize() { }
			@Override
			public void onAnyLocalChange() { observer.onAnyLocalChange(); }
			@Override
			public void onResourceAdded(InstructorGWT resource, boolean addedLocally) { }
			@Override
			public void onResourceEdited(InstructorGWT resource, boolean editedLocally) { }
			@Override
			public void onResourceDeleted(int localID, boolean deletedLocally) { }
		});
	}

	public LocationGWT getLocationByID(int id) {
		return locations.getByID(id);
	}
}
