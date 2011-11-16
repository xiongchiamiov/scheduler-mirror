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
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT.ProvidedEquipmentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimeGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;
import edu.calpoly.csc.scheduler.view.web.shared.WeekGWT;

public abstract class Conversion {
	public static InstructorGWT toGWT(int id, Instructor instructor) {
		InstructorGWT result = new InstructorGWT();
		result.setId(id);
		result.setUserID(instructor.getUserID());
		result.setFirstName(instructor.getFirstName());
		result.setLastName(instructor.getLastName());
		result.setRoomNumber(instructor.getRoomNumber());
		result.setBuilding(instructor.getBuilding());
		result.setDisabilities(instructor.getDisability());
		result.setMaxWtu(instructor.getMaxWTU());
		result.setCurWtu(instructor.getCurWtu());
		result.setFairness(instructor.getFairness());
		result.setGenerosity(instructor.getGenerosity());

		HashMap<Course, Integer> sourceCoursePreferences = instructor
				.getCoursePreferences();
		HashMap<CourseGWT, Integer> coursePreferences = new LinkedHashMap<CourseGWT, Integer>();
		for (Course course : sourceCoursePreferences.keySet()) {
			coursePreferences.put(Conversion.toGWT(0, course),
					sourceCoursePreferences.get(course));
		}
		result.setCoursePreferences(coursePreferences);

		HashMap<Day, LinkedHashMap<Time, TimePreference>> sourceTimePreferences = instructor
				.getTimePreferences();
		Map<DayGWT, Map<TimeGWT, TimePreferenceGWT>> timePreferences = new TreeMap<DayGWT, Map<TimeGWT, TimePreferenceGWT>>();
		for (Day sourceDay : sourceTimePreferences.keySet()) {
			LinkedHashMap<Time, TimePreference> sourceTimePreferencesForDay = sourceTimePreferences
					.get(sourceDay);
			DayGWT day = Conversion.toGWT(sourceDay);
			Map<TimeGWT, TimePreferenceGWT> timePreferencesForDay = new TreeMap<TimeGWT, TimePreferenceGWT>();
			for (Time sourceTime : sourceTimePreferencesForDay.keySet()) {
				TimePreference sourceTimePreferencesForTime = sourceTimePreferencesForDay
						.get(sourceTime);
				TimeGWT time = Conversion.toGWT(sourceTime);
				TimePreferenceGWT timePreferencesForTime = Conversion
						.toGWT(sourceTimePreferencesForTime);
				timePreferencesForDay.put(time, timePreferencesForTime);
			}
			timePreferences.put(day, timePreferencesForDay);
		}
		result.settPrefs(timePreferences);
		assert (result.gettPrefs().size() == instructor.getTimePreferences()
				.size());

		Vector<ScheduleItem> sourceItemsTaught = instructor.getItemsTaught();
		Vector<ScheduleItemGWT> itemsTaught = new Vector<ScheduleItemGWT>();
		for (ScheduleItem item : sourceItemsTaught)
			itemsTaught.add(Conversion.toGWT(item, false));
		result.setItemsTaught(itemsTaught);

		return result;
	}

	private static TimePreferenceGWT toGWT(TimePreference source) {
		TimePreferenceGWT timePref = new TimePreferenceGWT();
		timePref.setDesire(source.getDesire());
		timePref.setTime(Conversion.toGWT(source.getTime()));
		return timePref;
	}

	private static TimeGWT toGWT(Time source) {
		TimeGWT time = new TimeGWT();
		time.setHour(source.getHour());
		time.setMinute(source.getMinute());
		return time;
	}

	private static DayGWT toGWT(Day sourceDay) {
		DayGWT day = new DayGWT();
		day.setNum(sourceDay.getNum());
		return day;
	}

