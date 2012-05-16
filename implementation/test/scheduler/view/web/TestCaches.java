package scheduler.view.web;

import java.util.LinkedList;

import junit.framework.TestCase;
import scheduler.model.Document;
import scheduler.model.db.DatabaseException;
import scheduler.view.web.client.InvalidLoginException;
import scheduler.view.web.client.OriginalDocumentsCache;
import scheduler.view.web.client.views.resources.ResourceCache;
import scheduler.view.web.client.views.resources.ResourceCache.Observer;
import scheduler.view.web.server.GreetingServiceImpl;
import scheduler.view.web.shared.ClientChangesRequest;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SessionClosedFromInactivityExceptionGWT;
import scheduler.view.web.shared.SynchronizeRequest;

public class TestCaches extends TestCase {
	static abstract class Event { }
	
	static abstract class ChangeEvent extends Event {
		enum Location { CLIENT, SERVER };
		
		Location location;
		
		public ChangeEvent(Location location) {
			this.location = location;
		}
	}
	
	static abstract class AddOrEditEvent extends ChangeEvent {
		String name;
		int value;
		
		public AddOrEditEvent(Location location, String name, int value) {
			super(location);
			this.name = name;
			this.value = value;
		}
	}

	static class AddEvent extends AddOrEditEvent {
		public AddEvent(Location location, String name, int value) {
			super(location, name, value);
		}
	}

	static class EditEvent extends AddOrEditEvent {
		public EditEvent(Location location, String name, int value) {
			super(location, name, value);
		}
	}
	
	static class DeleteEvent extends ChangeEvent {
		String name;
		
		public DeleteEvent(Location location, String name) {
			super(location);
			this.name = name;
		}
	}
	
	static class NetworkEvent extends Event {
		enum Type {
			CLIENT_SEND_REQUEST,
			SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE,
			CLIENT_RECEIVES_RESPONSE
		}
		
		Type type;
		
		NetworkEvent(Type type) {
			this.type = type;
		}
	}
	
	class ExpectedServerDocument {
		String name;
		int value;
		
		ExpectedServerDocument(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}
	
	class ExpectedClientDocument {
		String name;
		int value;
		boolean hasRealID;
		
		ExpectedClientDocument(String name, int value, boolean hasRealID) {
			this.name = name;
			this.value = value;
			this.hasRealID = hasRealID;
		}
	}
	
	private void simulateEvents(
			Observer<OriginalDocumentGWT> observer,
			Event[] events,
			ExpectedServerDocument[] expectedServerDocuments,
			ExpectedClientDocument[] expectedClientDocuments) throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		
		GreetingServiceImpl service = new GreetingServiceImpl(false);
		GreetingServiceConnector asyncConnector = new GreetingServiceConnector(service);
		LoginResponse loginResponse = service.loginAndGetAllOriginalDocuments("testuser");
		int sessionID = loginResponse.sessionID;
		OriginalDocumentsCache cache = new OriginalDocumentsCache(false, asyncConnector, sessionID, loginResponse.initialOriginalDocuments);
		
		if (observer != null)
			cache.addObserver(observer);
		
