package scheduler.view.web.shared;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ClientChangesResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public List<Integer> addedResourcesIDs;
	
	public ClientChangesResponse() {
		this.addedResourcesIDs = new LinkedList<Integer>();
	}
	
	public ClientChangesResponse(List<Integer> addedResourcesIDs) {
		this.addedResourcesIDs = addedResourcesIDs;
	}
}
