package scheduler.view.web.client;

import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class OriginalDocumentsCache extends ResourceCache<OriginalDocumentGWT> {
	GreetingServiceAsync service;
	int sessionID;
	
	public OriginalDocumentsCache(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, ServerResourcesResponse<OriginalDocumentGWT> initialDocuments) {
		super("documents", deferredSynchronizationEnabled);
		
		this.sessionID = sessionID;
		this.service = service;

		readServerResources(initialDocuments);
	}
	
	@Override
	protected OriginalDocumentGWT cloneResource(OriginalDocumentGWT source) {
		return new OriginalDocumentGWT(source);
	}
	
	@Override
	protected OriginalDocumentGWT localToReal(OriginalDocumentGWT localResource) {
		OriginalDocumentGWT realDocument = new OriginalDocumentGWT(localResource);
		realDocument.setID(localIDToRealID(realDocument.getID()));
		return realDocument;
	}

	@Override
	protected OriginalDocumentGWT realToLocal(OriginalDocumentGWT realResource, Integer useThisID) {
		OriginalDocumentGWT localDocument = new OriginalDocumentGWT(realResource);
		localDocument.setID(useThisID == null ? realIDToLocalID(localDocument.getID()) : useThisID);
		return localDocument;
	}

	@Override
	protected boolean resourceChanged(OriginalDocumentGWT oldResource, OriginalDocumentGWT newResource) {
		return !oldResource.attributesEqual(newResource);
	}
}
