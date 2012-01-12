package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.Lab;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.Location.ProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.udb.UserData;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT.ProvidedEquipmentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;
import edu.calpoly.csc.scheduler.view.web.shared.WeekGWT;

public abstract class Conversion {
	public static UserData fromGWT(UserDataGWT gwt) {
		UserData user = new UserData();
		user.setDbid(gwt.getID());
		user.setPermission(gwt.getPermissionLevel());
		user.setUserId(gwt.getUserName());
		user.setScheduleDBId(gwt.getScheduleID());
		return user;
	}
	
	public static InstructorGWT toGWT(Instructor instructor) {
		InstructorGWT result = new InstructorGWT();
		result.setID(instructor.getDbid());
		result.setUserID(instructor.getUserID());
		result.setFirstName(instructor.getFirstName());
		result.setLastName(instructor.getLastName());
		result.setRoomNumber(instructor.getRoomNumber());
		result.setBuilding(instructor.getBuilding());
		System.out.println("instructor disabled on toGWT? " + instructor.getDisability());
		result.setDisabilities(instructor.getDisability());
		result.setMaxWtu(instructor.getMaxWTU());
		result.setCurWtu(instructor.getCurWtu());
		result.setFairness(instructor.getFairness());
		result.setGenerosity(instructor.getGenerosity());

		HashMap<Course, Integer> sourceCoursePreferences = instructor
				.getCoursePreferences();
		HashMap<Integer, Integer> coursePreferences = new LinkedHashMap<Integer, Integer>();
		for (Course course : sourceCoursePreferences.keySet()) {
			coursePreferences.put(course.getDbid(),
					sourceCoursePreferences.get(course));
		}
		result.setCoursePreferences(coursePreferences);

		HashMap<Day, LinkedHashMap<Time, TimePreference>> sourceTimePreferences = instructor
				.getTimePreferences();
		Map<Integer, Map<Integer, TimePreferenceGWT>> timePreferences = new TreeMap<Integer, Map<Integer, TimePreferenceGWT>>();
		for (Day sourceDay : sourceTimePreferences.keySet()) {
			LinkedHashMap<Time, TimePreference> sourceTimePreferencesForDay = sourceTimePreferences
					.get(sourceDay);
			Integer day = Conversion.toGWT(sourceDay);
			Map<Integer, TimePreferenceGWT> timePreferencesForDay = new TreeMap<Integer, TimePreferenceGWT>();
			for (Time sourceTime : sourceTimePreferencesForDay.keySet()) {
				TimePreference sourceTimePreferencesForTime = sourceTimePreferencesForDay
						.get(sourceTime);
				Integer time = Conversion.toGWT(sourceTime);
				TimePreferenceGWT timePreferencesForTime = Conversion
						.toGWT(sourceTimePreferencesForTime);
				timePreferencesForDay.put(time, timePreferencesForTime);
			}
			timePreferences.put(day, timePreferencesForDay);
		}
		result.settPrefs(timePreferences);
		assert (result.gettPrefs().size() == instructor.getTimePreferences()
				.size());

		return result;
	}

	private static TimePreferenceGWT toGWT(TimePreference source) {
		TimePreferenceGWT timePref = new TimePreferenceGWT();
		timePref.setDesire(source.getDesire());
		timePref.setTime(Conversion.toGWT(source.getTime()));
		return timePref;
	}

	private static Integer toGWT(Time source) {
		return source.getHour() * 60 + source.getMinute();
	}

	private static Integer toGWT(Day sourceDay) {
		return sourceDay.getNum();
	}

