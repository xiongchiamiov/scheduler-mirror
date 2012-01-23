package edu.calpoly.csc.scheduler.model;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Vector;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.TimeRange;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;

public class CSV {
	private static final String[] TOP_COMMENTS = new String[] {
		"(This is a CSV file whose contents represent a schedule.)",
		"(It is highly recommended you make a backup before modifying anything.)",
		"(Feel free to modify it, but please do not modify any lines completely contained in parentheses.)"
	};
	
	private static final String SCHEDULE_MARKER = "(Schedule)";
	private static final String LOCATIONS_MARKER = "(Locations)";
	private static final String INSTRUCTORS_MARKER = "(Instructors)";
	private static final String COURSES_MARKER = "(Courses)";
	private static final String INSTRUCTORS_ITEMS_TAUGHT_MARKER = "(Instructors' Items Taught)";
	private static final String INSTRUCTORS_TIME_PREFS_MARKER = "(Instructors' Time Preferences)";
	private static final String INSTRUCTORS_COURSE_PREFS_MARKER = "(Instructors' Course Preferences)";
	private static final String SCHEDULE_ITEMS_MARKER = "(Schedule Items)";
	
	private ArrayList<String[]> locations = new ArrayList<String[]>();
	private ArrayList<String[]> instructors = new ArrayList<String[]>();
	private ArrayList<String[]> instructorsItemsTaught = new ArrayList<String[]>();
	private ArrayList<String[][]> instructorsTimePrefs = new ArrayList<String[][]>();
	private ArrayList<String[][]> instructorsCoursePrefs = new ArrayList<String[][]>();
	private ArrayList<String[]> courses = new ArrayList<String[]>();
	private ArrayList<String[]> scheduleItems = new ArrayList<String[]>();
	
	public CSV() { }
	
	private String compileLocation(Location location) {
		location.verify();
		
		int index = locations.indexOf(location);
		if (index < 0) {
			index = locations.size();
			locations.add(new String[] {
					"location#" + index,
					location.getBuilding(),
					location.getRoom(),
					Integer.toString(location.getMaxOccupancy()),
					location.getType(),
					Boolean.toString(location.getProvidedEquipment().hasLaptopConnectivity),
					Boolean.toString(location.getProvidedEquipment().hasOverhead),
					Boolean.toString(location.getProvidedEquipment().isSmartRoom),
					Boolean.toString(location.getAdaCompliant())});
		}
		
		return "location#" + index;
	}
	
	private String compileInstructor(Instructor instructor) {
		int index = instructors.indexOf(instructor);
		if (index < 0) {
			index = instructors.size();
			instructors.add(new String[] {
					"instructor#" + index,
					instructor.getUserID(),
					instructor.getFirstName(),
					instructor.getLastName(),
					Integer.toString(instructor.getMaxWTU()),
					Integer.toString(instructor.getCurWtu()),
					instructor.getOffice().getBuilding(),
					instructor.getOffice().getRoom(),
					Integer.toString(instructor.getFairness()),
					Boolean.toString(instructor.getDisability()),
					compileCoursePrefs(instructor.getCoursePreferences()),
					compileTimePrefs(instructor.getTimePreferences()),
					compileItemsTaught(instructor.getItemsTaught())
			});
		}
		return "instructor#" + index;
	}
	
	private String compileItemsTaught(Vector<ScheduleItem> itemsTaught) {
		int newIndex = itemsTaught.size();
		String[] strings = new String[1 + itemsTaught.size()];
		strings[0] = "itemsTaught#" + newIndex;
		for (int i = 0; i < itemsTaught.size(); i++)
			strings[i] = compileScheduleItem(itemsTaught.get(i));
		instructorsItemsTaught.add(strings);
		return "itemsTaught#" + newIndex;
	}

	private String compileTimePrefs(HashMap<Day, LinkedHashMap<Time, TimePreference>> timePreferences) {
		String[][] strings = new String[1 + Time.ALL_TIMES_IN_DAY.length][1 + Day.ALL_DAYS.length];
		
		strings[0][0] = "Time";

		for (int row = 0; row < Time.ALL_TIMES_IN_DAY.length; row++)
			strings[row + 1][0] = Time.ALL_TIMES_IN_DAY[row].toString();

		for (int col = 0; col < Day.ALL_DAYS.length; col++)
			strings[0][col + 1] = Day.ALL_DAYS[col].toString();

		for (int row = 0; row < Time.ALL_TIMES_IN_DAY.length; row++) {
			for (int col = 0; col < Day.ALL_DAYS.length; col++) {
				if (timePreferences.get(Day.ALL_DAYS[col]) == null || timePreferences.get(Day.ALL_DAYS[col]).get(Time.ALL_TIMES_IN_DAY[row]) == null)
					strings[row + 1][col + 1] = Integer.toString(Instructor.DEFAULT_PREF);
				else
					strings[row + 1][col + 1] = Integer.toString(timePreferences.get(Day.ALL_DAYS[col]).get(Time.ALL_TIMES_IN_DAY[row]).getDesire());
			}
		}
		
		int newIndex = instructorsTimePrefs.size();
		instructorsTimePrefs.add(strings);
		return "timePrefs#" + newIndex;
	}

	private String compileCoursePrefs(HashMap<Course, Integer> coursePreferences) {
		String[][] strings = new String[coursePreferences.size()][2];
		int row = 0;
		for (Entry<Course, Integer> pref : coursePreferences.entrySet()) {
			strings[row][0] = compileCourse(pref.getKey());
			strings[row][1] = Integer.toString(pref.getValue());
			row++;
		}
		
		int newIndex = instructorsCoursePrefs.size();
		instructorsCoursePrefs.add(strings);
		return "coursePrefs#" + newIndex;
	}

