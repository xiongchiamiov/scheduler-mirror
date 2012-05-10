package scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ServerResourcesResponse<ResourceGWT extends Identified> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public Collection<ResourceGWT> resourcesOnServer;
	
	public ServerResourcesResponse() {
		this.resourcesOnServer = new LinkedList<ResourceGWT>();
	}
	
	public ServerResourcesResponse(Collection<ResourceGWT> resourcesOnServer) {
		this.resourcesOnServer = resourcesOnServer;
	}
}
