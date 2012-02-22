package edu.calpoly.csc.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.Schedule;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;
import edu.calpoly.csc.scheduler.model.tempalgorithm.GenerationAlgorithm;
import edu.calpoly.csc.scheduler.model.tempalgorithm.GenerationAlgorithm.CouldNotBeScheduledException;

public abstract class TempAlgorithmTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	public void testGenerate() throws NotFoundException, CouldNotBeScheduledException {
		Model model = createBlankModel();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		
		model.insertLocation(model.assembleLocation(doc, "roomlol", "LEC", "30", new HashSet<String>(), true));
		
		Course course = model.assembleCourse(doc, "Test", "101", "CSC", "4", "4", "1", "LEC", "60", "6", new HashSet<String>(), new ArrayList<Set<Day>>(), true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		model.insertCourse(course);
		
		Instructor instructor = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), new HashMap<Integer, Integer>(), true));
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		
		Schedule schedule = model.assembleSchedule(doc);
		model.insertSchedule(schedule);
		
		Collection<ScheduleItem> result = GenerationAlgorithm.generateRestOfSchedule(model, schedule);
		assertEquals(result.size(), 1);
	}

}
