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
	
	public void testTransientsNotInserted() {
		Model model = createBlankModel();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true);
		
		assertEquals(model.findInstructorsForDocument(doc).size(), 0);
	}

	public void testInsertAndFindBasicInstructor() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			instructorID = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true)).getID();
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
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			
			int[][] timePrefs = ModelTestUtility.createSampleTimePreferences(doc);
			
			instructorID = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", timePrefs, new HashMap<Integer, Integer>(), true)).getID();
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
			doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			
			int[][] timePrefs = ModelTestUtility.createSampleTimePreferences(doc);
			
			instructorID = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", timePrefs, new HashMap<Integer, Integer>(), true)).getID();
		}
		
		model.deleteInstructor(model.findInstructorByID(instructorID));
		model.deleteDocument(doc);
		
		assertTrue(model.isEmpty());
	}
	
	public void testModifyInstructorValueDoesntAutomaticallyUpdateDatabase() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Instructor ins = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true));
			ins.setFirstName("Verdagon");
			instructorID = ins.getID();
		}
		
		{
			Instructor ins = model.findInstructorByID(instructorID);
			assertTrue(ins.getFirstName().equals("Evan"));
		}
	}
	
	public void testUpdateInstructor() throws NotFoundException {
		Model model = createBlankModel();

		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Instructor ins = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true));
			ins.setFirstName("Verdagon");
			ins.setLastName("Kalland");
			ins.setUsername("vkalland");
			ins.setMaxWTU("30");
			instructorID = ins.getID();
			model.updateInstructor(ins);
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
			doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Instructor ins = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true));
			instructorID = ins.getID();
			model.deleteInstructor(ins);
		}
		
		try {
			model.findInstructorByID(instructorID);
			assertTrue(false); // should have failed
		}
		catch (NotFoundException e) { }
		
		model.deleteDocument(doc);
		
		assertTrue(model.isEmpty());
	}
	
	public void testFindAllInstructorsForDocument() {
		Model model = createBlankModel();

		Set<Integer> instructorIDs = new HashSet<Integer>();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		instructorIDs.add(model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true)).getID());
		instructorIDs.add(model.insertInstructor(model.assembleInstructor(doc, "Herp", "Derp", "hderp", "10", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true)).getID());
		
		Collection<Instructor> returnedInstructors = model.findInstructorsForDocument(doc);
		for (Instructor returnedDoc : returnedInstructors) {
			assertTrue(instructorIDs.contains(returnedDoc.getID()));
			instructorIDs.remove(returnedDoc.getID());
		}
		assertTrue(instructorIDs.isEmpty());
	}

	public void testFindAllInstructorsInMultipleDocuments() {
		Model model = createBlankModel();

		{
			Set<Integer> instructorIDs1 = new HashSet<Integer>();
			
			Document doc1 = model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR));
			instructorIDs1.add(model.insertInstructor(model.assembleInstructor(doc1, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true)).getID());
			instructorIDs1.add(model.insertInstructor(model.assembleInstructor(doc1, "Herp", "Derp", "hderp", "10", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true)).getID());
			
			Collection<Instructor> returnedInstructors1 = model.findInstructorsForDocument(doc1);
			for (Instructor returnedDoc : returnedInstructors1) {
				assertTrue(instructorIDs1.contains(returnedDoc.getID()));
				instructorIDs1.remove(returnedDoc.getID());
			}
			assertTrue(instructorIDs1.isEmpty());
		}
		
		{
			Set<Integer> instructorIDs2 = new HashSet<Integer>();
			
			Document doc2 = model.insertDocument(model.assembleDocument("doc2", START_HALF_HOUR, END_HALF_HOUR));
			instructorIDs2.add(model.insertInstructor(model.assembleInstructor(doc2, "Baby", "Seals", "bseals", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true)).getID());
			instructorIDs2.add(model.insertInstructor(model.assembleInstructor(doc2, "Monster", "Otters", "motters", "10", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true)).getID());
			
			Collection<Instructor> returnedInstructors2 = model.findInstructorsForDocument(doc2);
			for (Instructor returnedDoc : returnedInstructors2) {
				assertTrue(instructorIDs2.contains(returnedDoc.getID()));
				instructorIDs2.remove(returnedDoc.getID());
			}
			assertTrue(instructorIDs2.isEmpty());
		}
	}
}
