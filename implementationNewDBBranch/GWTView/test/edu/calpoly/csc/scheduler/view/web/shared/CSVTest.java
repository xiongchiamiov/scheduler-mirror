package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import junit.framework.TestCase;

import edu.calpoly.csc.scheduler.model.CSVExporter;
import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.Schedule;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.*;

/****
 * 
 * This is skeleton testing class for CSVExporter.java. It outlines the unit
 * test plan for testing the method CSVExporter.export. For unit test details,
 * see the documentation for the testExport method below.
 * 
 * The path to this testing class parallels exactly the path to the class under
 * test. I.e., the full path to CSVExporter.java is
 * 
 * <pre>
 * scheduler / testing / implementation / GWTView / src / edu / calpoly / csc
 * 		/ scheduler / view / web / shared / CSVExporterTest.java
 * </pre>
 * 
 * The full path to this file, CSVExporterTest.java, is
 * 
 * <pre>
 * scheduler / testing / testing / implementation / GWTView / src / edu / calpoly
 * 		/ csc / scheduler / view / web / shared / CSVExporterTest.java
 * </pre>
 * 
 * There is a considerable amount of superfluous directory structure in these
 * paths. This can be cleaned when and if there is an overall refactoring of the
 * project repository.
 * 
 * @author: Gene Fisher (gfisher@calpoly.edu)
 * @version: 17feb12
 */

// public class CSVTest extends ModelTestCase {
public class CSVTest extends TestCase {

	/**
	 * Method testExport is the unit testing method that calls
	 * CSVExporter.export. Here is an outline of the unit test plan:
	 * 
	 * <pre>
	 *  Test
	 *  Case    Input               Output              Remarks
	 * ====================================================================
	 *   1      empty schedule      empty CSV file      Null case
	 * 
	 *   2      schedule with       CSV file with       1 item case
	 *          1 scheduled item    that item, and
	 *                              nothing else
	 * 
	 *   3      schedule with       CSV file with       2 item case
	 *          2 scheduled items   those items, and
	 *                              nothing else
	 * 
	 *   4 - N  schedule with a     CSV files with      a variety of N
	 *          wide variety of     those items, and    different test
	 *          scheduled items,    nothing else,       cases
	 *          based on range      each of which
	 *          testing of the      matches the
	 *          scheduled item      corresponding
	 *          data fields         expected output
	 *                              file
	 * 
	 *  N+1     schedule with       CSV file that       stress test
	 *          1000 items of       matches expected
	 *          different data      output file
	 *          range values
	 * </pre>
	 */
	public void testExport() {

		//
		// Foreach test case, do the following:
		// Set up the input data by calling a method to construct and
		// populate a schedule with the desired number of items.

		// Wrap that schedule in a Model object, since CSVExporter.export
		// needs a model for its input.

		// Call CSVExporter.export with the input for this test case.

		// Capture the string-valued output and write it to an output file.

		// Compare the actual output value from the method with the expected
		// output, which is stored in a pre-defined expected output file.

		// It's common JUnit practice to implement each test case in a separate
		// method, but this is not required. You could have one parameterized,
		// helper method, like that sketched out in the testExportCase() method
		// below.
		//
	}

	/**
	 * Call CSVExporter.export with a schedule containing the given
	 * numberOfItems. Data values in each item will contain ranges of values for
	 * scheduled item data fields, per standard testing practices of data range
	 * testing.
	 * 
	 * Capture the string result of the export method and write it to a file.
	 * Compare the actual output file with the expected output in the given
	 * expectedOutputFile.
	 */
	private void testExportCase(int numberOfItems, String expectedOutputFile) {
		// CSV Used model, which calls get on Locations, Courses, Instructors,
		// Schedule Item, DirtyScheduleItem

		// ...
	}

	public void testDayPrefs() throws DatabaseException {

		// Setup
		Model model = new Model();
		model.createTransientDocument("TestDayPrefs", 14, 44).insert();
		Collection<Document> docs = model.findAllDocuments();
		Document doc = model.findAllDocuments().iterator().next();

		//Initial course creation
		model.createTransientCourse("Name" + Integer.toString(0),
				"Catalog" + Integer.toString(0), "dept" + Integer.toString(0),
				"WTU" + Integer.toString(0), "SCU" + Integer.toString(0),
				"Numsec" + Integer.toString(0), "Type" + Integer.toString(0),
				"maxenrollment" + Integer.toString(0),
				"numHalfHours" + Integer.toString(0), true).setDocument(doc)
				.insert();

		Collection<Course> courseList;

		courseList = doc.getCourses();
		for (Course course : courseList) {
			
			//Add additional data
			Set<String> usedEquipment = new HashSet<String>();
			usedEquipment.add("TestEquipment");

			Set<Day> dayPatternsA = new HashSet<Day>();
			Set<Day> dayPatternsB = new HashSet<Day>();

			dayPatternsA.add(Day.FRIDAY);
			dayPatternsA.add(Day.WEDNESDAY);

			dayPatternsB.add(Day.THURSDAY);
			dayPatternsB.add(Day.SUNDAY);

			Collection<Set<Day>> dayPatternsList = new ArrayList<Set<Day>>();
			dayPatternsList.add(dayPatternsA);
			dayPatternsList.add(dayPatternsB);

			course.setUsedEquipment(usedEquipment);
			course.setDayPatterns(dayPatternsList);
			course.setTetheredToLecture(true);
			course.setLecture(model.createTransientCourse("Name" + Integer.toString(4),
					"Catalog" + Integer.toString(0), "dept" + Integer.toString(0),
					"WTU" + Integer.toString(0), "SCU" + Integer.toString(0),
					"Numsec" + Integer.toString(0), "Type" + Integer.toString(0),
					"maxenrollment" + Integer.toString(0),
					"numHalfHours" + Integer.toString(0), true));
			
			assertEquals("Name0", course.getName());

			/* Currently fails due to getUsedEquipment and dayDayPatterns not being saved when set */
			assertEquals("TestEquipment", course.getUsedEquipment());
			assertEquals(dayPatternsList, course.getDayPatterns());
		}

	}

