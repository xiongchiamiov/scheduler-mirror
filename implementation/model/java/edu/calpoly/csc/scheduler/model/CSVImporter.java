package edu.calpoly.csc.scheduler.model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.csvreader.CsvReader;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.TimeRange;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.Course.CourseType;
import edu.calpoly.csc.scheduler.model.db.cdb.Lab;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.Location.ProvidedEquipment;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;

public class CSVImporter {
	List<Course> courses;
	List<Location> locations;
	List<HashMap<Course, Integer>> instructorsCoursePrefs;
	List<HashMap<Day, LinkedHashMap<Time, TimePreference>>> instructorsTimePrefs;
	List<Instructor> instructors;
	List<ScheduleItem> scheduleItems;
	
	public void read(Model model, String value) throws IOException {
		Reader stringReader = new StringReader(value);
		CsvReader reader = new CsvReader(stringReader);
		
		Collection<List<String>> lines = new LinkedList<List<String>>();
		for (String[] line; (line = reader.getValues()) != null; ) {
			if (line.length == 0)
				continue;
			if (line.length == 1 && line[0].trim().equals(""))
				continue;
			lines.add(Arrays.asList(line));
		}
		
		Iterator<List<String>> linesIterator = lines.iterator();
		
		readSchedule(linesIterator);
		
		readCourses(linesIterator);
		
		readLocations(linesIterator);
		
		readAllInstructorsCoursePrefs(linesIterator);
		
		readAllInstructorsTimePrefs(linesIterator);
		
		readInstructors(linesIterator);
		
		readScheduleItems(linesIterator);
	}
	
	private HashMap<Course, Integer> readSingleInstructorsCoursePrefs(Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.INSTRUCTORS_COURSE_PREFS_MARKER);

		HashMap<Course, Integer> instructorCoursePrefs = new HashMap<Course, Integer>();
		
		while (true) {			
			assert(linesIterator.hasNext());

			List<String> cells = linesIterator.next();
			if (cells.size() == 1 && cells.get(0).equals(CSVStructure.INSTRUCTOR_COURSE_PREFS_END_MARKER))
				break;

			Iterator<String> cellI = cells.iterator();
			Course course = courses.get(extractIndex("course#", cellI.next()));
			Integer desire = Integer.parseInt(cellI.next());
			instructorCoursePrefs.put(course, desire);
		}
		
		instructorsCoursePrefs.add(instructorCoursePrefs);
		
