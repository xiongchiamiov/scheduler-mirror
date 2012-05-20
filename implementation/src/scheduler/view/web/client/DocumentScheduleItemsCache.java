package scheduler.view.web.client;

import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.WorkingDocumentGWT;

// Called "Document"ScheduleItemsCache because it only deals with the ScheduleItems for a given document.
public class DocumentScheduleItemsCache extends ResourceCache<ScheduleItemGWT> {
	GreetingServiceAsync service;
	int sessionID;
	int workingDocumentRealID;

	final WorkingDocumentGWT realWorkingDocument;
	final WorkingDocumentGWT localWorkingDocument;

	final DocumentCoursesCache coursesCache;
	final DocumentInstructorsCache instructorsCache;
	final DocumentLocationsCache locationsCache;
	
	public DocumentScheduleItemsCache(
			boolean deferredSynchronizationEnabled,
			GreetingServiceAsync service, int sessionID, int workingDocumentRealID,
			WorkingDocumentGWT realWorkingDocument,
			WorkingDocumentGWT localWorkingDocument,
			DocumentCoursesCache coursesCache,
			DocumentInstructorsCache instructorsCache,
			DocumentLocationsCache locationsCache,
			ServerResourcesResponse<ScheduleItemGWT> initialScheduleItems) {
		
		super("doc" + workingDocumentRealID + "ScheduleItems", deferredSynchronizationEnabled);
		this.service = service;
		this.sessionID = sessionID;
		this.workingDocumentRealID = workingDocumentRealID;
		
		this.realWorkingDocument = realWorkingDocument;
		this.localWorkingDocument = localWorkingDocument;
		
		this.coursesCache = coursesCache;
		this.instructorsCache = instructorsCache;
		this.locationsCache = locationsCache;

		readServerResources(initialScheduleItems);
	}
	
	@Override
	protected ScheduleItemGWT cloneResource(ScheduleItemGWT source) {
		return new ScheduleItemGWT(source);
	}
	
	@Override
	protected ScheduleItemGWT localToReal(ScheduleItemGWT localResource) {
		ScheduleItemGWT realScheduleItem = new ScheduleItemGWT(localResource);
		realScheduleItem.setID(localIDToRealID(realScheduleItem.getID()));
		
		
		realScheduleItem.setCourseID(coursesCache.localIDToRealID(localResource.getCourseID()));
		
		
		if (localResource.getInstructorID() == localWorkingDocument.getStaffInstructorID())
			realScheduleItem.setInstructorID(realWorkingDocument.getStaffInstructorID());
		else
			realScheduleItem.setInstructorID(instructorsCache.localIDToRealID(localResource.getInstructorID()));
		
		
		if (localResource.getLocationID() == localWorkingDocument.getTBALocationID())
			realScheduleItem.setLocationID(realWorkingDocument.getTBALocationID());
		else
			realScheduleItem.setLocationID(locationsCache.localIDToRealID(localResource.getLocationID()));
		
		
		return realScheduleItem;
	}

	@Override
	protected ScheduleItemGWT realToLocal(ScheduleItemGWT realResource, Integer useThisID) {
		ScheduleItemGWT localScheduleItem = new ScheduleItemGWT(realResource);
		localScheduleItem.setID(useThisID == null ? realIDToLocalID(localScheduleItem.getID()) : useThisID);

		
		localScheduleItem.setCourseID(coursesCache.realIDToLocalID(realResource.getCourseID()));
		
		
		if (realResource.getInstructorID() == realWorkingDocument.getStaffInstructorID())
			localScheduleItem.setInstructorID(localWorkingDocument.getStaffInstructorID());
		else
			localScheduleItem.setInstructorID(instructorsCache.realIDToLocalID(realResource.getInstructorID()));
		
		
		if (realResource.getLocationID() == realWorkingDocument.getTBALocationID())
			localScheduleItem.setLocationID(localWorkingDocument.getTBALocationID());
		else
			localScheduleItem.setLocationID(locationsCache.realIDToLocalID(realResource.getLocationID()));
		
		
		return localScheduleItem;
	}

	@Override
	protected boolean resourceChanged(ScheduleItemGWT oldResource, ScheduleItemGWT newResource) {
		return !oldResource.attributesEqual(newResource);
	}
}
