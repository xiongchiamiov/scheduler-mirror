package scheduler.view.web.client.views.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import scheduler.view.web.shared.Identified;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

// This class acts as both a proxy for the network calls, and as a cache
// because it keeps the server up to date with the changes the client makes
// It operates on an "act first, inform later" sort of philosophy. We can do this
// because we know no conflicts will occur, because the server can't modify
// resources, only the client can. (In the future, if we ever have two clients
// modifying the server at the same time, that will break this assumption)
public abstract class ResourceCache<ResourceGWT extends Identified> implements ResourceCollection<ResourceGWT> {
	public interface Observer {
		void onModify();
		void onPopulate();
	}

	static abstract class State { }
	static class UnpopulatedState extends State { }
	static class PopulatingState extends State { }
	static abstract class PopulatedState extends State { }
	static class PopulatedAndReadyState extends PopulatedState { }
	static class PopulatedAndSynchronizingWithServerState extends PopulatedState { }
	
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

	private State state;
	int nextLocalID = 1;
	final HashMap<Integer, Entry> entriesByLocalID = new HashMap<Integer, Entry>();
	final HashMap<Integer, Entry> entriesByRealID = new HashMap<Integer, Entry>();
	
	final Collection<Observer> observers = new HashSet<Observer>();
	public void addObserver(Observer observer) { observers.add(observer); }
	public void removeObserver(Observer observer) { observers.remove(observer); }
	
	
	protected abstract void getInitialResourcesFromServer(AsyncCallback<List<ResourceGWT>> callback);
	protected abstract void sendActivityToServer(List<ResourceGWT> addedResources, Collection<ResourceGWT> editedResources, List<Integer> deletedResourcesRealIDs, AsyncCallback<List<Integer>> asyncCallback);
	protected abstract ResourceGWT cloneResource(ResourceGWT source);
	protected abstract ResourceGWT localToReal(ResourceGWT localResource);
	protected abstract ResourceGWT realToLocal(ResourceGWT realResource);
	
	protected ResourceCache() {
		state = new UnpopulatedState();
	}
	
	public boolean isPopulated() {
		return state instanceof PopulatedState;
	}
	
	public void populateFromServer() {
		assert(state instanceof UnpopulatedState);
		
		state = new PopulatingState();
		
		getInitialResourcesFromServer(new AsyncCallback<List<ResourceGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve initial resources from server!");
			}
			
			@Override
			public void onSuccess(List<ResourceGWT> initialResources) {
//				Window.alert("got response from server for populating!");
				
				assert(state instanceof PopulatingState);
				
				for (ResourceGWT realResource : initialResources) {
					int realID = realResource.getID();
					
					int localID = nextLocalID++;
					
					Entry newEntry = new Entry(localID, realID, null, EntryActivity.NO_CHANGES);
					entriesByLocalID.put(localID, newEntry);
					entriesByRealID.put(realID, newEntry);
				}

				
				
				for (ResourceGWT realResource : initialResources) {
					Entry newEntry = entriesByRealID.get(realResource.getID());
					
					newEntry.localResource = realToLocal(realResource);
					assert(newEntry.localResource.getID() == newEntry.localID);
				}
				
				state = new PopulatedAndReadyState();

				for (Observer observer : new LinkedList<Observer>(observers))
					observer.onPopulate();
			}
		});
	}
	
	public Collection<ResourceGWT> getAll() {
		assert(state instanceof PopulatedState);
		
		Collection<ResourceGWT> result = new LinkedList<ResourceGWT>();
		for (Entry entry : entriesByLocalID.values())
			result.add(entry.localResource);
		return result;
	}
	
	public void add(ResourceGWT localResource) {
		assert(state instanceof PopulatedState);
		
		assert(localResource.getID() == null);
		
		int localID = nextLocalID++;
		localResource.setID(localID);
		
		Entry entry = new Entry(localID, null, cloneResource(localResource), EntryActivity.ADDED);
		entriesByLocalID.put(localID, entry);
		
		sendAllActivityToServer();
		
		for (Observer observer : new LinkedList<Observer>(observers))
			observer.onModify();
	}
	
	public void edit(ResourceGWT localResource) {
		assert(state instanceof PopulatedState);
		
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
		
		sendAllActivityToServer();

		for (Observer observer : observers)
			observer.onModify();
	}
	
	public void delete(int localResourceID) {
		assert(state instanceof PopulatedState);
		
		Entry entry = entriesByLocalID.get(localResourceID);
		assert(entry != null);
		
		switch (entry.activity) {
			case NO_CHANGES:
				entry.activity = EntryActivity.DELETED;
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
		
		sendAllActivityToServer();

		for (Observer observer : observers)
			observer.onModify();
	}
	
	void sendAllActivityToServer() {
		assert(state instanceof PopulatedState);
		
		if (state instanceof PopulatedAndSynchronizingWithServerState)
			return;
		
		final List<Entry> addedResourcesEntries = new LinkedList<Entry>();
		
		List<ResourceGWT> addedResources = new LinkedList<ResourceGWT>();
		Collection<ResourceGWT> editedResources = new LinkedList<ResourceGWT>();
		List<Integer> deletedResourcesRealIDs = new LinkedList<Integer>();
		
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
			
			entry.activity = EntryActivity.NO_CHANGES;
		}
		
		if (addedResources.isEmpty() && editedResources.isEmpty() && deletedResourcesRealIDs.isEmpty())
			return;

		state = new PopulatedAndSynchronizingWithServerState();
		
//		System.out.println("Sending activity to server! " + addedResources.size() + " " + editedResources.size() + " " + deletedResourcesRealIDs.size());
		sendActivityToServer(addedResources, editedResources, deletedResourcesRealIDs, new AsyncCallback<List<Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to send updates to server!");
			}
			
			@Override
			public void onSuccess(List<Integer> addedResourcesRealIDs) {
//				System.out.println("Success!");
				
				assert(state instanceof PopulatedAndSynchronizingWithServerState);
				
				assert(addedResourcesRealIDs.size() == addedResourcesEntries.size());
				
				Iterator<Entry> addedResourcesEntriesIterator = addedResourcesEntries.iterator();
				Iterator<Integer> addedResourcesRealIDsIterator = addedResourcesRealIDs.iterator();
				
				while (true) {
					assert(addedResourcesEntriesIterator.hasNext() == addedResourcesRealIDsIterator.hasNext());
					if (!addedResourcesEntriesIterator.hasNext())
						break;
					Entry addedResourceEntry = addedResourcesEntriesIterator.next();
					int addedResourceRealID = addedResourcesRealIDsIterator.next();
					
					addedResourceEntry.realID = addedResourceRealID;
					
					assert(!entriesByRealID.containsKey(addedResourceRealID));
					entriesByRealID.put(addedResourceRealID, addedResourceEntry);
				}
				
				state = new PopulatedAndReadyState();
				sendAllActivityToServer();
			}
		});
	}
	
	protected Integer localIDToRealID(Integer id) {
		Entry entry = entriesByLocalID.get(id);
		assert(entry != null);
		return entry.realID;
	}

	protected Integer realIDToLocalID(Integer id) {
		Entry entry = entriesByRealID.get(id);
		assert(entry != null);
		return entry.localID;
	}
}
