package scheduler.view.web.client.views.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.shared.ClientChangesRequest;
import scheduler.view.web.shared.Identified;
import scheduler.view.web.shared.Pair;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

// This class acts as both a proxy for the network calls, and as a cache.
public abstract class ResourceCache<ResourceGWT extends Identified> implements ResourceCollection<ResourceGWT> {
	public interface Observer<ResourceGWT extends Identified> {
		void afterSynchronize();
		void onAnyLocalChange();
		void onResourceAdded(ResourceGWT resource, boolean addedLocally);
		void onResourceEdited(ResourceGWT resource, boolean editedLocally);
		void onResourceDeleted(int localID, boolean deletedLocally);
	}

	enum EntryActivity { NO_CHANGES, ADDED, MODIFIED, DELETED };
	
	class Entry {
		final int localID;
		Integer realID;
		ResourceGWT localResource;
		EntryActivity activity;
		
		public Entry(int localID, Integer realID, ResourceGWT localResource, EntryActivity activity) {
			this.localID = localID;
			this.realID = realID;
			this.localResource = localResource;
			this.activity = activity;
		}
	}

	boolean deferredSynchronizationEnabled;
	int nextSyncNumber = 1;
	final String cachedebugname;
	private boolean anotherSynchronizeRequested = false;
	private Integer currentSyncNumber = null;
	
	int nextLocalID = 1;
	final HashMap<Integer, Entry> entriesByLocalID = new HashMap<Integer, Entry>();
	final HashMap<Integer, Entry> entriesByRealID = new HashMap<Integer, Entry>();
	
	final Collection<Observer<ResourceGWT>> observers = new HashSet<Observer<ResourceGWT>>();
	public void addObserver(Observer<ResourceGWT> observer) { observers.add(observer); }
	public void removeObserver(Observer<ResourceGWT> observer) { observers.remove(observer); }
	
	protected abstract void synchronizeWithServer(SynchronizeRequest<ResourceGWT> request, AsyncCallback<SynchronizeResponse<ResourceGWT>> callback);
	protected abstract ResourceGWT cloneResource(ResourceGWT source);
	protected abstract ResourceGWT localToReal(ResourceGWT localResource);
	protected abstract ResourceGWT realToLocal(ResourceGWT realResource);
	protected abstract boolean resourceChanged(ResourceGWT oldResource, ResourceGWT newResource);
	
	public ResourceCache(final String cachedebugname, boolean deferredSynchronizationEnabled, ServerResourcesResponse<ResourceGWT> initialResources) {
		this.cachedebugname = cachedebugname;
		this.deferredSynchronizationEnabled = deferredSynchronizationEnabled;
		readServerResources(initialResources);
	}
	
	public ResourceGWT getByID(int localID) {
		return entriesByLocalID.get(localID).localResource;
	}
	
	public Collection<ResourceGWT> getAll() {
		Collection<ResourceGWT> result = new LinkedList<ResourceGWT>();
		for (Entry entry : entriesByLocalID.values())
			if (entry.activity != EntryActivity.DELETED)
				result.add(entry.localResource);
		return result;
	}
	
