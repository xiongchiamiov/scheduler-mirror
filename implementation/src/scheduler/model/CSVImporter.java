package scheduler.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scheduler.model.db.DatabaseException;

import com.csvreader.CsvReader;

/**
 * The Class CSVImporter.
 * Imports the text of a CSV file, converts information into Scheduler project data, creates a new document
 *  and inserts the data into it.
 *  
 * CURRENT STATUS:
 *     The gathering of information from the CSV works, however the adding and retrieving of data from the
 *     database has issues somewhere along the line.
 *     
 *     The result using import is as follows:
 *     -New Document creation works.
 *     -Course import works except for Day combos. Day combos work intermittently.
 *     -Location import works.
 *     -Instructor import works except for preferences.  They have worked irregularly in the past.
 *         As of the last attempt to figure out why, it appears somewhere along the line something is happening where
 *         the values are not being set in the database and a set of default values is being used.
 *     -Schedule items currently do not go into the document at all and are commented out.
 *     
 * INTEGRATION FLOW: 
 *     -The overall flow of data needs to go: Main view AdminScheduleNavView/HomeView->Import [gets CSV text and passes it to]->CachedService
 *              ->GreetingServiceImpl->CSVImporter
 *              This is done because CachedService has a reference to GreetingServiceImpl and GreetingServiceImpl is server
 *              side and thus has access to CSVImport.
 *     This was not officially integrated in due to inconsistent functionality as detailed previously.
 *
 *    
 * @author Evan Ovadia
 * @author Jordan Hand
 */
public class CSVImporter {

	/** The courses. */
	List<Course> courses = new ArrayList<Course>();

	/** The associations. */
	List<Integer> associations = new ArrayList<Integer>();

	/** The locations. */
	List<Location> locations = new ArrayList<Location>();

	/** The instructors course prefs. */
	List<HashMap<Integer, Integer>> instructorsCoursePrefs = new ArrayList<HashMap<Integer, Integer>>();

	/** The instructors time prefs. */
	ArrayList<int[][]> instructorsTimePrefs = new ArrayList<int[][]>();

	/** The instructors. */
	List<Instructor> instructors = new ArrayList<Instructor>();

	/** The schedule items. */
	List<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();

	/** The schedule name. */
	String scheduleName;

	/** The model. */
	Model model;

	/** STAFF instructor */
	Instructor STAFF;

	/**  TBA Location */
	Location TBA;

