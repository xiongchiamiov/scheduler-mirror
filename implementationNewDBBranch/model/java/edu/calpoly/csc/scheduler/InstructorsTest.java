package edu.calpoly.csc.scheduler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class InstructorsTest extends ModelTestCase {
	public void testInsertAndFindInstructor() throws NotFoundException {
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

	public void testModifyInstructorValueDoesntAutomaticallyUpdateDatabase() throws NotFoundException {
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
	
	public void testUpdateInstructor() throws NotFoundException {
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

	public void testDeleteInstructor() throws Exception {
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
	
	public void testFindAllInstructorsForDocument() {
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

	public void testFindAllInstructorsInMultipleDocuments() {
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
}
