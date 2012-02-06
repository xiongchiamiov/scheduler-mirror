package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
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
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT.ProvidedEquipmentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

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
		result.setDisabilities(instructor.getDisability());
		result.setMaxWtu(instructor.getMaxWTU());
		
		HashMap<Integer, Integer> coursePreferences = new LinkedHashMap<Integer, Integer>();
		try {
			HashMap<Integer, Integer> sourceCoursePreferences = instructor
					.getCoursePreferences();
			for (Integer dbID : sourceCoursePreferences.keySet()) {
				if (dbID == null)
					System.out.println("ERROR, course id is null when converting from model");
				coursePreferences.put(dbID, sourceCoursePreferences.get(dbID));
			}
		}
		catch (Exception e) {
			System.out.println("Unable to convert course preferences!");
			e.printStackTrace();
		}
		result.setCoursePreferences(coursePreferences);

		Map<Integer, Map<Integer, TimePreferenceGWT>> timePreferences = new TreeMap<Integer, Map<Integer, TimePreferenceGWT>>();
		try {
			HashMap<Integer, LinkedHashMap<Integer, TimePreference>> sourceTimePreferences = instructor
					.getTimePreferences();
			for (Integer sourceDay : sourceTimePreferences.keySet()) {
				LinkedHashMap<Integer, TimePreference> sourceTimePreferencesForDay = sourceTimePreferences
						.get(sourceDay);
				Integer day = sourceDay;//Conversion.toGWT(sourceDay);
				Map<Integer, TimePreferenceGWT> timePreferencesForDay = new TreeMap<Integer, TimePreferenceGWT>();
				for (Integer sourceTime : sourceTimePreferencesForDay.keySet()) {
					TimePreference sourceTimePreferencesForTime = sourceTimePreferencesForDay
							.get(sourceTime);
					Integer time = sourceTime;//Conversion.toGWT(sourceTime);
					TimePreferenceGWT timePreferencesForTime = Conversion
							.toGWT(sourceTimePreferencesForTime);
					timePreferencesForDay.put(time, timePreferencesForTime);
				}
				timePreferences.put(day, timePreferencesForDay);
			}
		}
		catch (Exception e) {
			System.out.println("Unable to convert time preferences!");
			e.printStackTrace();
		}
		result.settPrefs(timePreferences);
		assert (result.gettPrefs().size() == instructor.getTimePreferences().size());

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
		ins.setCurWtu(-1337);
		ins.setOffice(null);
		ins.setFairness(-1337);
		ins.setDisability(instructor.getDisabilities());
		System.out.println("instructor disabled on fromGWT? " + ins.getDisability());
		ins.setGenerosity(-1337);
		ins.setAvailability(new WeekAvail());

		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		for (Integer courseID : instructor.getCoursePreferences().keySet()) {
			Integer desire = instructor.getCoursePreferences().get(courseID);
			Course course = coursesByID.get(courseID);
			if (course.getDbid() == null)
				System.out.println("ERROR, course id is null when converting to model");
			coursePrefs.put(course.getDbid(), desire);
		}
		ins.setCoursePreferences(coursePrefs);


		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> prefs = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
		for (Integer sourceDay : instructor.gettPrefs().keySet()) {
			Map<Integer, TimePreferenceGWT> sourceDayPrefs = instructor
					.gettPrefs().get(sourceDay);

			Integer day = sourceDay;//Day day = dayFromGWT(sourceDay);
			LinkedHashMap<Integer, TimePreference> dayPrefs = new LinkedHashMap<Integer, TimePreference>();

			for (Integer sourceTime : sourceDayPrefs.keySet()) {
				TimePreferenceGWT sourceTimePrefs = sourceDayPrefs
						.get(sourceTime);
				dayPrefs.put(sourceTime, fromGWT(sourceTimePrefs));
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
		
		String courseNum = schdItem.getCourse().getCatalogNum();
		
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

		// TODO: yero make this constructor take courseNum (string) instead of courseNumIntForSI (int)
		//int courseNumIntForSI = Integer.parseInt(courseNum);
		
		return new ScheduleItemGWT(course, courseName, instructor, courseDept,
				courseNum, section, dayNums, startTimeHour, startTimeMin,
				endTimeHour, endTimeMin, location, isConflicted);
	}

	public static CourseGWT toGWT(Course course) {
		CourseGWT newCourse = new CourseGWT();
		newCourse.setID(course.getDbid());
		
		// TODO: holland make getCatalogNum() return a string
		String courseNumString = course.getCatalogNum();
		newCourse.setCatalogNum(courseNumString);
		
		newCourse.setCourseName(course.getName());
		newCourse.setDept(course.getDept());
		
		// TODO: temporary solution, right now the model only supports one day combination,
		// the view supports many. We need to change the model.
		Set<Week> courseCombinations = new HashSet<Week>();
		courseCombinations.add(course.getDays());
		newCourse.setDays(Conversion.toGWT(courseCombinations));
		
		
		newCourse.setLength(course.getLength());
		newCourse.setMaxEnroll(course.getEnrollment());
		newCourse.setNumSections(course.getNumOfSections());
		newCourse.setScu(course.getScu());
		System.out.println("recalling model course " + course.getCatalogNum() + " type is " + course.getType() + " lec id is " + course.getLectureID());
		newCourse.setType(course.getType().toString());
		newCourse.setWtu(course.getWtu());
		newCourse.setLectureID(course.getLectureID());
		newCourse.setTetheredToLecture(course.getTetheredToLecture());
		
		System.out.println("recalled model course into gwt course lecture id is " + newCourse.getLectureID());
		
		return newCourse;
	}

	private static Set<DayCombinationGWT> toGWT(Set<Week> dayCombinationsSource) {
		Set<DayCombinationGWT> dayCombinationsResult = new HashSet<DayCombinationGWT>();
		for (Week dayCombinationSource : dayCombinationsSource) {
			DayCombinationGWT dayCombinationResult = new DayCombinationGWT();
			Set<Integer> dayCombinationResultInts = new HashSet<Integer>();
			for (Day daySource : dayCombinationSource.getDays())
				dayCombinationResultInts.add(toGWT(daySource));
			dayCombinationResult.setDays(dayCombinationResultInts);
			dayCombinationsResult.add(dayCombinationResult);
			
		}
		return dayCombinationsResult;
	}

	private static Set<Week> fromGWT(Set<DayCombinationGWT> dayCombinationsSource) {
		Set<Week> dayCombinationsResult = new HashSet<Week>();
		
		for (DayCombinationGWT dayCombinationSource : dayCombinationsSource) {
			Week week = new Week();
			for (Integer day : dayCombinationSource.getDays())
				week.add(dayFromGWT(day));
			dayCombinationsResult.add(week);
		}
		
		return dayCombinationsResult;
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
		
		// TODO: holland make setCatalogNum() take a string
		String catalogNum = course.getCatalogNum();
		newCourse.setCatalogNum(catalogNum);
		
		newCourse.setWtu(course.getWtu());
		newCourse.setScu(course.getScu());
		newCourse.setType(course.getType());
		System.out.println("storing model course " + newCourse.getCatalogNum() + " type is " + course.getType());
		newCourse.setEnrollment(course.getMaxEnroll());
		
		newCourse.setLectureID(course.getLectureID());
		newCourse.setTetheredToLecture(course.getTetheredToLecture());
		
		newCourse.setDept(course.getDept());
		newCourse.setLength(course.getLength());
		newCourse.setNumOfSections(course.getNumSections());

		// TODO: temporary solution, right now the model only supports one day combination,
		// the view supports many. We need to change the model.
		Set<Week> courseDayCombinations = fromGWT(course.getDays());
		Week newCourseWeek = Week.fiveDayWeek;
		if (!courseDayCombinations.isEmpty())
			newCourseWeek = courseDayCombinations.iterator().next();
		newCourse.setDays(newCourseWeek);
		
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
