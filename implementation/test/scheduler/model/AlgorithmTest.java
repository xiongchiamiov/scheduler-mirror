package scheduler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import scheduler.model.algorithm.BadInstructorDataException;
import scheduler.model.algorithm.GenerateEntryPoint;
import scheduler.model.db.DatabaseException;

public abstract class AlgorithmTest extends ModelTestCase {
	
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm

	public static void testThing() throws DatabaseException, BadInstructorDataException {
		Model model = new Model();
		
		Document doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
		
		List<Course> courses = generateCourseList(model, doc);
		
		List<Instructor> instructors = generateInstructorList(model, doc, courses);
		
		List<Location> locations = generateLocationList(model, doc);
		
		Vector<ScheduleItem> sids = new Vector<ScheduleItem>();
		//ScheduleItem schedItem = new ScheduleItem(model, null);
		//sids.add(schedItem);
		
		System.err.println("Starting schedule generation...");
		
		long start = System.currentTimeMillis();
		
	    Vector<ScheduleItem> sis = GenerateEntryPoint.generate(model, doc, sids, courses, instructors, locations);
	    
	    long end = System.currentTimeMillis();
	    
	    System.err.println("Schedule generation complete in: " + ((end - start) / 1000) + " seconds");
	    
	    for (ScheduleItem si : sis) {
	    	System.err.println(si);
	    }
	    System.out.println("Second run with schedule items");
	    courses.addAll(generateAdditionalCourses(model, doc));
	    Vector<ScheduleItem> items = GenerateEntryPoint.generate(model, doc, sis, courses, instructors, locations);
	  
	    System.err.println("************************************************");
	    for (ScheduleItem si : items) {
	    	System.err.println(si);
	    }
	    
	    model.closeModel();
	}
	
	public static List<Course> generateCourseList(Model model, Document doc) throws DatabaseException {
		List<Course> courses = new ArrayList<Course>();
		
		Course course = model.createTransientCourse("Test1", "101", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		Course lab = model.createTransientCourse("Test1 - Lab", "101", "CSC", "4", "4", "2", "LAB", "60", "6", true);
		ModelTestUtility.addDayPattern(lab, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		lab.setLecture(course);
		lab.setTetheredToLecture(Boolean.TRUE);
		lab.setDocument(doc).insert();
		courses.add(lab);
		
		course = model.createTransientCourse("Test2", "102", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		course = model.createTransientCourse("Test3", "103", "CSC", "4", "4", "1", "LEC", "30", "6", true);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		course = model.createTransientCourse("IND COURSE", "400", "CSC", "1", "1", "1", "IND", "500", "0", true);
		course.setDocument(doc).insert();
		courses.add(course);
		
		return courses;
	}
	
	public static List<Course> generateAdditionalCourses(Model model, Document doc) throws DatabaseException {
		List<Course> courses = new ArrayList<Course>();
		
		Course course = model.createTransientCourse("Test4", "480", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		Course lab = model.createTransientCourse("Test4 - Lab", "480", "CSC", "4", "4", "2", "LAB", "60", "6", true);
		ModelTestUtility.addDayPattern(lab, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		lab.setLecture(course);
		lab.setTetheredToLecture(Boolean.TRUE);
		lab.setDocument(doc).insert();
		courses.add(lab);
		
		course = model.createTransientCourse("Test5", "484", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		course = model.createTransientCourse("Test6", "406", "CSC", "4", "4", "1", "LEC", "30", "6", true);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		return courses;
	}
	
	public static List<Instructor> generateInstructorList(Model model, Document doc, List<Course> courses) throws DatabaseException {
		List<Instructor> instructors = new ArrayList<Instructor>();
		
		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		coursePrefs.put(courses.get(0).getID(), 10); //Test1
		coursePrefs.put(courses.get(1).getID(), 10); //Test1 - Lab
		coursePrefs.put(courses.get(2).getID(), 10); //Test2
		coursePrefs.put(courses.get(3).getID(), 0);  //Test3
		coursePrefs.put(courses.get(4).getID(), 10); //IND COURSE
		
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
		
		Location location = model.createTransientLocation("roomlol", "LEC", "100", true);
		location.setDocument(doc).insert();

		locations.add(location);
		return locations;
	}
}
