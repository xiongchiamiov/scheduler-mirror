package edu.calpoly.csc.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class DocumentsTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	public void testInsertDocument() {
		Model model = createBlankModel();

		model.insertDocument(model.assembleDocument("doc1", 14, 44));
	}
	
	public void testInsertDocuments() {
		Model model = createBlankModel();
		
		model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR));
		model.insertDocument(model.assembleDocument("doc2", START_HALF_HOUR, END_HALF_HOUR));
	}

	public void testInsertAndFindDocument() throws NotFoundException {
		Model model = createBlankModel();

		int documentID = model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR)).getID();
		
		Document foundDocument = model.findDocumentByID(documentID);

		assert(foundDocument.getName().equals("doc1"));
	}
	
	public void testUpdateDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;
		
		{
			Document document = model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR);
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
			Document document = model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR));
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
		
		docIDs.add(model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR)).getID());
		docIDs.add(model.insertDocument(model.assembleDocument("doc2", START_HALF_HOUR, END_HALF_HOUR)).getID());
		
		Collection<Document> docs = model.findAllDocuments();
		for (Document returnedDoc : docs) {
			assert(docIDs.contains(returnedDoc.getID()));
			docIDs.remove(returnedDoc.getID());
		}
		assert(docIDs.isEmpty());
	}
}
