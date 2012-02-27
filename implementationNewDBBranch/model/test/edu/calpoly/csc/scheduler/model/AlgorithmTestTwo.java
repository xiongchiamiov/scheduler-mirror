package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.ModelTestCase;
import edu.calpoly.csc.scheduler.model.ModelTestUtility;
import edu.calpoly.csc.scheduler.model.Schedule;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.algorithm.CouldNotBeScheduledException;
import edu.calpoly.csc.scheduler.model.algorithm.Generate;
import edu.calpoly.csc.scheduler.model.algorithm.ScheduleItemDecorator;

public abstract class AlgorithmTestTwo extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm

	public void testGenerate() throws DatabaseException, CouldNotBeScheduledException {
		Model model = createBlankModel();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		
		model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
		
		Course course = model.createTransientCourse("Test", "101", "CSC", "4", "4", "1", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		course.setDocument(doc).insert();
		
		Instructor instructor = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setDocument(doc).insert();
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		
		Schedule schedule = model.createTransientSchedule().setDocument(doc).insert();
		Collection<ScheduleItemDecorator> c = new Vector<ScheduleItemDecorator>();
		
		Collection<ScheduleItem> result = Generate.generate(model, schedule,
				c, model.findCoursesForDocument(doc),
				model.findInstructorsForDocument(doc), model.findLocationsForDocument(doc));
		//GenerationAlgorithm.generateRestOfSchedule(model, schedule);
		assertEquals(result.size(), 1);

		checkScheduleConflicts(model, schedule);
		checkScheduleHasNoUnacceptableItems(model, schedule);
	}

	public void testGenerateMultiple() throws DatabaseException, CouldNotBeScheduledException {
		Model model = createBlankModel();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		
		model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
		
		Course course = model.createTransientCourse("Test", "101", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		course.setDocument(doc).insert();

		Instructor instructor = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setDocument(doc).insert();
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		
		Schedule schedule = model.createTransientSchedule().setDocument(doc).insert();
		
		Collection<ScheduleItemDecorator> c = new Vector<ScheduleItemDecorator>();
		Collection<ScheduleItem> result = Generate.generate(model, schedule,
				c, model.findCoursesForDocument(doc),
				model.findInstructorsForDocument(doc), model.findLocationsForDocument(doc));//GenerationAlgorithm.generateRestOfSchedule(model, schedule);
		
		assertEquals(result.size(), 2);
		
		checkScheduleConflicts(model, schedule);
		checkScheduleHasNoUnacceptableItems(model, schedule);
	}

	public void testRunOutOfInstructors() throws DatabaseException, CouldNotBeScheduledException {
		Model model = createBlankModel();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		
		model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
		
		Course course = model.createTransientCourse("Test", "101", "CSC", "4", "4", "6", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		course.setDocument(doc).insert();

		Instructor instructor = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setDocument(doc).insert();
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		instructor.update();
		
		Schedule schedule = model.createTransientSchedule().setDocument(doc).insert();
		Collection<ScheduleItemDecorator> c = new Vector<ScheduleItemDecorator>();
		
//		try {
			Collection<ScheduleItem> result = Generate.generate(model, schedule,
					c, model.findCoursesForDocument(doc),
					model.findInstructorsForDocument(doc), model.findLocationsForDocument(doc));
			//GenerationAlgorithm.generateRestOfSchedule(model, schedule);
			assertTrue(false);
//		}
//		catch (CouldNotBeScheduledException e) { }
	}
	
	private void checkScheduleConflicts(Model model, Schedule schedule) throws DatabaseException {
		HashMap<Integer, boolean[][]> blockedOffTimesByInstructorID = new HashMap<Integer, boolean[][]>();
		HashMap<Integer, boolean[][]> blockedOffTimesByLocationID = new HashMap<Integer, boolean[][]>();
		
		for (ScheduleItem item : model.findAllScheduleItemsForSchedule(schedule)) {
			if (item.isConflicted())
				continue;
			if (blockedOffTimesByInstructorID.get(item.getInstructor().getID()) == null)
				blockedOffTimesByInstructorID.put(item.getInstructor().getID(), new boolean[Day.values().length][48]);
			if (blockedOffTimesByLocationID.get(item.getLocation().getID()) == null)
				blockedOffTimesByLocationID.put(item.getLocation().getID(), new boolean[Day.values().length][48]);
			for (Day day : item.getDays()) {
				for (int halfHour = item.getStartHalfHour(); halfHour < item.getEndHalfHour(); halfHour++) {
					assertTrue(blockedOffTimesByInstructorID.get(item.getInstructor().getID())[day.ordinal()][halfHour] == false);
					blockedOffTimesByInstructorID.get(item.getInstructor().getID())[day.ordinal()][halfHour] = true;
					assertTrue(blockedOffTimesByLocationID.get(item.getLocation().getID())[day.ordinal()][halfHour] == false);
					blockedOffTimesByLocationID.get(item.getLocation().getID())[day.ordinal()][halfHour] = true;
				}
			}
		}
	}
	
	private void checkScheduleHasNoUnacceptableItems(Model model, Schedule schedule) throws DatabaseException {
		for (ScheduleItem item : model.findAllScheduleItemsForSchedule(schedule)) {
			if (item.isConflicted())
				continue;
			for (Day day : item.getDays()) {
				for (int halfHour = item.getStartHalfHour(); halfHour < item.getEndHalfHour(); halfHour++) {
					int[][] timePrefs = model.findInstructorByID(item.getInstructor().getID()).getTimePreferences();
					assertTrue(timePrefs[day.ordinal()][halfHour] > 0);
				}
			}
		}
	}
}