	public void add(ResourceGWT localResource) {
		assert(localResource.getID() == null);
		
		int localID = nextLocalID++;
		localResource.setID(localID);
		
		Entry entry = new Entry(localID, null, cloneResource(localResource), EntryActivity.ADDED);
		entriesByLocalID.put(localID, entry);

		for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
			observer.onResourceAdded(cloneResource(localResource), true);
		
		for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
			observer.onAnyLocalChange();
		
		if (deferredSynchronizationEnabled) {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					synchronizeWithServer();
				}
			});
		}
		else {
			synchronizeWithServer();
		}
	}
	
	public void edit(ResourceGWT localResource) {
		Entry entry = entriesByLocalID.get(localResource.getID());
		assert(entry != null);
		
		entry.localResource = cloneResource(localResource);
		
		switch (entry.activity) {
			case NO_CHANGES:
				entry.activity = EntryActivity.MODIFIED;
				break;
				
			case ADDED:
				// Do nothing, we'll just pretend these new values were the initial values
				break;
				
			case MODIFIED:
				// Do nothing, it's already marked to be sent to the server with updates
				break;
				
			case DELETED:
				// How are we editing it if we already deleted it?
				assert(false);
				break;
		}

		for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
			observer.onResourceEdited(cloneResource(localResource), true);
		
		for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
			observer.onAnyLocalChange();

		if (deferredSynchronizationEnabled) {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					synchronizeWithServer();
				}
			});
		}
		else {
			synchronizeWithServer();
		}
	}
	
	public void delete(int localResourceID) {
		Entry entry = entriesByLocalID.get(localResourceID);
		assert(entry != null);
		
		switch (entry.activity) {
			case NO_CHANGES:
				entry.activity = EntryActivity.DELETED;
				System.out.println("Marking entry deleted!");
				break;
			
			case ADDED:
				// We're deleting something that the server doesn't know we added yet.
				// Let's just nuke the entry and pretend it never existed.
				entriesByLocalID.remove(localResourceID);
				break;
				
			case MODIFIED:
				entry.activity = EntryActivity.DELETED;
				break;
				
			case DELETED:
				// How are we deleting it if we already did?
				assert(false);
				break;
		}

		for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
			observer.onResourceDeleted(localResourceID, true);
		
		for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
			observer.onAnyLocalChange();

		if (deferredSynchronizationEnabled) {
			Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
				public void execute() {
					synchronizeWithServer();
				}
			});
		}
		else {
			synchronizeWithServer();
		}
	}

	public Integer localIDToRealID(Integer id) {
		Entry entry = entriesByLocalID.get(id);
		assert(entry != null);
		return entry.realID;
	}

	public Integer realIDToLocalID(Integer id) {
		Entry entry = entriesByRealID.get(id);
		assert entry != null : "Cache doesn't know about real ID " + id + " so it can't convert to local.";
		return entry.localID;
	}
	
	public void synchronizeWithServer() {
		if (currentSyncNumber != null)
			return;
		
		final List<Entry> addedResourcesEntries = new LinkedList<Entry>();
		
		List<ResourceGWT> addedResources = new LinkedList<ResourceGWT>();
		Collection<ResourceGWT> editedResources = new LinkedList<ResourceGWT>();
		Set<Integer> deletedResourcesRealIDs = new TreeSet<Integer>();
		
		for (Entry entry : entriesByLocalID.values()) {
			switch (entry.activity) {
				case NO_CHANGES:
					break;
					
				case ADDED:
					assert(entry.realID == null);
					ResourceGWT realResource = localToReal(cloneResource(entry.localResource));
					addedResources.add(realResource);
					addedResourcesEntries.add(entry);
					break;
					
				case MODIFIED:
					ResourceGWT resourceWithRealID = localToReal(cloneResource(entry.localResource));
					editedResources.add(resourceWithRealID);
					break;
					
				case DELETED:
					deletedResourcesRealIDs.add(entry.realID);
					break;
			}
		}
		
		if (anotherSynchronizeRequested || !addedResources.isEmpty() || !editedResources.isEmpty() || !deletedResourcesRealIDs.isEmpty()) {
			anotherSynchronizeRequested = false;
			currentSyncNumber = nextSyncNumber++;

			for (Entry entry : new LinkedList<Entry>(entriesByLocalID.values())) {
				switch (entry.activity) {
					case NO_CHANGES:
						break;
						
					case ADDED:
					case MODIFIED:
						entry.activity = EntryActivity.NO_CHANGES;
						break;
						
					case DELETED:
						entriesByLocalID.remove(entry.localID);
						
						assert(entry.realID != null);
						entriesByRealID.remove(entry.realID);
						break;
				}
			}
			
			ClientChangesRequest<ResourceGWT> clientChanges = new ClientChangesRequest<ResourceGWT>(addedResources, editedResources, deletedResourcesRealIDs);
			SynchronizeRequest<ResourceGWT> request = new SynchronizeRequest<ResourceGWT>(clientChanges);
			
			synchronizeWithServer(request, new AsyncCallback<SynchronizeResponse<ResourceGWT>>() {
				@Override
				public void onFailure(Throwable caught) {
					assert(caught != null);
					Window.alert("Failed to send updates to server! " + caught);
				}
				
				@Override
				public void onSuccess(SynchronizeResponse<ResourceGWT> response) {
					assert(response != null);
					assert(response.changesResponse.addedResourcesIDs != null);
					assert(response.resourcesOnServer != null);
					assert(currentSyncNumber != null);
					
					List<Integer> addedResourcesRealIDs = response.changesResponse.addedResourcesIDs;
					assert(addedResourcesRealIDs.size() == addedResourcesEntries.size());
					
					for (Pair<Entry, Integer> addedResourceEntryAndRealID : new DoubleIterator<Entry, Integer>(addedResourcesEntries, addedResourcesRealIDs)) {
						Entry addedResourceEntry = addedResourceEntryAndRealID.getLeft();
						Integer addedResourceRealID = addedResourceEntryAndRealID.getRight();
						
						addedResourceEntry.realID = addedResourceRealID;
						
						assert(!entriesByRealID.containsKey(addedResourceRealID));
						entriesByRealID.put(addedResourceRealID, addedResourceEntry);
					}
					
					readServerResources(response.resourcesOnServer);

					for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
						observer.afterSynchronize();
					
					currentSyncNumber = null;

					synchronizeWithServer();
				}
			});
		}
	}
	
	private void readServerResources(ServerResourcesResponse<ResourceGWT> response) {
		Set<Integer> realIDsOfResourcesOnServer = new TreeSet<Integer>();
		
		for (ResourceGWT resourceOnServer : response.resourcesOnServer) {
			int resourceRealID = resourceOnServer.getID();
			realIDsOfResourcesOnServer.add(resourceRealID);
			
			Entry existingEntry = entriesByRealID.get(resourceRealID);
			
			if (existingEntry == null) {
				ResourceGWT localResource = cloneResource(resourceOnServer);
				localResource.setID(nextLocalID++);

				System.out.println("got a new course from server! remote id " + resourceRealID + " and now its local id is " + localResource.getID());
				
				Entry newEntry = new Entry(localResource.getID(), resourceRealID, localResource, EntryActivity.NO_CHANGES);
				entriesByRealID.put(resourceRealID, newEntry);
				entriesByLocalID.put(localResource.getID(), newEntry);
				
				for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
					observer.onResourceAdded(cloneResource(localResource), false);
			}
			else {
				
				ResourceGWT newLocalResource = realToLocal(cloneResource(resourceOnServer));
				
				switch (existingEntry.activity) {
					case DELETED:
						// resource still exists on server. we have a local delete, ignore that the server has it
						
						if (resourceChanged(cloneResource(existingEntry.localResource), cloneResource(newLocalResource))) {
							// If we ever want to handle conflicts (they edit, we delete), use this if statement
							System.out.println("Conflict! Edit came in, but marked for deletion. Ignoring server edit, proceeding with delete.");
						}
						break;
						
					case MODIFIED:
						// resource still exists on server. we have a local edit, ignore what the server says

						if (resourceChanged(cloneResource(existingEntry.localResource), cloneResource(newLocalResource))) {
							System.out.println("Conflict! Edit came in, but marked for editing. Ignoring server edit, proceeding with our own edit.");
						}
						break;
						
					case NO_CHANGES:
						// Most common case, we havent done anything to this resource, and an edit came in.

						if (resourceChanged(cloneResource(existingEntry.localResource), cloneResource(newLocalResource))) {
							existingEntry.localResource = newLocalResource;

							for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
								observer.onResourceEdited(cloneResource(existingEntry.localResource), false);
						}
						
						break;
						
					case ADDED:
						assert(false); // how does the server know about it if we just added it and havent sent it yet?
						break;
				}
			}
		}
		
		Set<Integer> realIDsOfResourcesHere = entriesByRealID.keySet();
		
		Set<Integer> realIDsOfResourcesDeletedByServer = new TreeSet<Integer>(realIDsOfResourcesHere);
		realIDsOfResourcesDeletedByServer.removeAll(realIDsOfResourcesOnServer);
		
		for (Integer realIDOfResourceDeletedByServer : realIDsOfResourcesDeletedByServer) {
			Entry existingEntry = entriesByRealID.get(realIDOfResourceDeletedByServer);
			assert(existingEntry != null);
			
			entriesByLocalID.remove(existingEntry.localID);
			entriesByRealID.remove(realIDOfResourceDeletedByServer);

			for (Observer<ResourceGWT> observer : new LinkedList<Observer<ResourceGWT>>(observers))
				observer.onResourceDeleted(existingEntry.localID, false);
		}
	}
	
	public void forceSynchronize(final AsyncCallback<Void> callback) {
		if (callback != null) {
			final int syncNumberToCallAfter = nextSyncNumber;
			
			addObserver(new Observer<ResourceGWT>() {
				@Override
				public void onAnyLocalChange() { }
				@Override
				public void onResourceAdded(ResourceGWT resource, boolean addedLocally) { }
				@Override
				public void onResourceEdited(ResourceGWT resource, boolean editedLocally) { }
				@Override
				public void onResourceDeleted(int localID, boolean deletedLocally) { }
				@Override
				public void afterSynchronize() {
					assert(currentSyncNumber <= syncNumberToCallAfter);
					
					if (currentSyncNumber == syncNumberToCallAfter) {
						removeObserver(this);
						callback.onSuccess(null);
					}
				}
			});
		}
		
		anotherSynchronizeRequested = true;
		
		synchronizeWithServer();
	}
}
