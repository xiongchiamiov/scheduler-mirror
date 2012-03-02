package edu.calpoly.csc.scheduler.model;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.Set;

import com.csvreader.CsvWriter;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

/**
 * The Class CSVExporter. Exports a model to a CSV formatted string.
 * 
 * Current Status: partially working due to other uninitialized data and
 * inability to test w/ teacher populated schedules. TODO Once fully working,
 * will likely need to be resynced with CSV importer
 * 
 * @author Evan Ovadia
 * @author Jordan Hand
 */
public class CSVExporter {
	/** The locations. */
	private ArrayList<String[]> locations = new ArrayList<String[]>();
	private Map<Integer, Integer> locationRowIndexByID = new HashMap<Integer, Integer>();

	/** The instructors. */
	private ArrayList<String[]> instructors = new ArrayList<String[]>();
	private Map<Integer, Integer> instructorRowIndexByID = new HashMap<Integer, Integer>();

	/** The instructors time prefs. */
	private ArrayList<String[][]> instructorsTimePrefs = new ArrayList<String[][]>();
	private Map<Integer, Integer> instructorsTimePrefsRowIndexByID = new HashMap<Integer, Integer>();

	/** The instructors course prefs. */
	private ArrayList<String[][]> instructorsCoursePrefs = new ArrayList<String[][]>();
	private Map<Integer, Integer> instructorsCoursePrefsRowIndexByID = new HashMap<Integer, Integer>();

	/** The courses. */
	private ArrayList<String[]> courses = new ArrayList<String[]>();
	private Map<Integer, Integer> courseRowIndexByID = new HashMap<Integer, Integer>();

	/** The schedule items. */
	private ArrayList<String[]> scheduleItems = new ArrayList<String[]>();
	private Map<Integer, Integer> scheduleItemsRowIndexByID = new HashMap<Integer, Integer>();

	private static String join(Collection<String> strings, String glue) {
		String result = "";
		for (String str : strings) {
			if (!result.equals(""))
				result += glue;
			result += str;
		}
		return result;
	}

	/**
	 * Instantiates a new cSV exporter.
	 */
	public CSVExporter() {
	}

	/**
	 * Compile location. Turns location data into a string and adds it to the
	 * global locations ArrayList
	 * 
	 * @param location
	 *            A location
	 * @return A string with the location's index
	 */
	private String compileLocation(Location location) throws DatabaseException {
		// location.verify(); //TODO Re-enable. Location.verify uses deprecated
		// item ADA and room TBA has uninitialized data

		int index = locations.indexOf(location);
		if (index < 0) {
			index = locations.size();
			locations.add(new String[] { "location#" + index,
					location.getRoom(), location.getMaxOccupancy(),
					location.getType(),
					join(location.getProvidedEquipment(), " & ") });
			locationRowIndexByID.put(location.getID(), index);
			// TODO Note: Removed ADA
		}

		return "location#" + index;
	}

	/**
	 * Compile instructor. Turns instructor data into a string and adds it to
	 * the global instructors ArrayList
	 * 
	 * @param instructor
	 *            the instructor
	 * @return A string with the instructor index
	 */
	private String compileInstructor(Instructor instructor)
			throws DatabaseException {

		int index = instructors.indexOf(instructor);
		if (index < 0) {
			index = instructors.size();

			// Separates STAFF due to STAFF having uninitialized variables.
			// Uninitialized variables are commented out. This can potentially
			// can cause issues for CSVimporter
			// Fairness is commented out in both due to it currently not being
			// used.
			if (instructor.getFirstName().equals("STAFF"))
				instructors.add(new String[] { "instructor#" + index,
						instructor.getFirstName(), instructor.getLastName(),
						instructor.getUsername(), instructor.getMaxWTU(),

				// Integer.toString(instructor.getFairness()),
				// TODO Note: Removed Office/Officeroom
				// Boolean.toString(instructor.getDisability()),
				// compileCoursePrefs(instructor.getCoursePreferences()),
				// compileTimePrefs(instructor.getTimePreferences())
						});
			else
				instructors.add(new String[] { "instructor#" + index,
						instructor.getFirstName(), instructor.getLastName(),
						instructor.getUsername(), instructor.getMaxWTU(),

						compileCoursePrefs(instructor.getCoursePreferences()),
						compileTimePrefs(instructor.getTimePreferences()) });
			instructorRowIndexByID.put(instructor.getID(), index);

		}
		return "instructor#" + index;
	}

