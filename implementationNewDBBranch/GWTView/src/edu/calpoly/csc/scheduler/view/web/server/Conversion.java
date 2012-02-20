package edu.calpoly.csc.scheduler.view.web.server;

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
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
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

	private static Day gwtDayToModelDay(DayGWT gwtDay) {
		return Day.values()[gwtDay.ordinal()];
	}
	
	private static DayGWT modelDayToGWTDay(Day gwtDay) {
		return DayGWT.values()[gwtDay.ordinal()];
	}
	
	private static HashMap<Day, HashMap<Integer, Integer>> gwtTimePrefsToModelTimePrefs(HashMap<DayGWT, HashMap<Integer, Integer>> gwtPrefs) {
		HashMap<Day, HashMap<Integer, Integer>> modelPrefs = new HashMap<Day, HashMap<Integer, Integer>>();
		
		for (Entry<DayGWT, HashMap<Integer, Integer>> gwtPrefsForDay : gwtPrefs.entrySet()) {
			Day modelDay = gwtDayToModelDay(gwtPrefsForDay.getKey());
			modelPrefs.put(modelDay, gwtPrefsForDay.getValue());
		}
		
		return modelPrefs;
	}
	
	private static HashMap<DayGWT, HashMap<Integer, Integer>> modelTimePrefsToGWTTimePrefs(HashMap<Day, HashMap<Integer, Integer>> modelPrefs) {
		HashMap<DayGWT, HashMap<Integer, Integer>> gwtPrefs = new HashMap<DayGWT, HashMap<Integer, Integer>>();
		
		for (Entry<Day, HashMap<Integer, Integer>> gwtPrefsForDay : modelPrefs.entrySet()) {
			DayGWT gwtDay = modelDayToGWTDay(gwtPrefsForDay.getKey());
			gwtPrefs.put(gwtDay, gwtPrefsForDay.getValue());
		}
		
		return gwtPrefs;
	}
	
	public static InstructorGWT modelInstructorToGWTInstructor(Instructor instructor) {
		return new InstructorGWT(
				instructor.getID(),
				instructor.getUsername(),
				instructor.getFirstName(),
				instructor.getLastName(),
				instructor.getMaxWTU(),
				modelTimePrefsToGWTTimePrefs(instructor.getTimePreferences()),
				instructor.getCoursePreferences());
	}
	
	public static Course insertGWTCourseIntoModel(Model model, Document document, CourseGWT course) {
		Collection<Set<Day>> modelDayPatterns = new LinkedList<Set<Day>>();
		for (Set<DayGWT> gwtDayPattern : course.getDayPatterns())
			modelDayPatterns.add(gwtDayPatternToModelDayPattern(gwtDayPattern));
		
		return model.insertCourse(
			document,
			course.getCourseName(),
			course.getCatalogNum(),
			course.getDept(),
			course.getWtu(),
			course.getScu(),
			course.getRawNumSections(),
			course.getType(),
			course.getMaxEnroll(),
			course.getHalfHoursPerWeek(),
			course.getUsedEquipment(),
			modelDayPatterns,
			course.isSchedulable());

	}
	
	public static Instructor insertGWTInstructorIntoModel(Model model, Document document, InstructorGWT instructor) {
		return model.insertInstructor(
				document,
				instructor.getFirstName(),
				instructor.getLastName(),
				instructor.getUsername(),
				instructor.getRawMaxWtu(),
				gwtTimePrefsToModelTimePrefs(instructor.gettPrefs()),
				instructor.getCoursePreferences());
	}

	static Set<DayGWT> modelDayPatternToGWTDayPattern(Set<Day> modelDayPattern) {
		Set<DayGWT> gwtDayPattern = new TreeSet<DayGWT>();
		for (Day modelDay : modelDayPattern)
			gwtDayPattern.add(modelDayToGWTDay(modelDay));
		return gwtDayPattern;
	}

	static Set<Day> gwtDayPatternToModelDayPattern(Set<DayGWT> gwtDayPattern) {
		Set<Day> modelDayPattern = new TreeSet<Day>();
		for (DayGWT gwtDay : gwtDayPattern)
			modelDayPattern.add(gwtDayToModelDay(gwtDay));
		return modelDayPattern;
	}

	static CourseGWT modelCourseToGWTCourse(Course course) {
		Collection<Set<DayGWT>> dayPatterns = new LinkedList<Set<DayGWT>>();
		for (Set<Day> combo : course.getDayPatterns())
			dayPatterns.add(modelDayPatternToGWTDayPattern(combo));
		
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
				course.getLectureID(),
				course.getNumHalfHoursPerWeek(),
				dayPatterns,
				course.getID(),
				course.isTetheredToLecture(),
				course.getUsedEquipment());
	}
	
	static void readGWTCourseIntoModelCourse(CourseGWT source, Course result) {
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
			dayPatterns.add(gwtDayPatternToModelDayPattern(combo));
		result.setDayPatterns(dayPatterns);
		
		result.setUsedEquipment(source.getUsedEquipment());
	}

	public static void readGWTInstructorIntoModelInstructor(InstructorGWT source, Instructor result) {
		result.setIsSchedulable(source.isSchedulable());
		result.setFirstName(source.getFirstName());
		result.setLastName(source.getLastName());
		result.setUsername(source.getUsername());
		result.setCoursePreferences(source.getCoursePreferences());
		result.setTimePreferences(gwtTimePrefsToModelTimePrefs(source.gettPrefs()));
	}

	public static void readGWTLocationIntoModelLocation(LocationGWT source, Location result) {
		result.setIsSchedulable(source.isSchedulable());
		result.setMaxOccupancy(source.getRawMaxOccupancy());
		result.setRoom(source.getRoom());
		result.setType(source.getType());
	}

	public static LocationGWT modelLocationToGWTLocation(Location location) {
		LocationGWT result = new LocationGWT(location.getID(), location.getRoom(), location.getType(), location.getMaxOccupancy(), location.getProvidedEquipment(), location.isSchedulable());
		System.out.println("result room: " + result.getRoom() + " from " + location.getRoom());
		return result;
	}

	public static DocumentGWT modelDocumentToGWTDocument(Document doc) {
		return new DocumentGWT(doc.getID(), doc.getName());
	}

	public static ScheduleItemGWT modelScheduleItemToGWTScheduleItem(Schedule.Item item) {
		Set<DayGWT> dayPattern = modelDayPatternToGWTDayPattern(item.getDays());
		
		return new ScheduleItemGWT(
				item.getCourseID(),
				item.getInstructorID(),
				item.getLocationID(),
				item.getSection(),
				dayPattern,
				item.getStartHalfHour(),
				item.getEndHalfHour(),
				item.isPlaced(),
				item.isConflicted());
	}

