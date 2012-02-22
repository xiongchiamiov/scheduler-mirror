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
		
		Instructor instructor = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true));
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		
		Schedule schedule = model.assembleSchedule(doc);
		model.insertSchedule(schedule);
		
		Collection<ScheduleItem> result = GenerationAlgorithm.generateRestOfSchedule(model, schedule);
		assertEquals(result.size(), 1);

		checkScheduleConflicts(model, schedule);
		checkScheduleHasNoUnacceptableItems(model, schedule);
	}

	public void testGenerateMultiple() throws NotFoundException, CouldNotBeScheduledException {
		Model model = createBlankModel();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		
		model.insertLocation(model.assembleLocation(doc, "roomlol", "LEC", "30", new HashSet<String>(), true));
		
		Course course = model.assembleCourse(doc, "Test", "101", "CSC", "4", "4", "2", "LEC", "60", "6", new HashSet<String>(), new ArrayList<Set<Day>>(), true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		model.insertCourse(course);
		
		Instructor instructor = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true));
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		
		Schedule schedule = model.assembleSchedule(doc);
		model.insertSchedule(schedule);
		
		Collection<ScheduleItem> result = GenerationAlgorithm.generateRestOfSchedule(model, schedule);
		assertEquals(result.size(), 2);
		
		checkScheduleConflicts(model, schedule);
		checkScheduleHasNoUnacceptableItems(model, schedule);
	}

	public void testRunOutOfInstructors() throws NotFoundException, CouldNotBeScheduledException {
		Model model = createBlankModel();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		
		model.insertLocation(model.assembleLocation(doc, "roomlol", "LEC", "30", new HashSet<String>(), true));
		
		Course course = model.assembleCourse(doc, "Test", "101", "CSC", "4", "4", "6", "LEC", "60", "6", new HashSet<String>(), new ArrayList<Set<Day>>(), true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		model.insertCourse(course);
		
		Instructor instructor = model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", Instructor.createDefaultTimePreferences(), new HashMap<Integer, Integer>(), true));
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());

		for (int halfHour = 0; halfHour < 48; halfHour++) {
			System.out.print(halfHour + ":");
			for (Day day : Day.values()) {
				System.out.print(" " + instructor.getTimePreferences(day, halfHour));
			}
			System.out.println();
		}
		
		
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		
		for (int halfHour = 0; halfHour < 48; halfHour++) {
			System.out.print(halfHour + ":");
			for (Day day : Day.values()) {
				System.out.print(" " + instructor.getTimePreferences(day, halfHour));
			}
			System.out.println();
		}
		
		Schedule schedule = model.assembleSchedule(doc);
		model.insertSchedule(schedule);
		
		try {
			GenerationAlgorithm.generateRestOfSchedule(model, schedule);
			assertTrue(false);
		}
		catch (CouldNotBeScheduledException e) { }
	}
	
	private void checkScheduleConflicts(Model model, Schedule schedule) throws NotFoundException {
		HashMap<Integer, boolean[][]> blockedOffTimesByInstructorID = new HashMap<Integer, boolean[][]>();
		HashMap<Integer, boolean[][]> blockedOffTimesByLocationID = new HashMap<Integer, boolean[][]>();
		
		for (ScheduleItem item : model.findAllScheduleItemsForSchedule(schedule)) {
			if (item.isConflicted())
				continue;
			if (blockedOffTimesByInstructorID.get(item.getInstructorID()) == null)
				blockedOffTimesByInstructorID.put(item.getInstructorID(), new boolean[Day.values().length][48]);
			if (blockedOffTimesByLocationID.get(item.getLocationID()) == null)
				blockedOffTimesByLocationID.put(item.getLocationID(), new boolean[Day.values().length][48]);
			for (Day day : item.getDays()) {
				for (int halfHour = item.getStartHalfHour(); halfHour < item.getEndHalfHour(); halfHour++) {
					assertTrue(blockedOffTimesByInstructorID.get(item.getInstructorID())[day.ordinal()][halfHour] == false);
					blockedOffTimesByInstructorID.get(item.getInstructorID())[day.ordinal()][halfHour] = true;
					assertTrue(blockedOffTimesByLocationID.get(item.getLocationID())[day.ordinal()][halfHour] == false);
					blockedOffTimesByLocationID.get(item.getLocationID())[day.ordinal()][halfHour] = true;
				}
			}
		}
	}
	
	private void checkScheduleHasNoUnacceptableItems(Model model, Schedule schedule) throws NotFoundException {
		for (ScheduleItem item : model.findAllScheduleItemsForSchedule(schedule)) {
			if (item.isConflicted())
				continue;
			for (Day day : item.getDays()) {
				for (int halfHour = item.getStartHalfHour(); halfHour < item.getEndHalfHour(); halfHour++) {
					int[][] timePrefs = model.findInstructorByID(item.getInstructorID()).getTimePreferences();
					assertTrue(timePrefs[day.ordinal()][halfHour] > 0);
				}
			}
		}
	}
}
