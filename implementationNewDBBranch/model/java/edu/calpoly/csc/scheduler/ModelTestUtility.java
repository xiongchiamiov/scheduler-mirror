package edu.calpoly.csc.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;

public class ModelTestUtility {
	public static Course createCourse(Model model, Document document) {
		return model.assembleCourse(document, "Test", "101", "CSC", "4", "4", "1",
				"LEC", "60", "6", new HashSet<String>(),
				new ArrayList<Set<Day>>(), true);
	}
	
	public static Location createLocation(Model model, Document document) {
		return model.assembleLocation(document, "123", "LEC", "60", new HashSet<String>());
	}
	
	public static Instructor createBasicInstructor(Model model, Document document) {
		return model.assembleInstructor(document, "TestFirst", "TestLast", "testid", "4", new HashMap<Day, HashMap<Integer, Integer>>(), new HashMap<Integer, Integer>());
	}
	
	public static Instructor insertInstructorWPrefs(Model model, Document doc) {
		int courseID1 = model.insertCourse(model.assembleCourse(doc, "Graphics", "201", "GRC", "10", "20", "2", "LEC", "20", "6", new TreeSet<String>(), new LinkedList<Set<Day>>(), true)).getID();
		int courseID2 = model.insertCourse(model.assembleCourse(doc, "Graphics: The Return", "202", "GRC", "10", "20", "2", "LEC", "20", "6", new TreeSet<String>(), new LinkedList<Set<Day>>(), true)).getID();
		
		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		coursePrefs.put(courseID1, 2);
		coursePrefs.put(courseID2, 3);
		
		HashMap<Day, HashMap<Integer, Integer>> timePrefs = createSampleTimePreferences(doc);
		
		return model.insertInstructor(model.assembleInstructor(doc, "Evan", "Ovadia", "eovadia", "20", new HashMap<Day, HashMap<Integer,Integer>>(), coursePrefs));
	}
	
	public static boolean coursesContentsEqual(Course a, Course b) {
		if (!a.getCatalogNumber().equals(b.getCatalogNumber()))
			return false;
		if (!a.getDayPatterns().equals(b.getDayPatterns()))
			return false;
		if (!a.getDepartment().equals(b.getDepartment()))
			return false;
		if (a.getLectureID() != b.getLectureID())
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

//		if (!a.getCoursePreferences().keySet().containsAll(b.getCoursePreferences().keySet()))
//			return false;
//		if (!b.getCoursePreferences().keySet().containsAll(a.getCoursePreferences().keySet()))
//			return false;
//		for (Entry<Integer, Integer> entry : a.getCoursePreferences().entrySet())
//			if (!b.getCoursePreferences().get(entry.getKey()).equals(entry.getValue()))
//				return false;
//
//		if (!a.getTimePreferences().keySet().containsAll(b.getTimePreferences().keySet()))
//			return false;
//		if (!b.getTimePreferences().keySet().containsAll(a.getTimePreferences().keySet()))
//			return false;
//		for (Entry<Day, HashMap<Integer, Integer>> entry : a.getTimePreferences().entrySet()) {
//			HashMap<Integer, Integer> aPrefsForDay = entry.getValue();
//			HashMap<Integer, Integer> bPrefsForDay = b.getTimePreferences().get(entry.getKey());
//
//			if (!aPrefsForDay.keySet().containsAll(bPrefsForDay.keySet()))
//				return false;
//			if (!bPrefsForDay.keySet().containsAll(aPrefsForDay.keySet()))
//				return false;
//			for (Entry<Integer, Integer> entry2 : aPrefsForDay.entrySet())
//				if (!bPrefsForDay.get(entry2.getKey()).equals(entry2.getValue()))
//					return false;
//		}
		
		if (!a.getCoursePreferences().equals(b.getCoursePreferences()))
			return false;
		if (!a.getTimePreferences().equals(b.getTimePreferences()))
			return false;
		return true;
	}
	
	public static HashMap<Day, HashMap<Integer, Integer>> createSampleTimePreferences(Document document) {
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
}