		return instructorCoursePrefs;
	}

	private void readAllInstructorsCoursePrefs(Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.INSTRUCTORS_COURSE_PREFS_MARKER);
		
		while (true) {
			assert(linesIterator.hasNext());

			List<String> cells = linesIterator.next();
			if (cells.size() == 1 && cells.get(0).equals(CSVStructure.INSTRUCTORS_COURSE_PREFS_END_MARKER))
				break;

			Iterator<String> cellI = cells.iterator();

			int instructorCoursePrefIndex = instructorsCoursePrefs.size();
			assert(extractIndex("coursePrefs#", cellI.next()) == instructorCoursePrefIndex);
			
			instructorsCoursePrefs.add(readSingleInstructorsCoursePrefs(linesIterator));
		}
	}
	
	private void readAllInstructorsTimePrefs(Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.INSTRUCTORS_COURSE_PREFS_MARKER);
		
		while (true) {
			assert(linesIterator.hasNext());

			List<String> cells = linesIterator.next();
			if (cells.size() == 1 && cells.get(0).equals(CSVStructure.INSTRUCTORS_COURSE_PREFS_END_MARKER))
				break;

			int instructorTimePrefIndex = instructorsTimePrefs.size();
			
			instructorsTimePrefs.add(readSingleInstructorsTimePrefs(instructorTimePrefIndex, linesIterator));
		}
	}

	private HashMap<Day, LinkedHashMap<Time, TimePreference>> readSingleInstructorsTimePrefs(
			int instructorTimePrefIndex, Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.INSTRUCTOR_TIME_PREFS_MARKER);
		
		List<String> indexLine = linesIterator.next();
		assert(extractIndex("timePrefs#", indexLine.get(0)) == instructorTimePrefIndex);

		List<String> headersLine = linesIterator.next();
		Iterator<String> headerCellI = headersLine.iterator();
		assert(headerCellI.next() == "Time");
		
		HashMap<Day, LinkedHashMap<Time, TimePreference>> instructorTimePrefs = new HashMap<Day, LinkedHashMap<Time, TimePreference>>();

		for (Day day : Day.ALL_DAYS) {
			assert(headerCellI.next() == day.getName());
			instructorTimePrefs.put(day, new LinkedHashMap<Time, TimePreference>());
		}

		for (int row = 0; row < Time.ALL_TIMES_IN_DAY.length; row++) {
			assert(linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			Iterator<String> cellI = cells.iterator();
			String timeString = cellI.next();
			Time time = Time.ALL_TIMES_IN_DAY[row];
			assert(timeString.equals(time.toString()));
			
			for (int col = 0; col < Day.ALL_DAYS.length; col++) {
				int desire = Integer.parseInt(cellI.next());
				TimePreference preference = new TimePreference(time, desire);
				instructorTimePrefs.get(Day.ALL_DAYS[col]).put(Time.ALL_TIMES_IN_DAY[row], preference);
			}
		}

		return instructorTimePrefs;
	}

	private Integer extractIndex(String prefix, String indexString) {
		if (indexString.equals(""))
			return null;
		assert(indexString.startsWith(prefix));
		return Integer.parseInt(indexString.substring(prefix.length()));
	}
	
	void readCourses(Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.COURSES_MARKER);

		HashMap<Integer, Integer> labIndexByCourseIndex = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> componentIndexByLabIndex = new HashMap<Integer, Integer>();
		
		while (true) {
			assert(linesIterator.hasNext());
			
			List<String> cells = linesIterator.next();
			if (cells.size() == 1 && cells.get(0).equals(CSVStructure.LOCATIONS_END_MARKER))
				break;
	
			Iterator<String> cellI = cells.iterator();

			int index = extractIndex("course#", cellI.next());
			assert(index == courses.size());
			
			CourseType type = CourseType.valueOf(cellI.next());
			Course course = (type == CourseType.LEC ? new Course() : new Lab());
			course.setType(cellI.next());
			
			course.setName(cellI.next());
			course.setCatalogNum(Integer.parseInt(cellI.next()));
			course.setDept(cellI.next());
			course.setWtu(Integer.parseInt(cellI.next()));
			course.setScu(Integer.parseInt(cellI.next()));
			course.setNumOfSections(Integer.parseInt(cellI.next()));
			course.setLength(Integer.parseInt(cellI.next()));
			course.setDays(readWeek(cellI.next()));
			course.setEnrollment(Integer.parseInt(cellI.next()));
			
			Integer labIndex = extractIndex("course#", cellI.next());
			if (labIndex != null)
				labIndexByCourseIndex.put(index, labIndex);
			
			if (course instanceof Lab) {
				Lab lab = (Lab)course;
				lab.setTethered(Boolean.parseBoolean(cellI.next()));
				
				Integer componentIndex = extractIndex("course#", cellI.next());
				if (componentIndex != null)
					componentIndexByLabIndex.put(index, componentIndex);
				
				lab.setUseLectureInstructor(Boolean.parseBoolean(cellI.next()));
			}
			
			courses.add(course);
		}
		
		for (Entry<Integer, Integer> entry : labIndexByCourseIndex.entrySet()) {
			Integer courseIndex = entry.getKey();
			Integer labIndex = entry.getValue();
			courses.get(courseIndex).setLab((Lab)courses.get(labIndex));
		}
		
		for (Entry<Integer, Integer> entry : componentIndexByLabIndex.entrySet()) {
			Integer labIndex = entry.getKey();
			Integer componentIndex = entry.getValue();
			((Lab)courses.get(labIndex)).setComponent(courses.get(componentIndex));
		}
	}

	private Week readWeek(String string) {
		Week result = new Week();
		
		String[] dayStrings = string.split(" ");
		for (String dayString : dayStrings) {
			for (Day possibleDay : Day.ALL_DAYS) {
				if (dayString.equals(possibleDay.getName())) {
					result.add(possibleDay);
					break;
				}
			}
		}
		
		assert(result.getDays().size() == dayStrings.length);
		
		return result;
	}

	void readInstructors(Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.INSTRUCTORS_MARKER);
		while (true) {
			assert(linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			if (cells.size() == 1 && cells.get(0).equals(CSVStructure.INSTRUCTORS_END_MARKER))
				return;

			Iterator<String> cellI = cells.iterator();

			int index = instructors.size();
			assert(extractIndex("instructor#", cellI.next()) == index);
			
			Instructor instructor = new Instructor();
			instructor.setUserID(cellI.next());
			instructor.setFirstName(cellI.next());
			instructor.setLastName(cellI.next());
			instructor.setMaxWtu(Integer.parseInt(cellI.next()));
			instructor.setCurWtu(Integer.parseInt(cellI.next()));
			
			Location office = new Location();
			office.setBuilding(cellI.next());
			office.setRoom(cellI.next());
			instructor.setOffice(office);

			instructor.setFairness(Integer.parseInt(cellI.next()));
			instructor.setDisability(Boolean.parseBoolean(cellI.next()));
			
			instructor.setCoursePreferences(instructorsCoursePrefs.get(extractIndex("coursePrefs#", cellI.next())));
			instructor.setTimePreferences(instructorsTimePrefs.get(extractIndex("timePrefs#", cellI.next())));
			
			instructors.add(instructor);
		}
	}

	void readLocations(Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.LOCATIONS_MARKER);
		while (true) {
			assert(linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			if (cells.size() == 1 && cells.get(0).equals(CSVStructure.LOCATIONS_END_MARKER))
				return;

			Iterator<String> cellI = cells.iterator();

			int index = instructors.size();
			assert(extractIndex("instructor#", cellI.next()) == index);
			
			Location location = new Location();
			location.setBuilding(cellI.next());
			location.setRoom(cellI.next());
			location.setMaxOccupancy(Integer.parseInt(cellI.next()));
			location.setType(cellI.next());
			
			ProvidedEquipment equipment = new ProvidedEquipment();
			equipment.hasLaptopConnectivity = Boolean.parseBoolean(cellI.next());
			equipment.hasOverhead = Boolean.parseBoolean(cellI.next());
			equipment.isSmartRoom = Boolean.parseBoolean(cellI.next());
			location.setProvidedEquipment(equipment);
			
			location.setAdaCompliant(Boolean.parseBoolean(cellI.next()));
						
			locations.add(location);
		}
	}
	
	void skipUntilComment(Iterator<List<String>> lineIterator, String comment) {
		while (lineIterator.hasNext()) {
			List<String> line = lineIterator.next();
			if (line.size() == 1 && line.get(0).equals(comment))
				return;
		}
		
		assert(false);
	}
	
	void readSchedule(Iterator<List<String>> lineIterator) {
		skipUntilComment(lineIterator, CSVStructure.SCHEDULE_MARKER);
		List<String> line = lineIterator.next();
		assert(line.size() == 1 && line.get(0).equals("NameHere"));
		skipUntilComment(lineIterator, CSVStructure.SCHEDULE_END_MARKER);
	}

	void readScheduleItems(Iterator<List<String>> linesIterator) {
		skipUntilComment(linesIterator, CSVStructure.LOCATIONS_MARKER);
		while (true) {
			assert(linesIterator.hasNext());
			List<String> cells = linesIterator.next();
			if (cells.size() == 1 && cells.get(0).equals(CSVStructure.LOCATIONS_END_MARKER))
				return;

			Iterator<String> cellI = cells.iterator();

			int index = instructors.size();
			assert(extractIndex("instructor#", cellI.next()) == index);
			
			ScheduleItem item = new ScheduleItem();
			item.setInstructor(instructors.get(extractIndex("instructor#", cellI.next())));
			item.setCourse(courses.get(extractIndex("course#", cellI.next())));
			item.setLocation(locations.get(extractIndex("location#", cellI.next())));
			item.setSection(Integer.parseInt(cellI.next()));
			item.setDays(readWeek(cellI.next()));
			// Value?
			item.setTimeRange(readTimeRange(cellI.next()));
			// Locked?
			// Labs?
			
			scheduleItems.add(item);
		}
	}
	
	TimeRange readTimeRange(String string) {
		String startString = string.substring(0, string.indexOf(" to "));
		String endString = string.substring(string.indexOf(" to ") + " to ".length());
		return new TimeRange(readTime(startString), readTime(endString));
	}
	
	Time readTime(String string) {
		return new Time(string);
	}
}