	public static Instructor fromGWT(InstructorGWT instructor) {
		instructor.verify();
		Instructor ins = new Instructor();
		ins.setFirstName(instructor.getFirstName());
		ins.setLastName(instructor.getLastName());
		ins.setUserID(instructor.getUserID());
		ins.setMaxWtu(instructor.getMaxWtu());
		ins.setCurWtu(instructor.getCurWtu());
		ins.setOffice(new Location(instructor.getBuilding(), instructor
				.getRoomNumber()));
		ins.setFairness(instructor.getFairness());
		ins.setDisability(instructor.getDisabilities());
		ins.setGenerosity(instructor.getGenerosity());
		ins.setAvailability(new WeekAvail());

		HashMap<Course, Integer> coursePrefs = new HashMap<Course, Integer>();
		for (CourseGWT course : instructor.getCoursePreferences().keySet()) {
			Integer desire = instructor.getCoursePreferences().get(course);
			coursePrefs.put(fromGWT(course), desire);
		}
		ins.setCoursePreferences(coursePrefs);

		HashMap<Day, LinkedHashMap<Time, TimePreference>> prefs = new HashMap<Day, LinkedHashMap<Time, TimePreference>>();
		for (DayGWT sourceDay : instructor.gettPrefs().keySet()) {
			Map<TimeGWT, TimePreferenceGWT> sourceDayPrefs = instructor
					.gettPrefs().get(sourceDay);

			Day day = fromGWT(sourceDay);
			LinkedHashMap<Time, TimePreference> dayPrefs = new LinkedHashMap<Time, TimePreference>();

			for (TimeGWT sourceTime : sourceDayPrefs.keySet()) {
				TimePreferenceGWT sourceTimePrefs = sourceDayPrefs
						.get(sourceTime);
				dayPrefs.put(fromGWT(sourceTime), fromGWT(sourceTimePrefs));
			}

			prefs.put(day, dayPrefs);
		}
		ins.setTimePreferences(prefs);

		ins.setItemsTaught(new Vector<ScheduleItem>());

		ins.verify();

		return ins;
	}

	private static Time fromGWT(TimeGWT sourceTime) {
		return new Time(sourceTime.getHour(), sourceTime.getMinute());
	}

	private static TimePreference fromGWT(TimePreferenceGWT sourceTimePrefs) {
		return new TimePreference(fromGWT(sourceTimePrefs.getTime()),
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
		CourseGWT course = toGWT(0, schdItem.getCourse());

		return new ScheduleItemGWT(course, courseName, instructor, courseDept,
				courseNum, section, dayNums, startTimeHour, startTimeMin,
				endTimeHour, endTimeMin, location, isConflicted);
	}

	public static CourseGWT toGWT(int id, Course course) {
		CourseGWT newCourse = new CourseGWT();
		newCourse.setId(id);
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
		Vector<DayGWT> days = new Vector<DayGWT>();
		for (Day sourceDay : source.getDays())
			days.add(toGWT(sourceDay));
		result.setDays(days);
		return result;
	}

	private static Week fromGWT(WeekGWT days) {
		Week week = new Week();
		for (DayGWT day : days.getDays())
			week.add(fromGWT(day));
		return week;
	}

	private static Day fromGWT(DayGWT day) {
		if (day.getNum() == DayGWT.SUN.getNum())
			return Day.SUN;
		if (day.getNum() == DayGWT.MON.getNum())
			return Day.MON;
		if (day.getNum() == DayGWT.TUE.getNum())
			return Day.TUE;
		if (day.getNum() == DayGWT.WED.getNum())
			return Day.WED;
		if (day.getNum() == DayGWT.THU.getNum())
			return Day.THU;
		if (day.getNum() == DayGWT.FRI.getNum())
			return Day.FRI;
		if (day.getNum() == DayGWT.SAT.getNum())
			return Day.SAT;
		assert (false);
		return null;
	}

	public static LocationGWT toGWT(int id, Location location) {
		return new LocationGWT(id, location.getBuilding(), location.getRoom(),
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
		newCourse.setName(course.getCourseName());
		newCourse.setCatalogNum(course.getCatalogNum());
		newCourse.setWtu(course.getWtu());
		newCourse.setScu(course.getScu());
		newCourse.setType(course.getType());
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
		return newCourse;
	}
}
