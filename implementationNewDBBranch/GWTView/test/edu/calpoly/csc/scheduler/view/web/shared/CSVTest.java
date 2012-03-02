package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

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

//public class CSVTest extends ModelTestCase {
public class CSVTest {

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
    	for(int i = 0; i <= 1000; i++)
    		testExportCase(i, "a");
        //    Set up the input data by calling a method to construct and
        //    populate a schedule with the desired number of items.

        //    Wrap that schedule in a Model object, since CSVExporter.export
        //    needs a model for its input.

        //    Call CSVExporter.export with the input for this test case.

        //    Capture the string-valued output and write it to an output file.

        //    Compare the actual output value from the method with the expected
        //    output, which is stored in a pre-defined expected output file.

        // It's common JUnit practice to implement each test case in a separate
        // method, but this is not required.  You could have one parameterized,
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
	
	public static void main(String[] args){
		Model model = new Model();
		
		CSVExporter export = new CSVExporter();
		try {
			System.out.println(export.exportTest(model, model.createTransientDocument("Test", 14, 44).insert()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*
	// setup called by TestCase when run
	public void setUp() {
		// model = new Model("chem");
		// availableSchedules = model.getSchedules();
	}

	// called by TestCase when run
	public void tearDown() {

	}

	private void generateInstructors(int numberOfInstructors, Model model,
			Document doc) {
		
		final int hoursInDay = 48;
		final int prefChoices = 4;

		Instructor base = ModelTestUtility.createBasicInstructor(model, doc);

		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		HashMap<Day, HashMap<Integer, Integer>> timePrefs = new HashMap<Day, HashMap<Integer, Integer>>();
		Day[] days = Day.values();

		for (Day day : Day.values())
			timePrefs.put(day, new HashMap<Integer, Integer>());

		for (int index = 0; index <= numberOfInstructors; index++) {

			coursePrefs.put(index, index % 4);
			timePrefs.get(days[index % 7]).put(index % hoursInDay, index % prefChoices);

			Instructor instructor = model.assembleInstructor(doc, Integer
					.toString(numberOfInstructors), Integer
					.toString(numberOfInstructors), Integer
					.toString(numberOfInstructors), Integer
					.toString(numberOfInstructors), 
					new HashMap<Day, HashMap<Integer, Integer>>(timePrefs),
					new HashMap<Integer, Integer>(coursePrefs));

			model.insertInstructor(instructor);
		}
	}

	private void generateCourses(int numberOfCourses, Model model, Document doc) {
		Course base = ModelTestUtility.createCourse(model, doc);
		final int days = 7;
		Random rand = new Random(numberOfCourses);
		Day[] days = Day.values();
		for (int index = 0; index <= numberOfCourses; index++) {
			
			ArrayList<Set<Day>> dayPattern = new ArrayList<Set<Day>>();
			HashSet<String> dayPattern = new HashSet<String>();
			int daysOffered = index %7;
				//TODO Make more day patterns  RANDOM
			for(int offered = 0; offered < daysOffered; offered++)
				dayPattern.add(days[rand.nextInt(7)].toString());
		
			Course course = model.assembleCourse(doc, Integer.toString(index),
					Integer.toString(index), Integer.toString(index),
					Integer.toString(index),Integer.toString(index), Integer.toString(index),
					Integer.toString(index), Integer.toString(index),
					Integer.toString(index), base.getUsedEquipment(),
					dayPatterns, (index % 2) == 0);
			model.insertCourse(course);
		}

	}

	private void generateLocations(int numberOfLocations, Model model,
			Document doc) {
		Location base = ModelTestUtility.createLocation(model, doc);

		for (int index = 0; index <= numberOfLocations; index++) {
			Location location = model.assembleLocation(doc, base.getRoom(),
					base.getType(), base.getMaxOccupancy(),
					base.getProvidedEquipment());

			model.insertLocation(location);
		}
	}

	private void generateScheduleItems(int numberOfItems, Model model,
			Document doc, Schedule schedule) {
		Random rand = new Random(numberOfItems);

		ArrayList<Course> courses = (ArrayList<Course>) model
				.findCoursesForDocument(doc);
		ArrayList<Location> locations = (ArrayList<Location>) model
				.findLocationsForDocument(doc);
		ArrayList<Instructor> instructors = (ArrayList<Instructor>) model
				.findInstructorsForDocument(doc);

		for (int index = 0; index <= numberOfItems; index++) {
			int startHalfHour = rand.nextInt(46);
			int endHalfHour = rand.nextInt(48);
			while (endHalfHour <= startHalfHour) {
				endHalfHour = rand.nextInt(48);
			}

			Boolean isPlaced = (index % 2) == 0 ? true : false;
			Boolean isConflicted = (index % 3) == 0 ? true : false;

			ScheduleItem item = model.assembleScheduleItem(schedule,
					courses.get(index), instructors.get(index),
					locations.get(index), index, new HashSet<Day>(),
					startHalfHour, endHalfHour, isPlaced, isConflicted);

			model.insertScheduleItem(item);
		}
	}

	private Model generateData(int numOfItems) {

		Model model = this.createBlankModel();
		Document doc = model.assembleDocument(Integer.toString(numOfItems), 14,
				44);
		Schedule sched = model.assembleSchedule(doc);

		generateInstructors(numOfItems, model, doc);
		generateCourses(numOfItems, model, doc);
		generateLocations(numOfItems, model, doc);

		generateScheduleItems(numOfItems, model, doc, sched);

		return model;
	}

	@Override
	IDatabase createDatabase() {
		// TODO Auto-generated method stub
		return null;
	}
*/
}
