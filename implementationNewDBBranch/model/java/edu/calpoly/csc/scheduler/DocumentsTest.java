package edu.calpoly.csc.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class DocumentsTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm

	public void testInsertDocument() throws DatabaseException {
		Model model = createBlankModel();

		model.createTransientDocument("doc1", 14, 44).insert();
	}

	public void testInsertDocuments() throws DatabaseException {
		Model model = createBlankModel();

		model.createTransientDocument("doc1", START_HALF_HOUR,
				END_HALF_HOUR).insert();
		model.createTransientDocument("doc2", START_HALF_HOUR,
				END_HALF_HOUR).insert();
	}

	public void testInsertAndFindDocument() throws DatabaseException {
		Model model = createBlankModel();

		int documentID = 
				model.createTransientDocument("doc1", START_HALF_HOUR, END_HALF_HOUR).insert()
				.getID();

		Document foundDocument = model.findDocumentByID(documentID);

		assert (foundDocument.getName().equals("doc1"));
	}

	public void testUpdateDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;

		{
			Document document = model.createTransientDocument("doc1", START_HALF_HOUR,
					END_HALF_HOUR);
			document.insert();
			document.setName("doc1renamed");
			documentID = document.getID();

			document.update();
		}

		assert (model.findDocumentByID(documentID).getName()
				.equals("doc1renamed"));
	}

	public void testDeleteDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;

		{
			Document document = model.createTransientDocument(
					"doc1", START_HALF_HOUR, END_HALF_HOUR).insert();
			documentID = document.getID();
			document.delete();
		}

		try {
			model.findDocumentByID(documentID);
			fail();
		} catch (NotFoundException e) {
		}
	}

	public void testDeleteFullDocument() throws Exception {
		Model model = createBlankModel();
		int documentID;
		{
			Document document = insertFullDocumentIntoModel(model);
			documentID = document.getID();
			document.delete();
		}
		try {
			model.findDocumentByID(documentID);
			fail();
		} catch (NotFoundException e) {
		}
	}

	public void testDatabaseIsEmptyAfterDelete() throws DatabaseException {
		Model model = createBlankModel();
		assert (model.isEmpty());
		Document document = insertFullDocumentIntoModel(model);
		document.delete();
		assertTrue(model.isEmpty());
	}
	
	public void testScheduleConsistancy() throws DatabaseException
	{
		Model model = createBlankModel();
		Document predocument = model.createTransientDocument("doc1",
				START_HALF_HOUR, END_HALF_HOUR).insert();
		int documentID = predocument.getID();
		
		Course precourse = ModelTestUtility.createCourse(model).setDocument(predocument).insert();
		Location prelocation = ModelTestUtility.createLocation(model).setDocument(predocument).insert();
		Instructor preinstructor = ModelTestUtility.createBasicInstructor(model).setDocument(predocument).insert();
		
		try {
			Document postdocument = model.findDocumentByID(documentID);
			Course postcourse = ((Collection<Course>) model.findCoursesForDocument(postdocument)).iterator().next();
			Location postlocation = ((Collection<Location>) model.findLocationsForDocument(postdocument)).iterator().next();
			Instructor postinstructor = ((Collection<Instructor>) model.findInstructorsForDocument(postdocument)).iterator().next();
			assertTrue(ModelTestUtility.instructorsContentsEqual(postinstructor, preinstructor));
			assertTrue(ModelTestUtility.coursesContentsEqual(postcourse, precourse));
			assertTrue(ModelTestUtility.locationsContentsEqual(postlocation, prelocation));
		} catch (NotFoundException e) {
		}
	}

	private Document insertFullDocumentIntoModel(Model model) throws DatabaseException {
		Document document = model.createTransientDocument("doc1",
				START_HALF_HOUR, END_HALF_HOUR).insert();;
		ModelTestUtility.createCourse(model).setDocument(document).insert();
		ModelTestUtility.createBasicInstructor(model).setDocument(document).insert();
		ModelTestUtility.createLocation(model).setDocument(document).insert();
		model.createTransientSchedule().setDocument(document).insert();
		return document;
	}

	public void testFindAllDocuments() throws DatabaseException {
		Model model = createBlankModel();

		Set<Integer> docIDs = new HashSet<Integer>();

		docIDs.add(model.createTransientDocument("doc1", START_HALF_HOUR, END_HALF_HOUR).insert()
				.getID());
		docIDs.add(
				model.createTransientDocument("doc2", START_HALF_HOUR, END_HALF_HOUR)
				.insert().getID());

		Collection<Document> docs = model.findAllDocuments();
		for (Document returnedDoc : docs) {
			assert (docIDs.contains(returnedDoc.getID()));
			docIDs.remove(returnedDoc.getID());
		}
		assert (docIDs.isEmpty());
	}
}
