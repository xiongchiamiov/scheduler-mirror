package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.Schedule;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public abstract class Conversion {
//	public static UserData fromGWT(UserDataGWT gwt) {
//		UserData user = new UserData();
//		user.setDbid(gwt.getID());
//		user.setPermission(gwt.getPermissionLevel());
//		user.setUserId(gwt.getUserName());
//		user.setScheduleDBId(gwt.getScheduleID());
//		return user;
//	}
//	

	private static Day dayFromGWT(DayGWT gwtDay) {
		return Day.values()[gwtDay.ordinal()];
	}
	
	private static DayGWT dayToGWT(Day gwtDay) {
		return DayGWT.values()[gwtDay.ordinal()];
	}
	
	private static HashMap<DayGWT, HashMap<Integer, Integer>> timePrefsToGWT(HashMap<Day, HashMap<Integer, Integer>> modelPrefs) {
		HashMap<DayGWT, HashMap<Integer, Integer>> gwtPrefs = new HashMap<DayGWT, HashMap<Integer, Integer>>();
		
		for (Entry<Day, HashMap<Integer, Integer>> gwtPrefsForDay : modelPrefs.entrySet()) {
			DayGWT gwtDay = dayToGWT(gwtPrefsForDay.getKey());
			gwtPrefs.put(gwtDay, gwtPrefsForDay.getValue());
		}
		
		return gwtPrefs;
	}
	
	public static InstructorGWT instructorToGWT(Instructor instructor) throws DatabaseException {
		return new InstructorGWT(
				instructor.getID(),
				instructor.getUsername(),
				instructor.getFirstName(),
				instructor.getLastName(),
				instructor.getMaxWTU(),
				instructor.getTimePreferences(),
				instructor.getCoursePreferences());
	}
	
	public static Course courseFromGWT(Model model, CourseGWT course) throws DatabaseException {
		Collection<Set<Day>> modelDayPatterns = new LinkedList<Set<Day>>();
		for (Set<DayGWT> gwtDayPattern : course.getDayPatterns())
			modelDayPatterns.add(dayPatternFromGWT(gwtDayPattern));
		
		Course result = model.createTransientCourse(
						course.getCourseName(),
						course.getCatalogNum(),
						course.getDept(),
						course.getWtu(),
						course.getScu(),
						course.getRawNumSections(),
						course.getType(),
						course.getMaxEnroll(),
						course.getHalfHoursPerWeek(),
						course.isSchedulable());
		result.setDayPatterns(modelDayPatterns);
		result.setUsedEquipment(course.getUsedEquipment());
		return result;
	}
	
	public static Instructor instructorFromGWT(Model model, InstructorGWT instructor) throws DatabaseException {
		Instructor result = model.createTransientInstructor(
				instructor.getFirstName(),
				instructor.getLastName(),
				instructor.getUsername(),
				instructor.getRawMaxWtu(),
				instructor.isSchedulable());
		result.setTimePreferences(instructor.gettPrefs());
		result.setCoursePreferences(instructor.getCoursePreferences());
		return result;
	}

	static Set<DayGWT> dayPatternToGWT(Set<Day> modelDayPattern) {
		Set<DayGWT> gwtDayPattern = new TreeSet<DayGWT>();
		for (Day modelDay : modelDayPattern)
			gwtDayPattern.add(dayToGWT(modelDay));
		return gwtDayPattern;
	}

	static Set<Day> dayPatternFromGWT(Set<DayGWT> gwtDayPattern) {
		Set<Day> modelDayPattern = new TreeSet<Day>();
		for (DayGWT gwtDay : gwtDayPattern)
			modelDayPattern.add(dayFromGWT(gwtDay));
		return modelDayPattern;
	}

	static CourseGWT courseToGWT(Course course) throws DatabaseException {
		Collection<Set<DayGWT>> dayPatterns = new LinkedList<Set<DayGWT>>();
		for (Set<Day> combo : course.getDayPatterns())
			dayPatterns.add(dayPatternToGWT(combo));
		
		return new CourseGWT(
				course.isSchedulable(),
				course.getName(),
				course.getCatalogNumber(),
				course.getDepartment(),
				course.getWTU(),
				course.getSCU(),
				course.getNumSections(),
				course.getType(),
				course.getMaxEnrollment(),
				course.getLecture() == null ? -1 : course.getLecture().getID(),
				course.getNumHalfHoursPerWeek(),
				dayPatterns,
				course.getID(),
				course.isTetheredToLecture(),
				course.getUsedEquipment());
	}
	
	static void readCourseFromGWT(CourseGWT source, Course result) {
		result.setIsSchedulable(source.isSchedulable());
		result.setName(source.getCourseName());
		result.setCatalogNumber(source.getCatalogNum());
		result.setDepartment(source.getDept());
		result.setWTU(source.getWtu());
		result.setSCU(source.getScu());
		result.setNumSections(source.getRawNumSections());
		result.setType(source.getType());
		result.setMaxEnrollment(source.getMaxEnroll());
		result.setNumHalfHoursPerWeek(source.getHalfHoursPerWeek());

		Collection<Set<Day>> dayPatterns = new LinkedList<Set<Day>>();
		for (Set<DayGWT> combo : source.getDayPatterns())
			dayPatterns.add(dayPatternFromGWT(combo));
		result.setDayPatterns(dayPatterns);
		
		result.setUsedEquipment(source.getUsedEquipment());
	}

	public static void readInstructorFromGWT(InstructorGWT source, Instructor result) {
		result.setIsSchedulable(source.isSchedulable());
		result.setFirstName(source.getFirstName());
		result.setLastName(source.getLastName());
		result.setUsername(source.getUsername());
		result.setMaxWTU(source.getRawMaxWtu());
		result.setCoursePreferences(source.getCoursePreferences());
		result.setTimePreferences(source.gettPrefs());
	}

	public static void readLocationFromGWT(LocationGWT source, Location result) {
		result.setIsSchedulable(source.isSchedulable());
		result.setMaxOccupancy(source.getRawMaxOccupancy());
		result.setRoom(source.getRoom());
		result.setType(source.getType());
	}

	public static LocationGWT locationToGWT(Location location) throws DatabaseException {
		LocationGWT result = new LocationGWT(location.getID(), location.getRoom(), location.getType(), location.getMaxOccupancy(), location.getProvidedEquipment(), location.isSchedulable());
		System.out.println("result room: " + result.getRoom() + " from " + location.getRoom());
		return result;
	}

	public static DocumentGWT documentToGWT(Document doc, int scheduleID) throws DatabaseException {
		return new DocumentGWT(doc.getID(), doc.getName(), scheduleID, doc.getStaffInstructor().getID(), doc.getTBALocation().getID());
	}

	@Deprecated
	public static OldScheduleItemGWT scheduleItemFromGWTToOldGWT(ScheduleItemGWT source, Course course, Instructor instructor, Location location) throws DatabaseException {
		CourseGWT courseGWT = courseToGWT(course);
		LocationGWT locationGWT = locationToGWT(location);
		InstructorGWT instructorGWT = instructorToGWT(instructor);
		
		ArrayList<Integer> daysInts = new ArrayList<Integer>();
		for (DayGWT day : source.getDays())
			daysInts.add(day.ordinal());
		int startTimeHour = source.getStartHalfHour() / 2;
		int startTimeMinute = source.getStartHalfHour() % 2 * 30 + 10;
		int endTimeHour = source.getEndHalfHour() / 2;
		int endTimeMinute = source.getEndHalfHour() % 2 * 30 + 10;
		return new OldScheduleItemGWT(courseGWT, courseGWT.getCourseName(), instructorGWT.getUsername(), courseGWT.getDept(), courseGWT.getCatalogNum(), source.getSection(), daysInts, startTimeHour, startTimeMinute, endTimeHour, endTimeMinute, locationGWT.getRoom(), source.isConflicted());
	}

	public static void readScheduleItemGWTFromOldGWT(ScheduleItemGWT result, OldScheduleItemGWT itemOldGWT, Collection<Instructor> instructors, Collection<Location> locations) {
		Set<DayGWT> days = new TreeSet<DayGWT>();
		for (int integer : itemOldGWT.getDayNums())
			days.add(DayGWT.values()[integer]);

		int startHalfHour = itemOldGWT.getStartTimeHour() * 2 + itemOldGWT.getStartTimeMin() / 30;
		int endHalfHour = itemOldGWT.getEndTimeHour() * 2 + itemOldGWT.getEndTimeMin() / 30;
		
		int courseID = itemOldGWT.getCourse().getID();
		
		int instructorID = -1;
		for (Instructor instructor : instructors)
			if (instructor.getUsername().equals(itemOldGWT.getProfessor()))
				instructorID = instructor.getID();
		assert(instructorID >= 0);
		
		int locationID = -1;
		for (Location location : locations)
			if (location.getRoom().equals(itemOldGWT.getProfessor()))
				locationID = location.getID();
		assert(locationID >= 0);
		
		result.setCourseID(courseID);
		result.setInstructorID(instructorID);
		result.setLocationID(locationID);
		result.setSection(itemOldGWT.getSection());
		result.setDays(days);
		result.setStartHalfHour(startHalfHour);
		result.setEndHalfHour(endHalfHour);
		result.setPlaced(itemOldGWT.isPlaced());
		result.setConflicted(itemOldGWT.isConflicted());
	}
	
	public static ScheduleItemGWT scheduleItemGWTFromOldGWT(Model model, OldScheduleItemGWT itemOldGWT, Collection<Instructor> instructors, Collection<Location> locations) {
		Set<DayGWT> days = new TreeSet<DayGWT>();
		for (int integer : itemOldGWT.getDayNums())
			days.add(DayGWT.values()[integer]);

		int startHalfHour = itemOldGWT.getStartTimeHour() * 2 + itemOldGWT.getStartTimeMin() / 30;
		int endHalfHour = itemOldGWT.getEndTimeHour() * 2 + itemOldGWT.getEndTimeMin() / 30;
		
		int courseID = itemOldGWT.getCourse().getID();
		
		int instructorID = -1;
		for (Instructor instructor : instructors)
			if (instructor.getUsername().equals(itemOldGWT.getProfessor()))
				instructorID = instructor.getID();
		assert(instructorID >= 0);
		
		int locationID = -1;
		for (Location location : locations)
			if (location.getRoom().equals(itemOldGWT.getProfessor()))
				locationID = location.getID();
		assert(locationID >= 0);
		
		return new ScheduleItemGWT(
				-1, courseID, instructorID, locationID, itemOldGWT.getSection(), days,
				startHalfHour, endHalfHour, itemOldGWT.isPlaced(), itemOldGWT.isConflicted());
	}

	public static ScheduleItem scheduleItemFromGWT(Model model, Schedule schedule, ScheduleItemGWT source) throws DatabaseException {
		Set<Day> dayPattern = dayPatternFromGWT(source.getDays());
		
		ScheduleItem result = model.createTransientScheduleItem(
				source.getSection(),
				dayPattern,
				source.getStartHalfHour(),
				source.getEndHalfHour(),
				source.isPlaced(),
				source.isConflicted());
		if (source.getCourseID() >= 0)
			result.setCourse(model.findCourseByID(source.getCourseID()));
		if (source.getInstructorID() >= 0)
			result.setInstructor(model.findInstructorByID(source.getInstructorID()));
		if (source.getLocationID() >= 0)
			result.setLocation(model.findLocationByID(source.getLocationID()));
		return result;
	}

	public static ScheduleItemGWT scheduleItemToGWT(ScheduleItem item) throws DatabaseException {
		Set<DayGWT> pattern = dayPatternToGWT(item.getDays());
		return new ScheduleItemGWT(item.getID(), item.getCourse().getID(), item.getInstructor().getID(), item.getLocation().getID(), item.getSection(), pattern, item.getStartHalfHour(), item.getEndHalfHour(), item.isPlaced(), item.isConflicted());
	}

	public static void readScheduleItemFromGWT(Model model, ScheduleItemGWT itemGWT, ScheduleItem item) throws DatabaseException {
		item.setCourse(model.findCourseByID(itemGWT.getCourseID()));
		item.setDays(Conversion.dayPatternFromGWT(itemGWT.getDays()));
		item.setEndHalfHour(itemGWT.getEndHalfHour());
		item.setInstructor(model.findInstructorByID(itemGWT.getInstructorID()));
		item.setIsConflicted(itemGWT.isConflicted());
		item.setIsPlaced(itemGWT.isPlaced());
		item.setLocation(model.findLocationByID(itemGWT.getLocationID()));
		item.setSection(itemGWT.getSection());
		item.setStartHalfHour(itemGWT.getStartHalfHour());
	}

	public static Document readDocumentFromGWT(Model model, DocumentGWT documentGWT) throws DatabaseException {
		Document document = model.findDocumentByID(documentGWT.getID());

		document.setName(documentGWT.getName());
		
		return document;
	}
}
