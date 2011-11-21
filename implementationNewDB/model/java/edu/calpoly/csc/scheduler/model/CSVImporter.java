package edu.calpoly.csc.scheduler.model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.csvreader.CsvReader;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;

public class CSVImporter {
	List<Course> courses;
	List<Location> locations;
	List<HashMap<Course, Integer>> coursePrefs;
	List<HashMap<Day, LinkedHashMap<Time, TimePreference>>> timePrefs;
	List<Vector<ScheduleItem>> itemsTaught;
	List<Instructor> instructors;
	List<ScheduleItem> scheduleItems;
	
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
		
		Iterator<String[]> lineIterator = lines.iterator();
		
		readSchedule(lineIterator);
		
		readCourses(lineIterator);
		
		readLocations(lineIterator);
	}

	private int extractIndex(String prefix, String indexString) {
		assert(indexString.startsWith(prefix));
		return Integer.parseInt(indexString.substring(prefix.length()));
	}
	
	void readCourses(Iterator<String[]> lineIterator) {
		skipUntilComment(lineIterator, CSVStructure.COURSES_MARKER);
		
//		start here
	}

	void readLocations(Iterator<String[]> lineIterator) {
		skipUntilComment(lineIterator, CSVStructure.LOCATIONS_MARKER);
		while (lineIterator.hasNext()) {
			String[] line = lineIterator.next();
			if (line.length == 1 && line[0].equals(CSVStructure.LOCATIONS_END_MARKER))
				return;
			assert(line[0].equals("instructor#" + instructors.size()));
			Instructor instructor = new Instructor();
			instructor.setUserID(line[1]);
			instructor.setFirstName(line[2]);
			instructor.setLastName(line[3]);
			instructor.setMaxWtu(Integer.parseInt(line[4]));
			instructor.setCurWtu(Integer.parseInt(line[5]));
			
			Location office = new Location();
			office.setBuilding(line[6]);
			office.setRoom(line[7]);
			instructor.setOffice(office);

			instructor.setFairness(Integer.parseInt(line[8]));
			instructor.setDisability(Boolean.parseBoolean(line[9]));
			
			instructor.setCoursePreferences(coursePrefs.get(extractIndex("coursePrefs#", line[10])));
			instructor.setTimePreferences(timePrefs.get(extractIndex("timePrefs#", line[11])));
			instructor.setItemsTaught(itemsTaught.get(extractIndex("itemsTaught#", line[12])));
			
			instructors.add(instructor);
		}
		
		assert(false);
	}
	
	void skipUntilComment(Iterator<String[]> lineIterator, String comment) {
		while (lineIterator.hasNext()) {
			String[] line = lineIterator.next();
			if (line.length == 1 && line[0].equals(comment))
				return;
		}
		
		assert(false);
	}
	
	void readSchedule(Iterator<String[]> lineIterator) {
		skipUntilComment(lineIterator, CSVStructure.SCHEDULE_MARKER);
		String[] line = lineIterator.next();
		assert(line.length == 1 && line[0].equals("NameHere"));
		skipUntilComment(lineIterator, CSVStructure.SCHEDULE_END_MARKER);
	}
}
