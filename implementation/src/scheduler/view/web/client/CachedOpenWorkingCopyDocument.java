package scheduler.view.web.client;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.client.views.resources.ValidatorUtil;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.WorkingDocumentGWT;
import scheduler.view.web.shared.WorkingDocumentSynchronizeRequest;
import scheduler.view.web.shared.WorkingDocumentSynchronizeResponse;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CachedOpenWorkingCopyDocument {
	public interface Observer {
		void onAnyLocalChange();
	}

	GreetingServiceAsync service;
	final int sessionID;
	private Collection<Observer> observers = new LinkedList<Observer>();
	
	private final WorkingDocumentGWT realWorkingDocument;
	private final WorkingDocumentGWT localWorkingDocument;
	private final DocumentInstructorsCache instructors;
	private final DocumentCoursesCache courses;
	private final DocumentLocationsCache locations;
	private final DocumentScheduleItemsCache scheduleItems;

	boolean synchronizing = false;
	Collection<AsyncCallback<Void>> callbacksToCallAfterNextSynchronize = new LinkedList<AsyncCallback<Void>>();
	
	CachedOpenWorkingCopyDocument(boolean deferredSynchronizationEnabled,
			GreetingServiceAsync service, int sessionID,
			CompleteWorkingCopyDocumentGWT completeDocument) {

		this.service = service;
		this.sessionID = sessionID;

		this.realWorkingDocument = completeDocument.realWorkingDocument;

		this.localWorkingDocument = new WorkingDocumentGWT(
				this.realWorkingDocument);
		this.localWorkingDocument.setStaffInstructorID(null);
		this.localWorkingDocument.setTBALocationID(null);
		this.localWorkingDocument.setChooseForMeInstructorID(null);
		this.localWorkingDocument.setChooseForMeLocationID(null);

		this.courses = new DocumentCoursesCache(deferredSynchronizationEnabled,
				service, sessionID, realWorkingDocument.getRealID(),
				completeDocument.courses);
		
		for (CourseGWT course : courses.getAll())
			System.out.println("have local course " + course.getID() + " real is " + courses.localIDToRealID(course.getID()));

		this.instructors = new DocumentInstructorsCache(
				deferredSynchronizationEnabled, service, sessionID,
				realWorkingDocument.getRealID(), this.courses,
				completeDocument.instructors);
		this.localWorkingDocument.setStaffInstructorID(this.instructors
				.realIDToLocalID(this.realWorkingDocument
						.getStaffInstructorID()));
		this.localWorkingDocument.setChooseForMeInstructorID(this.instructors
				.realIDToLocalID(this.realWorkingDocument
						.getChooseForMeInstructorID()));

		this.locations = new DocumentLocationsCache(
				deferredSynchronizationEnabled, service, sessionID,
				realWorkingDocument.getRealID(), completeDocument.locations);
		this.localWorkingDocument.setTBALocationID(this.locations
				.realIDToLocalID(this.realWorkingDocument.getTBALocationID()));
		this.localWorkingDocument.setChooseForMeLocationID(this.locations
				.realIDToLocalID(this.realWorkingDocument
						.getChooseForMeLocationID()));

		this.scheduleItems = new DocumentScheduleItemsCache(
				deferredSynchronizationEnabled, service, sessionID,
				realWorkingDocument.getRealID(), realWorkingDocument,
				localWorkingDocument, this.courses, this.instructors,
				this.locations, completeDocument.scheduleItems);
		

		(new Timer() {
			public void run() {
				forceSynchronize(null);
			}
		}).scheduleRepeating(5000);
	}

	public WorkingDocumentGWT getDocument() {
		return new WorkingDocumentGWT(localWorkingDocument);
		// Cloning for defense. Makes you wish java had const.
	}

	public void copyToAndAssociateWithDifferentOriginalDocument(
			DocumentGWT existingOriginalDocumentByThatName,
			AsyncCallback<Void> callback) {
		service.associateWorkingCopyWithNewOriginalDocument(sessionID,
				localWorkingDocument.getRealID(),
				existingOriginalDocumentByThatName.getName(), true, callback);
	}

	public void copyIntoAssociatedOriginalDocument(AsyncCallback<Void> callback) {
		assert (service != null);
		assert (localWorkingDocument != null);
		service.saveWorkingCopyToOriginalDocument(sessionID,
				localWorkingDocument.getRealID(), callback);
	}

	public void forceSynchronize(AsyncCallback<Void> nextSyncCallback) {
		System.out.println("Got a force sync call!");
		
		assert(courses.isSynchronizing() == synchronizing);
		
		if (nextSyncCallback == null) {
			nextSyncCallback = new AsyncCallback<Void>() {
				public void onSuccess(Void result) { }
				public void onFailure(Throwable caught) {
					Window.alert("Failed to synchronize working document! " + caught.getMessage());
				}
			};
		}

		callbacksToCallAfterNextSynchronize.add(nextSyncCallback);
		
		if (!synchronizing) {
			System.out.println("Not syncing already, doing the sync!");
			synchronizing = true;

			final Collection<AsyncCallback<Void>> callbacksToCallAfterThisSynchronize = callbacksToCallAfterNextSynchronize;
			callbacksToCallAfterNextSynchronize = new LinkedList<AsyncCallback<Void>>();
			

			assert(!courses.isSynchronizing());
			
			SynchronizeRequest<CourseGWT> coursesRequest = courses.startSynchronize();
			SynchronizeRequest<InstructorGWT> instructorsRequest = instructors.startSynchronize();
			SynchronizeRequest<LocationGWT> locationsRequest = locations.startSynchronize();
			SynchronizeRequest<ScheduleItemGWT> scheduleItemsRequest = scheduleItems.startSynchronize();
			
			WorkingDocumentSynchronizeRequest documentRequest = new WorkingDocumentSynchronizeRequest(
					coursesRequest, instructorsRequest, locationsRequest, scheduleItemsRequest);
			
			service.synchronizeWorkingDocument(sessionID, realWorkingDocument.getRealID(), documentRequest, new AsyncCallback<WorkingDocumentSynchronizeResponse>() {
				public void onSuccess(WorkingDocumentSynchronizeResponse response) {
					System.out.println("got response from server!");
					
					assert(synchronizing);
					
					courses.finishSynchronize(response.courses);
					instructors.finishSynchronize(response.instructors);
					locations.finishSynchronize(response.locations);
					scheduleItems.finishSynchronize(response.scheduleItems);

					synchronizing = false;

					sanityCheck();
					
					for (AsyncCallback<Void> callback : callbacksToCallAfterThisSynchronize)
						callback.onSuccess(null);
					
					System.out.println("done with response, marked not syncing");
					
					if (!callbacksToCallAfterNextSynchronize.isEmpty()) {
						System.out.println("since another sync was requested in the meantime, calling self");
						forceSynchronize(null);
					}
				}
				
				public void onFailure(Throwable caught) {
					System.out.println("failure!");
					assert(synchronizing);
					synchronizing = false;
					for (AsyncCallback<Void> callback : callbacksToCallAfterThisSynchronize)
						callback.onFailure(caught);
				}
			});
		}
	}
	
	public void sanityCheck() {
		Set<Integer> courseIDs = new TreeSet<Integer>();
		for (CourseGWT course : courses.getAll()) {
			if (courses.localIDToRealID(course.getID()) != null)
				courseIDs.add(course.getID());
		}
		
		for (InstructorGWT instructor : instructors.getAll()) {
			Set<Integer> courseIDsInPrefs = instructor.getCoursePreferences().keySet();
			assert(courseIDs.containsAll(courseIDsInPrefs));
			assert(courseIDsInPrefs.containsAll(courseIDs));
		}
	}

	public Collection<LocationGWT> getLocations(
			boolean excludeSpecialCaseLocations) {
		if (excludeSpecialCaseLocations) {
			Collection<LocationGWT> locationsExcludingSpecialCases = new LinkedList<LocationGWT>();
			for (LocationGWT location : locations.getAll()) {
				if (location.getID().equals(
						localWorkingDocument.getTBALocationID()))
					continue;
				if (location.getID().equals(
						localWorkingDocument.getChooseForMeLocationID()))
					continue;
				locationsExcludingSpecialCases.add(location);
			}
			return locationsExcludingSpecialCases;
		} else {
			return locations.getAll();
		}
	}

	public Collection<InstructorGWT> getInstructors(
			boolean excludeSpecialCaseInstructors) {
		Collection<InstructorGWT> result;
		
		if (excludeSpecialCaseInstructors) {
			Collection<InstructorGWT> instructorsExcludingSpecialCases = new LinkedList<InstructorGWT>();
			for (InstructorGWT location : instructors.getAll()) {
				if (location.getID().equals(
						localWorkingDocument.getStaffInstructorID()))
					continue;
				if (location.getID().equals(
						localWorkingDocument.getChooseForMeInstructorID()))
					continue;
				instructorsExcludingSpecialCases.add(location);
			}
			result = instructorsExcludingSpecialCases;
		} else {
			result = instructors.getAll();
		}
		
		for (InstructorGWT instructor : result) {
			for (CourseGWT course : getCourses())
				assert(instructor.getCoursePreferences().containsKey(course.getID()));
			assert(instructor.getCoursePreferences().size() == getCourses().size());
		}
		
		return result;
	}

	public Collection<CourseGWT> getCourses() {
		return courses.getAll();
	}
	
	public Collection<ScheduleItemGWT> getScheduleItems() {
		return scheduleItems.getAll();
	}

	public InstructorGWT getInstructorByID(int instructorID) {
		return instructors.getByID(instructorID);
	}

	public CourseGWT getCourseByID(int id) {
		return courses.getByID(id);
	}

	public LocationGWT getLocationByID(int id) {
		return locations.getByID(id);
	}

	public void addCourseObserver(ResourceCache.Observer<CourseGWT> obs) {
		courses.addObserver(obs);
	}


	
	
	public void addCourse(CourseGWT newCourse) {
		courses.add(newCourse);
		
		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void editCourse(CourseGWT course) {
		courses.edit(course);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void deleteCourse(Integer id) {
		for(InstructorGWT instructor : instructors.getAll())
		{
			instructor.getCoursePreferences().remove(id);
		}
		courses.delete(id);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void editInstructor(InstructorGWT instructor) {
		instructors.edit(instructor);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void editScheduleItem(ScheduleItemGWT item) {
		scheduleItems.edit(item);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void addScheduleItem(ScheduleItemGWT item) {
		scheduleItems.add(item);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void deleteScheduleItem(Integer id) {
		scheduleItems.delete(id);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void deleteInstructor(Integer id) {
		instructors.delete(id);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void addInstructor(InstructorGWT newInstructor) {
		instructors.add(newInstructor);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void addLocation(LocationGWT newLocation) {
		locations.add(newLocation);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void editLocation(LocationGWT location) {
		locations.edit(location);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void deleteLocation(Integer id) {
		locations.delete(id);

		for (Observer obs : observers)
			obs.onAnyLocalChange();
	}

	public void addObserver(final Observer observer) {
		observers.add(observer);
	}

	public void copyToAndAssociateWithNewOriginalDocument(
			String newDocumentName, AsyncCallback<Void> callback) {
		service.associateWorkingCopyWithNewOriginalDocument(sessionID,
				localWorkingDocument.getRealID(), newDocumentName, false,
				callback);
	}

	public boolean documentIsValid() {
		if (ValidatorUtil.isValidCourseCollection(courses.getAll())) {
			if (ValidatorUtil.isValidInstructorCollection(instructors.getAll())) {
				if (ValidatorUtil.isValidLocationCollection(locations.getAll())) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean courseWithLocalIDExistsOnServer(Integer localID) {
		return locations.localIDToRealID(localID) != null;
	}

	public void generateRestOfSchedule(final AsyncCallback<Void> callback) {
		if (documentIsValid()) {
			service.generateRestOfSchedule(sessionID,
					realWorkingDocument.getRealID(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void v) {
							callback.onSuccess(null);
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to generate schedule!"
									+ caught);
							callback.onFailure(caught);
						}
					});

			forceSynchronize(callback);

			for (Observer obs : observers)
				obs.onAnyLocalChange();
		}
		else
		{
			callback.onFailure(new InvalidResourcesException("The resources are invalid, look for red cells"));
		}
	}

}
