package edu.calpoly.csc.scheduler.model.tempalgorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Identified;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.Schedule;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

class BlockedOffTimes {
	boolean[][] blockedOffTimes;
	
	public BlockedOffTimes() {
		blockedOffTimes = new boolean[Day.values().length][48];
	}

	public void blockOffTimes(Set<Day> days, int startHalfHour, int endHalfHour) {
		for (Day day : days) {
			for (int halfHour = startHalfHour; halfHour < endHalfHour; halfHour++) {
				blockedOffTimes[day.ordinal()][halfHour] = true;
			}
		}
	}
}

class InstructorDecorator {
	Instructor instructor;
	BlockedOffTimes blockedOffTimes;
	
	InstructorDecorator(Instructor instructor) {
		this.instructor = instructor;
		this.blockedOffTimes = new BlockedOffTimes();
	}
}

class LocationDecorator {
	Location location;
	BlockedOffTimes blockedOffTimes;
	
	LocationDecorator(Location location) {
		this.location = location;
		this.blockedOffTimes = new BlockedOffTimes();
	}
}

class CourseDecorator {
	Course course;
	int numSectionsScheduled;
	
	CourseDecorator(Course course, int numSectionsScheduled) {
		this.course = course;
		this.numSectionsScheduled = numSectionsScheduled;
	}
	
	@Override
	public String toString() {
		return course.getDepartment() + " " + course.getCatalogNumber();
	}
}

class GenerationDataLayer {
	public static void blockOffInstructorUnacceptableTimes(InstructorDecorator instructor, Document document, HashMap<Integer, ScheduleItem> items) {
		for (Day day : Day.values()) {
			for (int halfHour = 0; halfHour < 48; halfHour++) {
				int preference = instructor.instructor.getTimePreferences()[day.ordinal()][halfHour];
				if (preference == 0)
					instructor.blockedOffTimes.blockedOffTimes[day.ordinal()][halfHour] = true;
			}
		}
	}

	private static <T extends Identified> HashMap<Integer, T> collectionToMap(Collection<T> coll) {
		HashMap<Integer, T> result = new HashMap<Integer, T>();
		for (T t : coll)
			result.put(t.getID(), t);
		return result;
	}

	private final Model model;
	private final Document document;
	private final Schedule schedule;
	private final HashMap<Integer, ScheduleItem> existingItems;
	private final HashMap<Integer, InstructorDecorator> instructors;
	private final HashMap<Integer, LocationDecorator> locations;
	private final HashMap<Integer, CourseDecorator> courses;
	
	public GenerationDataLayer(Model model, Schedule schedule) throws NotFoundException {
		this.model = model;
		this.schedule = schedule;
		this.document = model.getDocumentForSchedule(schedule);

		Collection<ScheduleItem> existingItemsList = model.findAllScheduleItemsForSchedule(schedule);
		Collection<Course> coursesToScheduleList = model.findCoursesForDocument(document);
		Collection<Instructor> sourceInstructorsList = model.findInstructorsForDocument(document);
		Collection<Location> sourceLocationsList = model.findLocationsForDocument(document);

		assert(!coursesToScheduleList.isEmpty());
		assert(!sourceInstructorsList.isEmpty());
		assert(!sourceLocationsList.isEmpty());

		existingItems = collectionToMap(existingItemsList);

		instructors = new HashMap<Integer, InstructorDecorator>();
		for (Instructor innerInstructor : sourceInstructorsList) {
			InstructorDecorator instructor = new InstructorDecorator(innerInstructor);

			blockOffInstructorUnacceptableTimes(instructor, document, existingItems);
			
			for (ScheduleItem item : existingItems.values()) {
				if (item.isConflicted())
					continue;
				instructor.blockedOffTimes.blockOffTimes(item.getDays(), item.getStartHalfHour(), item.getEndHalfHour());
			}
			
			instructors.put(innerInstructor.getID(), instructor);
		}
		
		locations = new HashMap<Integer, LocationDecorator>();
		for (Location innerLocation : sourceLocationsList) {
			LocationDecorator location = new LocationDecorator(innerLocation);

			for (ScheduleItem item : existingItems.values()) {
				if (item.isConflicted())
					continue;
				location.blockedOffTimes.blockOffTimes(item.getDays(), item.getStartHalfHour(), item.getEndHalfHour());
			}
			
			locations.put(innerLocation.getID(), location);
		}

		courses = new HashMap<Integer, CourseDecorator>();
		for (Course course : coursesToScheduleList) {
			courses.put(course.getID(), new CourseDecorator(course, 0));
		}
		
		for (ScheduleItem item : existingItemsList) {
			if (!courses.containsKey(item.getCourseID()))
				continue;
			CourseDecorator course = courses.get(item.getCourseID());
			course.numSectionsScheduled++;
		}
		
	}

