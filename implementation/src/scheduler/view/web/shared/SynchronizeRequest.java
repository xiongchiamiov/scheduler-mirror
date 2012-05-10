package scheduler.view.web.shared;

import java.io.Serializable;

public class SynchronizeRequest<ResourceGWT extends Identified> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ClientChangesRequest<ResourceGWT> clientChanges = new ClientChangesRequest<ResourceGWT>();
	
	public SynchronizeRequest() { }
	
	public SynchronizeRequest(ClientChangesRequest<ResourceGWT> clientChanges) {
		this.clientChanges = clientChanges;
	}
}
