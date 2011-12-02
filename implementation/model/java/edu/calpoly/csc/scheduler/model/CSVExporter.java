package edu.calpoly.csc.scheduler.model;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

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

public class CSVExporter {
	private ArrayList<String[]> locations = new ArrayList<String[]>();
	private ArrayList<String[]> instructors = new ArrayList<String[]>();
	private ArrayList<String[][]> instructorsTimePrefs = new ArrayList<String[][]>();
	private ArrayList<String[][]> instructorsCoursePrefs = new ArrayList<String[][]>();
	private ArrayList<String[]> courses = new ArrayList<String[]>();
	private ArrayList<String[]> scheduleItems = new ArrayList<String[]>();
	
	public CSVExporter() { }
	
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
					instructor.getFirstName(),
					instructor.getLastName(),
					instructor.getUserID(),
					Integer.toString(instructor.getMaxWTU()),
					Integer.toString(instructor.getCurWtu()),
					instructor.getOffice().getBuilding(),
					instructor.getOffice().getRoom(),
					Integer.toString(instructor.getFairness()),
					Boolean.toString(instructor.getDisability()),
					compileCoursePrefs(instructor.getCoursePreferences()),
					compileTimePrefs(instructor.getTimePreferences())
			});
		}
		return "instructor#" + index;
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

	private String compileScheduleItem(boolean conflictingScheduleItem, ScheduleItem item) {
		int index = scheduleItems.indexOf(item);
		if (index < 0) {
			String labsString = "";
			for (ScheduleItem lab : item.getLabs()) {
				if (!labsString.equals(""))
					labsString += " ";
				labsString += compileScheduleItem(false, lab);
			}
			
			index = scheduleItems.size();
			scheduleItems.add(new String[] {
					"item#" + index,
					Boolean.toString(conflictingScheduleItem),
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
	
	private String compileTime(Time time) {
		return time.getHour() + ":" + time.getMinute();
	}
	
	private String compileTimeRange(TimeRange timeRange) {
		return compileTime(timeRange.getS()) + " to " + compileTime(timeRange.getE());
	}

	private String compileWeek(Week week) {
		String result = new String();
		for (Day day : week.getDays())
			result += (result.equals("") ? "" : " ") + day.getName();
		return result;
	}

	private String compileCourse(Course course) {
		int index = courses.indexOf(course);
		if (index < 0) {
			String labIndexString = course.getLab() == null ? "" : "course#" + courses.indexOf(course.getLab());
			
			index = courses.size();
			courses.add(new String[] {
					"course#" + index,
					course.getType().toString(),
					course.getName(),
					Integer.toString(course.getCatalogNum()),
					course.getDept(),
					Integer.toString(course.getWtu()),
					Integer.toString(course.getScu()),
					Integer.toString(course.getNumOfSections()),
					Integer.toString(course.getLength()),
					compileWeek(course.getDays()),
					Integer.toString(course.getEnrollment()),
					labIndexString});
		}
		
		return "course#" + index;
	}
	
	public String export(Model model) throws IOException {
		Schedule schedule = model.getSchedule();
		
		for (Location location : model.getLocations())
			compileLocation(location);
		
		for (Instructor instructor : model.getInstructors())
			compileInstructor(instructor);
		
		for (Course course : model.getCourses())
			compileCourse(course);
		
		for (ScheduleItem item : schedule.getItems())
			compileScheduleItem(false, item);
		
		for (ScheduleItem item : schedule.getDirtyList())
			compileScheduleItem(true, item);
		
		Writer stringWriter = new CharArrayWriter();
		CsvWriter writer = new CsvWriter(stringWriter, ',');

		for (String topComment : CSVStructure.TOP_COMMENTS)
			writer.writeComment(topComment);
		
		writer.endRecord();
		writer.writeComment(CSVStructure.SCHEDULE_MARKER);
		writer.write(schedule.getName());
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
}