	public void insertNewScheduleItem(ScheduleItem item) {
		System.out.println("Inserting schedule item! Instructor " + item.getInstructorID() + " location " + item.getLocationID() + " course " + item.getCourseID() + " days " + item.getDays() + " from " + item.getStartHalfHour() + " to " + item.getEndHalfHour());
		
		if (instructorIsFreeDuring(findInstructorByID(item.getInstructorID()), item.getDays(), item.getStartHalfHour(), item.getEndHalfHour()) &&
				locationIsFreeDuring(findLocationByID(item.getLocationID()), item.getDays(), item.getStartHalfHour(), item.getEndHalfHour())) {
			instructors.get(item.getInstructorID()).blockedOffTimes.blockOffTimes(item.getDays(), item.getStartHalfHour(), item.getEndHalfHour());
			locations.get(item.getLocationID()).blockedOffTimes.blockOffTimes(item.getDays(), item.getStartHalfHour(), item.getEndHalfHour());
			item.setIsConflicted(false);
		}
		else {
			item.setIsConflicted(true);
		}
		
		model.insertScheduleItem(item);
	}
	

	InstructorDecorator findInstructorFreeDuring(
			Set<Day> dayPattern, int startHalfHour, int endHalfHour) {
		
		for (InstructorDecorator instructor : instructors.values())
			if (instructorIsFreeDuring(instructor, dayPattern, startHalfHour, endHalfHour))
				return instructor;
		
		return null;
	}

	public static boolean instructorIsFreeDuring(
			InstructorDecorator instructor, Set<Day> dayPattern,
			int startHalfHour, int endHalfHour) {
		System.out.println("Checking if instructor " + instructor.instructor.getUsername() + " is free " + dayPattern + " from " + startHalfHour + " to " + endHalfHour);
		
		for (Day day : dayPattern) {
			for (int halfHour = startHalfHour; halfHour < endHalfHour; halfHour++) {
				if (instructor.blockedOffTimes.blockedOffTimes[day.ordinal()][halfHour])
					return false;
			}
		}
		return true;
	}

	LocationDecorator findLocationFreeDuring(
			Set<Day> dayPattern, int startHalfHour, int endHalfHour) {
		
		for (LocationDecorator location : locations.values())
			if (locationIsFreeDuring(location, dayPattern, startHalfHour, endHalfHour))
				return location;
		
		return null;
	}

	public static boolean locationIsFreeDuring(
			LocationDecorator location, Set<Day> dayPattern,
			int startHalfHour, int endHalfHour) {
		for (Day day : dayPattern) {
			for (int halfHour = startHalfHour; halfHour < endHalfHour; halfHour++) {
				if (location.blockedOffTimes.blockedOffTimes[day.ordinal()][halfHour])
					return false;
			}
		}
		return true;
	}

	public ScheduleItem assembleScheduleItem(Course course,
			Instructor instructor, Location location, int newSectionNumber,
			Set<Day> dayPattern, int startHalfHour, int endHalfHour, boolean b,
			boolean c) {
		return model.assembleScheduleItem(schedule, course, instructor, location, newSectionNumber, dayPattern, startHalfHour, endHalfHour, b, c);
	}

	public Collection<CourseDecorator> getCourses() { return courses.values(); }

	public int getDocumentStartHalfHour() { return document.getStartHalfHour(); }

	public int getDocumentEndHalfHour() { return document.getEndHalfHour(); }

	public InstructorDecorator findInstructorByID(int instructorID) {
		return instructors.get(instructorID);
	}

	public LocationDecorator findLocationByID(int locationID) {
		return locations.get(locationID);
	}
}

public class GenerationAlgorithm {
	public static class CouldNotBeScheduledException extends Exception { }
	
