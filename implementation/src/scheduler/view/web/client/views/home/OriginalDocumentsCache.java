package scheduler.view.web.client.views.home;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OriginalDocumentsCache {
	interface Observer {
		void afterDocumentAdded(DocumentGWT document);
		void afterDocumentEdited(DocumentGWT oldDocument, DocumentGWT newDocument);
		void beforeDocumentRemoved(DocumentGWT document);
	}
	
	GreetingServiceAsync service;
	HashMap<Integer, DocumentGWT> documents = new HashMap<Integer, DocumentGWT>();
	List<Observer> observers = new LinkedList<Observer>();
	
	public OriginalDocumentsCache(GreetingServiceAsync service) {
		this.service = service;
		
		updateFromServer();
	}
	
	public void addObserver(Observer observer) {
		observers.add(observer);
	}
	
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}
	
	public void updateFromServer() {
		service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>() {
			public void onSuccess(Collection<DocumentGWT> result) {
				refreshFromResponse(result);
			}
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve documents from server!");
			}
		});
	}
	
	private void refreshFromResponse(Collection<DocumentGWT> responseDocuments) {
		for (int documentID : new LinkedList<Integer>(documents.keySet())) {
			if (!collectionHasDocID(responseDocuments, documentID)) {
				DocumentGWT document = documents.get(documentID);
				System.out.println("removing doc id " + document.getID());
				for (Observer observer : new LinkedList<Observer>(observers))
					observer.beforeDocumentRemoved(new DocumentGWT(document));
				documents.remove(documentID);
			}
		}
		
		for (DocumentGWT responseDocument : responseDocuments) {
			if (documents.containsKey(responseDocument.getID())) {
				DocumentGWT documentInCache = documents.get(responseDocument.getID());
				if (!documentInCache.fieldsEqual(responseDocument)) {
					documents.put(responseDocument.getID(), responseDocument);
					System.out.println("editing doc id " + documentInCache.getID());
					for (Observer observer : new LinkedList<Observer>(observers))
						observer.afterDocumentEdited(new DocumentGWT(documentInCache), new DocumentGWT(responseDocument));
				}
			}
			else {
				documents.put(responseDocument.getID(), responseDocument);
				System.out.println("added doc id " + responseDocument.getID());
				for (Observer observer : new LinkedList<Observer>(observers))
					observer.afterDocumentAdded(new DocumentGWT(responseDocument));
			}
		}
	}
	
	public void addDocument(String documentName, final AsyncCallback<DocumentGWT> callback) {
		service.createOriginalDocument(documentName, new AsyncCallback<DocumentGWT>() {
			public void onSuccess(DocumentGWT result) {
				assert(!documents.containsKey(result.getID()));
				documents.put(result.getID(), result);
				for (Observer observer : new LinkedList<Observer>(observers))
					observer.afterDocumentAdded(new DocumentGWT(result));
				callback.onSuccess(new DocumentGWT(result));
			}
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public void updateDocument(DocumentGWT document) {
		DocumentGWT documentInCache = documents.get(document.getID());
		
		System.out.println("derp");
		
		assert(documentInCache != null);
		
		if (documentInCache.fieldsEqual(document))
			return;

		System.out.println("gferp");
		
		documents.put(document.getID(), new DocumentGWT(document));
		for (Observer observer : new LinkedList<Observer>(observers))
			observer.afterDocumentEdited(new DocumentGWT(documentInCache), new DocumentGWT(document));

		System.out.println("plerp");
		
		service.updateDocument(document, new AsyncCallback<Void>() {
			public void onSuccess(Void result) { }
			public void onFailure(Throwable caught) {
				Window.alert("Failed to update document!");
			}
		});
		
		updateFromServer();
	}
	
	public void removeDocument(DocumentGWT document) {
		DocumentGWT documentInCache = documents.get(document.getID());
		
		assert(documentInCache != null);
		
		assert(document.fieldsEqual(documentInCache));
		
		for (Observer observer : new LinkedList<Observer>(observers))
			observer.beforeDocumentRemoved(new DocumentGWT(document));
		documents.remove(document.getID());
		
		service.removeOriginalDocument(document.getID(), new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to remove document!");
			}
			public void onSuccess(Void result) { }
		});
		
		updateFromServer();
	}

	private static boolean collectionHasDocID(Collection<DocumentGWT> documents, int id) {
		for (DocumentGWT document : documents)
			if (document.getID() == id)
				return true;
		return false;
	}
	
	public Collection<DocumentGWT> getAllDocuments() {
		LinkedList<DocumentGWT> result = new LinkedList<DocumentGWT>();
		for (DocumentGWT document : documents.values())
			result.add(new DocumentGWT(document));
		return result;
	}

	public DocumentGWT getDocumentByID(int docID) {
		return new DocumentGWT(documents.get(docID));
	}
}
