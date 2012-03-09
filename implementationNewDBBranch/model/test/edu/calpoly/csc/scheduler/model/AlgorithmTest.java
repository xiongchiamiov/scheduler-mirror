package edu.calpoly.csc.scheduler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.Schedule;
import edu.calpoly.csc.scheduler.model.algorithm.Generate;
import edu.calpoly.csc.scheduler.model.algorithm.ScheduleItemDecorator;
import edu.calpoly.csc.scheduler.model.algorithm.Week;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.simple.DBSchedule;
import edu.calpoly.csc.scheduler.model.db.simple.Database;

public class AlgorithmTest {
	
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm

	public static void main(String[] args) throws DatabaseException {
		Model model = new Model();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		
		doc.setStaffInstructor(model.createTransientInstructor("STAFF", "STAFF", "STAFF", "1000000", true)
				.setDocument(doc).insert());
		
		doc.setTBALocation(model.createTransientLocation("TBA", "Lecture", "1000000", true)
				.setDocument(doc).insert());
		
		Schedule schedule = model.createTransientSchedule().setDocument(doc).insert();
		
		List<Course> courses = generateCourseList(model, doc);
		
		List<Instructor> instructors = generateInstructorList(model, doc, courses);
		
		instructors.add(doc.getStaffInstructor());
		
		List<Location> locations = generateLocationList(model, doc);
		
		locations.add(doc.getTBALocation());
		
		Vector<ScheduleItem> sids = new Vector<ScheduleItem>();
		
		System.err.println("Starting schedule generation...");
		
		long start = System.currentTimeMillis();
		
	    Generate.generate(model, schedule, sids, courses, instructors, locations);
	    
	    long end = System.currentTimeMillis();
	    
	    System.err.println("Schedule generation complete in: " + ((end - start) / 1000) + " seconds");
	}
	
	public static List<Course> generateCourseList(Model model, Document doc) throws DatabaseException {
		List<Course> courses = new ArrayList<Course>();
		
		Course course = model.createTransientCourse("Test1", "101", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		course = model.createTransientCourse("Test2", "102", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		return courses;
	}
	
	public static List<Instructor> generateInstructorList(Model model, Document doc, List<Course> courses) throws DatabaseException {
		List<Instructor> instructors = new ArrayList<Instructor>();
		
		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		coursePrefs.put(courses.get(0).getID(), 10);
		coursePrefs.put(courses.get(1).getID(), 10);
		
		Instructor instructor = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setCoursePreferences(coursePrefs)
				.setDocument(doc).insert();
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		instructors.add(instructor);
		
		return instructors;
	}
	
	public static List<Location> generateLocationList(Model model, Document doc) throws DatabaseException {
		List <Location> locations = new ArrayList<Location>();
		
		Location location = model.createTransientLocation("roomlol", "LEC", "30", true);
		location.setDocument(doc).insert();
		locations.add(location);
		
		return locations;
	}
}
