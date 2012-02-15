package edu.calpoly.csc.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;
import junit.framework.*;

public class ModelTest extends TestCase{
	final ICreateDatabaseCallback createDatabase = new ICreateDatabaseCallback() {
		public IDatabase createDatabase() {
			return new edu.calpoly.csc.scheduler.model.db.simple.Database();
		}
	};
	
	public void setUp() {
		
	}
	
	public void tearDown() {
		
	}
	
	public void test() {
		
	}
	
	public interface ICreateDatabaseCallback {
		IDatabase createDatabase();
	}
	
//	//final ICreateDatabaseCallback createDatabase;
//	ModelTest(ICreateDatabaseCallback createDatabase) {
//		this.createDatabase = createDatabase;
//	}
	
	private Model createBlankModel() {
		return new Model(createDatabase.createDatabase());
	}
	
//	public static void main(String[] args) {
//		new ModelTest(new ICreateDatabaseCallback() {
//			public IDatabase createDatabase() {
//				return new edu.calpoly.csc.scheduler.model.db.simple.Database();
//			}
//		}).runTests();
//	}
	
//	void runTests() {
//		try {
//			testInsertDocument();
//			testInsertDocuments();
//			testInsertAndFindDocument();
//			testUpdateDocument();
//			testDeleteDocument();
//			testFindAllDocuments();
//			
//			testInsertAndFindInstructor();
//			testModifyInstructorValueDoesntAutomaticallyUpdateDatabase();
//			testUpdateInstructor();
//			testDeleteInstructor();
//			testFindAllInstructorsForDocument();
//			testFindAllInstructorsInMultipleDocuments();
//			
//			System.out.println("Success");
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	void testInsertDocument() {
		Model model = new Model(createDatabase.createDatabase());

		model.insertDocument("doc1");
	}
	
	void testInsertDocuments() {
		Model model = createBlankModel();
		
		model.insertDocument("doc1");
		model.insertDocument("doc2");
	}

	void testInsertAndFindDocument() throws NotFoundException {
		Model model = createBlankModel();

		int documentID = model.insertDocument("doc1").getID();
		
		Document foundDocument = model.findDocumentByID(documentID);

		assert(foundDocument.getName().equals("doc1"));
	}
	
	void testUpdateDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;
		
		{
			Document document = model.insertDocument("doc1");
			document.setName("doc1renamed");
			documentID = document.getID();
			
			// make sure that it doesnt change in the db until we call updateDocument
			assert(model.findDocumentByID(documentID).getName().equals("doc1"));
			
			model.updateDocument(document);
		}
		
