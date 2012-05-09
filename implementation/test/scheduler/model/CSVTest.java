package scheduler.model;

import scheduler.model.Model;
import scheduler.model.db.DatabaseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

/****
 * 
 * This is the Testing class for CSVExporter.java
 * 
 * @author: Jordan Hand
 * @author: Gene Fisher (gfisher@calpoly.edu)
 * @version: 17feb12
 */

public abstract class CSVTest extends ModelTestCase {

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

		for (int items = 0; items < 50; items++) {
			try {
				testExportCase(items, "ExportTest" + items);
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			testExportCase(1000, "ExportTest1000");
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		final String exportOraclePath = "test/scheduler/model/CSVExportOracle/";
		final String exportOutputPath = "test/scheduler/model/CSVExportResult/";

		Model model = new Model();
		model.createAndInsertDocumentWithTBAStaffAndScheduleAndChooseForMe(
				"Doc" + numberOfItems, 14, 44).insert();
		Document doc = model.findAllDocuments().iterator().next();

		CSVExporter export = new CSVExporter();

		generateCourses(numberOfItems, model, doc);
		generateInstructors(numberOfItems, model, doc);
		generateLocations(numberOfItems, model, doc);
		generateSchedule(model, doc);
		generateScheduleItems(numberOfItems, model, doc);

		File file = new File(exportOutputPath + expectedOutputFile);

		Writer output = new BufferedWriter(new FileWriter(file));
		try {
			output.write(export.export(model, doc));
		} finally {
			output.close();
		}

		String out = null;
		Process p = Runtime.getRuntime().exec(
				"diff " + exportOraclePath + expectedOutputFile + " "
						+ exportOutputPath + expectedOutputFile);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		out = stdInput.readLine();
		assertEquals(null, out);

	}

	/**
	 * Generates a number of instructors and adds them to a document
	 * 
	 * @param numberOfInstructors
	 * @param model
	 * @param doc
	 * @throws DatabaseException
	 */
	private void generateInstructors(int testCase, Model model, Document doc)
			throws DatabaseException {

		Collection<Course> courseList;
		courseList = doc.getCourses();
		Random instructorRandom = new Random(testCase);

		int numberOfInstructors;
		switch (testCase) {
		case 0:
			numberOfInstructors = 0;
			break;
		case 1:
			numberOfInstructors = 1;
			break;
		default:
			numberOfInstructors = 1 + instructorRandom.nextInt(60);
			break;
		}

		for (int index = 0; index < numberOfInstructors; index++) {
			// Pseudo-random number generators
			Random nameGen = new Random((index + 1) * 2);
			Random nameAmtGen = new Random((index + 1) * 10);
			Random timePrefGen = new Random((numberOfInstructors + 1) * 4);

			// Generate names
			String firstName = "";
			String lastName = "";

			int nameBound = 1 + nameAmtGen.nextInt(10);

			for (int nameChar = 0; nameChar < nameBound; nameChar++) {
				firstName += String.valueOf((char) ('a' + nameGen.nextInt(26)));
				lastName += String.valueOf((char) ('a' + nameGen.nextInt(26)));
			}

			String userName = firstName.charAt(0) + lastName;

			String maxWTU = Integer.toString(index * index);
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

	/**
	 * Generates a number of courses and added them to a document.
	 * If the test case is 0 or 1 it will generate the respective number of courses, otherwise
	 * it will generate a random number of courses > 1
	 * @param testCase The test case to generate data for
	 * @param model The Model
	 * @param doc The doument that the courses should be inserted into
	 * @throws DatabaseException
	 */
	private void generateCourses(int testCase, Model model, Document doc)
			throws DatabaseException {
		Random dayPatternRandom = new Random(testCase);
		Random coursesRandom = new Random(testCase);

		int numberOfCourses;
		switch (testCase) {
		case 0:
			numberOfCourses = 0;
			break;
		case 1:
			numberOfCourses = 1;
			break;
		default:
			numberOfCourses = 1 + coursesRandom.nextInt(60);
			break;
		}

		for (int index = 0; index < numberOfCourses; index++) {
			Random nameGen = new Random(index);
			Random equipGen = new Random(index);

			String name = "";
			String catalog = "";
			String dept = "";
			String wtu = Integer.toString(index * 13);
			String scu = Integer.toString(index * 12);
			String numSec = Integer.toString(index * 11);
			int types = Course.CourseType.values().length;
			String type = Course.CourseType.values()[nameGen.nextInt(types - 1)]
					.toString();
			String maxEnrollment = Integer.toString(index * 15);
			String numHalfHours = Integer.toString(index * 16);
			Boolean isScheduleable = index % 2 == 0 ? true : false;

			name = "NameValue" + index;
			catalog = "Num" + index;
			dept = "Dept" + index;

			// Generate initial data

			model.createTransientCourse(name, catalog, dept, wtu, scu, numSec,
					type, maxEnrollment, numHalfHours, isScheduleable)
					.setDocument(doc).insert();

			Iterator<Course> courseiter = model.findCoursesForDocument(doc)
					.iterator();
			Course course = null;

			while (courseiter.hasNext()) {
				course = courseiter.next();
				if (course.getName().equals(name))
					break;
			}

			// Generate Used equipment
			Set<String> usedEquipment = new HashSet<String>();

			int equipBound = index == 0 ? 0 : equipGen.nextInt(40);

			for (int i = 0; i < equipBound; i++) {
				String equip = "";
				for (int nameChar = 0; nameChar < index; nameChar++)
					equip += String
							.valueOf((char) ('a' + equipGen.nextInt(26)));
				usedEquipment.add(equip);
			}
			course.setUsedEquipment(usedEquipment);

			// Generate Day patterns
			Collection<Set<Day>> dayPatternsList = new ArrayList<Set<Day>>();
			int patternSize = dayPatternRandom.nextInt(8);
			while (dayPatternsList.size() < patternSize) {
				dayPatternsList.add(getDayPattern(dayPatternRandom.nextInt()));
			}
			course.setDayPatterns(dayPatternsList);

			// Set tethered to lecture
			if (index > 0 && course.getType().equals("LAB") && index % 2 == 0) {
				Random randTether = new Random(index);
				ArrayList<Course> tetherPool = new ArrayList<Course>(
						doc.getCourses());
				tetherPool.remove(course);

				course.setLecture(tetherPool.get(randTether.nextInt(tetherPool
						.size() - 1)));
				course.setTetheredToLecture(true);
			} else {
				course.setTetheredToLecture(false);
				course.setLecture(null);
			}

		}

	}

	/**
	 * Generates a number of locations and adds them to a document.
	 * If the test case is 0, 1, or 1000 it will generate the respective number of locations, otherwise
	 * it will generate a random number of locations > 1
	 * @param testCase - The test case to generate data for
	 * @param model - The model
	 * @param doc - The document that locations should be associated with
	 * @throws DatabaseException
	 */
	private void generateLocations(int testCase, Model model, Document doc)
			throws DatabaseException {

		Random locationsRandom = new Random(testCase);
		Random equipGen = new Random(testCase);
		Random typeGen = new Random(testCase);

		int numberOfLocations;
		switch (testCase) {
		case 0:
			numberOfLocations = 0;
			break;
		case 1:
			numberOfLocations = 1;
			break;
		default:
			numberOfLocations = 1 + locationsRandom.nextInt(60);
			break;
		}

		for (int index = 0; index < numberOfLocations; index++) {
			String[] equip = { "Overhead", "Projector", "Whiteboard",
					"Blackboard", "Smartboard", "Computer" };
			String[] types = { "LEC", "LAB" };

			String room = Integer.toString(1 + index * 15) + "-"
					+ Integer.toString(index * 14);

			int num = typeGen.nextInt(types.length);
			String type = types[num];

			String maxOccupancy = Integer.toString(1 + index * 140);
			Boolean isScheduleable = index % 2 == 0 ? true : false;

			model.createTransientLocation(room, type, maxOccupancy,
					isScheduleable).setDocument(doc).insert();

			Location loc = null;
			;
			Iterator<Location> lociter = model.findLocationsForDocument(doc)
					.iterator();
			while (lociter.hasNext())
				loc = lociter.next();

			Set<String> equipment = new HashSet<String>();

			int equipbound = equipGen.nextInt(equip.length);

			while (equipment.size() < equipbound) {
				equipment.add(equip[equipGen.nextInt(equip.length)]);
			}

			loc.setProvidedEquipment(equipment);
		}

	}

	/**
	 * Generates a Schedule.
	 *
	 * @param model the model
	 * @param doc the document to be associated with the schedule
	 * @throws DatabaseException the database exception
	 */
	private void generateSchedule(Model model, Document doc)
			throws DatabaseException {

		model.createTransientSchedule().setDocument(doc).insert();

	}

	/**
	 * Generates scheduleItems.
	 * If the test case is 0, 1, or 1000 it will generate the respective number of schedule items, otherwise
	 * it will generate a random number of items > 1
	 *
	 * @param testCase the test case that schedule items should be generated for
	 * @param model A model
	 * @param doc The document to associated schedule items with
	 * @throws DatabaseException the database exception
	 */
	private void generateScheduleItems(int testCase, Model model, Document doc)
			throws DatabaseException {
		// Min and Max end times
		final int startTimeLimit = 14;
		final int endTimeLimit = 29;

		// Random number generators for schedule items, times, and days
		Random itemRandom = new Random(testCase + 1);
		Random randTime = new Random(testCase - 1);
		Random randDay = new Random(testCase);

		// Get the schedule
		Schedule schedule = doc.getSchedules().iterator().next();

		// Convert data collections into ArrayLists
		ArrayList<Course> courses = new ArrayList<Course>(doc.getCourses());
		ArrayList<Instructor> instructors = new ArrayList<Instructor>(
				doc.getInstructors());
		ArrayList<Location> locations = new ArrayList<Location>(
				doc.getLocations());

		int numberOfItems;
		switch (testCase) {
		case 0:
			numberOfItems = 0;
			break;
		case 1:
			numberOfItems = 1;
			break;
		case 1000:
			numberOfItems = 1000;
			break;
		default:
			numberOfItems = 1 + itemRandom.nextInt(60);
			break;
		}

		// Generate a number of ScheduleItems
		for (int index = 0; index < numberOfItems; index++) {

			// Generate start and end times for schedule item
			int startTime = startTimeLimit
					+ randTime.nextInt(endTimeLimit - startTimeLimit - 1);
			int endTime = startTime
					+ randTime.nextInt(endTimeLimit - startTime - 1) + 1;

			// Set other information
			Set<Day> dayPatterns = getDayPattern(randDay.nextInt());

			ScheduleItem item = model.createTransientScheduleItem(index,
					dayPatterns, startTime, endTime, index % 2 == 0,
					index % 5 == 0);

			item.setCourse(courses.get(itemRandom.nextInt(courses.size())));
			item.setInstructor(instructors.get(itemRandom.nextInt(instructors
					.size())));
			item.setLocation(locations.get(itemRandom.nextInt(locations.size())));

			item.setSchedule(schedule);
			item.insert();
		}

	}

	/**
	 * Generates oracle data for CSVTest.
	 *
	 * @param testCase The test case to generate data for
	 * @param exportFilePath the export file path
	 * @param exportFileName the export file name
	 */
	public void generateTestData(int testCase, String exportFilePath,
			String exportFileName) {

		CSVExporter export = new CSVExporter();

		try {
			// Model and Document setup
			Model model = new Model();
			model.createAndInsertDocumentWithTBAStaffAndScheduleAndChooseForMe(
					"Doc" + testCase, 14, 44).insert();
			Document doc = model.findAllDocuments().iterator().next();

			// Generates Data
			generateCourses(testCase, model, doc);
			generateInstructors(testCase, model, doc);
			generateLocations(testCase, model, doc);
			generateSchedule(model, doc);
			generateScheduleItems(testCase, model, doc);

			// Output for Oracle data
			File file = new File(exportFilePath + exportFileName + testCase);

			Writer output = new BufferedWriter(new FileWriter(file));
			try {
				output.write(export.export(model, doc));
			} finally {
				output.close();
			}

		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Generates a set of day patterns. Generates the patterns using a
	 * pseudo-random number generator with a seed.
	 * 
	 * @param seed
	 *            Seed for the test case values
	 * @return
	 */
	private Set<Day> getDayPattern(int seed) {
		// Works by deciding how many different days to use then adds new days
		// to set till it reaches the desires number.
		Random rand = new Random(seed);
		Set<Day> dayPattern = new HashSet<Day>();

		// Converts enumerator Day's values to an ArrayList
		ArrayList<Day> dayValues = new ArrayList<Day>(Arrays.asList(Day
				.values()));

		// Decides which days to use
		int size = 1 + rand.nextInt(7);
		while (dayPattern.size() < size) {
			Day day = dayValues.get(rand.nextInt(dayValues.size()));
			dayPattern.add(day);
		}

		return dayPattern;
	}

}