	public static Instructor fromGWT(InstructorGWT instructor, Map<Integer, Course> coursesByID) {
		instructor.verify();
		Instructor ins = new Instructor();
		ins.setDbid(instructor.getID());
		ins.setFirstName(instructor.getFirstName());
		ins.setLastName(instructor.getLastName());
		ins.setUserID(instructor.getUserID());
		ins.setMaxWtu(instructor.getMaxWtu());
		ins.setCurWtu(instructor.getCurWtu());
		ins.setOffice(new Location(instructor.getBuilding(), instructor.getRoomNumber()));
		ins.setFairness(instructor.getFairness());
		ins.setDisability(instructor.getDisabilities());
		System.out.println("instructor disabled on fromGWT? " + ins.getDisability());
		ins.setGenerosity(instructor.getGenerosity());
		ins.setAvailability(new WeekAvail());

		HashMap<Course, Integer> coursePrefs = new HashMap<Course, Integer>();
		for (Integer course : instructor.getCoursePreferences().keySet()) {
			Integer desire = instructor.getCoursePreferences().get(course);
			coursePrefs.put(coursesByID.get(course), desire);
		}
		ins.setCoursePreferences(coursePrefs);

		HashMap<Day, LinkedHashMap<Time, TimePreference>> prefs = new HashMap<Day, LinkedHashMap<Time, TimePreference>>();
		for (Integer sourceDay : instructor.gettPrefs().keySet()) {
			Map<Integer, TimePreferenceGWT> sourceDayPrefs = instructor
					.gettPrefs().get(sourceDay);

			Day day = dayFromGWT(sourceDay);
			LinkedHashMap<Time, TimePreference> dayPrefs = new LinkedHashMap<Time, TimePreference>();

			for (Integer sourceTime : sourceDayPrefs.keySet()) {
				TimePreferenceGWT sourceTimePrefs = sourceDayPrefs
						.get(sourceTime);
				dayPrefs.put(timeFromGWT(sourceTime), fromGWT(sourceTimePrefs));
			}

			prefs.put(day, dayPrefs);
		}
		ins.setTimePreferences(prefs);

		ins.setItemsTaught(new Vector<ScheduleItem>());

		ins.verify();

		return ins;
	}

	private static Time timeFromGWT(Integer sourceTime) {
		return new Time(sourceTime / 60, sourceTime % 60);
	}

	private static TimePreference fromGWT(TimePreferenceGWT sourceTimePrefs) {
		return new TimePreference(timeFromGWT(sourceTimePrefs.getTime()),
				sourceTimePrefs.getDesire());
	}

	public static ScheduleItemGWT toGWT(ScheduleItem schdItem, boolean isConflicted) {
		String courseName = schdItem.getCourse().getName();
		String instructor = (schdItem.getInstructor() == null ? "" : schdItem
				.getInstructor().getName());
		String courseDept = schdItem.getCourse().getDept();
		int courseNum = schdItem.getCourse().getCatalogNum();
		int section = schdItem.getSection();

		ArrayList<Integer> dayNums = new ArrayList<Integer>();
		for (Day d : schdItem.getDays().getDays())
			dayNums.add(d.getNum());

		int startTimeHour = schdItem.getStart().getHour();
		int endTimeHour = schdItem.getEnd().getHour();
		int startTimeMin = schdItem.getStart().getMinute();
		int endTimeMin = schdItem.getEnd().getMinute();
		String location = (schdItem.getLocation() == null ? "" : schdItem
				.getLocation().toString());
		CourseGWT course = toGWT(schdItem.getCourse());

		return new ScheduleItemGWT(course, courseName, instructor, courseDept,
				courseNum, section, dayNums, startTimeHour, startTimeMin,
				endTimeHour, endTimeMin, location, isConflicted);
	}

	public static CourseGWT toGWT(Course course) {
		CourseGWT newCourse = new CourseGWT();
		newCourse.setID(course.getDbid());
		newCourse.setCatalogNum(course.getCatalogNum());
		newCourse.setCourseName(course.getName());
		newCourse.setDept(course.getDept());
		newCourse.setDays(Conversion.toGWT(course.getDays()));
		newCourse.setLength(course.getLength());
		newCourse.setMaxEnroll(course.getEnrollment());
		newCourse.setNumSections(course.getNumOfSections());
		newCourse.setScu(course.getScu());
		newCourse.setType(course.getType().toString());
		newCourse.setWtu(course.getWtu());
		Lab lab = course.getLab();
		if (lab != null) {
			newCourse.setLabDept(lab.getDept());
			newCourse.setLabName(lab.getName());
			newCourse.setLabCatalogNum(lab.getCatalogNum());
		} else {
			newCourse.setLabDept("");
			newCourse.setLabName("");
			newCourse.setLabCatalogNum(0);
		}
		return newCourse;
	}