		assert(model.findDocumentByID(documentID).getName().equals("doc1renamed"));
	}
	
	void testDeleteDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;
		
		{
			Document document = model.insertDocument("doc1");
			documentID = document.getID();
			model.deleteDocument(document);
		}
		
		try {
			model.findDocumentByID(documentID);
			assert(false); // should have failed
		}
		catch (NotFoundException e) { }
	}
	
	void testFindAllDocuments() {
		Model model = createBlankModel();

		Set<Integer> docIDs = new HashSet<Integer>();
		
		docIDs.add(model.insertDocument("doc1").getID());
		docIDs.add(model.insertDocument("doc2").getID());
		
		Collection<Document> docs = model.findAllDocuments();
		for (Document returnedDoc : docs) {
			assert(docIDs.contains(returnedDoc.getID()));
			docIDs.remove(returnedDoc.getID());
		}
		assert(docIDs.isEmpty());
	}

	void testInsertAndFindInstructor() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument("doc");
			instructorID = model.insertInstructor(doc, "Evan", "Ovadia", "eovadia", "20").getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assert(found.getFirstName().equals("Evan"));
		assert(found.getLastName().equals("Ovadia"));
		assert(found.getUsername().equals("eovadia"));
		assert(found.getMaxWTU().equals("20"));
	}

	void testModifyInstructorValueDoesntAutomaticallyUpdateDatabase() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument("doc");
			Instructor ins = model.insertInstructor(doc, "Evan", "Ovadia", "eovadia", "20");
			ins.setFirstName("Verdagon");
			instructorID = ins.getID();
		}
		
		{
			Instructor ins = model.findInstructorByID(instructorID);
			assert(ins.getFirstName().equals("Evan"));
		}
	}
	
	void testUpdateInstructor() throws NotFoundException {
		Model model = createBlankModel();

		int instructorID;
		
		{
			Document doc = model.insertDocument("doc");
			Instructor ins = model.insertInstructor(doc, "Evan", "Ovadia", "eovadia", "20");
			ins.setFirstName("Verdagon");
			ins.setLastName("Kalland");
			ins.setUsername("vkalland");
			ins.setMaxWTU("30");
			instructorID = ins.getID();
			model.updateInstructor(ins);
		}
		
		Instructor ins = model.findInstructorByID(instructorID);
		assert(ins.getFirstName().equals("Verdagon"));
		assert(ins.getLastName().equals("Kalland"));
		assert(ins.getUsername().equals("vkalland"));
		assert(ins.getMaxWTU().equals("30"));
	}

	void testDeleteInstructor() throws Exception {
		Model model = createBlankModel();

		int instructorID;
		
		{
			Document doc = model.insertDocument("doc");
			Instructor ins = model.insertInstructor(doc, "Evan", "Ovadia", "eovadia", "20");
			instructorID = ins.getID();
			model.deleteInstructor(ins);
		}
		
		try {
			model.findInstructorByID(instructorID);
			assert(false); // should have failed
		}
		catch (NotFoundException e) { }
	}
	
	void testFindAllInstructorsForDocument() {
		Model model = createBlankModel();

		Set<Integer> instructorIDs = new HashSet<Integer>();
		
		Document doc = model.insertDocument("doc");
		instructorIDs.add(model.insertInstructor(doc, "Evan", "Ovadia", "eovadia", "20").getID());
		instructorIDs.add(model.insertInstructor(doc, "Herp", "Derp", "hderp", "10").getID());
		
		Collection<Instructor> returnedInstructors = model.findInstructorsForDocument(doc);
		for (Instructor returnedDoc : returnedInstructors) {
			assert(instructorIDs.contains(returnedDoc.getID()));
			instructorIDs.remove(returnedDoc.getID());
		}
		assert(instructorIDs.isEmpty());
	}

	void testFindAllInstructorsInMultipleDocuments() {
		Model model = createBlankModel();

		{
			Set<Integer> instructorIDs1 = new HashSet<Integer>();
			
			Document doc1 = model.insertDocument("doc1");
			instructorIDs1.add(model.insertInstructor(doc1, "Evan", "Ovadia", "eovadia", "20").getID());
			instructorIDs1.add(model.insertInstructor(doc1, "Herp", "Derp", "hderp", "10").getID());
			
			Collection<Instructor> returnedInstructors1 = model.findInstructorsForDocument(doc1);
			for (Instructor returnedDoc : returnedInstructors1) {
				assert(instructorIDs1.contains(returnedDoc.getID()));
				instructorIDs1.remove(returnedDoc.getID());
			}
			assert(instructorIDs1.isEmpty());
		}
		
		{
			Set<Integer> instructorIDs2 = new HashSet<Integer>();
			
			Document doc2 = model.insertDocument("doc2");
			instructorIDs2.add(model.insertInstructor(doc2, "Baby", "Seals", "bseals", "20").getID());
			instructorIDs2.add(model.insertInstructor(doc2, "Monster", "Otters", "motters", "10").getID());
			
			Collection<Instructor> returnedInstructors2 = model.findInstructorsForDocument(doc2);
			for (Instructor returnedDoc : returnedInstructors2) {
				assert(instructorIDs2.contains(returnedDoc.getID()));
				instructorIDs2.remove(returnedDoc.getID());
			}
			assert(instructorIDs2.isEmpty());
		}
	}
	
	void testRemoveSchedule() {
		
	}
	
	void testGetInstructors() {
		
	}
	
	void testGetCourses() {
		
	}
	
	void testRescheduleCourse() {//ScheduleItem, days, starthour, athalfhour, inSchedule, scheduleItems) {
		
	}
	
	void testGetLocations() {
		
	}
	
	void testGetScheduleItems() {
		
	}
	
	void testExportCSV() {
		
	}
	
	void testCopySchedule() {
		
	}
}
