package scheduler.view.web.client.views.home;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ServerResourcesResponse;

public class NewOriginalDocumentsCache extends ResourceCache<OriginalDocumentGWT> {
	GreetingServiceAsync service;
	int sessionID;
	
	public NewOriginalDocumentsCache(boolean deferredSynchronizationEnabled, GreetingServiceAsync service, int sessionID, ServerResourcesResponse<OriginalDocumentGWT> initialDocuments) {
		super("documents", deferredSynchronizationEnabled, initialDocuments);
		
		this.sessionID = sessionID;
		this.service = service;
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
	protected OriginalDocumentGWT realToLocal(OriginalDocumentGWT realResource) {
		OriginalDocumentGWT localDocument = new OriginalDocumentGWT(realResource);
		localDocument.setID(realIDToLocalID(localDocument.getID()));
		return localDocument;
	}

	@Override
	protected boolean resourceChanged(OriginalDocumentGWT oldResource, OriginalDocumentGWT newResource) {
		return !oldResource.attributesEqual(newResource);
	}
}