	private static String halfHourToString(int halfHourInDay) {
		int hour = halfHourInDay / 2;
		int halfHourInHour = halfHourInDay % 2;
		boolean am = hour < 12;
		hour %= 12;
		return hour + ":" + (halfHourInHour == 0 ? "00" : "30")
				+ (am ? "am" : "pm");
	}

	/**
	 * Compile time prefs. Turns Time Preference data into a string and adds it
	 * to the global instructorTimePrefs ArrayList
	 * 
	 * @param hashMap
	 *            A hashmap<Day rows, Hashmap<Time Columns, TimePreference>>
	 *            mapping the days and times with a teacher's preference for
	 *            that combination.
	 * @return A string of time prefs. ie Time,SUN,MON,TUE,WED,THU,FRI,SAT
	 *         00:00,5,5,5,5,5,5,5
	 */
	private String compileTimePrefs(int[][] hashMap) {
		final int startHalfHour = 14;
		final int endHalfHour = 44;
		final int numTimesInDay = endHalfHour - startHalfHour;
		String[][] strings = new String[1 + numTimesInDay][1 + Day.values().length];

		strings[0][0] = "Time";

		for (int halfHour = 0; halfHour < numTimesInDay; halfHour++)
			strings[halfHour + 1][0] = halfHourToString(halfHour
					+ startHalfHour);

		for (int col = 0; col < Day.values().length; col++)
			strings[0][col + 1] = Day.values()[col].toString();

		for (int halfHourNum = 0; halfHourNum < numTimesInDay; halfHourNum++) {
			for (int dayNum = 0; dayNum < Day.values().length; dayNum++) {
				Day day = Day.values()[dayNum];
				int row = halfHourNum + 1;
				int col = dayNum + 1;
				strings[row][col] = Integer
						.toString(hashMap[day.ordinal()][halfHourNum
								+ startHalfHour]);
			}
		}

		int newIndex = instructorsTimePrefs.size();
		instructorsTimePrefs.add(strings);
		return "timePrefs#" + newIndex;
	}

	/**
	 * Compile course prefs. Turns course preference data into a string and adds
	 * it to the global instructorsCoursePrefs ArrayList
	 * 
	 * @param coursePreferences
	 *            A hashmap of course ID's to preferences
	 * @return A string representing the course preference index
	 */
	private String compileCoursePrefs(
			HashMap<Integer, Integer> coursePreferences) {
		String[][] strings = new String[coursePreferences.size()][2];
		int row = 0;
		for (Entry<Integer, Integer> pref : coursePreferences.entrySet()) {
			strings[row][0] = "course#" + courseRowIndexByID.get(pref.getKey());
			strings[row][1] = Integer.toString(pref.getValue()); // Preference
																	// for the
																	// course
			row++;
		}

		int newIndex = instructorsCoursePrefs.size();
		instructorsCoursePrefs.add(strings);

		return "coursePrefs#" + newIndex;
	}

	private ScheduleItem findScheduleItemByID(int id,
			Collection<ScheduleItem> items) {
		for (ScheduleItem item : items)
			if (item.getID() == id)
				return item;
		assert (false);
		return null;
	}

	private Course findCourseByID(int id, Collection<Course> items) {
		for (Course item : items)
			if (item.getID() == id)
				return item;
		assert (false);
		return null;
	}

	/**
	 * Compile schedule item. Turns Schedule Item data into a string and adds it
	 * to the global Schedule.Items ArrayList
	 * 
	 * @param conflictingSchedule
	 *            .Item True is part of a conflicting schedule item, false if
	 *            not
	 * @param item
	 *            A Schedule.Item
	 * @return the string of the Schedule.Item Number
	 * @throws NotFoundException
	 */
	private String compileScheduleItem(ScheduleItem item,
			Collection<ScheduleItem> others) throws DatabaseException {

		// int index = scheduleItemsRowIndexByID.get(item.getID());
		int index = scheduleItems.indexOf(item);

		if (index < 0) {
		
			index = scheduleItems.size();
			scheduleItems.add(new String[] {
					"item#" + index,
					"instructor#"
							+ instructorRowIndexByID.get(item.getInstructor().getID()),
					"course#"
							+ courseRowIndexByID.get(item.getCourse().getID()),
					"location#"
							+ locationRowIndexByID.get(item.getLocation().getID()),
					Integer.toString(item.getSection()),
					Boolean.toString(item.isPlaced()),
					compileDayPattern(item.getDays()),
					compileTimeRange(item.getStartHalfHour(),
							item.getEndHalfHour()), });
		}

		return "item#" + index;
	}

