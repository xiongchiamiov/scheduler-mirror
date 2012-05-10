package scheduler.view.web.shared;

import java.io.Serializable;

public class SynchronizeResponse<ResourceGWT extends Identified> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ClientChangesResponse changesResponse;
	public ServerResourcesResponse<ResourceGWT> resourcesOnServer;
	
	public SynchronizeResponse() {
		this.changesResponse = new ClientChangesResponse();
		this.resourcesOnServer = new ServerResourcesResponse<ResourceGWT>();
	}
	
	public SynchronizeResponse(ClientChangesResponse changesResponse, ServerResourcesResponse<ResourceGWT> resourcesOnServer) {
		this.changesResponse = changesResponse;
		this.resourcesOnServer = resourcesOnServer;
	}
}
