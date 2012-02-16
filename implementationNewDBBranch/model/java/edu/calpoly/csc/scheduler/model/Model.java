package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.LinkedList;

import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Model {
	final IDatabase database;
	
	public Model() {
		this.database = new edu.calpoly.csc.scheduler.model.db.simple.Database();
	}
	
	public Model(IDatabase database) {
		this.database = database;
	}

	public String generateUnusedUsername() { return database.generateUnusedUsername(); }

	public User insertUser(String username, boolean b) {
		return new User(database.insertUser(username, b));
	}

	public Document insertDocument(String name) {
		return new Document(database.insertDocument(name));
	}

	public Document findDocumentByID(int documentID) throws NotFoundException {
		return new Document(database.findDocumentByID(documentID));
	}

	public void updateDocument(Document document) {
		database.updateDocument(document.underlyingDocument);
	}

	public void deleteDocument(Document document) {
		database.deleteDocument(document.underlyingDocument);
		document.underlyingDocument = null;
	}

	public Collection<Document> findAllDocuments() {
		Collection<Document> result = new LinkedList<Document>();
		for (IDBDocument underlying : database.findAllDocuments())
			result.add(new Document(underlying));
		return result;
	}

	public Instructor insertInstructor(Document containingDocument, String firstName, String lastName, String username, String maxWTU) {
		return new Instructor(database.insertInstructor(containingDocument.underlyingDocument, firstName, lastName, username, maxWTU));
	}

	public Collection<Instructor> findInstructorsForDocument(Document doc) {
		Collection<Instructor> result = new LinkedList<Instructor>();
		for (IDBInstructor underlying : database.findInstructorsForDocument(doc.underlyingDocument))
			result.add(new Instructor(underlying));
		return result;
	}

	public Instructor findInstructorByID(int instructorID) throws NotFoundException {
		return new Instructor(database.findInstructorByID(instructorID));
	}

	public void updateInstructor(Instructor ins) {
		database.updateInstructor(ins.underlyingInstructor);
	}

	public void deleteInstructor(Instructor ins) {
		database.deleteInstructor(ins.underlyingInstructor);
		ins.underlyingInstructor = null;
	}
}