		for (Event event : events) {
			if (event instanceof NetworkEvent) {
				NetworkEvent netEvent = (NetworkEvent)event;
				switch (netEvent.type) {
					case CLIENT_SEND_REQUEST:
						cache.forceSynchronize(null);
						break;
					case SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE:
						asyncConnector.serverReceiveRequesAndSendResponset();
						break;
					case CLIENT_RECEIVES_RESPONSE:
						asyncConnector.clientReceiveResponse();
						break;
					default:
						assert(false);
				}
			}
			else if (event instanceof AddEvent) {
				AddEvent addEvent = (AddEvent)event;
				if (addEvent.location == ChangeEvent.Location.CLIENT) {
					cache.add(new OriginalDocumentGWT(null, addEvent.name, 0, 0, 0, 0, false, 0, addEvent.value, null));
				}
				else if (addEvent.location == ChangeEvent.Location.SERVER) {
					service.inner.model.createAndInsertDocumentWithSpecialInstructorsAndLocations(addEvent.name, 0, addEvent.value);
				}
			}
			else if (event instanceof EditEvent) {
				EditEvent editEvent = (EditEvent)event;
				
				if (editEvent.location == ChangeEvent.Location.CLIENT) {
					OriginalDocumentGWT documentWithName = null;
					
					for (OriginalDocumentGWT document : cache.getAll())
						if (document.getName().equals(editEvent.name))
							documentWithName = document;
					
					documentWithName.setEndHalfHour(editEvent.value);
					cache.edit(documentWithName);
				}
				else if (editEvent.location == ChangeEvent.Location.SERVER) {
					Document documentWithName = service.inner.model.findDocumentByNameOrNull(editEvent.name);
					
					documentWithName.setEndHalfHour(editEvent.value);
					documentWithName.update();
				}
			}
			else if (event instanceof DeleteEvent) {
				DeleteEvent deleteEvent = (DeleteEvent)event;
				
				if (deleteEvent.location == ChangeEvent.Location.CLIENT) {
					OriginalDocumentGWT documentWithName = null;
					
					for (OriginalDocumentGWT document : cache.getAll())
						if (document.getName().equals(deleteEvent.name))
							documentWithName = document;
					
					cache.delete(documentWithName.getID());
				}
				else if (deleteEvent.location == ChangeEvent.Location.SERVER) {
					Document documentWithName = service.inner.model.findDocumentByNameOrNull(deleteEvent.name);
					
					documentWithName.delete();
				}
			}
		}

		assertEquals(expectedServerDocuments.length, service.synchronizeOriginalDocuments(sessionID, new SynchronizeRequest<OriginalDocumentGWT>(new ClientChangesRequest<OriginalDocumentGWT>())).resourcesOnServer.resourcesOnServer.size());
		for (ExpectedServerDocument expectedServerDocument : expectedServerDocuments) {
			OriginalDocumentGWT actualServerDocument = findServerDocumentByName(service, sessionID, expectedServerDocument.name);
			assert(actualServerDocument != null);
			assert(actualServerDocument.getEndHalfHour() == expectedServerDocument.value);
		}

		assertEquals(expectedClientDocuments.length, cache.getAll().size());
		for (ExpectedClientDocument expectedClientDocument : expectedClientDocuments) {
			OriginalDocumentGWT actualClientDocument = findClientDocumentByName(cache, expectedClientDocument.name);
			assert(actualClientDocument != null);
			assert(actualClientDocument.getEndHalfHour() == expectedClientDocument.value);
			
			boolean actualHasRealID = (cache.localIDToRealID(actualClientDocument.getID()) != null);
			assert(actualHasRealID == expectedClientDocument.hasRealID);
		}
	}
	
	static OriginalDocumentGWT findServerDocumentByName(GreetingServiceImpl service, int sessionID, String name) throws SessionClosedFromInactivityExceptionGWT {
		for (OriginalDocumentGWT document : service.synchronizeOriginalDocuments(sessionID, new SynchronizeRequest<OriginalDocumentGWT>(new ClientChangesRequest<OriginalDocumentGWT>())).resourcesOnServer.resourcesOnServer)
			if (name.equals(document.getName()))
				return document;
		assert(false);
		return null;
	}
	
	static OriginalDocumentGWT findClientDocumentByName(ResourceCache<OriginalDocumentGWT> cache, String name) {
		for (OriginalDocumentGWT document : cache.getAll())
			if (name.equals(document.getName()))
				return document;
		assert(false);
		return null;
	}
	
	public void testSimpleObserver() {
		ResourceCache<OriginalDocumentGWT> cache = new OriginalDocumentsCache(false, null, 0, new ServerResourcesResponse<OriginalDocumentGWT>());
		Observer<OriginalDocumentGWT> obs = new ResourceCache.Observer<OriginalDocumentGWT>() {
			public void afterSynchronize() { }
			public void onAnyLocalChange() { }
			public void onResourceAdded(OriginalDocumentGWT resource, boolean addedLocally) { }
			public void onResourceEdited(OriginalDocumentGWT resource, boolean editedLocally) { }
			public void onResourceDeleted(int localID, boolean deletedLocally) { }
		};
		cache.addObserver(obs);
		cache.removeObserver(obs);
	}

