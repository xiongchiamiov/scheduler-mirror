package scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ClientChangesRequest<ResourceGWT extends Identified> implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<ResourceGWT> addedResources = new LinkedList<ResourceGWT>();
	public Collection<ResourceGWT> editedResources = new LinkedList<ResourceGWT>();
	public Set<Integer> deletedResourceIDs = new TreeSet<Integer>();
	
	public ClientChangesRequest() { }
	
	public ClientChangesRequest(
			List<ResourceGWT> addedResources,
			Collection<ResourceGWT> editedResources,
			Set<Integer> deletedResourceIDs) {
		this.addedResources = addedResources;
		this.editedResources = editedResources;
		this.deletedResourceIDs = deletedResourceIDs;
	}
}