	private String compileDayPattern(Set<Day> days) {
		String result = "";
		for (Day day : days)
			result += day.abbreviation;
		return result;
	}

	/**
	 * Compile time range. Converts a TimeRange into a string
	 * 
	 * @param timeRange
	 *            the time range
	 * @return the string of time ranges
	 */
	private static String compileTimeRange(int startHalfHourNum,
			int endHalfHourNum) {
		return halfHourToString(startHalfHourNum) + " to "
				+ halfHourToString(endHalfHourNum);
	}

	/**
	 * Compile course. Turns Course data into a string and adds it to the global
	 * courses ArrayList
	 * 
	 * @param course
	 *            A course
	 * @return A string representing the course index
	 * @throws DatabaseException
	 */
	private String compileCourse(Course course, Collection<Course> others)
			throws DatabaseException {

		int index = courses.indexOf(course);
		if (index < 0) {
			assert (false);
			/*
			 * The following was commented out because there is no longer a
			 * "getLab()" method for a Course. Instead the getLectureID method
			 * should be used.
			 * 
			 * If a course is a lecture, the value of the lectureID will be -1.
			 * If the course is a lab, the value of the lectureID will be equal
			 * to the lecture id.
			 * 
			 * String labIndexString = course.getLab() == null ? "" : "course#"
			 * + courses.indexOf(course.getLab());
			 */

			String dayPatterns = "";
			for (Set<Day> pattern : course.getDayPatterns()) {
				if (!dayPatterns.equals(""))
					dayPatterns += " ";
				dayPatterns += compileDayPattern(pattern);
			}

			String association;
			if (course.getLecture() == null) {
				association = "Course# -1";
			} else {
				association = compileCourse(
						findCourseByID(course.getLecture().getID(), others),
						others);// for associations

			}

			index = courses.size();
			courses.add(new String[] { "course#" + index,

			course.getType().toString(), course.getName(),
					course.getCatalogNumber(), course.getDepartment(),
					course.getWTU(), course.getSCU(), course.getNumSections(),
					course.getNumHalfHoursPerWeek(), dayPatterns,
					course.getMaxEnrollment(), association });
			courseRowIndexByID.put(course.getID(), index);
			assert (false);

		}

		return "course#" + index;
	}

