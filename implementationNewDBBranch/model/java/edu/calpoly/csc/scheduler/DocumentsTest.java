package edu.calpoly.csc.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class DocumentsTest extends ModelTestCase {
	public void testInsertDocument() {
		Model model = createBlankModel();

		model.insertDocument(model.createDocument("doc1"));
	}
	
	public void testInsertDocuments() {
		Model model = createBlankModel();
		
		model.insertDocument(model.createDocument("doc1"));
		model.insertDocument(model.createDocument("doc2"));
	}

	public void testInsertAndFindDocument() throws NotFoundException {
		Model model = createBlankModel();

		int documentID = model.insertDocument(model.createDocument("doc1")).getID();
		
		Document foundDocument = model.findDocumentByID(documentID);

		assert(foundDocument.getName().equals("doc1"));
	}
	
	public void testUpdateDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;
		
		{
			Document document = model.createDocument("doc1");
			model.insertDocument(document);
			document.setName("doc1renamed");
			documentID = document.getID();
			
			// make sure that it doesnt change in the db until we call updateDocument
			assert(model.findDocumentByID(documentID).getName().equals("doc1"));
			
			model.updateDocument(document);
		}
		
		assert(model.findDocumentByID(documentID).getName().equals("doc1renamed"));
	}
	
	public void testDeleteDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;
		
		{
			Document document = model.insertDocument(model.createDocument("doc1"));
			documentID = document.getID();
			model.deleteDocument(document);
		}
		
		try {
			model.findDocumentByID(documentID);
			assert(false); // should have failed
		}
		catch (NotFoundException e) { }
	}
	
	public void testFindAllDocuments() {
		Model model = createBlankModel();

		Set<Integer> docIDs = new HashSet<Integer>();
		
		docIDs.add(model.insertDocument(model.createDocument("doc1")).getID());
		docIDs.add(model.insertDocument(model.createDocument("doc2")).getID());
		
		Collection<Document> docs = model.findAllDocuments();
		for (Document returnedDoc : docs) {
			assert(docIDs.contains(returnedDoc.getID()));
			docIDs.remove(returnedDoc.getID());
		}
		assert(docIDs.isEmpty());
	}
}