	private String compileScheduleItem(ScheduleItem item) {
		int index = scheduleItems.indexOf(item);
		if (index < 0) {
			String labsString = "";
			for (ScheduleItem lab : item.getLabs()) {
				if (!labsString.equals(""))
					labsString += " ";
				labsString += compileScheduleItem(lab);
			}
			
			index = scheduleItems.size();
			scheduleItems.add(new String[] {
					"item#" + index,
					compileInstructor(item.getInstructor()),
					compileCourse(item.getCourse()),
					compileLocation(item.getLocation()),
					Integer.toString(item.getSection()),
					compileWeek(item.getDays()),
					Double.toString(item.getValue()),
					compileTimeRange(item.getTimeRange()),
					"Locked?",
					labsString});
		}
		
		return "item#" + index;
	}
	
	private String compileTimeRange(TimeRange timeRange) {
		return timeRange.toString();
	}

	private String compileWeek(Week week) {
		return week.toString();
	}

	private String compileCourse(Course course) {
		int index = courses.indexOf(course);
		if (index < 0) {
			String labID = "none";
			assert(false);
			/*
			 * This was commented out because there is no longer
			 * a "getLab()" method for a Course. Instead the getLectureID
			 * method should be used. 
			 * 
			 * If a course is a lecture, the value
			 * of the lectureID will be -1. If the course is a lab, the 
			 * value of the lectureID will be equal to the lecture id.
			 * 
			    if (course.getLab() != null)
				labID = compileCourse(course.getLab());
			*/
			index = courses.size();
			courses.add(new String[] {
					"course#" + index,
					course.getName(),
					Integer.toString(course.getCatalogNum()),
					course.getDept(),
					Integer.toString(course.getWtu()),
					Integer.toString(course.getScu()),
					Integer.toString(course.getNumOfSections()),
					course.getType().toString(),
					Integer.toString(course.getLength()),
					compileWeek(course.getDays()),
					Integer.toString(course.getEnrollment()),
					labID});
		}
		
		return "course#" + index;
	}

	public String export(Model model, Schedule schedule) throws IOException {
		for (Location location : model.getLocations())
			compileLocation(location);
		for (Instructor instructor : model.getInstructors())
			compileInstructor(instructor);
		for (Course course : model.getCourses())
			compileCourse(course);
		for (ScheduleItem item : schedule.getItems())
			compileScheduleItem(item);
		
		Writer stringWriter = new CharArrayWriter();
		CsvWriter writer = new CsvWriter(stringWriter, ',');

		for (String topComment : TOP_COMMENTS)
			writer.writeComment(topComment);
		
		writer.endRecord();
		writer.writeComment(SCHEDULE_MARKER);
		writer.write("NameHere");
		

		writer.endRecord();
		writer.writeComment(LOCATIONS_MARKER);
		for (int i = 0; i < locations.size(); i++) {
			writer.writeRecord(locations.get(i));
		}

		writer.endRecord();
		writer.writeComment(INSTRUCTORS_MARKER);
		for (int i = 0; i < instructors.size(); i++) {
			writer.writeRecord(instructors.get(i));
		}

		writer.endRecord();
		writer.writeComment(COURSES_MARKER);
		for (int i = 0; i < courses.size(); i++) {
			writer.writeRecord(courses.get(i));
		}

		writer.endRecord();
		writer.writeComment(INSTRUCTORS_ITEMS_TAUGHT_MARKER);
		for (int i = 0; i < instructorsItemsTaught.size(); i++) {
			writer.writeRecord(instructorsItemsTaught.get(i));
		}

		writer.endRecord();
		writer.writeComment(INSTRUCTORS_TIME_PREFS_MARKER);
		for (int i = 0; i < instructorsTimePrefs.size(); i++) {
			writer.write("timePrefs#" + i + ":");
			writer.endRecord();
			String[][] prefs = instructorsTimePrefs.get(i);
			for (String[] rec : prefs)
				writer.writeRecord(rec);
		}

		writer.endRecord();
		writer.writeComment(INSTRUCTORS_COURSE_PREFS_MARKER);
		for (int i = 0; i < instructorsCoursePrefs.size(); i++) {
			writer.write("coursePrefs#" + i + ":");
			writer.endRecord();
			for (String[][] prefs : instructorsCoursePrefs)
				for (String[] rec : prefs)
					writer.writeRecord(rec);
		}

		writer.endRecord();
		writer.writeComment(SCHEDULE_ITEMS_MARKER);
		for (int i = 0; i < scheduleItems.size(); i++) {
			writer.writeRecord(scheduleItems.get(i));
		}
		
		writer.flush();
		writer.close();
		stringWriter.flush();
		stringWriter.close();
		
		return stringWriter.toString();
	}

	public void read(Model model, String value) throws IOException {
		Reader stringReader = new StringReader(value);
		CsvReader reader = new CsvReader(stringReader);
		
		Collection<String[]> lines = new LinkedList<String[]>();
		for (String[] line; (line = reader.getValues()) != null; ) {
			if (line.length == 0)
				continue;
			if (line.length == 1 && line[0].trim().equals(""))
				continue;
			lines.add(line);
		}
		
		for (String[] line : lines) {
			System.out.println(line.length);
		}
	}
}
