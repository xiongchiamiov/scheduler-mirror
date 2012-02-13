package edu.calpoly.csc.scheduler;

import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class ModelTest {
	public interface ICreateDatabaseCallback {
		IDatabase createDatabase();
	}
	
	final ICreateDatabaseCallback createDatabase;
	ModelTest(ICreateDatabaseCallback createDatabase) {
		this.createDatabase = createDatabase;
	}
	
	public static void main(String[] args) {
		new ModelTest(new ICreateDatabaseCallback() {
			public IDatabase createDatabase() {
				return new edu.calpoly.csc.scheduler.model.db.simple.Database();
			}
		}).runTests();
	}
	
	void runTests() {
		testInsertDocument();
		testInsertDocuments();
		testInsertAndFindDocument();
		
		System.out.println("Done with tests");
	}

	void testInsertDocument() {
		System.out.println("testInsertDocument");
		
		Model model = new Model(createDatabase.createDatabase());

		model.insertDocument("doc1");
	}
	
	void testInsertDocuments() {
		System.out.println("testInsertDocuments");
		
		Model model = new Model(createDatabase.createDatabase());
		
		model.insertDocument("doc1");
		model.insertDocument("doc2");
	}

	void testInsertAndFindDocument() {
		System.out.println("testInsertAndFindDocument");
		
		try {

			Model model = new Model(createDatabase.createDatabase());
	
			int documentID = model.insertDocument("doc1").getID();
			
			Document foundDocument = model.findDocumentByID(documentID);

			assert(foundDocument.getName().equals("doc1"));
			
		} catch (NotFoundException e) {
			assert(false);
		}
	}
}