	public void testNothing() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	public void testEmptySendAndReceive() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	public void testCreateOnClient() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
//				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
//				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
//				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 11, false)
		});
	}
	
	public void testCreateOnClientAndServerAndClientDoesntReceiveResponse() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
//				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 11, false)
		});
	}

	public void testCreateOnClientAndServer() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 11, true)
		});
	}

	public void testEditNoSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testUnreceivedEdit() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testBasicEdit() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 12)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testEditBeforeSyncEnd() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testEditBeforeSyncEnd2() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testUnreceivedDelete() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
		});
	}

	public void testBasicDelete() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	public void testDeleteBeforeSyncEnd() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
		});
	}

	public void testDeleteBeforeSyncEnd2() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client deletes something, and a server sends a new version, discard the new version and just delete
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
		});
	}
	
	public void testAddAndDeleteBeforeSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	// PF stands for pre filled

	public void testPFNothing() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
		}, new ExpectedClientDocument[] {
		});
	}

	public void testPFEmptySendAndReceive() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true)
		});
	}

	public void testPFCreateOnClient() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
//				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
//				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
//				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 11, false)
		});
	}
	
	public void testPFCreateOnClientAndServerAndClientDoesntReceiveResponse() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
//				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 11, false)
		});
	}

	public void testPFCreateOnClientAndServer() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
				new ExpectedClientDocument("mydoc", 11, true)
		});
	}

	public void testPFEditNoSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testPFUnreceivedEdit() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testPFBasicEdit() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 12)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testPFEditBeforeSyncEnd() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testPFEditBeforeSyncEnd2() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testPFUnreceivedDelete() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
		});
	}

	public void testPFBasicDelete() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
		});
	}

	public void testPFDeleteBeforeSyncEnd() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client edits something, and a server sends a new version, discard the new version and keep local edits
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
		});
	}

	public void testPFDeleteBeforeSyncEnd2() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		// If a client deletes something, and a server sends a new version, discard the new version and just delete
		
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
		});
	}
	
	public void testPFAddAndDeleteBeforeSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("servdoc", 5),
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
		});
	}

	public void testAddAndDeleteBeforeReceive() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new DeleteEvent(ChangeEvent.Location.SERVER, "servdoc"),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	public void testAddRespondThenDelete() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.SERVER, "servdoc"),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("servdoc", 5, true),
		});
	}
	
	public void testSimpleServerAddAndDelete() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.SERVER, "servdoc"),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	public void testSimpleServerAddAndDeleteWithObserver() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		RememberingObserver observer = new RememberingObserver();
		
		simulateEvents(observer, new Event[] {
				new AddEvent(ChangeEvent.Location.SERVER, "servdoc", 5),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new DeleteEvent(ChangeEvent.Location.SERVER, "servdoc"),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});

		assertEquals(2, observer.calledSynchronize);
		assertEquals(0, observer.calledAnyLocalChange);
		assertEquals(0, observer.calledAddedLocally);
		assertEquals(0, observer.calledEditedLocally);
		assertEquals(0, observer.calledDeletedLocally);
		assertEquals(1, observer.calledAddedRemotely);
		assertEquals(0, observer.calledEditedRemotely);
		assertEquals(1, observer.calledDeletedRemotely);
	}
	
	public void testCacheGetByID() throws InvalidLoginException {
		GreetingServiceImpl service = new GreetingServiceImpl(false);
		GreetingServiceConnector asyncConnector = new GreetingServiceConnector(service);
		LoginResponse loginResponse = service.loginAndGetAllOriginalDocuments("testuser");
		int sessionID = loginResponse.sessionID;
		OriginalDocumentsCache cache = new OriginalDocumentsCache(false, asyncConnector, sessionID, loginResponse.initialOriginalDocuments);
		
		OriginalDocumentGWT newDoc = new OriginalDocumentGWT(null, "derp", 0, 0, 0, 0, false, 0, 0, null);
		cache.add(newDoc);
		
		int newDocID = newDoc.getID();
		
		newDoc = null;
		
		newDoc = cache.getByID(newDocID);
		
		assert(newDoc.getName().equals("derp"));
	}
	
	public void testClientDeleteModified() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 11)
		}, new ExpectedClientDocument[] {
		});
	}

	
	public void testClientDeleteModifiedBeforeSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	public void testClientAddAndEditBeforeSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 12)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 12, true)
		});
	}

	public void testClientAddAndEditAndEditBeforeSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 13),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 13)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 13, true)
		});
	}

	public void testClientAddAndEditAndDeleteBeforeSend() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 13),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE)
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}


	public void testClientEditEdited() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 13),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
				new ExpectedServerDocument("mydoc", 13)
		}, new ExpectedClientDocument[] {
				new ExpectedClientDocument("mydoc", 13, true)
		});
	}


	public void testClientDeleteEdited() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});
	}

	class RememberingObserver implements ResourceCache.Observer<OriginalDocumentGWT> {
		int calledSynchronize = 0;
		int calledAnyLocalChange = 0;
		int calledAddedLocally = 0;
		int calledEditedLocally = 0;
		int calledDeletedLocally = 0;
		int calledAddedRemotely = 0;
		int calledEditedRemotely = 0;
		int calledDeletedRemotely = 0;

		@Override
		public void afterSynchronize() {
			calledSynchronize++;
		}
		@Override
		public void onAnyLocalChange() { calledAnyLocalChange++; }
		@Override
		public void onResourceAdded(OriginalDocumentGWT resource, boolean addedLocally) {
			if (addedLocally)
				calledAddedLocally++;
			else
				calledAddedRemotely++;
		}
		@Override
		public void onResourceEdited(OriginalDocumentGWT resource, boolean editedLocally) {
			if (editedLocally)
				calledEditedLocally++;
			else
				calledEditedRemotely++;
		}
		@Override
		public void onResourceDeleted(int localID, boolean deletedLocally) {
			if (deletedLocally)
				calledDeletedLocally++;
			else
				calledDeletedRemotely++;
		}
	}
	
	public void testClientDeleteEditedWithObserver() throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		RememberingObserver observer = new RememberingObserver();
		
		simulateEvents(observer, new Event[] {
				new AddEvent(ChangeEvent.Location.CLIENT, "mydoc", 11),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_SEND_REQUEST),
				new EditEvent(ChangeEvent.Location.CLIENT, "mydoc", 12),
				new DeleteEvent(ChangeEvent.Location.CLIENT, "mydoc"),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.SERVER_RECEIVES_REQUEST_AND_SENDS_RESPONSE),
				new NetworkEvent(NetworkEvent.Type.CLIENT_RECEIVES_RESPONSE),
		}, new ExpectedServerDocument[] {
		}, new ExpectedClientDocument[] {
		});

		assertEquals(3, observer.calledSynchronize);
		assertEquals(3, observer.calledAnyLocalChange);
		assertEquals(1, observer.calledAddedLocally);
		assertEquals(1, observer.calledEditedLocally);
		assertEquals(1, observer.calledDeletedLocally);
		assertEquals(0, observer.calledAddedRemotely);
		assertEquals(1, observer.calledEditedRemotely);
		assertEquals(0, observer.calledDeletedRemotely);
	}

	private void simulateEvents(
			Event[] events,
			ExpectedServerDocument[] expectedServerDocuments,
			ExpectedClientDocument[] expectedClientDocuments) throws DatabaseException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException, SessionClosedFromInactivityExceptionGWT, InvalidLoginException {
		simulateEvents(null, events, expectedServerDocuments, expectedClientDocuments);
	}

}
