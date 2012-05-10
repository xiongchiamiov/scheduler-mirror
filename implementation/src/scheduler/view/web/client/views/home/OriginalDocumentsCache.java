//package scheduler.view.web.client.views.home;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
//import scheduler.view.web.client.GreetingServiceAsync;
//import scheduler.view.web.client.views.resources.ResourceCache;
//import scheduler.view.web.shared.CourseGWT;
//import scheduler.view.web.shared.DocumentGWT;
//import scheduler.view.web.shared.InstructorGWT;
//import scheduler.view.web.shared.LocationGWT;
//
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//
//public class OriginalDocumentsCache {
//	interface Observer {
//		void afterDocumentAdded(DocumentGWT document);
//		void afterDocumentEdited(DocumentGWT oldDocument, DocumentGWT newDocument);
//		void beforeDocumentRemoved(DocumentGWT document);
//	}
//	
//	class Entry {
//		DocumentGWT document;
//		ResourceCache<CourseGWT> coursesCache;
//		ResourceCache<InstructorGWT> instructorsCache;
//		ResourceCache<LocationGWT> locationsCache;
//	}
//	
//	GreetingServiceAsync service;
//	HashMap<Integer, Entry> entriesByDocumentID = new HashMap<Integer, Entry>();
//	List<Observer> observers = new LinkedList<Observer>();
//	boolean connectionDead = false;
//	
//	public OriginalDocumentsCache(GreetingServiceAsync service) {
//		this.service = service;
//		
//		updateFromServer();
//	}
//	
//	public void addObserver(Observer observer) {
//		observers.add(observer);
//	}
//	
//	public void removeObserver(Observer observer) {
//		observers.remove(observer);
//	}
//	
//	public void updateFromServer() {
//		if (connectionDead)
//			return;
//		
//		service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>() {
//			public void onSuccess(Collection<DocumentGWT> result) {
//				refreshFromResponse(result);
//			}
//			public void onFailure(Throwable caught) {
//				connectionDead = true;
//				Window.alert("Failed to retrieve documents from server. The page will not automatically update from the server. Please refresh the page. " +  caught.getMessage());
//			}
//		});
//	}
//	
//	private void refreshFromResponse(Collection<DocumentGWT> responseDocuments) {
//		for (int documentID : new LinkedList<Integer>(entriesByDocumentID.keySet())) {
//			if (!collectionHasDocID(responseDocuments, documentID)) {
//				Entry entry = entriesByDocumentID.get(documentID);
//				DocumentGWT document = entry.document;
//				System.out.println("removing doc id " + document.getID());
//				for (Observer observer : new LinkedList<Observer>(observers))
//					observer.beforeDocumentRemoved(new DocumentGWT(document));
//				entriesByDocumentID.remove(documentID);
//			}
//		}
//		
//		for (DocumentGWT responseDocument : responseDocuments) {
//			if (entriesByDocumentID.containsKey(responseDocument.getID())) {
//				Entry entryInCache = entriesByDocumentID.get(responseDocument.getID());
//				DocumentGWT documentInCache = entryInCache.document;
//				if (!documentInCache.fieldsEqual(responseDocument)) {
//					entryInCache.document = responseDocument;
//					System.out.println("editing doc id " + documentInCache.getID());
//					for (Observer observer : new LinkedList<Observer>(observers))
//						observer.afterDocumentEdited(new DocumentGWT(documentInCache), new DocumentGWT(responseDocument));
//				}
//			}
//			else {
//				Entry newEntry = new Entry();
//				newEntry.document = responseDocument;
//				entriesByDocumentID.put(responseDocument.getID(), newEntry);
//				System.out.println("added doc id " + responseDocument.getID());
//				for (Observer observer : new LinkedList<Observer>(observers))
//					observer.afterDocumentAdded(new DocumentGWT(responseDocument));
//			}
//		}
//	}
//	
//	public void addDocument(String documentName, final AsyncCallback<DocumentGWT> callback) {
//		service.createOriginalDocument(documentName, new AsyncCallback<DocumentGWT>() {
//			public void onSuccess(DocumentGWT result) {
//				assert(!entriesByDocumentID.containsKey(result.getID()));
//				
//				Entry newEntry = new Entry();
//				newEntry.document = result;
//				
//				entriesByDocumentID.put(result.getID(), result);
//				for (Observer observer : new LinkedList<Observer>(observers))
//					observer.afterDocumentAdded(new DocumentGWT(result));
//				callback.onSuccess(new DocumentGWT(result));
//			}
//			public void onFailure(Throwable caught) {
//				callback.onFailure(caught);
//			}
//		});
//	}
//	
//	public void updateDocument(DocumentGWT document) {
//		DocumentGWT documentInCache = entriesByDocumentID.get(document.getID());
//		
//		System.out.println("derp");
//		
//		assert(documentInCache != null);
//		
//		if (documentInCache.fieldsEqual(document))
//			return;
//
//		System.out.println("gferp");
//		
//		entriesByDocumentID.put(document.getID(), new DocumentGWT(document));
//		for (Observer observer : new LinkedList<Observer>(observers))
//			observer.afterDocumentEdited(new DocumentGWT(documentInCache), new DocumentGWT(document));
//
//		System.out.println("plerp");
//		
//		service.updateDocument(document, new AsyncCallback<Void>() {
//			public void onSuccess(Void result) { }
//			public void onFailure(Throwable caught) {
//				Window.alert("Failed to update document!");
//			}
//		});
//		
//		updateFromServer();
//	}
//	
//	public void removeDocument(DocumentGWT document) {
//		DocumentGWT documentInCache = entriesByDocumentID.get(document.getID());
//		
//		assert(documentInCache != null);
//		
//		assert(document.fieldsEqual(documentInCache));
//		
//		for (Observer observer : new LinkedList<Observer>(observers))
//			observer.beforeDocumentRemoved(new DocumentGWT(document));
//		entriesByDocumentID.remove(document.getID());
//		
//		service.removeOriginalDocument(document.getID(), new AsyncCallback<Void>() {
//			public void onFailure(Throwable caught) {
//				Window.alert("Failed to remove document!");
//			}
//			public void onSuccess(Void result) { }
//		});
//		
//		updateFromServer();
//	}
//
//	private static boolean collectionHasDocID(Collection<DocumentGWT> documents, int id) {
//		for (DocumentGWT document : documents)
//			if (document.getID() == id)
//				return true;
//		return false;
//	}
//	
//	public Collection<DocumentGWT> getAllDocuments() {
//		LinkedList<DocumentGWT> result = new LinkedList<DocumentGWT>();
//		for (DocumentGWT document : entriesByDocumentID.values())
//			result.add(new DocumentGWT(document));
//		return result;
//	}
//
//	public DocumentGWT getDocumentByID(int docID) {
//		return new DocumentGWT(entriesByDocumentID.get(docID));
//	}
//}
