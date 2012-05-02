package scheduler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import scheduler.model.algorithm.BadInstructorDataException;
import scheduler.model.algorithm.GenerateEntryPoint;
import scheduler.model.db.DatabaseException;

public abstract class AlgorithmJUnitTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	private Model model;
	private Document doc;
	private Schedule schedule;
	private List<Course> courses;
	private List<Instructor> instructors;
	private List<Location> locations;
	private Vector<ScheduleItem> sids;
	
	public void setUp() throws DatabaseException {
        model = new Model();
		
		doc = model.createAndInsertDocumentWithTBAStaffAndSchedule("doc", START_HALF_HOUR, END_HALF_HOUR);
		
		schedule = model.createTransientSchedule().setDocument(doc).insert();
		
		courses = generateCourseList(model, doc);
		
		instructors = generateInstructorList(model, doc, courses);
		
		locations = generateLocationList(model, doc);
		
		sids = new Vector<ScheduleItem>();
	}
	
	private List<Course> generateCourseList(Model model, Document doc) throws DatabaseException {
		List<Course> courses = new ArrayList<Course>();
		
		Course course = model.createTransientCourse("Test1", "101", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		Course lab = model.createTransientCourse("Test1 - Lab", "101", "CSC", "4", "4", "2", "LAB", "60", "6", true);
		ModelTestUtility.addDayPattern(lab, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		lab.setLecture(course);
		lab.setTetheredToLecture(Boolean.TRUE);
		lab.setDocument(doc).insert();
		courses.add(lab);
		
		course = model.createTransientCourse("Test2", "102", "CSC", "4", "4", "2", "LEC", "60", "6", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		course = model.createTransientCourse("Test3", "103", "CSC", "4", "4", "1", "LEC", "30", "6", true);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		course = model.createTransientCourse("Test4", "300", "CSC", "8", "4", "2", "LEC", "20", "12", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY, Day.WEDNESDAY, Day.FRIDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		course = model.createTransientCourse("Test5", "305", "CSC", "4", "4", "4", "LEC", "40", "12", true);
		ModelTestUtility.addDayPattern(course, Day.TUESDAY, Day.THURSDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		return courses;
	}
	
	private List<Instructor> generateInstructorList(Model model, Document doc, List<Course> courses) throws DatabaseException {
		List<Instructor> instructors = new ArrayList<Instructor>();
		
		HashMap<Integer, Integer> coursePrefs1 = new HashMap<Integer, Integer>();
		coursePrefs1.put(courses.get(0).getID(), 10);
		coursePrefs1.put(courses.get(1).getID(), 10);
		coursePrefs1.put(courses.get(2).getID(), 10);
		coursePrefs1.put(courses.get(3).getID(), 0);
		coursePrefs1.put(courses.get(4).getID(), 0);
		coursePrefs1.put(courses.get(5).getID(), 5);
		
		HashMap<Integer, Integer> coursePrefs2 = new HashMap<Integer, Integer>();
		coursePrefs2.put(courses.get(0).getID(), 0);
		coursePrefs2.put(courses.get(1).getID(), 0);
		coursePrefs2.put(courses.get(2).getID(), 0);
		coursePrefs2.put(courses.get(3).getID(), 0);
		coursePrefs2.put(courses.get(4).getID(), 10);
		coursePrefs2.put(courses.get(5).getID(), 10);
		
		Instructor instructor = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setCoursePreferences(coursePrefs1)
				.setDocument(doc).insert();
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
		instructors.add(instructor);
		
		instructor = model.createTransientInstructor("Adam", "Armstrong", "abarmstr", "8", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setCoursePreferences(coursePrefs2)
				.setDocument(doc).insert();
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 10, 14, 20, Day.values());
		instructors.add(instructor);
		
		instructor = model.createTransientInstructor("Kayleen", "Scanlon", "kscanlon", "12", true)
				.setTimePreferences(Instructor.createDefaultTimePreferences())
				.setCoursePreferences(coursePrefs2)
				.setDocument(doc).insert();
		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
		ModelTestUtility.setPreferenceBlocks(instructor, 10, 24, 36, Day.values());
		instructors.add(instructor);
		
		return instructors;
	}
	
	private List<Location> generateLocationList(Model model, Document doc) throws DatabaseException {
		List <Location> locations = new ArrayList<Location>();
		
		Location location = model.createTransientLocation("roomlol", "LEC", "100", true);
		location.setDocument(doc).insert();
		locations.add(location);
		
		location = model.createTransientLocation("14-249", "LEC", "30", true);
		location.setDocument(doc).insert();
		locations.add(location);
		
		location = model.createTransientLocation("14-301", "LAB", "45", true);
		location.setDocument(doc).insert();
		locations.add(location);
		
		return locations;
	}

	//TEST METHODS
	
	public void testGenerateBasic() throws DatabaseException, BadInstructorDataException{
		Vector<ScheduleItem> items = GenerateEntryPoint.generate(model, schedule, sids, courses, instructors, locations);
		
		assertFalse(items.isEmpty());
	}
}
