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
	
	private static HashMap<Day, HashMap<Integer, Integer>> createSampleTimePreferences(Document document) {
		HashMap<Day, HashMap<Integer, Integer>> result = new HashMap<Day, HashMap<Integer, Integer>>();
		
		for (Day day : Day.values()) {
			HashMap<Integer, Integer> prefsInDay = new HashMap<Integer, Integer>();
			for (int halfHour = document.getStartHalfHour(); halfHour < document.getEndHalfHour(); halfHour++) {
				int newPref = (day.ordinal() + halfHour) % 5;
				prefsInDay.put(halfHour, newPref);
			}
			result.put(day, prefsInDay);
		}
		
		return result;
	}
	
	public void testTransientsNotInserted() {
		Model model = createBlankModel();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>());
		
		assertEquals(model.findInstructorsForDocument(doc).size(), 0);
	}

	public void testInsertAndFindBasicInstructor() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			instructorID = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>())).getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assert(found.getFirstName().equals("Evan"));
		assert(found.getLastName().equals("Ovadia"));
		assert(found.getUsername().equals("eovadia"));
		assert(found.getMaxWTU().equals("20"));
	}

	public void testInsertAndFindInstructorWTimePrefs() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			
			HashMap<Day, HashMap<Integer, Integer>> timePrefs = createSampleTimePreferences(doc);
			
			instructorID = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", timePrefs, new HashMap<Integer, Integer>())).getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assert(found.getFirstName().equals("Evan"));
		assert(found.getLastName().equals("Ovadia"));
		assert(found.getUsername().equals("eovadia"));
		assert(found.getMaxWTU().equals("20"));
	}

	public void testModifyInstructorValueDoesntAutomaticallyUpdateDatabase() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Instructor ins = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>()));
			ins.setFirstName("Verdagon");
			instructorID = ins.getID();
		}
		
		{
			Instructor ins = model.findInstructorByID(instructorID);
			assert(ins.getFirstName().equals("Evan"));
		}
	}
	
	public void testUpdateInstructor() throws NotFoundException {
		Model model = createBlankModel();

		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Instructor ins = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>()));
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

	public void testDeleteInstructor() throws Exception {
		Model model = createBlankModel();

		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Instructor ins = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>()));
			instructorID = ins.getID();
			model.deleteInstructor(ins);
		}
		
		try {
			model.findInstructorByID(instructorID);
			assert(false); // should have failed
		}
		catch (NotFoundException e) { }
	}
	
	public void testFindAllInstructorsForDocument() {
		Model model = createBlankModel();

		Set<Integer> instructorIDs = new HashSet<Integer>();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		instructorIDs.add(model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>())).getID());
		instructorIDs.add(model.insertInstructor(model.assembleInstructor(doc, "Herp", "Derp", "hderp", "10", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>())).getID());
		
		Collection<Instructor> returnedInstructors = model.findInstructorsForDocument(doc);
		for (Instructor returnedDoc : returnedInstructors) {
			assert(instructorIDs.contains(returnedDoc.getID()));
			instructorIDs.remove(returnedDoc.getID());
		}
		assert(instructorIDs.isEmpty());
	}

	public void testFindAllInstructorsInMultipleDocuments() {
		Model model = createBlankModel();

		{
			Set<Integer> instructorIDs1 = new HashSet<Integer>();
			
			Document doc1 = model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR));
			instructorIDs1.add(model.insertInstructor(model.assembleInstructor(doc1, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>())).getID());
			instructorIDs1.add(model.insertInstructor(model.assembleInstructor(doc1, "Herp", "Derp", "hderp", "10", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>())).getID());
			
			Collection<Instructor> returnedInstructors1 = model.findInstructorsForDocument(doc1);
			for (Instructor returnedDoc : returnedInstructors1) {
				assert(instructorIDs1.contains(returnedDoc.getID()));
				instructorIDs1.remove(returnedDoc.getID());
			}
			assert(instructorIDs1.isEmpty());
		}
		
		{
			Set<Integer> instructorIDs2 = new HashSet<Integer>();
			
			Document doc2 = model.insertDocument(model.assembleDocument("doc2", START_HALF_HOUR, END_HALF_HOUR));
			instructorIDs2.add(model.insertInstructor(model.assembleInstructor(doc2, "Baby", "Seals", "bseals", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>())).getID());
			instructorIDs2.add(model.insertInstructor(model.assembleInstructor(doc2, "Monster", "Otters", "motters", "10", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>())).getID());
			
			Collection<Instructor> returnedInstructors2 = model.findInstructorsForDocument(doc2);
			for (Instructor returnedDoc : returnedInstructors2) {
				assert(instructorIDs2.contains(returnedDoc.getID()));
				instructorIDs2.remove(returnedDoc.getID());
			}
			assert(instructorIDs2.isEmpty());
		}
	}
}