	public static Collection<ScheduleItem> generateRestOfSchedule(Model model, Schedule schedule)
			throws CouldNotBeScheduledException, NotFoundException {

		GenerationDataLayer dataLayer = new GenerationDataLayer(model, schedule);
		
		// LAUNCH ZE ALGORIZM
		Collection<ScheduleItem> result = new LinkedList<ScheduleItem>();
		for (CourseDecorator courseToSchedule : dataLayer.getCourses()) {
			result.addAll(generateAndInsertScheduleItemsForCourse(dataLayer, courseToSchedule));
		}
		return result;
	}
	
	private static Collection<ScheduleItem> generateAndInsertScheduleItemsForCourse(GenerationDataLayer layer, CourseDecorator course) throws CouldNotBeScheduledException {
		Collection<ScheduleItem> result = new LinkedList<ScheduleItem>();
		for (int sectionNumber = course.numSectionsScheduled; sectionNumber < course.course.getNumSectionsInt(); sectionNumber++) {
			ScheduleItem item = generateScheduleItemForCourse(layer, course, sectionNumber);
			if (item == null)
				throw new CouldNotBeScheduledException();
			layer.insertNewScheduleItem(item);
			result.add(item);
		}
		return result;
	}
	
	private static ScheduleItem generateScheduleItemForCourse(
			GenerationDataLayer layer, CourseDecorator course, int newSectionNumber) {
		System.out.println("Generating course " + course.toString() + " section " + newSectionNumber);
		for (Set<Day> possibleDayPattern : course.course.getDayPatterns()) {
			ScheduleItem newScheduleItem = generateScheduleItemForCourseWithDayPattern(layer, course, newSectionNumber, possibleDayPattern);
			if (newScheduleItem != null)
				return newScheduleItem;
		}
		return null;
	}

	private static ScheduleItem generateScheduleItemForCourseWithDayPattern(
			GenerationDataLayer layer, CourseDecorator course,
			int newSectionNumber, Set<Day> dayPattern) {
		System.out.println("Trying generating course " + course.toString() + " section " + newSectionNumber + " on days " + dayPattern);
		int numHalfHoursPerDay = course.course.getNumHalfHoursPerWeekInt() / dayPattern.size();
		for (int startHalfHour = layer.getDocumentStartHalfHour(); startHalfHour + numHalfHoursPerDay < layer.getDocumentEndHalfHour(); startHalfHour++) {
			ScheduleItem newScheduleItem = generateScheduleItemForCourseWithDayPatternStartingAtTime(layer, course, newSectionNumber, dayPattern, startHalfHour);
			if (newScheduleItem != null)
				return newScheduleItem;
		}
		return null;
	}

	private static ScheduleItem generateScheduleItemForCourseWithDayPatternStartingAtTime(
			GenerationDataLayer layer, CourseDecorator course, int newSectionNumber,
			Set<Day> dayPattern, int startHalfHour) {
		int numHalfHoursPerDay = course.course.getNumHalfHoursPerWeekInt() / dayPattern.size();
		int endHalfHour = startHalfHour + numHalfHoursPerDay;

		System.out.println("Trying generating course " + course.toString() + " section " + newSectionNumber + " on days " + dayPattern + " from " + startHalfHour + " to " + endHalfHour);

		InstructorDecorator instructorFreeAtThatTime = layer.findInstructorFreeDuring(dayPattern, startHalfHour, endHalfHour);
		if (instructorFreeAtThatTime == null) {
			System.out.println("No instructor free at that time.");
			return null;
		}

		LocationDecorator locationFreeAtThatTime = layer.findLocationFreeDuring(dayPattern, startHalfHour, endHalfHour);
		if (locationFreeAtThatTime == null) {
			System.out.println("No location free at that time.");
			return null;
		}
		
		return layer.assembleScheduleItem(course.course, instructorFreeAtThatTime.instructor, locationFreeAtThatTime.location, newSectionNumber, dayPattern, startHalfHour, endHalfHour, false, false);
	}

	public static void insertNewScheduleItem(Model model, Schedule schedule, ScheduleItem item) throws NotFoundException {
		GenerationDataLayer dataLayer = new GenerationDataLayer(model, schedule);
		
		insertNewScheduleItem(dataLayer, item);
	}

	private static void insertNewScheduleItem(GenerationDataLayer dataLayer, ScheduleItem item) {
		dataLayer.insertNewScheduleItem(item);
	}
}
