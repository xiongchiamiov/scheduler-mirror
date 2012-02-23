package edu.calpoly.csc.scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class InstructorsTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	public void testTransientsNotInserted() throws NotFoundException {
		Model model = createBlankModel();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true);
		
		assertEquals(model.findInstructorsForDocument(doc).size(), 0);
	}

	public void testInsertAndFindBasicInstructor() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			instructorID = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
					.setDocument(doc).insert()
					.getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assertTrue(found.getFirstName().equals("Evan"));
		assertTrue(found.getLastName().equals("Ovadia"));
		assertTrue(found.getUsername().equals("eovadia"));
		assertTrue(found.getMaxWTU().equals("20"));
	}

	public void testInsertAndFindInstructorWTimePrefs() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			
			int[][] timePrefs = ModelTestUtility.createSampleTimePreferences(doc);
			
			instructorID = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true).
					setTimePreferences(timePrefs)
					.setDocument(doc).insert()
					.getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assertTrue(found.getFirstName().equals("Evan"));
		assertTrue(found.getLastName().equals("Ovadia"));
		assertTrue(found.getUsername().equals("eovadia"));
		assertTrue(found.getMaxWTU().equals("20"));
	}

	public void testInsertAndDeleteInstructorWTimePrefs() throws NotFoundException {
		Model model = createBlankModel();
		
		Document doc;
		int instructorID;
		
		{
			doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			
			instructorID = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
					.setTimePreferences(ModelTestUtility.createSampleTimePreferences(doc))
					.setDocument(doc).insert()
					.getID();
		}
		
		model.findInstructorByID(instructorID).delete();
		doc.delete();
		
		assertTrue(model.isEmpty());
	}
	
	public void testUpdateInstructor() throws NotFoundException {
		Model model = createBlankModel();

		int instructorID;
		
		{
			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			Instructor ins = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
					.setTimePreferences(Instructor.createDefaultTimePreferences())
					.setDocument(doc).insert();
			ins.setFirstName("Verdagon");
			ins.setLastName("Kalland");
			ins.setUsername("vkalland");
			ins.setMaxWTU("30");
			instructorID = ins.getID();
			ins.update();
		}
		
		Instructor ins = model.findInstructorByID(instructorID);
		assertTrue(ins.getFirstName().equals("Verdagon"));
		assertTrue(ins.getLastName().equals("Kalland"));
		assertTrue(ins.getUsername().equals("vkalland"));
		assertTrue(ins.getMaxWTU().equals("30"));
	}

	public void testDeleteInstructor() throws Exception {
		Model model = createBlankModel();

		Document doc;
		int instructorID;
		
		{
			doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			Instructor ins = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
					.setTimePreferences(Instructor.createDefaultTimePreferences())
					.setDocument(doc).insert();
			instructorID = ins.getID();
			ins.delete();
		}
		
		try {
			model.findInstructorByID(instructorID);
			assertTrue(false); // should have failed
		}
		catch (NotFoundException e) { }
		
		doc.delete();
		
		assertTrue(model.isEmpty());
	}
	
	public void testFindAllInstructorsForDocument() throws NotFoundException {
		Model model = createBlankModel();

		Set<Integer> instructorIDs = new HashSet<Integer>();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		instructorIDs.add(
				model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setDocument(doc).insert()
				.getID());
		instructorIDs.add(
				model.createTransientInstructor("Herp", "Derp", "hderp", "10", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setDocument(doc).insert()
				.getID());
		
		Collection<Instructor> returnedInstructors = model.findInstructorsForDocument(doc);
		for (Instructor returnedDoc : returnedInstructors) {
			assertTrue(instructorIDs.contains(returnedDoc.getID()));
			instructorIDs.remove(returnedDoc.getID());
		}
		assertTrue(instructorIDs.isEmpty());
	}

	public void testFindAllInstructorsInMultipleDocuments() throws NotFoundException {
		Model model = createBlankModel();

		{
			Set<Integer> instructorIDs1 = new HashSet<Integer>();
			
			Document doc1 = model.createTransientDocument("doc1", START_HALF_HOUR, END_HALF_HOUR).insert();
			instructorIDs1.add(
					model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
					.setTimePreferences(Instructor.createDefaultTimePreferences())
					.setDocument(doc1).insert()
					.getID());
			instructorIDs1.add(
					model.createTransientInstructor("Herp", "Derp", "hderp", "10", true)
					.setTimePreferences(Instructor.createDefaultTimePreferences())
					.setDocument(doc1).insert()
					.getID());
			
			Collection<Instructor> returnedInstructors1 = model.findInstructorsForDocument(doc1);
			for (Instructor returnedDoc : returnedInstructors1) {
				assertTrue(instructorIDs1.contains(returnedDoc.getID()));
				instructorIDs1.remove(returnedDoc.getID());
			}
			assertTrue(instructorIDs1.isEmpty());
		}
		
		{
			Set<Integer> instructorIDs2 = new HashSet<Integer>();
			
			Document doc2 = model.createTransientDocument("doc2", START_HALF_HOUR, END_HALF_HOUR).insert();
			instructorIDs2.add(
					model.createTransientInstructor("Baby", "Seals", "bseals", "20", true)
					.setTimePreferences(Instructor.createDefaultTimePreferences())
					.setDocument(doc2).insert()
					.getID());
			instructorIDs2.add(
					model.createTransientInstructor("Monster", "Otters", "motters", "10", true)
					.setTimePreferences(Instructor.createDefaultTimePreferences())
					.setDocument(doc2).insert()
					.getID());
			
			Collection<Instructor> returnedInstructors2 = model.findInstructorsForDocument(doc2);
			for (Instructor returnedDoc : returnedInstructors2) {
				assertTrue(instructorIDs2.contains(returnedDoc.getID()));
				instructorIDs2.remove(returnedDoc.getID());
			}
			assertTrue(instructorIDs2.isEmpty());
		}
	}
}
