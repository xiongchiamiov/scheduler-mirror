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

/**
 * The Class CSVExporter.
 * Exports a model to a CSV formatted string.
 * 
 * Current Status: partially working due to other uninitialized data and inability to test w/ teacher populated schedules.
 * TODO Once fully working, will likely need to be resynced with CSV importer
 * @author Evan Ovadia
 * @author Jordan Hand
 */
public class CSVExporter {
	
	/** The locations. */
	private ArrayList<String[]> locations = new ArrayList<String[]>();
	
	/** The instructors. */
	private ArrayList<String[]> instructors = new ArrayList<String[]>();
	
	/** The instructors time prefs. */
	private ArrayList<String[][]> instructorsTimePrefs = new ArrayList<String[][]>();
	
	/** The instructors course prefs. */
	private ArrayList<String[][]> instructorsCoursePrefs = new ArrayList<String[][]>();
	
	/** The courses. */
	private ArrayList<String[]> courses = new ArrayList<String[]>();
	
	/** The schedule items. */
	private ArrayList<String[]> scheduleItems = new ArrayList<String[]>();
	
	/** The course id. Course dbID -> index in courses */
	private HashMap<Integer,Integer> courseID = new HashMap<Integer,Integer>(); 
	
	/**
	 * Instantiates a new cSV exporter.
	 */
	public CSVExporter() { }
	