	private static WeekGWT toGWT(Week source) {
		WeekGWT result = new WeekGWT();
		Vector<Integer> days = new Vector<Integer>();
		for (Day sourceDay : source.getDays())
			days.add(toGWT(sourceDay));
		result.setDays(days);
		return result;
	}

	private static Week fromGWT(WeekGWT days) {
		Week week = new Week();
		for (Integer day : days.getDays())
			week.add(dayFromGWT(day));
		return week;
	}

	private static Day dayFromGWT(Integer dayNum) {
		for (Day day : Day.ALL_DAYS)
			if (dayNum == day.getNum())
				return day;
		assert(false);
		return null;
	}

	public static LocationGWT toGWT(Location location) {
		return new LocationGWT(location.getDbid(), location.getBuilding(), location.getRoom(),
				location.getType(), location.getMaxOccupancy(),
				location.getAdaCompliant(),
				toGWT(location.getProvidedEquipment()));
	}

	public static LocationGWT.ProvidedEquipmentGWT toGWT(Location.ProvidedEquipment equipment) {
		LocationGWT.ProvidedEquipmentGWT result = new LocationGWT.ProvidedEquipmentGWT();
		result.hasLaptopConnectivity = equipment.hasLaptopConnectivity;
		result.hasOverhead = equipment.hasOverhead;
		result.isSmartRoom = equipment.isSmartRoom;
		return result;
	}
	
	public static Location fromGWT(LocationGWT location) {
		Location loc = new Location();
		loc.setDbid(location.getID());
		loc.setRoom(location.getRoom());
		loc.setAdaCompliant(location.isADACompliant());
		loc.setAvailability(new WeekAvail());
		loc.setBuilding(location.getBuilding());
		loc.setMaxOccupancy(location.getMaxOccupancy());
		loc.setProvidedEquipment(fromGWT(location.getEquipment()));
		loc.setRoom(location.getRoom());
		loc.setType(location.getType());
		loc.setProvidedEquipment(fromGWT(location.getEquipment()));
		loc.verify();
		return loc;
	}

	private static ProvidedEquipment fromGWT(ProvidedEquipmentGWT equipment) {
		ProvidedEquipment result = new ProvidedEquipment();
		result.hasLaptopConnectivity = equipment.hasLaptopConnectivity;
		result.hasOverhead = equipment.hasOverhead;
		result.isSmartRoom = equipment.isSmartRoom;
		return result;
	}

	public static Course fromGWT(CourseGWT course) {
		Course newCourse = new Course();
		newCourse.setDbid(course.getID());
		newCourse.setName(course.getCourseName());
		newCourse.setCatalogNum(course.getCatalogNum());
		newCourse.setWtu(course.getWtu());
		newCourse.setScu(course.getScu());
		newCourse.setType(course.getType());
		System.out.println("model course " + newCourse.getCatalogNum() + " type is " + course.getType());
		newCourse.setEnrollment(course.getMaxEnroll());
		if (!course.getLabDept().equals("")) {
			newCourse.setLab(new Lab(course.getLabName(), course.getLabDept(),
					course.getLabCatalogNum()));
		} else {
			newCourse.setLab(null);
		}
		newCourse.setDept(course.getDept());
		newCourse.setLength(course.getLength());
		newCourse.setNumOfSections(course.getNumSections());
		newCourse.setDays(fromGWT(course.getDays()));
		assert(newCourse.getLength() >= 0);
	   assert(newCourse.getDays() != null);
		return newCourse;
	}

	public static UserDataGWT toGWT(UserData value) {
		UserDataGWT result = new UserDataGWT();
		assert(value.getDbid() != null);
		result.setID(value.getDbid());
		result.setPermissionLevel(value.getPermission());
		result.setUserName(value.getUserId());
		result.setScheduleID(value.getScheduleDBId());
		return result;
	}
}