//	public static Instructor fromGWT(InstructorGWT instructor, Map<Integer, Course> coursesByID) {
//		instructor.verify();
//		Instructor ins = new Instructor();
//		ins.setDbid(instructor.getID());
//		ins.setFirstName(instructor.getFirstName());
//		ins.setLastName(instructor.getLastName());
//		ins.setUserID(instructor.getUsername());
//		ins.setMaxWtu(instructor.getMaxWtu());
//		ins.setCurWtu(-1337);
//		ins.setOffice(null);
//		ins.setFairness(-1337);
//		ins.setDisability(instructor.getDisabilities());
//		System.out.println("instructor disabled on fromGWT? " + ins.getDisability());
//		ins.setGenerosity(-1337);
//		ins.setAvailability(new WeekAvail());
//
//		HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
//		for (Integer courseID : instructor.getCoursePreferences().keySet()) {
//			Integer desire = instructor.getCoursePreferences().get(courseID);
//			Course course = coursesByID.get(courseID);
//			if (course.getDbid() == null)
//				System.out.println("ERROR, course id is null when converting to model");
//			coursePrefs.put(course.getDbid(), desire);
//		}
//		ins.setCoursePreferences(coursePrefs);
//
//
//		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> prefs = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
//		for (Integer sourceDay : instructor.gettPrefs().keySet()) {
//			Map<Integer, TimePreferenceGWT> sourceDayPrefs = instructor
//					.gettPrefs().get(sourceDay);
//
//			Integer day = sourceDay;//Day day = dayFromGWT(sourceDay);
//			LinkedHashMap<Integer, TimePreference> dayPrefs = new LinkedHashMap<Integer, TimePreference>();
//
//			for (Integer sourceTime : sourceDayPrefs.keySet()) {
//				TimePreferenceGWT sourceTimePrefs = sourceDayPrefs
//						.get(sourceTime);
//				dayPrefs.put(sourceTime, fromGWT(sourceTimePrefs));
//			}
//
//			prefs.put(day, dayPrefs);
//		}
//		ins.setTimePreferences(prefs);
//
//		ins.setItemsTaught(new Vector<ScheduleItem>());
//
//		ins.verify();
//
//		return ins;
//	}
//
//	private static Time timeFromGWT(Integer sourceTime) {
//		return new Time(sourceTime / 60, sourceTime % 60);
//	}
//
//	private static TimePreference fromGWT(TimePreferenceGWT sourceTimePrefs) {
//		return new TimePreference(timeFromGWT(sourceTimePrefs.getTime()),
//				sourceTimePrefs.getDesire());
//	}
//
//	public static ScheduleItemGWT toGWT(ScheduleItem schdItem, boolean isConflicted) {
//		String courseName = schdItem.getCourse().getName();
//		String instructor = (schdItem.getInstructor() == null ? "" : schdItem
//				.getInstructor().getName());
//		String courseDept = schdItem.getCourse().getDept();
//		
//		String courseNum = schdItem.getCourse().getCatalogNum();
//		
//		int section = schdItem.getSection();
//
//		ArrayList<Integer> dayNums = new ArrayList<Integer>();
//		for (Day d : schdItem.getDays().getDays())
//			dayNums.add(d.getNum());
//
//		int startTimeHour = schdItem.getStart().getHour();
//		int endTimeHour = schdItem.getEnd().getHour();
//		int startTimeMin = schdItem.getStart().getMinute();
//		int endTimeMin = schdItem.getEnd().getMinute();
//		String location = (schdItem.getLocation() == null ? "" : schdItem
//				.getLocation().toString());
//		CourseGWT course = toGWT(schdItem.getCourse());
//
//		// TODO: yero make this constructor take courseNum (string) instead of courseNumIntForSI (int)
//		//int courseNumIntForSI = Integer.parseInt(courseNum);
//		
//		return new ScheduleItemGWT(course, courseName, instructor, courseDept,
//				courseNum, section, dayNums, startTimeHour, startTimeMin,
//				endTimeHour, endTimeMin, location, isConflicted);
//	}
//
//	public static CourseGWT toGWT(Course course) {
//		CourseGWT newCourse = new CourseGWT();
//		newCourse.setID(course.getDbid());
//		
//		// TODO: holland make getCatalogNum() return a string
//		String courseNumString = course.getCatalogNum();
//		newCourse.setCatalogNum(courseNumString);
//		
//		newCourse.setCourseName(course.getName());
//		newCourse.setDept(course.getDept());
//		
//		// TODO: temporary solution, right now the model only supports one day combination,
//		// the view supports many. We need to change the model.
//		Set<Week> courseCombinations = new HashSet<Week>();
//		courseCombinations.add(course.getDays());
//		newCourse.setDays(Conversion.toGWT(courseCombinations));
//		
//		
//		newCourse.setHalfHoursPerWeek(Integer.toString(course.getLength()));
//		newCourse.setMaxEnroll(Integer.toString(course.getEnrollment()));
//		newCourse.setNumSections(Integer.toString(course.getNumOfSections()));
//		newCourse.setScu(Integer.toString(course.getScu()));
//		System.out.println("recalling model course " + course.getCatalogNum() + " type is " + course.getType() + " lec id is " + course.getLectureID());
//		newCourse.setType(course.getType().toString());
//		newCourse.setWtu(Integer.toString(course.getWtu()));
//		newCourse.setLectureID(course.getLectureID());
//		newCourse.setTetheredToLecture(course.getTetheredToLecture());
//		
//		System.out.println("recalled model course into gwt course lecture id is " + newCourse.getLectureID());
//		
//		return newCourse;
//	}
//
//	private static Set<DayCombinationGWT> toGWT(Set<Week> dayCombinationsSource) {
//		Set<DayCombinationGWT> dayCombinationsResult = new HashSet<DayCombinationGWT>();
//		for (Week dayCombinationSource : dayCombinationsSource) {
//			DayCombinationGWT dayCombinationResult = new DayCombinationGWT();
//			Set<Integer> dayCombinationResultInts = new HashSet<Integer>();
//			for (Day daySource : dayCombinationSource.getDays())
//				dayCombinationResultInts.add(toGWT(daySource));
//			dayCombinationResult.setDays(dayCombinationResultInts);
//			dayCombinationsResult.add(dayCombinationResult);
//			
//		}
//		return dayCombinationsResult;
//	}
//
//	private static Set<Week> fromGWT(Set<DayCombinationGWT> dayCombinationsSource) {
//		Set<Week> dayCombinationsResult = new HashSet<Week>();
//		
//		for (DayCombinationGWT dayCombinationSource : dayCombinationsSource) {
//			Week week = new Week();
//			for (Integer day : dayCombinationSource.getDays())
//				week.add(dayFromGWT(day));
//			dayCombinationsResult.add(week);
//		}
//		
//		return dayCombinationsResult;
//	}
//
//	private static Day dayFromGWT(Integer dayNum) {
//		for (Day day : Day.ALL_DAYS)
//			if (dayNum == day.getNum())
//				return day;
//		assert(false);
//		return null;
//	}
//
//	public static LocationGWT toGWT(Location location) {
//		return new LocationGWT(location.getDbid(), location.getBuilding(), location.getRoom(),
//				location.getType(), Integer.toString(location.getMaxOccupancy()),
//				location.getAdaCompliant(),
//				toGWT(location.getProvidedEquipment()));
//	}
//
//	public static LocationGWT.ProvidedEquipmentGWT toGWT(Location.ProvidedEquipment equipment) {
//		LocationGWT.ProvidedEquipmentGWT result = new LocationGWT.ProvidedEquipmentGWT();
//		result.hasLaptopConnectivity = equipment.hasLaptopConnectivity;
//		result.hasOverhead = equipment.hasOverhead;
//		result.isSmartRoom = equipment.isSmartRoom;
//		return result;
//	}
//	
//	public static Location fromGWT(LocationGWT location) {
//		Location loc = new Location();
//		loc.setDbid(location.getID());
//		loc.setRoom(location.getRoom());
//		loc.setAdaCompliant(location.isADACompliant());
//		loc.setAvailability(new WeekAvail());
//		loc.setBuilding(location.getBuilding());
//		loc.setMaxOccupancy(location.getMaxOccupancy());
//		loc.setProvidedEquipment(fromGWT(location.getEquipment()));
//		loc.setRoom(location.getRoom());
//		loc.setType(location.getType());
//		loc.setProvidedEquipment(fromGWT(location.getEquipment()));
//		loc.verify();
//		return loc;
//	}
//
//	private static ProvidedEquipment fromGWT(ProvidedEquipmentGWT equipment) {
//		ProvidedEquipment result = new ProvidedEquipment();
//		result.hasLaptopConnectivity = equipment.hasLaptopConnectivity;
//		result.hasOverhead = equipment.hasOverhead;
//		result.isSmartRoom = equipment.isSmartRoom;
//		return result;
//	}
//	
//	private static Integer parseIntOr0(String value) {
//		try {
//			return Integer.parseInt(value);
//		}
//		catch (NumberFormatException e) {
//			return 0;
//		}
//	}
//
//	public static Course fromGWT(CourseGWT course) {
//		Course newCourse = new Course();
//		newCourse.setDbid(course.getID());
//		newCourse.setName(course.getCourseName());
//		
//		// TODO: holland make setCatalogNum() take a string
//		String catalogNum = course.getCatalogNum();
//		newCourse.setCatalogNum(catalogNum);
//		
//		newCourse.setWtu(parseIntOr0(course.getWtu()));
//		newCourse.setScu(parseIntOr0(course.getScu()));
//		newCourse.setType(course.getType());
//		System.out.println("storing model course " + newCourse.getCatalogNum() + " type is " + course.getType());
//		newCourse.setEnrollment(parseIntOr0(course.getMaxEnroll()));
//		
//		newCourse.setLectureID(course.getLectureID());
//		newCourse.setTetheredToLecture(course.getTetheredToLecture());
//		
//		newCourse.setDept(course.getDept());
//		newCourse.setLength(parseIntOr0(course.getHalfHoursPerWeek()));
//		newCourse.setNumOfSections(course.getNumSections());
//
//		// TODO: temporary solution, right now the model only supports one day combination,
//		// the view supports many. We need to change the model.
//		Set<Week> courseDayCombinations = fromGWT(course.getDays());
//		Week newCourseWeek = Week.fiveDayWeek;
//		if (!courseDayCombinations.isEmpty())
//			newCourseWeek = courseDayCombinations.iterator().next();
//		newCourse.setDays(newCourseWeek);
//		
//		assert(newCourse.getLength() >= 0);
//		assert(newCourse.getDays() != null);
//		return newCourse;
//	}
//
//	public static UserDataGWT toGWT(UserData value) {
//		UserDataGWT result = new UserDataGWT();
//		assert(value.getDbid() != null);
//		result.setID(value.getDbid());
//		result.setPermissionLevel(value.getPermission());
//		result.setUserName(value.getUserId());
//		result.setScheduleID(value.getScheduleDBId());
//		return result;
//	}
}
