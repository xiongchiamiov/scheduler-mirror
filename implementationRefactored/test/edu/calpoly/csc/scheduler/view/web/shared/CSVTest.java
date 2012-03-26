package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

		for (int items = 0; items < 100; items++) {
			try {
				testExportCase(items, "ExportTest" + items);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	 * 
	 * @throws DatabaseException
	 * @throws IOException
	 */
	private void testExportCase(int numberOfItems, String expectedOutputFile)
			throws DatabaseException, IOException {
/*
		try {
			Model model = new Model();
			model.createTransientDocument("Doc" + numberOfItems, 14, 44)
					.insert();
			Document doc = model.findAllDocuments().iterator().next();

			CSVExporter export = new CSVExporter();

			generateCourses(numberOfItems, model, doc);
			generateInstructors(numberOfItems, model, doc);
			generateLocations(numberOfItems, model, doc);
			generateSchedule(model, doc);
			generateScheduleItems(numberOfItems, model, doc);

			File file = new File(
					"test/edu/calpoly/csc/scheduler/view/web/shared/CSVExporterOutput/exportTestCase"
							+ numberOfItems);

			Writer output = new BufferedWriter(new FileWriter(file));
			try {
				output.write(export.export(model, doc));
			} finally {
				output.close();
			}

			String out = null;
			String path = file.getAbsolutePath();
			Process p = Runtime
					.getRuntime()
					.exec("diff "
							+ "test/edu/calpoly/csc/scheduler/view/web/shared/CSVExporterOutput/exportTestCase"
							+ numberOfItems
							+ " "
							+ "test/edu/calpoly/csc/scheduler/view/web/shared/CSVExporterOracle/exportCase"
							+ numberOfItems);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			out = stdInput.readLine();
			assertEquals(null, out);

		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
	}

	public void testDayPrefs() throws DatabaseException {

		// Setup
		Model model = new Model();
		model.createTransientDocument("TestDayPrefs", 14, 44).insert();
		Collection<Document> docs = model.findAllDocuments();
		Document doc = model.findAllDocuments().iterator().next();

		model.insertEquipmentType("TestEquipment");

		// Initial course creation
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

			// Add additional data
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
			course.setLecture(model
					.createTransientCourse("Name" + Integer.toString(4),
							"Catalog" + Integer.toString(0),
							"dept" + Integer.toString(0),
							"WTU" + Integer.toString(0),
							"SCU" + Integer.toString(0),
							"Numsec" + Integer.toString(0),
							"Type" + Integer.toString(0),
							"maxenrollment" + Integer.toString(0),
							"numHalfHours" + Integer.toString(0), true)
					.setDocument(doc).insert());

			assertEquals("Name0", course.getName());

			course.update();

			assertEquals(course.getUsedEquipment().size(), 1);
			assertEquals("TestEquipment", course.getUsedEquipment().iterator()
					.next());
			assertEquals(dayPatternsList, course.getDayPatterns());
		}

	}

	public static void main(String[] args) throws DatabaseException,
			IOException {
		/*
		 * Model model = new Model(); model.createTransientDocument("TestDoc",
		 * 14, 44).insert(); Collection<Document> docs =
		 * model.findAllDocuments(); Document doc =
		 * model.findAllDocuments().iterator().next();
		 * 
		 * CSVExporter export = new CSVExporter();
		 * 
		 * generateCourses(10, model, doc); generateInstructors(10, model, doc);
		 * generateLocations(10, model, doc); generateSchedule(model, doc);
		 * generateScheduleItems(10, model, doc);
		 * 
		 * System.out.println(export.export(model, doc)); //
		 * System.out.println(export.exportTest(model, //
		 * model.createTransientDocument("Test", 14, 44).insert()));
		 */

		// generateTestData();
	}

	/**
	 * @param numberOfInstructors
	 * @param model
	 * @param doc
	 * @throws DatabaseException
	 */
	/**
	 * @param numberOfInstructors
	 * @param model
	 * @param doc
	 * @throws DatabaseException
	 */
	private static void generateInstructors(int numberOfInstructors,
			Model model, Document doc) throws DatabaseException {

		Collection<Course> courseList;
		courseList = doc.getCourses();

		for (int index = 0; index <= numberOfInstructors; index++) {
			// Pseudo-random number generators
			Random nameGen = new Random(index);
			Random timePrefGen = new Random(numberOfInstructors);

			// Generate names
			String firstName = "";
			String lastName = "";

			for (int nameChar = 0; nameChar < index; nameChar++) {
				firstName += String.valueOf((char) ('a' + nameGen.nextInt(26)));
				lastName += String.valueOf((char) ('a' + nameGen.nextInt(26)));
			}

			String userName = lastName + firstName;

			String maxWTU = Integer.toString(index * 10);
			Boolean isSchedulable = index % 2 == 0 ? true : false;

			// Create instructor
			Instructor ins = model.createTransientInstructor(firstName,
					lastName, userName, maxWTU, isSchedulable);

			// Generate Time preferences
			HashMap<Day, HashMap<Integer, Integer>> timePrefs = new HashMap<Day, HashMap<Integer, Integer>>();

			for (Day day : Day.values())
				timePrefs.put(day, new HashMap<Integer, Integer>());

			int[][] tprefs = new int[7][48]; // Column is days, row is half
												// hours
			for (int j = 0; j < 7; j++) {
				for (int k = 0; k < 48; k++) {
					tprefs[j][k] = timePrefGen.nextInt(4);
				}
			}
			ins.setTimePreferences(tprefs);

			// Generate Course Preferences
			HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
			int i = 0;
			for (Course course : courseList)
				coursePrefs.put(course.getID(), ++i % 4);

			ins.setCoursePreferences(coursePrefs);

			// Insert instructor into document
			ins.setDocument(doc).insert();

		}

	}

	private static void generateCourses(int numberOfCourses, Model model,
			Document doc) throws DatabaseException {

		for (int index = 0; index < numberOfCourses; index++) {
			Random nameGen = new Random(index);
			Random equipGen = new Random(index);

			String name = "";
			String catalog = "";
			String dept = "";
			String wtu = Integer.toString(index * 15);
			String scu = Integer.toString(index * 15);
			String numSec = Integer.toString(index);
			int types = Course.CourseType.values().length;
			String type = Course.CourseType.values()[nameGen.nextInt(types - 1)]
					.toString();
			String maxEnrollment = Integer.toString(index * 15);
			String numHalfHours = Integer.toString(index * 15);
			Boolean isScheduleable = index % 2 == 0 ? true : false;

			for (int nameChar = 0; nameChar < index; nameChar++) {
				name += String.valueOf((char) ('a' + nameGen.nextInt(26)));
				catalog += String.valueOf((char) ('a' + nameGen.nextInt(26)));
				dept += String.valueOf((char) ('a' + nameGen.nextInt(26)));
			}

			// Generate initial data

			model.createTransientCourse(name, catalog, dept, wtu, scu, numSec,
					type, maxEnrollment, numHalfHours, isScheduleable)
					.setDocument(doc).insert();

			Course course = model.findCoursesForDocument(doc).iterator().next();
			// Generate Used equipment
			Set<String> usedEquipment = new HashSet<String>();
			int bound = index == 0 ? 0 : equipGen.nextInt(index);

			for (int i = 0; i < bound; i++) {
				String equip = "";
				for (int nameChar = 0; nameChar < index; nameChar++)
					equip += String
							.valueOf((char) ('a' + equipGen.nextInt(26)));
				usedEquipment.add(equip);
			}
			course.setUsedEquipment(usedEquipment);

			// Generate Day patterns TODO
			Set<Day> dayPatternsA = new HashSet<Day>();
			Set<Day> dayPatternsB = new HashSet<Day>();

			dayPatternsA.add(Day.FRIDAY);
			dayPatternsA.add(Day.WEDNESDAY);
			
			dayPatternsB.add(Day.FRIDAY);
			dayPatternsB.add(Day.SUNDAY);

			Collection<Set<Day>> dayPatternsList = new ArrayList<Set<Day>>();
			dayPatternsList.add(dayPatternsA);
			dayPatternsList.add(dayPatternsB);
			course.setDayPatterns(dayPatternsList);

			// Set tethered to lecture TODO
			course.setTetheredToLecture(false);
			course.setLecture(null);

		}

	}

	private static void generateLocations(int numberOfLocations, Model model,
			Document doc) throws DatabaseException {

		for (int index = 0; index < numberOfLocations; index++) {
			Random equipGen = new Random(index);
			// TODO Increase randomness for type

			String room = Integer.toString(index * 15);
			String type = Integer.toString(index * 15);
			String maxOccupancy = Integer.toString(index * 15);
			Boolean isScheduleable = index % 2 == 0 ? true : false;
			model.createTransientLocation(room, type, maxOccupancy,
					isScheduleable).setDocument(doc).insert();

			Location loc = model.findLocationsForDocument(doc).iterator()
					.next();

			Set<String> equipment = new HashSet<String>();
			int bound = index == 0 ? 0 : equipGen.nextInt(index);

			for (int i = 0; i < bound; i++) {
				String equip = "";
				for (int nameChar = 0; nameChar < index; nameChar++)
					equip += String
							.valueOf((char) ('a' + equipGen.nextInt(26)));
				equipment.add(equip);
			}

			loc.setProvidedEquipment(equipment);
		}

	}

	private static void generateSchedule(Model model, Document doc)
			throws DatabaseException {

		model.createTransientSchedule().setDocument(doc).insert();

	}

	private static void generateScheduleItems(int num, Model model, Document doc)
			throws DatabaseException {
		Schedule schedule = doc.getSchedules().iterator().next();
		// TODO Randomize
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

	private static void generateTestData() {
		for (int items = 0; items < 100; items++) {
			try {
				Model model = new Model();
				model.createTransientDocument("Doc" + items, 14, 44).insert();
				Collection<Document> docs = model.findAllDocuments();
				Document doc = model.findAllDocuments().iterator().next();

				CSVExporter export = new CSVExporter();

				generateCourses(items, model, doc);
				generateInstructors(items, model, doc);
				generateLocations(items, model, doc);
				generateSchedule(model, doc);
				generateScheduleItems(items, model, doc);

				File file = new File(
						"test/edu/calpoly/csc/scheduler/view/web/shared/CSVExporterOracle/exportCase"
								+ items);

				Writer output = new BufferedWriter(new FileWriter(file));
				try {
					output.write(export.export(model, doc));
				} finally {
					output.close();
				}

			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
