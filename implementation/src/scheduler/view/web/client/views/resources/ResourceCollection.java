package scheduler.view.web.client.views.resources;

import java.util.Collection;

public interface ResourceCollection<ResourceGWT> {
	Collection<ResourceGWT> getAll();
	public void add(ResourceGWT newResource);
	public void edit(ResourceGWT resource);
	public void delete(int resourceLocalID);
}
