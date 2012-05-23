package scheduler.view.web.server;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import scheduler.model.Course;
import scheduler.model.Day;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.Location;
import scheduler.model.Model;
import scheduler.model.ScheduleItem;
import scheduler.model.db.DatabaseException;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.WeekGWT;
import scheduler.view.web.shared.WorkingDocumentGWT;

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
	
	public static InstructorGWT instructorToGWT(Instructor instructor) throws DatabaseException {
		return new InstructorGWT(
				instructor.getID(),
				instructor.getUsername(),
				instructor.getFirstName(),
				instructor.getLastName(),
				instructor.getMaxWTU(),
				instructor.getTimePreferences(),
				instructor.getCoursePreferences(),
				instructor.isSchedulable());
	}
	
	public static Course courseFromGWT(Model model, CourseGWT course) throws DatabaseException {
		assert(course.getID() == null || course.getID() >= 0);
		
		Collection<Set<Day>> modelDayPatterns = new LinkedList<Set<Day>>();
		for (WeekGWT gwtDayPattern : course.getDayPatterns())
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
		assert(instructor.getID() == null || instructor.getID() >= 0);
		
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

	static WeekGWT dayPatternToGWT(Set<Day> modelDayPattern) {
		WeekGWT gwtDayPattern = new WeekGWT();
		for (Day modelDay : modelDayPattern)
			gwtDayPattern.getDays().add(dayToGWT(modelDay));
		return gwtDayPattern;
	}

	static Set<Day> dayPatternFromGWT(WeekGWT gwtDayPattern) {
		Set<Day> modelDayPattern = new TreeSet<Day>();
		for (DayGWT gwtDay : gwtDayPattern.getDays())
			modelDayPattern.add(dayFromGWT(gwtDay));
		return modelDayPattern;
	}

	static CourseGWT courseToGWT(Course course) throws DatabaseException {
		Set<WeekGWT> dayPatterns = new TreeSet<WeekGWT>();
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
	
	static void readCourseFromGWT(CourseGWT source, Course result, Model model) throws DatabaseException {
		assert(source.getID() == null || source.getID() >= 0);
		
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
		for (WeekGWT combo : source.getDayPatterns())
			dayPatterns.add(dayPatternFromGWT(combo));
		result.setDayPatterns(dayPatterns);
		
		result.setUsedEquipment(source.getUsedEquipment());
		result.setLecture(source.getLectureID() == -1 ? null : model.findCourseByID(source.getLectureID()));
		result.setTetheredToLecture(source.getTetheredToLecture());
	}

	public static void readInstructorFromGWT(InstructorGWT source, Instructor result) {
		assert(source.getID() == null || source.getID() >= 0);
		
		result.setIsSchedulable(source.isSchedulable());
		result.setFirstName(source.getFirstName());
		result.setLastName(source.getLastName());
		result.setUsername(source.getUsername());
		result.setMaxWTU(source.getRawMaxWtu());
		result.setCoursePreferences(source.getCoursePreferences());
		result.setTimePreferences(source.gettPrefs());
	}

	public static void readLocationFromGWT(LocationGWT source, Location result) {
		assert(source.getID() == null || source.getID() >= 0);
		
		result.setIsSchedulable(source.isSchedulable());
		result.setMaxOccupancy(source.getRawMaxOccupancy());
		result.setRoom(source.getRoom());
		result.setType(source.getType());
	}

	public static LocationGWT locationToGWT(Location location) throws DatabaseException {
		LocationGWT result = new LocationGWT(location.getID(), location.getRoom(), location.getType(), location.getMaxOccupancy(), location.getProvidedEquipment(), location.isSchedulable());
//		System.out.println("result room: " + result.getRoom() + " from " + location.getRoom());
		return result;
	}

	public static WorkingDocumentGWT workingDocumentToGWT(Document doc) throws DatabaseException {
		assert(doc != null);
		assert(doc.getID() != null);
		assert(doc.getName() != null);
		assert(doc.getStaffInstructor() != null);
		assert(doc.getStaffInstructor().getID() != null);
		assert(doc.getTBALocation() != null);
		assert(doc.getTBALocation().getID() != null);
		assert(doc.getChooseForMeInstructor() != null);
		assert(doc.getChooseForMeInstructor().getID() != null);
		assert(doc.getChooseForMeLocation() != null);
		assert(doc.getChooseForMeLocation().getID() != null);
		
		assert(doc.isWorkingCopy());
		
		return new WorkingDocumentGWT(
				doc.getID(),
				doc.getName(),
				doc.getStaffInstructor().getID(),
				doc.getTBALocation().getID(),
				doc.getChooseForMeInstructor().getID(),
				doc.getChooseForMeLocation().getID(),
				doc.isTrashed(),
				doc.getStartHalfHour(),
				doc.getEndHalfHour());
	}

	public static OriginalDocumentGWT originalDocumentToGWT(Document doc, String workingChangesSummary) throws DatabaseException {
		assert(doc != null);
		assert(doc.getID() != null);
		assert(doc.getName() != null);
		assert(doc.getStaffInstructor() != null);
		assert(doc.getStaffInstructor().getID() != null);
		assert(doc.getTBALocation() != null);
		assert(doc.getTBALocation().getID() != null);
		
		assert(!doc.isWorkingCopy());
		
		return new OriginalDocumentGWT(
				doc.getID(),
				doc.getName(),
				doc.getStaffInstructor().getID(),
				doc.getTBALocation().getID(),
				doc.getChooseForMeInstructor().getID(),
				doc.getChooseForMeLocation().getID(),
				doc.isTrashed(),
				doc.getStartHalfHour(),
				doc.getEndHalfHour(),
				workingChangesSummary);
	}

	public static ScheduleItem scheduleItemFromGWT(Model model, ScheduleItemGWT source) throws DatabaseException {
		assert(source.getID() == null || source.getID() >= 0);
		
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
		WeekGWT pattern = dayPatternToGWT(item.getDays());
		return new ScheduleItemGWT(item.getID(), item.getCourse().getID(), item.getInstructor().getID(), item.getLocation().getID(), item.getSection(), pattern, item.getStartHalfHour(), item.getEndHalfHour(), item.isPlaced(), item.isConflicted());
	}

	public static void readScheduleItemFromGWT(Model model, ScheduleItemGWT itemGWT, ScheduleItem item) throws DatabaseException {
		assert(itemGWT.getID() == null || itemGWT.getID() >= 0);
		
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
		Document document = null;
		
		if (documentGWT instanceof OriginalDocumentGWT) {
			OriginalDocumentGWT originalDocumentGWT = (OriginalDocumentGWT)documentGWT;
			assert(originalDocumentGWT.getID() >= 0);
			document = model.findDocumentByID(originalDocumentGWT.getID());
		}
		else if (documentGWT instanceof WorkingDocumentGWT) {
			WorkingDocumentGWT workingDocumentGWT = (WorkingDocumentGWT)documentGWT;
			assert(workingDocumentGWT.getRealID() >= 0);
			document = model.findDocumentByID(workingDocumentGWT.getRealID());
		}
		else
			assert(false);
		
		assert(document != null);

		document.setName(documentGWT.getName());
		document.setEndHalfHour(documentGWT.getEndHalfHour());
		document.setStartHalfHour(documentGWT.getStartHalfHour());
		document.setIsTrashed(documentGWT.isTrashed());
		
		return document;
	}
}
