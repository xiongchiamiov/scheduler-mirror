package scheduler.view.web.client;

import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.ServerResourcesResponse;

// Called "Document"LocationsCache because it only deals with the Locations for a given document.
public class DocumentLocationsCache extends ResourceCache<LocationGWT> {
	GreetingServiceAsync service;
	int sessionID;
	int workingDocumentRealID;
	
	public DocumentLocationsCache(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, int workingDocumentRealID, ServerResourcesResponse<LocationGWT> initialLocations) {
		super("doc" + workingDocumentRealID + "locations", deferredSynchronizationEnabled, initialLocations);
		
		this.service = service;
		this.sessionID = sessionID;
		this.workingDocumentRealID = workingDocumentRealID;

	}
	
	@Override
	protected LocationGWT cloneResource(LocationGWT source) {
		return new LocationGWT(source);
	}
	
	@Override
	protected LocationGWT localToReal(LocationGWT localResource) {
		LocationGWT realLocation = new LocationGWT(localResource);
		realLocation.setID(localIDToRealID(realLocation.getID()));
		return realLocation;
	}

	@Override
	protected LocationGWT realToLocal(LocationGWT realResource) {
		LocationGWT localLocation = new LocationGWT(realResource);
		localLocation.setID(realIDToLocalID(localLocation.getID()));
		return localLocation;
	}

	@Override
	protected boolean resourceChanged(LocationGWT oldResource, LocationGWT newResource) {
		return !oldResource.attributesEqual(newResource);
	}
}