	/**
	 * Read a CSV file and turn data into a Document (Courses, Locations, Instructors, and Schedule Items).
	 * 
	 * @param model
	 *            the model
	 * @param newScheduleName
	 *            the new schedule name
	 * @param value
	 *            the value
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void read(Model model, String newScheduleName, String value)
			throws IOException, DatabaseException {

		this.model = model;

		CsvReader reader = CsvReader.parse(value);

		Collection<List<String>> lines = new LinkedList<List<String>>();
		while (reader.readRecord()) {
			String[] line = reader.getValues();
			if (line.length == 0)
				continue;
			if (line.length == 1 && line[0].trim().equals(""))
				continue;
			lines.add(Arrays.asList(line));
		}

		Iterator<List<String>> linesIterator = lines.iterator();

		for (String comment : CSVStructure.TOP_COMMENTS)
			skipBlanksUntilComment(linesIterator, comment);

		readSchedule(linesIterator);
		Document document = model
				.createAndInsertDocumentWithSpecialInstructorsAndLocations(
						this.scheduleName, 14, 44);

	
		this.TBA = document.getTBALocation();
		this.STAFF = document.getStaffInstructor();

		readCourses(linesIterator);
		assignAssociations();
		
		//Added courses into the document so ID's can be used for course prefs
		for (Course course : this.courses) {
			course.setDocument(document).insert();
		}

		readLocations(linesIterator);

		readAllInstructorsCoursePrefs(linesIterator);

		//Converts instructor course prefs to use the course ID instead of the arbitrary one assigned by CSVExport
		List<HashMap<Integer, Integer>> instructorsCoursePrefs2 = new ArrayList<HashMap<Integer, Integer>>();

		for (int idx = 0; idx < this.instructorsCoursePrefs.size(); idx++) {
			HashMap<Integer, Integer> oldPrefs = this.instructorsCoursePrefs
					.get(idx);
			HashMap<Integer, Integer> newPrefs = new HashMap<Integer, Integer>();
			for (Integer val : new ArrayList<Integer>(oldPrefs.keySet())) {
				newPrefs.put(this.courses.get(val).getID(), oldPrefs.get(val));
			}

			instructorsCoursePrefs2.add(newPrefs);
		}
		this.instructorsCoursePrefs = instructorsCoursePrefs2;

		
		readAllInstructorsTimePrefs(linesIterator);

		readInstructors(linesIterator);
		
		readScheduleItems(linesIterator);

		for (Location location : this.locations) {
			location.setDocument(document).insert();
		}

		for (Instructor instructor : this.instructors) {
			instructor.setDocument(document).insert();
		}

		for (ScheduleItem item : this.scheduleItems) {
			// item.setDocument(document).insert(); //TODO reenable
		}

	}

	/**
	 * Assign associations.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	private void assignAssociations() throws DatabaseException {
		for (int i = 0; i < this.associations.size(); i++) {
			if (this.associations.get(i) != -1) {
				Course association = this.courses.get(this.associations.get(i));

				this.courses.get(i).setLecture(association);
			}
		}

	}

	/**
	 * Read single instructors course pref and return a hashmap mapping the course to 
	 * its desirability  .
	 * 
	 * @param linesIterator
	 *            the lines iterator
	 * @return a hash map hashmap mapping the course to 
	 * its desirability  .
	 */
	private HashMap<Integer, Integer> readSingleInstructorsCoursePrefs(
			Iterator<List<String>> linesIterator) {
		skipBlanksUntilComment(linesIterator,
				CSVStructure.INSTRUCTOR_COURSE_PREFS_MARKER);

		HashMap<Integer, Integer> instructorCoursePrefs = new HashMap<Integer, Integer>();
		while (true) {
			assert (linesIterator.hasNext());

			List<String> cells = linesIterator.next();
			if (cells.size() == 1
					&& cells.get(0)
							.equals("#"
									+ CSVStructure.INSTRUCTOR_COURSE_PREFS_END_MARKER))
				break;

			Iterator<String> cellI = cells.iterator();
			Integer courseIndex = extractIndex("course#", cellI.next());
			Integer desire = Integer.parseInt(cellI.next());

			instructorCoursePrefs.put(courseIndex, desire);
		}

		return instructorCoursePrefs;
	}

	/**
	 * Read all instructors course prefs into instructorsCoursePrefs.
	 * 
	 * @param linesIterator
	 *            the lines iterator
	 */
	private void readAllInstructorsCoursePrefs(
			Iterator<List<String>> linesIterator) {
		skipBlanksUntilComment(linesIterator,
				CSVStructure.INSTRUCTORS_COURSE_PREFS_MARKER);

		while (true) {
			assert (linesIterator.hasNext());

			List<String> cells = linesIterator.next();
			if (cells.size() == 1
					&& cells.get(0)
							.equals("#"
									+ CSVStructure.INSTRUCTORS_COURSE_PREFS_END_MARKER))
				break;

			Iterator<String> cellI = cells.iterator();

			int instructorCoursePrefIndex = this.instructorsCoursePrefs.size();
			assert (extractIndex("coursePrefs#", cellI.next()) == instructorCoursePrefIndex);

			this.instructorsCoursePrefs
					.add(readSingleInstructorsCoursePrefs(linesIterator));
		}
	}