	public static void main(String[] args) throws DatabaseException, IOException {
		Model model = new Model();
		model.createTransientDocument("TestDoc", 14, 44).insert();
		Collection<Document> docs = model.findAllDocuments();
		Document doc = model.findAllDocuments().iterator().next();

		CSVExporter export = new CSVExporter();
	
			generateCourses(10, model, doc);
			generateInstructors(10, model, doc);
			generateLocations(10, model, doc);
			generateSchedule(model, doc);
			generateScheduleItems(10, model, doc);

			System.out.println(export.export(model, doc));
			// System.out.println(export.exportTest(model,
			// model.createTransientDocument("Test", 14, 44).insert()));
	
		
	}


	private static void generateInstructors(int numberOfInstructors,
			Model model, Document doc) throws DatabaseException {

		for (int index = 0; index <= numberOfInstructors; index++) {

			model.createTransientInstructor("Fname" + Integer.toString(index),
					"LName" + Integer.toString(index),
					"Uname" + Integer.toString(index), Integer.toString(index),
					true).setDocument(doc).insert();
		}

		Collection<Course> courseList;
		Collection<Instructor> insList;

		courseList = doc.getCourses();
		insList = doc.getInstructors();

		

		
		for (Instructor ins : insList) {

			HashMap<Day, HashMap<Integer, Integer>> timePrefs = new HashMap<Day, HashMap<Integer, Integer>>();

			for (Day day : Day.values())
				timePrefs.put(day, new HashMap<Integer, Integer>());

			int[][] tprefs = new int[7][48]; // Column is days, row is half
												// hours
			for (int j = 0; j < 7; j++) {
				for (int k = 0; k < 48; k++) {
					tprefs[j][k] = 3;
				}
			}

			HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();

			int i = 0;
			for (Course course : courseList)
				coursePrefs.put(course.getID(), ++i % 4);
			
			ins.setCoursePreferences(coursePrefs);
			ins.setTimePreferences(tprefs);
		}

	}

	private static void generateCourses(int numberOfCourses, Model model,
			Document doc) throws DatabaseException {

		for (int index = 0; index < numberOfCourses; index++) {

			model.createTransientCourse("Name" + Integer.toString(index),
					"Catalog" + Integer.toString(index),
					"dept" + Integer.toString(index),
					"WTU" + Integer.toString(index),
					"SCU" + Integer.toString(index),
					"Numsec" + Integer.toString(index),
					"Type" + Integer.toString(index),
					"maxenrollment" + Integer.toString(index),
					"numHalfHours" + Integer.toString(index), true)
					.setDocument(doc).insert();

		}

		Collection<Course> courseList;

		courseList = doc.getCourses();
		for (Course course : courseList) {
			Set<String> usedEquipment = new HashSet<String>();
			usedEquipment.add("TestEquipment");

			Set<Day> dayPatternsA = new HashSet<Day>();
			Set<Day> dayPatternsB = new HashSet<Day>();

			dayPatternsA.add(Day.FRIDAY);
			dayPatternsA.add(Day.WEDNESDAY);

			dayPatternsB.add(Day.FRIDAY);
			dayPatternsB.add(Day.SUNDAY);

			Collection<Set<Day>> dayPatternsList = new ArrayList<Set<Day>>();
			dayPatternsList.add(dayPatternsA);
			dayPatternsList.add(dayPatternsB);

			course.setUsedEquipment(usedEquipment);
			course.setDayPatterns(dayPatternsList);
			course.setTetheredToLecture(false);
			course.setLecture(null);

		}

	}

	private static void generateLocations(int numberOfLocations, Model model,
			Document doc) throws DatabaseException {

		Set<String> equipment = new HashSet<String>();
		equipment.add("Projector");

		for (int index = 0; index <= numberOfLocations; index++) {

			model.createTransientLocation(Integer.toString(index),
					Integer.toString(index), Integer.toString(index), true)
					.setDocument(doc).insert();
		}

		Collection<Location> locationList;
		locationList = doc.getLocations();

		for (Location location : locationList) {
			location.setProvidedEquipment(equipment);
		}

	}

	private static void generateSchedule(Model model, Document doc)
			throws DatabaseException {

		model.createTransientSchedule().setDocument(doc).insert();

	}

	private static void generateScheduleItems(int num, Model model, Document doc)
			throws DatabaseException {
		Schedule schedule = doc.getSchedules().iterator().next();

		Set<Day> dayPatterns = new HashSet<Day>();
		dayPatterns.add(Day.FRIDAY);

		Collection<Course> courses;

		courses = doc.getCourses();
		Collection<Instructor> instructors = doc.getInstructors();
		Collection<Location> locations = doc.getLocations();
		Iterator<Course> courseIter = courses.iterator();
		Iterator<Instructor> instructorIter = instructors.iterator();
		Iterator<Location> locationIter = locations.iterator();

		for (int index = 0; index < num; index++) {

			ScheduleItem item = model.createTransientScheduleItem(index,
					dayPatterns, 14, 29, true, false);
			item.setInstructor(instructorIter.next());
			item.setCourse(courseIter.next());
			item.setLocation(locationIter.next());
			item.setSchedule(schedule);

			item.setSchedule(schedule);
			item.insert();
		}

	}
}
