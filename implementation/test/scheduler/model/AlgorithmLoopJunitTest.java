package scheduler.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import scheduler.model.algorithm.BadInstructorDataException;
import scheduler.model.algorithm.GenerateEntryPoint;
import scheduler.model.db.DatabaseException;

public abstract class AlgorithmLoopJunitTest extends ModelTestCase {
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	private Model model;
	private Document doc;
	private List<Course> courses;
	private List<Instructor> instructors;
	private List<Location> locations;
	private Vector<ScheduleItem> sids;
	
	public void setUp() throws DatabaseException {
        model = new Model();
		
		doc = model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", START_HALF_HOUR, END_HALF_HOUR);
		
		courses = generateCourseList(model, doc);
		
		instructors = generateInstructorList(model, doc, courses);
		
		locations = generateLocationList(model, doc);
		
		sids = new Vector<ScheduleItem>();
	}
	
	private List<Course> generateCourseList(Model model, Document doc) throws DatabaseException {
		List<Course> courses = new ArrayList<Course>();
		
		Course course = model.createTransientCourse("GRC", "200", "Graphics", "3", "4", "1", "LEC", "20", "", true);
		ModelTestUtility.addDayPattern(course, Day.MONDAY);
		course.setDocument(doc).insert();
		courses.add(course);
		
		return courses;
	}
	
	private List<Instructor> generateInstructorList(Model model, Document doc, List<Course> courses) throws DatabaseException {
		List<Instructor> instructors = new ArrayList<Instructor>();
		
//		HashMap<Integer, Integer> coursePrefs1 = new HashMap<Integer, Integer>();
//		coursePrefs1.put(courses.get(0).getID(), 10);
//		coursePrefs1.put(courses.get(1).getID(), 10);
//		coursePrefs1.put(courses.get(2).getID(), 10);
//		coursePrefs1.put(courses.get(3).getID(), 0);
//		coursePrefs1.put(courses.get(4).getID(), 0);
//		coursePrefs1.put(courses.get(5).getID(), 5);
//		
//		HashMap<Integer, Integer> coursePrefs2 = new HashMap<Integer, Integer>();
//		coursePrefs2.put(courses.get(0).getID(), 0);
//		coursePrefs2.put(courses.get(1).getID(), 0);
//		coursePrefs2.put(courses.get(2).getID(), 0);
//		coursePrefs2.put(courses.get(3).getID(), 0);
//		coursePrefs2.put(courses.get(4).getID(), 10);
//		coursePrefs2.put(courses.get(5).getID(), 10);
//		
//		Instructor instructor = model.createTransientInstructor("Evan", "Ovadia", "eovadia", "20", true)
//				.setTimePreferences(Instructor.createDefaultTimePreferences())
//				.setCoursePreferences(coursePrefs1)
//				.setDocument(doc).insert();
//		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
//		ModelTestUtility.setPreferenceBlocks(instructor, 3, 20, 30, Day.values());
//		instructors.add(instructor);
//		
//		instructor = model.createTransientInstructor("Adam", "Armstrong", "abarmstr", "8", true)
//				.setTimePreferences(Instructor.createDefaultTimePreferences())
//				.setCoursePreferences(coursePrefs2)
//				.setDocument(doc).insert();
//		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
//		ModelTestUtility.setPreferenceBlocks(instructor, 10, 14, 20, Day.values());
//		instructors.add(instructor);
//		
//		instructor = model.createTransientInstructor("Kayleen", "Scanlon", "kscanlon", "12", true)
//				.setTimePreferences(Instructor.createDefaultTimePreferences())
//				.setCoursePreferences(coursePrefs2)
//				.setDocument(doc).insert();
//		ModelTestUtility.setPreferenceBlocks(instructor, 0, 0, 48, Day.values());
//		ModelTestUtility.setPreferenceBlocks(instructor, 10, 24, 36, Day.values());
//		instructors.add(instructor);
		
		return instructors;
	}
	
	private List<Location> generateLocationList(Model model, Document doc) throws DatabaseException {
		List <Location> locations = new ArrayList<Location>();
//		
//		Location location = model.createTransientLocation("roomlol", "LEC", "100", true);
//		location.setDocument(doc).insert();
//		locations.add(location);
//		
//		location = model.createTransientLocation("14-249", "LEC", "30", true);
//		location.setDocument(doc).insert();
//		locations.add(location);
//		
//		location = model.createTransientLocation("14-301", "LAB", "45", true);
//		location.setDocument(doc).insert();
//		locations.add(location);
//		
		return locations;
	}

	//TEST METHODS
	
	public void testGenerateBasic() throws DatabaseException, BadInstructorDataException{
		Vector<ScheduleItem> items = GenerateEntryPoint.generate(model, doc, sids, courses, instructors, locations);
		
		assertFalse(items.isEmpty());
	}
}