	/**
	 * Compile location.
	 * Turns location data into a string and adds it to the global locations ArrayList
	 * @param location A location
	 * @return A string with the location's index
	 */
	private String compileLocation(Location location) {
	//	 location.verify();  //TODO Re-enable. Location.verify uses deprecated item ADA and room TBA has uninitialized data
		
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
					}); 
			//TODO Note: Removed ADA
		}
		
		return "location#" + index;
	}
	
	/**
	 * Compile instructor.
	 * Turns instructor data into a string and adds it to the global instructors ArrayList
	 * @param instructor the instructor
	 * @return A string with the instructor index
	 */
	private String compileInstructor(Instructor instructor) {
			
		int index = instructors.indexOf(instructor);
				if (index < 0) {
		index = instructors.size();
		
		//Separates STAFF due to STAFF having uninitialized variables.
		//Uninitialized variables are commented out. This can potentially can cause issues for CSVimporter
		//Fairness is commented out in both due to it currently not being used.
		if(instructor.getFirstName().equals("STAFF"))
			instructors.add(new String[] {
					"instructor#" + index,
					instructor.getFirstName(),
					instructor.getLastName(),
					instructor.getUserID(),
					Integer.toString(instructor.getMaxWTU()),
					Integer.toString(instructor.getCurWtu()),

			//		Integer.toString(instructor.getFairness()),   
			//TODO  Note: Removed Office/Officeroom
			//		Boolean.toString(instructor.getDisability()),
			//		compileCoursePrefs(instructor.getCoursePreferences()),					
			//		compileTimePrefs(instructor.getTimePreferences())
			});
		else
			instructors.add(new String[] {
					"instructor#" + index,
					instructor.getFirstName(),
					instructor.getLastName(),
					instructor.getUserID(),
					Integer.toString(instructor.getMaxWTU()),
					Integer.toString(instructor.getCurWtu()),
			//		Integer.toString(instructor.getFairness()),   
		    //TODO  Removed Office/Officeroom
					Boolean.toString(instructor.getDisability()),
					compileCoursePrefs(instructor.getCoursePreferences()),					
					compileTimePrefs(instructor.getTimePreferences())
			});

		}
		return "instructor#" + index;
	}

	/**
	 * Compile time prefs.
	 * Turns Time Preference data into a string and adds it to the global instructorTimePrefs ArrayList
	 * @param hashMap A hashmap<Day rows, Hashmap<Time Columns, TimePreference>> mapping the days and times with a teacher's preference for that combination.
	 * @return A string of time prefs. ie Time,SUN,MON,TUE,WED,THU,FRI,SAT
										00:00,5,5,5,5,5,5,5
	 */
	private String compileTimePrefs(HashMap<Integer, LinkedHashMap<Integer, TimePreference>> hashMap) {
		String[][] strings = new String[1 + Time.ALL_TIMES_IN_DAY.length][1 + Day.ALL_DAYS.length];
		
		strings[0][0] = "Time";

		for (int row = 0; row < Time.ALL_TIMES_IN_DAY.length; row++)
			strings[row + 1][0] = Time.ALL_TIMES_IN_DAY[row].toString();

		for (int col = 0; col < Day.ALL_DAYS.length; col++)
			strings[0][col + 1] = Day.ALL_DAYS[col].toString();

		for (int row = 0; row < Time.ALL_TIMES_IN_DAY.length; row++) {
			for (int col = 0; col < Day.ALL_DAYS.length; col++) {
				if (hashMap.get(Day.ALL_DAYS[col]) == null || hashMap.get(Day.ALL_DAYS[col]).get(Time.ALL_TIMES_IN_DAY[row]) == null)
					strings[row + 1][col + 1] = Integer.toString(Instructor.DEFAULT_PREF);
				else
					strings[row + 1][col + 1] = Integer.toString(hashMap.get(Day.ALL_DAYS[col]).get(Time.ALL_TIMES_IN_DAY[row]).getDesire());
			}
		}
		
		int newIndex = instructorsTimePrefs.size();
		instructorsTimePrefs.add(strings);
		return "timePrefs#" + newIndex;
	}

	/**
	 * Compile course prefs.
	 * Turns course preference data into a string and adds it to the global instructorsCoursePrefs ArrayList
	 * @param coursePreferences A hashmap of course ID's to preferences
	 * @return A string representing the course preference index
	 */
	private String compileCoursePrefs(HashMap<Integer, Integer> coursePreferences) {
		String[][] strings = new String[coursePreferences.size()][2];
		int row = 0;
		for (Entry<Integer, Integer> pref : coursePreferences.entrySet()) {
			strings[row][0] = "course#" + courseID.get(pref.getKey()) ;
			strings[row][1] = Integer.toString(pref.getValue()); //Preference for the course
			row++;
		}
		
		int newIndex = instructorsCoursePrefs.size();
		instructorsCoursePrefs.add(strings);
		
		return "coursePrefs#" + newIndex;
	}

	/**
	 * Compile schedule item.
	 * Turns Schedule Item data into a string and adds it to the global scheduleItems ArrayList
	 * @param conflictingScheduleItem True is part of a conflicting schedule item, false if not 
	 * @param item A ScheduleItem
	 * @return the string of the ScheduleItem Number
	 */
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
					});
		}
		
		return "item#" + index;
	}
	
	/**
	 * Compile time.
	 * Converts a Time into a string
	 * @param time The section's time
	 * @return A string of the time.
	 */
	private String compileTime(Time time) {
		return time.getHour() + ":" + time.getMinute();
	}
	
	/**
	 * Compile time range.
	 * Converts a TimeRange into a string
	 * @param timeRange the time range
	 * @return the string of time ranges
	 */
	private String compileTimeRange(TimeRange timeRange) {
		return compileTime(timeRange.getS()) + " to " + compileTime(timeRange.getE());
	}

	/**
	 * Compile week.
	 * Converts a Week into a string
	 * @param week Days in a week a course can be offered
	 * @return the string representing the days in a week a SI can be taught
	 */
	private String compileWeek(Week week) {
		String result = new String();
		for (Day day : week.getDays())
			result += (result.equals("") ? "" : " ") + day.getName();
		return result;
	}

	/**
	 * Compile course.
	 * Turns Course data into a string and adds it to the global courses ArrayList
	 * @param course A course
	 * @return A string representing the course index
	 */
	private String compileCourse(Course course) {
		int index = courses.indexOf(course);
		if (index < 0) {
		    assert(false);
			/*
             * The following was commented out because there is no longer
             * a "getLab()" method for a Course. Instead the getLectureID
             * method should be used. 
             * 
             * If a course is a lecture, the value
             * of the lectureID will be -1. If the course is a lab, the 
             * value of the lectureID will be equal to the lecture id.
             * 
			 * String labIndexString = course.getLab() == null ? "" : "course#" + courses.indexOf(course.getLab());
			 */
		    
			index = courses.size();
			courses.add(new String[] {
					"course#" + index,
					course.getType().toString(),
					course.getName(),
					course.getCatalogNum(),
					course.getDept(),
					Integer.toString(course.getWtu()),
					Integer.toString(course.getScu()),
					Integer.toString(course.getNumOfSections()),
					Integer.toString(course.getLength()),
					compileWeek(course.getDays()),
					Integer.toString(course.getEnrollment()),
					Integer.toString(course.getLectureID()) //For associations
					});
			assert(false);
			/*
			 * Removed labIndexString since it no longer exists. If something
			 * needs to be 
			 */
					//labIndexString});
		}
		
		return "course#" + index;
	}
	
	/**
	 * Export.
	 * Turns a Model into a CSV String
	 * @param model A model
	 * @return The CSV String
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String export(Model model) throws IOException {
		Schedule schedule = model.getSchedule();
		
		/* Gather model information into the global string ArrayLists */
		for (Location location : model.getLocations())
			compileLocation(location);
		
		for (Course course : model.getCourses())
		{
			compileCourse(course);
			courseID.put(course.getDbid(),courses.size() -1); //ADDED
		}
			
		for (Instructor instructor : model.getInstructors())
			compileInstructor(instructor);
		
		for (ScheduleItem item : schedule.getItems())
			compileScheduleItem(false, item);
		
		for (ScheduleItem item : schedule.getDirtyList())
			compileScheduleItem(true, item);
		
		/* Start writing model data to a charArray that'll eventually be turned into a string*/
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