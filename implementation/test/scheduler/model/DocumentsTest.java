package scheduler.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import scheduler.model.Course;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.Location;
import scheduler.model.Model;
import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDatabase.NotFoundException;

public abstract class DocumentsTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm

	public void testInsertDocument() throws DatabaseException {
		Model model = createBlankModel();

		model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", 14, 44);
	}

	public void testInsertDocuments() throws DatabaseException {
		Model model = createBlankModel();

		model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", START_HALF_HOUR, END_HALF_HOUR);
		model.createAndInsertDocumentWithTBAStaffAndSchedule("doc2", START_HALF_HOUR, END_HALF_HOUR);
	}

	public void testInsertAndFindDocument() throws DatabaseException {
		Model model = createBlankModel();

		int documentID = 
				model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", START_HALF_HOUR, END_HALF_HOUR)
				.getID();

		Document foundDocument = model.findDocumentByID(documentID);

		assert (foundDocument.getName().equals("doc1"));
	}

	public void testUpdateDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;

		{
			Document document = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", START_HALF_HOUR, END_HALF_HOUR);
			document.setName("doc1renamed");
			document.setStartHalfHour(10);
			document.setEndHalfHour(20);
			document.setIsTrashed(true);
			documentID = document.getID();

			document.update();
		}

		Document document = model.findDocumentByID(documentID);
		assert(document.getName().equals("doc1renamed"));
		assert(document.getStartHalfHour() == 10);
		assert(document.getEndHalfHour() == 20);
		assert(document.isTrashed() == true);
	}

	public void testDeleteDocument() throws Exception {
		Model model = createBlankModel();

		int documentID;

		{
			Document document = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", START_HALF_HOUR, END_HALF_HOUR);
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
		Document predocument = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", START_HALF_HOUR, END_HALF_HOUR);
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
		Document document = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", START_HALF_HOUR, END_HALF_HOUR);
		ModelTestUtility.createCourse(model).setDocument(document).insert();
		ModelTestUtility.createBasicInstructor(model).setDocument(document).insert();
		ModelTestUtility.createLocation(model).setDocument(document).insert();
		model.createTransientSchedule().setDocument(document).insert();
		return document;
	}

	public void testFindAllDocuments() throws DatabaseException {
		Model model = createBlankModel();

		Set<Integer> docIDs = new HashSet<Integer>();

		docIDs.add(model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", START_HALF_HOUR, END_HALF_HOUR)
				.getID());
		docIDs.add(
				model.createAndInsertDocumentWithTBAStaffAndSchedule("doc2", START_HALF_HOUR, END_HALF_HOUR)
				.getID());

		Collection<Document> docs = model.findAllDocuments();
		for (Document returnedDoc : docs) {
			assert (docIDs.contains(returnedDoc.getID()));
			docIDs.remove(returnedDoc.getID());
		}
		assert (docIDs.isEmpty());
	}

	public void testDocumentTBALocation() throws DatabaseException {
		Model model = createBlankModel();
		
		int documentID = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc", 14, 44).getID();
		
		model.clearCache();
		
		assert(model.findDocumentByID(documentID).getTBALocation() != null);
	}

	public void testDocumentStaffInstructor() throws DatabaseException {
		Model model = createBlankModel();
		
		int documentID = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc", 14, 44).getID();
		
		model.clearCache();
		
		assert(model.findDocumentByID(documentID).getStaffInstructor() != null);
	}

	public void testCopyDocument() throws DatabaseException {
		Model model = createBlankModel();
		
		Document document = insertFullDocumentIntoModel(model);
		
		model.copyDocument(document, document.getName());
	}

	public void testCopyDocumentWithAssociations() throws DatabaseException {
		Model model = createBlankModel();
		
		Document document = insertFullDocumentIntoModel(model);

		Course course2 = model.createTransientCourse("mylec", "122", "dept", "10", "10", "2", "LEC", "2", "2", true).setDocument(document).insert();
		Course course3 = model.createTransientCourse("mylab", "122", "dept", "10", "10", "2", "LAB", "2", "2", true).setDocument(document).insert();
		course3.setLecture(course2);
		course3.setTetheredToLecture(true);
		course2.update();
		course3.update();
		

		for (Course course : document.getCourses()) {
			if (course.getName().equals("mylab")) {
				assert(course.getLecture() != null);
				assert(course.getLecture().getName().equals("mylec"));
			}
			else {
				assert(course.isTetheredToLecture() == false);
			}
		}
		
		Document newDocument = model.copyDocument(document, "newname");
		
		for (Course course : newDocument.getCourses()) {
			if (course.getName().equals("mylab")) {
				assert(course.getLecture() != null);
				assert(course.getLecture().getName().equals("mylec"));
			}
			else {
				assert(course.isTetheredToLecture() == false);
			}
		}
	}
	
	public void testWorkingCopy() throws DatabaseException {
		Model model = createBlankModel();

		int doc1id;
		int doc2id;
		
		{
			Document doc1 = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc1", 10, 30);
			Document doc2 = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc2", 10, 30);
			doc2.setOriginal(doc1);
			doc2.update();
			doc1.update();
			doc1id = doc1.getID();
			doc2id = doc2.getID();

			model.clearCache();
		}


		{
			Document doc1 = model.findDocumentByID(doc1id);
			assert(doc1 != null);
			assert(doc1.getWorkingCopy() != null);
			assert(doc1.getWorkingCopy().getName().equals("doc2"));

			model.clearCache();
		}

		
		model.findDocumentByID(doc2id).getOriginal().getName().equals("doc1");
	}
}
