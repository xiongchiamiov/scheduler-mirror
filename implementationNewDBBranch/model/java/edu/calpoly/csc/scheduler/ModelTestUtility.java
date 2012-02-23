package edu.calpoly.csc.scheduler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class ModelTestUtility {
	public static Course createCourse(Model model) {
		return model.createTransientCourse("Test", "101", "CSC", "4", "4", "1",
				"LEC", "60", "6",  true);
	}
	
	public static Location createLocation(Model model) {
		return model.createTransientLocation("123", "LEC", "60", true);
	}
	
	public static Instructor createBasicInstructor(Model model) {
		return model.createTransientInstructor("TestFirst", "TestLast", "testid", "4", true)
				.setTimePreferences( Instructor.createDefaultTimePreferences());
	}
	
	public static Instructor insertInstructorWPrefs(Model model, Document doc) throws NotFoundException {
		int courseID1 = model.createTransientCourse("Graphics", "201", "GRC", "10", "20", "2", "LEC", "20", "6", true).setDocument(doc).insert().getID();
		int courseID2 = model.createTransientCourse("Graphics: The Return", "202", "GRC", "10", "20", "2", "LEC", "20", "6", true).setDocument(doc).insert().getID();
		
		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		coursePrefs.put(courseID1, 2);
		coursePrefs.put(courseID2, 3);
		
		int[][] timePrefs = createSampleTimePreferences(doc);
		
		return model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
				.setTimePreferences(timePrefs)
				.setCoursePreferences(coursePrefs)
				.setDocument(doc).insert();
	}
	
	public static boolean coursesContentsEqual(Course a, Course b) throws NotFoundException {
		if (!a.getCatalogNumber().equals(b.getCatalogNumber()))
			return false;
		if (!a.getDayPatterns().equals(b.getDayPatterns()))
			return false;
		if (!a.getDepartment().equals(b.getDepartment()))
			return false;
		if (a.getLecture() != b.getLecture())
			return false;
		if (!a.getMaxEnrollment().equals(b.getMaxEnrollment()))
			return false;
		if (!a.getName().equals(b.getName()))
			return false;
		if (!a.getNumHalfHoursPerWeek().equals(b.getNumHalfHoursPerWeek()))
			return false;
		if (!a.getNumSections().equals(b.getNumSections()))
			return false;
		if (!a.getSCU().equals(b.getSCU()))
			return false;
		if (!a.getType().equals(b.getType()))
			return false;
		if (!a.getUsedEquipment().equals(b.getUsedEquipment()))
			return false;
		if (!a.getWTU().equals(b.getWTU()))
			return false;
		if (a.isSchedulable() != b.isSchedulable())
		   return false;
		return true;
	}
	
	public static boolean locationsContentsEqual(Location a, Location b) {
		if (!a.getMaxOccupancy().equals(b.getMaxOccupancy()))
			return false;
		if (!a.getProvidedEquipment().equals(b.getProvidedEquipment()))
			return false;
		if (!a.getRoom().equals(b.getRoom()))
			return false;
		if (!a.getType().equals(b.getType()))
			return false;
		return true;
	}
	
	public static boolean instructorsContentsEqual(Instructor a, Instructor b) {
		if (!a.getFirstName().equals(b.getFirstName()))
			return false;
		if (!a.getLastName().equals(b.getLastName()))
			return false;
		if (!a.getUsername().equals(b.getUsername()))
			return false;
		if (a.isSchedulable() != b.isSchedulable())
			return false;
		if (!a.getMaxWTU().equals(b.getMaxWTU()))
			return false;
		if (!a.getCoursePreferences().equals(b.getCoursePreferences()))
			return false;
		
		for (Day day : Day.values()) {
			for (int halfHour = 0; halfHour < 48; halfHour++) {
				if (a.getTimePreferences(day, halfHour) != b.getTimePreferences(day, halfHour))
					return false;
			}
		}
		
		return true;
	}
	
	public static int[][] createSampleTimePreferences(Document document) {
		int[][] result = new int[Day.values().length][48];
		
		for (Day day : Day.values()) {
			for (int halfHour = document.getStartHalfHour(); halfHour < document.getEndHalfHour(); halfHour++) {
				int newPref = (day.ordinal() + halfHour) % 5;
				result[day.ordinal()][halfHour] = newPref;
			}
		}
		
		return result;
	}
	
	public static void addDayPattern(Course course, Day... days) {
		course.getDayPatterns().add(new TreeSet(Arrays.asList(days)));
	}
	
	public static void setPreferenceBlocks(Instructor instructor, int preference, int startHalfHour, int endHalfHour, Day... days) {
		for (Day day : days)
			for (int halfHour = startHalfHour; halfHour < endHalfHour; halfHour++)
				instructor.setTimePreferences(day, halfHour, preference);
	}
}
