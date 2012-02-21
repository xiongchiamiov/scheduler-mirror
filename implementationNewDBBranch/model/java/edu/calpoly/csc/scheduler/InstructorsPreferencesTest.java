package edu.calpoly.csc.scheduler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class InstructorsPreferencesTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	public void testInsertAndFindInstructorWTimePrefs() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			
			HashMap<Day, HashMap<Integer, Integer>> timePrefs = ModelTestUtility.createSampleTimePreferences(doc);
			
			instructorID = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", timePrefs, new HashMap<Integer, Integer>())).getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assertEquals(found.getFirstName(), "Evan");
		assertEquals(found.getLastName(), "Ovadia");
		assertEquals(found.getUsername(), "eovadia");
		assertEquals(found.getMaxWTU(), "20");
	}


	public void testInsertAndFindInstructorWCoursePrefs() throws NotFoundException {
		Model model = createBlankModel();
		
		int instructorID;
		int courseID1;
		int courseID2;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));

			courseID1 = model.insertCourse(model.assembleCourse(doc, "Graphics", "201", "GRC", "10", "20", "2", "LEC", "20", "6", new TreeSet<String>(), new LinkedList<Set<Day>>(), true)).getID();
			courseID2 = model.insertCourse(model.assembleCourse(doc, "Graphics: The Return", "202", "GRC", "10", "20", "2", "LEC", "20", "6", new TreeSet<String>(), new LinkedList<Set<Day>>(), true)).getID();
			
			HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
			coursePrefs.put(courseID1, 2);
			coursePrefs.put(courseID2, 3);
			
			instructorID = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), coursePrefs)).getID();
		}
		
		Instructor found = model.findInstructorByID(instructorID);
		assert(found.getCoursePreferences().get(courseID1).equals(2));
		assert(found.getCoursePreferences().get(courseID2).equals(3));
	}

	public void testUtilityInstructorEquals() {
		Model model = createBlankModel();
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		
		int courseID1 = model.insertCourse(model.assembleCourse(doc, "Graphics", "201", "GRC", "10", "20", "2", "LEC", "20", "6", new TreeSet<String>(), new LinkedList<Set<Day>>(), true)).getID();
		int courseID2 = model.insertCourse(model.assembleCourse(doc, "Graphics: The Return", "202", "GRC", "10", "20", "2", "LEC", "20", "6", new TreeSet<String>(), new LinkedList<Set<Day>>(), true)).getID();
		
		HashMap<Integer, Integer> coursePrefs1 = new HashMap<Integer, Integer>();
		coursePrefs1.put(courseID1, 2);
		coursePrefs1.put(courseID2, 3);
		
		HashMap<Day, HashMap<Integer, Integer>> timePrefs1 = ModelTestUtility.createSampleTimePreferences(doc);
		
		Instructor ins1 = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", timePrefs1, coursePrefs1));
		
		HashMap<Integer, Integer> coursePrefs2 = new HashMap<Integer, Integer>();
		coursePrefs2.put(courseID1, 2);
		coursePrefs2.put(courseID2, 3);
		
		HashMap<Day, HashMap<Integer, Integer>> timePrefs2 = ModelTestUtility.createSampleTimePreferences(doc);
		
		Instructor ins2 = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", timePrefs2, coursePrefs2));
		
		
		assertTrue(ModelTestUtility.instructorsContentsEqual(ins1, ins2));
		
		ins1.getTimePreferences().put(Day.FRIDAY, new HashMap<Integer, Integer>());
		ins1.getTimePreferences().get(Day.FRIDAY).put(10, 4);

		assertFalse(ModelTestUtility.instructorsContentsEqual(ins1, ins2));
	}
	
}