	/**
	 * Read all instructors time prefs.
	 * 
	 * @param linesIterator
	 *            the lines iterator
	 */
	private void readAllInstructorsTimePrefs(
			Iterator<List<String>> linesIterator) {
		skipBlanksUntilComment(linesIterator,
				CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_MARKER);

		while (true) {
			assert (linesIterator.hasNext());

			List<String> cells = linesIterator.next();
			if (cells.size() == 1
					&& cells.get(0)
							.equals("#"
									+ CSVStructure.ALL_INSTRUCTORS_TIME_PREFS_END_MARKER))
				break;

			int instructorTimePrefIndex = instructorsTimePrefs.size();
			assert (extractIndex("timePrefs#", cells.get(0)) == instructorTimePrefIndex);

			this.instructorsTimePrefs.add(readSingleInstructorsTimePrefs(
					instructorTimePrefIndex, linesIterator));
		}
	}

	/**
	 * Read single instructors time prefs.
	 * 
	 * @param instructorTimePrefIndex
	 *            the instructor time pref index
	 * @param linesIterator
	 *            the lines iterator
	 * @return int[][] of time preferences where the column represents the day and the row represents the time
	 */
	private int[][] readSingleInstructorsTimePrefs(int instructorTimePrefIndex,
			Iterator<List<String>> linesIterator) {

		skipBlanksUntilComment(linesIterator,
				CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_MARKER);

		List<String> headersLine = linesIterator.next();
		Iterator<String> headerCellI = headersLine.iterator();
		assert (headerCellI.next().equals("Time"));

		int halfHours = 48;
		int[][] instructorTimePrefs = new int[Day.values().length][halfHours];

		for (int row = 14; row < 44; row++) {
			assert (linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			Iterator<String> cellI = cells.iterator();
			String timeString = cellI.next(); //Not used in CSV but there for readability

			for (int col = 0; col < Day.values().length; col++) {
				Integer desire = new Integer(cellI.next());

				instructorTimePrefs[col][row] = desire;
			}
		}

		for (int row = 0; row < 48; row++) {
			for (int col = 0; col < Day.values().length; col++) {
				instructorTimePrefs[col][row] = 0;

			}
		}

		skipBlanksUntilComment(linesIterator,
				CSVStructure.SINGLE_INSTRUCTOR_TIME_PREFS_END_MARKER);

		return instructorTimePrefs;
	}

	/**
	 * Read courses.
	 * 
	 * @param linesIterator
	 *            the lines iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	void readCourses(Iterator<List<String>> linesIterator)
			throws DatabaseException {
		skipBlanksUntilComment(linesIterator, CSVStructure.COURSES_MARKER);

		while (true) {
			assert (linesIterator.hasNext());

			List<String> cells = linesIterator.next();
			if (cells.size() == 1
					&& cells.get(0).equals(
							"#" + CSVStructure.COURSES_END_MARKER))
				break;

			Course course;
			Iterator<String> cellI = cells.iterator();

			int index = extractIndex("course#", cellI.next());
			assert (index == this.courses.size());

			String courseType = cellI.next();
			String courseName = cellI.next();
			String courseNum = cellI.next();
			String courseDept = cellI.next();

			String wtu = cellI.next();
			String scu = cellI.next();
			String numOfSections = cellI.next();
			String numOfHalfHours = cellI.next();
			Collection<Set<Day>> dayPattern = readDayPatterns(cellI.next());
			String maxEnrollment = cellI.next();
			Boolean isSchedulable = new Boolean(cellI.next());
			Boolean isTethered = new Boolean(cellI.next());
			this.associations.add(extractIndex("course#", cellI.next()));

			course = this.model.createTransientCourse(courseName, courseNum,
					courseDept, wtu, scu, numOfSections, courseType,
					maxEnrollment, numOfHalfHours, isSchedulable);
			course.setDayPatterns(dayPattern);
			course.setType(courseType);
			course.setTetheredToLecture(isTethered);

			this.courses.add(course);

		}

	}

	/**
	 * Read day patterns.
	 * Takes in a String of day patterns ie. MW, TR and return a collection of 
	 * sets of Days. {[Monday, Wednesday], [Tuesday, Thursday]}
	 * @param pattern
	 *            the day patterns
	 * @return A Collection of sets of days.
	 */
	private Collection<Set<Day>> readDayPatterns(String pattern) {
		Collection<Set<Day>> dayPatterns = new ArrayList<Set<Day>>();
		String[] dayStrings = pattern.split(" ");

		for (String dayString : dayStrings) {
			Set<Day> daySet = new HashSet<Day>();
			String dayVal = "";
			char[] dayCharacters = dayString.toCharArray();
			for (char dayChar : dayCharacters) {
				if (dayVal.equals("")) {
					dayVal = "" + dayChar;
				} else {
					dayVal += dayChar;
				}
				for (Day day : Day.values()) {
					if (dayVal.equals(day.abbreviation)) {
						daySet.add(day);
						dayVal = "";
						break;
					}
				}

			}
			dayPatterns.add(daySet);
		}
		return dayPatterns;

	}

	/**
	 * Extract index.
	 * Extracts index from an item in format of prefix from the overall string.
	 * ie. course#0 with a prefix of "course#" would return 0.
	 * @param prefix
	 *            the prefix
	 * @param indexString
	 *            the index string
	 * @return the integer and if null return null
	 */
	private Integer extractIndex(String prefix, String indexString) {
		if (indexString.equals("")
				|| (indexString.substring(prefix.length()).equals("null")))
			return null;
		assert (indexString.startsWith(prefix));
		return Integer.parseInt(indexString.substring(prefix.length()).trim());

	}

	/**
	 * Read instructors.
	 * 
	 * @param linesIterator
	 *            the lines iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	void readInstructors(Iterator<List<String>> linesIterator)
			throws DatabaseException {
		skipBlanksUntilComment(linesIterator, CSVStructure.INSTRUCTORS_MARKER);
		while (true) {
			assert (linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			if (cells.size() == 1
					&& cells.get(0).equals(
							"#" + CSVStructure.INSTRUCTORS_END_MARKER))
				return;
			Iterator<String> cellI = cells.iterator();
			int index = extractIndex("instructor#", cellI.next());
			assert (this.instructors.size() == index);
			Instructor instructor;

			String lastName = cellI.next();
			String firstName = cellI.next();
			String userName = cellI.next();
			String maxWTU = cellI.next();
			Boolean isSchedulable = new Boolean(cellI.next());

			int coursePrefIndex = extractIndex("coursePrefs#", cellI.next());
			int timePrefIndex = extractIndex("timePrefs#", cellI.next());

			instructor = this.model.createTransientInstructor(firstName, lastName,
					userName, maxWTU, isSchedulable);

			instructor.setCoursePreferences(this.instructorsCoursePrefs
					.get(coursePrefIndex));
			
			
			 instructor.setTimePreferences(this.instructorsTimePrefs
			 .get(timePrefIndex));

			 this.instructors.add(instructor);
		}
	}

	/**
	 * Read locations.
	 * 
	 * @param linesIterator
	 *            the lines iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	void readLocations(Iterator<List<String>> linesIterator)
			throws DatabaseException {
		skipBlanksUntilComment(linesIterator, CSVStructure.LOCATIONS_MARKER);
		while (true) {
			assert (linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			if (cells.size() == 1
					&& cells.get(0).equals(
							"#" + CSVStructure.LOCATIONS_END_MARKER))
				return;

			Iterator<String> cellI = cells.iterator();

			int index = extractIndex("location#", cellI.next());
			assert (this.locations.size() == index);

			Location location;
			String locationRoom = cellI.next();
			String locationMaxOccupancy = cellI.next();
			String locationType = cellI.next();

			Boolean isSchedulable = new Boolean(cellI.next());

			location = this.model.createTransientLocation(locationRoom,
					locationType, locationMaxOccupancy, isSchedulable);
			this.locations.add(location);
		}
	}

	/**
	 * Skip blanks until comment.
	 * 
	 * @param lineIterator
	 *            the line iterator
	 * @param comment
	 *            the comment
	 */
	void skipBlanksUntilComment(Iterator<List<String>> lineIterator,
			String comment) {
		while (lineIterator.hasNext()) {
			List<String> line = lineIterator.next();
			if (line.size() == 0)
				continue;
			else if (line.size() == 1 && line.get(0).equals("#" + comment))
				break;
			else
				assert (false);
		}
	}

	/**
	 * Read schedule Name.
	 * 
	 * @param lineIterator
	 *            the line iterator
	 */
	void readSchedule(Iterator<List<String>> lineIterator) {
		skipBlanksUntilComment(lineIterator, CSVStructure.SCHEDULE_MARKER);
		List<String> line = lineIterator.next();
		this.scheduleName = line.get(0);
		skipBlanksUntilComment(lineIterator, CSVStructure.SCHEDULE_END_MARKER);
	}

	/**
	 * Read schedule items.
	 * 
	 * @param linesIterator
	 *            the lines iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	void readScheduleItems(Iterator<List<String>> linesIterator)
			throws DatabaseException {
		skipBlanksUntilComment(linesIterator,
				CSVStructure.SCHEDULE_ITEMS_MARKER);
		while (true) {
			assert (linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			if (cells.size() == 1
					&& cells.get(0).equals(
							"#" + CSVStructure.SCHEDULE_ITEMS_END_MARKER))
				return;

			Iterator<String> cellI = cells.iterator();
			int index = extractIndex("item#", cellI.next());
			assert (this.scheduleItems.size() == index);


			ScheduleItem item;

			Integer instructorIndex = extractIndex("instructor#", cellI.next());
			Integer courseIndex = extractIndex("course#", cellI.next());
			Integer locationIndex = extractIndex("location#", cellI.next());
			Integer section = Integer.parseInt(cellI.next());
			Boolean isPlaced = new Boolean(cellI.next());
			Boolean isConflicted = new Boolean(cellI.next());
			ArrayList<Set<Day>> dayPattern = (ArrayList<Set<Day>>) readDayPatterns(cellI
					.next());
			String timeRange = cellI.next();

			String startString = timeRange.substring(0,
					timeRange.indexOf(" to "));
			String endString = timeRange.substring(timeRange.indexOf(" to ")
					+ " to ".length());

			int startHalfHour = stringToHalfHour(startString);
			int endHalfHour = stringToHalfHour(endString);

			item = this.model.createTransientScheduleItem(section,
					dayPattern.get(0), startHalfHour, endHalfHour, isPlaced,
					isConflicted);
			
			//Handles case where instructor is STAFF
			if (instructorIndex == null) {
				item.setInstructor(this.STAFF);
			} else {
				item.setInstructor(this.instructors.get(instructorIndex));
			}

			//Handles case where location is TBA
			if (locationIndex == null) {
				item.setLocation(this.TBA);
			} else {
				item.setLocation(this.locations.get(locationIndex));
			}

			item.setCourse(this.courses.get(courseIndex));

			this.scheduleItems.add(item);
		}
	}

	/**
	 * String to half hour. Converts String in format of 3:30pm to an int
	 * representing half hours in a day. ie. 7:00am would result in 14
	 * 
	 * @param text
	 *            Time text
	 * @return An int representing the half our of the time put in.
	 */
	private static int stringToHalfHour(String text) {

		String hourText = text.substring(0, text.indexOf(":"));
		String halfHourText = text.substring(text.indexOf(":") + 1,
				text.length() - 2);

		int hour = Integer.parseInt(hourText);
		if (text.substring(text.length() - 2).equals("pm"))
			hour += 12;

		if (halfHourText.equals("30") || halfHourText.equals("40"))
			hour++;

		return hour;

	}

}