	/**
	 * Export. Turns a Model into a CSV String
	 * 
	 * @param model
	 *            A model
	 * @return The CSV String
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws NotFoundException
	 */
	public String export(Model model, Document document) throws IOException,
			DatabaseException {
		/* Gather model information into the global string ArrayLists */
		for (Location location : model.findLocationsForDocument(document))
			compileLocation(location);

		Collection<Course> coursesInDocument = model
				.findCoursesForDocument(document);
		for (Course course : coursesInDocument) {
			compileCourse(course, coursesInDocument);
		}

		for (Instructor instructor : model.findInstructorsForDocument(document))
			compileInstructor(instructor);

		Schedule schedule = document.getSchedules().iterator().next();
		Collection<ScheduleItem> items = model
				.findAllScheduleItemsForSchedule(schedule);

		for (ScheduleItem item : items)
			compileScheduleItem(item, items);

		/*
		 * Start writing model data to a charArray that'll eventually be turned
		 * into a string
		 */
		Writer stringWriter = new CharArrayWriter();
		CsvWriter writer = new CsvWriter(stringWriter, ',');

		for (String topComment : CSVStructure.TOP_COMMENTS)
			writer.writeComment(topComment);

		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_MARKER);
		writer.write(document.getName());
		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.COURSES_MARKER);
		for (int i = 0; i < courses.size(); i++) {
			writer.writeRecord(courses.get(i));
		}
		writer.writeComment(CSVStructure.COURSES_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.LOCATIONS_MARKER);
		for (int i = 0; i < locations.size(); i++) {
			writer.writeRecord(locations.get(i));
		}
		writer.writeComment(CSVStructure.LOCATIONS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.INSTRUCTORS_COURSE_PREFS_MARKER);
		for (int i = 0; i < instructorsCoursePrefs.size(); i++) {
			writer.write("coursePrefs#" + i);
			writer.endRecord();
			writer.writeComment(CSVStructure.INSTRUCTOR_COURSE_PREFS_MARKER);
			for (String[][] prefs : instructorsCoursePrefs)
				for (String[] rec : prefs)
					writer.writeRecord(rec);
			writer.writeComment(CSVStructure.INSTRUCTOR_COURSE_PREFS_END_MARKER);
		}
		writer.writeComment(CSVStructure.INSTRUCTORS_COURSE_PREFS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_MARKER);
		for (int i = 0; i < instructorsTimePrefs.size(); i++) {
			writer.write("timePrefs#" + i);
			writer.endRecord();
			writer.writeComment(CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_MARKER);
			String[][] prefs = instructorsTimePrefs.get(i);
			for (String[] rec : prefs)
				writer.writeRecord(rec);
			writer.writeComment(CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_END_MARKER);
		}
		writer.writeComment(CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.INSTRUCTORS_MARKER);
		for (int i = 0; i < instructors.size(); i++) {
			writer.writeRecord(instructors.get(i));
		}
		writer.writeComment(CSVStructure.INSTRUCTORS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_ITEMS_MARKER);
		for (int i = 0; i < scheduleItems.size(); i++)
			writer.writeRecord(scheduleItems.get(i));
		writer.writeComment(CSVStructure.SCHEDULE_ITEMS_END_MARKER);

		writer.flush();
		writer.close();
		stringWriter.flush();
		stringWriter.close();

		return stringWriter.toString();
	}

	// //TESTING

	public String exportTest(Model model, Document document)
			throws IOException, DatabaseException {
		/* Gather model information into the global string ArrayLists */

		Collection<Location> locationsInDocument = generateLocations(10, model);
		for (Location location : locationsInDocument)
			compileLocation(location);

		Collection<Course> coursesInDocument = generateCourses(10, model);
		for (Course course : coursesInDocument) {
			compileCourse(course, coursesInDocument);
		}

		Collection<Instructor> instructorsInDocument = generateInstructors(10,
				model);
		for (Instructor instructor : instructorsInDocument)
			compileInstructor(instructor);

		// Schedule schedule = document.getSchedules().iterator().next();
		// Collection<ScheduleItem> items =
		// model.findAllScheduleItemsForSchedule(schedule);
		Collection<ScheduleItem> items = generateScheduleItems(10, model, (ArrayList<Instructor>)instructorsInDocument, (ArrayList<Course>) coursesInDocument,  (ArrayList<Location>)locationsInDocument);
		
		for (ScheduleItem item : items)
			compileScheduleItem(item, items);

		/*
		 * Start writing model data to a charArray that'll eventually be turned
		 * into a string
		 */
		Writer stringWriter = new CharArrayWriter();
		CsvWriter writer = new CsvWriter(stringWriter, ',');

		for (String topComment : CSVStructure.TOP_COMMENTS)
			writer.writeComment(topComment);

		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_MARKER);
		writer.write(document.getName());
		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.COURSES_MARKER);
		for (int i = 0; i < courses.size(); i++) {
			writer.writeRecord(courses.get(i));
		}
		writer.writeComment(CSVStructure.COURSES_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.LOCATIONS_MARKER);
		for (int i = 0; i < locations.size(); i++) {
			writer.writeRecord(locations.get(i));
		}
		writer.writeComment(CSVStructure.LOCATIONS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.INSTRUCTORS_COURSE_PREFS_MARKER);
		for (int i = 0; i < instructorsCoursePrefs.size(); i++) {
			writer.write("coursePrefs#" + i);
			writer.endRecord();
			writer.writeComment(CSVStructure.INSTRUCTOR_COURSE_PREFS_MARKER);
			for (String[][] prefs : instructorsCoursePrefs)
				for (String[] rec : prefs)
					writer.writeRecord(rec);
			writer.writeComment(CSVStructure.INSTRUCTOR_COURSE_PREFS_END_MARKER);
		}
		writer.writeComment(CSVStructure.INSTRUCTORS_COURSE_PREFS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_MARKER);
		for (int i = 0; i < instructorsTimePrefs.size(); i++) {
			writer.write("timePrefs#" + i);
			writer.endRecord();
			writer.writeComment(CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_MARKER);
			String[][] prefs = instructorsTimePrefs.get(i);
			for (String[] rec : prefs)
				writer.writeRecord(rec);
			writer.writeComment(CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_END_MARKER);
		}
		writer.writeComment(CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.INSTRUCTORS_MARKER);
		for (int i = 0; i < instructors.size(); i++) {
			writer.writeRecord(instructors.get(i));
		}
		writer.writeComment(CSVStructure.INSTRUCTORS_END_MARKER);

		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_ITEMS_MARKER);
		for (int i = 0; i < scheduleItems.size(); i++)
			writer.writeRecord(scheduleItems.get(i));
		writer.writeComment(CSVStructure.SCHEDULE_ITEMS_END_MARKER);

		writer.flush();
		writer.close();
		stringWriter.flush();
		stringWriter.close();

		return stringWriter.toString();
	}

	private static ArrayList<Instructor> generateInstructors(
			int numberOfInstructors, Model model) {
		numberOfInstructors = 10;

		ArrayList<Instructor> tempList = new ArrayList<Instructor>();

		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		HashMap<Day, HashMap<Integer, Integer>> timePrefs = new HashMap<Day, HashMap<Integer, Integer>>();

		for (Day day : Day.values())
			timePrefs.put(day, new HashMap<Integer, Integer>());

		for (int index = 0; index <= numberOfInstructors; index++) {
			Instructor instructor = null;

			try {
				// model.createTransientInstructor(firstName, lastName,
				// username, maxWTU, isSchedulable)
				instructor = model.createTransientInstructor(
						"Fname" + Integer.toString(index),
						"LName" + Integer.toString(index),
						"Uname" + Integer.toString(index),
						Integer.toString(index), true);
				int[][] tprefs = new int[7][48]; // Column is days, row is half
													// hours
				for (int j = 0; j < 7; j++) {
					for (int k = 0; k < 48; k++) {
						tprefs[j][k] = 3;
					}
				}
				
				coursePrefs.put(index, index % 4);

				instructor.setCoursePreferences(coursePrefs);
				instructor.setTimePreferences(tprefs);

				tempList.add(instructor);

			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return tempList;
	}

	private ArrayList<Course> generateCourses(int numberOfCourses, Model model) {

		ArrayList<Course> tempList = new ArrayList<Course>();

		for (int index = 0; index <= numberOfCourses; index++) {

			Set<String> usedEquipment = new HashSet<String>();
			usedEquipment.add("TestEquipment");
			ArrayList<Set<Day>> dayPatterns = new ArrayList<Set<Day>>();
			// dayPatterns.get(0).add(Day.FRIDAY); TODO
			Course course = null;

			try {
				// model.createTransientCourse(name, catalogNumber, department,
				// wtu, scu, numSections, type, maxEnrollment,
				// numHalfHoursPerWeek, isSchedulable)
				course = model.createTransientCourse(
						"Name" + Integer.toString(index),
						"Catalog" + Integer.toString(index),
						"dept" + Integer.toString(index),
						"WTU" + Integer.toString(index),
						"SCU" + Integer.toString(index),
						"Numsec" + Integer.toString(index),
						"Type" + Integer.toString(index), "maxenrollment"
								+ Integer.toString(index), "numHalfHours"
								+ Integer.toString(index), true);
				course.setUsedEquipment(usedEquipment);
				course.setDayPatterns(dayPatterns);
				course.setTetheredToLecture(false);
				course.setLecture(null);

			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			tempList.add(course);
		}

		return tempList;
	}

	private ArrayList<Location> generateLocations(int numberOfLocations,
			Model model) {

		ArrayList<Location> tempList = new ArrayList<Location>();

		Set<String> equipment = new HashSet<String>();
		equipment.add("Projector");

		for (int index = 0; index <= numberOfLocations; index++) {
			Integer.toString(index);
			Location location = null;
			try {
				// model.createTransientLocation(room, type, maxOccupancy,
				// isSchedulable)
				location = model.createTransientLocation(
						Integer.toString(index), Integer.toString(index),
						Integer.toString(index), true);
				location.setProvidedEquipment(equipment);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			tempList.add(location);
		}

		return tempList;
	}

	private ArrayList<ScheduleItem> generateScheduleItems(int num, Model model, ArrayList<Instructor> instructorList, ArrayList<Course> courseList,ArrayList<Location> locationList ) {
		ArrayList<ScheduleItem> tempList = new ArrayList<ScheduleItem>();

		/*
		
		ArrayList<Instructor> instructorList = generateInstructors(num, model);
		ArrayList<Course> courseList = generateCourses(num, model);
		ArrayList<Location> locationList = generateLocations(num, model);
*/
		Set<Day> dayPatterns = new HashSet<Day>();
		dayPatterns.add(Day.FRIDAY);

		for (int index = 0; index < num; index++) {
			// model.createTransientScheduleItem(section, days, startHalfHour,
			// endHalfHour, isPlaced, isConflicted)
			try {
				ScheduleItem si = model.createTransientScheduleItem(index,
						dayPatterns, 14, 29, true, false);
				si.setInstructor(instructorList.get(index));
				si.setCourse(courseList.get(index));
				si.setLocation(locationList.get(index));
				tempList.add(si);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return tempList;
	}

}
