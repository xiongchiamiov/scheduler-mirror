package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.CouldNotBeScheduledException;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private Model model;
	private Database db;
	private Schedule schedule;
	private HashMap<String, Course> availableCourses;
	private HashMap<String, ScheduleItem> scheduleItems;

	public void login(String username) {
		model = new Model(username);
	}

	public Map<String, Integer> getScheduleNames() {
		return model.getSchedules();
	}

	public Integer openNewSchedule(String newScheduleName) {
		db = model.openNewSchedule(newScheduleName);
		return model.getScheduleID();
    }
    
    // Returns 0, null if its a guest
    // Returns 1, instructor if its an instructor
    // Returns 2, null if its a admin
    public Pair<Integer, InstructorGWT> openExistingSchedule(int scheduleID) {
    	db = model.openExistingSchedule(scheduleID);
    	return new Pair<Integer, InstructorGWT>(2, null); // tyero, change this
    }
    
    public void removeSchedule(String schedName) {
    	model.deleteSchedule(schedName);
    }
    
	public ArrayList<InstructorGWT> getInstructors()
			throws IllegalArgumentException {
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		for (Instructor instructor : model.getInstructors()) {
			results.add(Conversion.toGWT(instructor));
		}
		return results;
	}

	public void saveInstructors(ArrayList<InstructorGWT> instructors)
			throws IllegalArgumentException {
		assert (model != null);

		HashMap<String, Instructor> newInstructorsByUserID = new LinkedHashMap<String, Instructor>();

		for (InstructorGWT instructorGWT : instructors) {
			Instructor instructor = Conversion.fromGWT(instructorGWT);
			newInstructorsByUserID.put(instructor.getUserID(), instructor);

			displayInstructorPrefs(instructor);

			model.saveInstructor(instructor);
		}

		for (Instructor instructor : model.getInstructors())
			if (newInstructorsByUserID.get(instructor.getUserID()) == null)
				model.removeInstructor(instructor);
		assert (model.getInstructors().size() == instructors.size());
	}

	@Override
	public Collection<InstructorGWT> saveInstructors(
			Collection<InstructorGWT> instructors) {
		HashMap<String, Instructor> newLocationsByUserID = new LinkedHashMap<String, Instructor>();

		for (InstructorGWT instructorGWT : instructors) {
			Instructor instructor = Conversion.fromGWT(instructorGWT);
			newLocationsByUserID.put(instructor.getUserID(), instructor);
			model.saveInstructor(instructor);
		}

		for (Instructor instructor : model.getInstructors())
			if (newLocationsByUserID.get(instructor.getUserID()) == null)
				model.removeInstructor(instructor);

		assert (model.getInstructors().size() == instructors.size());

		return getInstructors();
	}

	@Override
	public ArrayList<InstructorGWT> getInstructors2() {
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		int id = 1;
		for (Instructor instructor : model.getInstructors())
			results.add(Conversion.toGWT(id++, instructor));
		return results;
	}

	private void displayInstructorPrefs(Instructor instructor) {
		System.out.println("Prefs for instructor " + instructor.getLastName());

		for (Day day : instructor.getTimePreferences().keySet())
			for (Time time : instructor.getTimePreferences().get(day).keySet())
				System.out.println("Day "
						+ day.getNum()
						+ " time "
						+ time.getHour()
						+ ":"
						+ time.getMinute()
						+ " is "
						+ instructor.getTimePreferences().get(day).get(time)
								.getDesire());
	}

	public ArrayList<ScheduleItemGWT> generateSchedule() {
		assert (model != null);

		Collection<Course> coursesToGenerate = model.getCourses();

		// TODO: fix this hack.
		for (Course course : coursesToGenerate) {
			assert (course.getDays().size() > 0);
			if (course.getLength() < course.getDays().size() * 2) {
				course.setLength(course.getDays().size() * 2);
				System.err
						.println("Warning: the course length was too low, automatically set it to "
								+ course.getLength());
			}
		}

		for (Instructor instructor : model.getInstructors())
			System.out.println("outside, num instructor day prefs for "
					+ instructor.getLastName() + ": "
					+ instructor.getTimePreferences().size());

		Collection<ScheduleItem> scheduleItems = schedule
				.generate(coursesToGenerate);
		System.out.println("schedule items: " + schedule.getItems().size());

		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
		for (ScheduleItem item : scheduleItems) {
			gwtItems.add(Conversion.toGWT(item, false));
		}
		for (ScheduleItem item : schedule.getDirtyList()) {
			gwtItems.add(Conversion.toGWT(item, true));
		}
		return gwtItems;
	}

	@Override
	public ArrayList<ScheduleItemGWT> getGWTScheduleItems(
			ArrayList<CourseGWT> courses) {
		Course courseWithSections;

		assert (model != null);
		scheduleItems = new HashMap<String, ScheduleItem>();

		Collection<Course> coursesToGenerate = new LinkedList<Course>();
		for (CourseGWT course : courses) {
			courseWithSections = new Course(availableCourses.get(course
					.getDept() + course.getCatalogNum()));
			courseWithSections.setNumOfSections(course.getNumSections());
			coursesToGenerate.add(courseWithSections);
		}
		// TODO: fix this hack.
		for (Course course : coursesToGenerate) {
			assert (course.getDays().size() > 0);
			if (course.getLength() < course.getDays().size() * 2) {
				course.setLength(course.getDays().size() * 2);
				System.err
						.println("Warning: the course length was too low, automatically set it to "
								+ course.getLength());
			}
		}

		schedule.generate(coursesToGenerate);

		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();

		for (ScheduleItem item : schedule.getItems()) {
			gwtItems.add(Conversion.toGWT(item, false));
			scheduleItems.put(item.getCourse().getDept()
					+ item.getCourse().getCatalogNum() + item.getSection(),
					item);
		}
		for (ScheduleItem item : schedule.getDirtyList()) {
			gwtItems.add(Conversion.toGWT(item, true));
			scheduleItems.put(item.getCourse().getDept()
					+ item.getCourse().getCatalogNum() + item.getSection(),
					item);
		}
		return gwtItems;
	}

	public ScheduleItemList rescheduleCourse(ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule) {
		assert (model != null);
		ScheduleItemList gwtItems = new ScheduleItemList();
		Course course;
		int numberOfDays = days.size();
		Day[] daysScheduled = new Day[numberOfDays];
		Week daysInWeek;
		Time startTime;
		int i;
		ScheduleItem moved;
		String schdItemKey = scheduleItem.getDept()
				+ scheduleItem.getCatalogNum() + scheduleItem.getSection();
		String conflict = "";

		for (i = 0; i < numberOfDays; i++) {
			switch (days.get(i)) {
			case 1:
				daysScheduled[i] = (Day.MON);
				break;
			case 2:
				daysScheduled[i] = (Day.TUE);
				break;
			case 3:
				daysScheduled[i] = (Day.WED);
				break;
			case 4:
				daysScheduled[i] = (Day.THU);
				break;
			case 5:
				daysScheduled[i] = (Day.FRI);
				break;
			}
		}

		daysInWeek = new Week(daysScheduled);
		startTime = new Time(startHour, (atHalfHour ? 30 : 0));

		if (inSchedule) {
			moved = scheduleItems.get(schdItemKey);
			schedule.removeConflictingItem(moved);
			try {
				schedule.move(moved, daysInWeek, startTime);
			} catch (CouldNotBeScheduledException e) {
				conflict = e.toString();
				schedule.addConflictingItem(e.getSi());
			}
		} else {
			course = availableCourses.get(scheduleItem.getDept()
					+ scheduleItem.getCatalogNum());
			course.setNumOfSections(1);
			schedule.genItem(course, daysInWeek, startTime);
		}

		scheduleItems = new HashMap<String, ScheduleItem>();

		for (ScheduleItem item : schedule.getItems()) {
			gwtItems.add(Conversion.toGWT(item, false));
			scheduleItems.put(item.getCourse().getDept()
					+ item.getCourse().getCatalogNum() + item.getSection(),
					item);
		}
		for (ScheduleItem item : schedule.getDirtyList()) {
			gwtItems.add(Conversion.toGWT(item, true));
			scheduleItems.put(item.getCourse().getDept()
					+ item.getCourse().getCatalogNum() + item.getSection(),
					item);

		}

		gwtItems.conflict = conflict;
		return gwtItems;
	}

	@Override
	public ArrayList<LocationGWT> getLocations() {
		ArrayList<LocationGWT> results = new ArrayList<LocationGWT>();
		int id = 1;
		for (Location location : model.getLocations())
			results.add(Conversion.toGWT(id++, location));
		return results;
	}

	@Override
	public Collection<LocationGWT> saveLocations(
			Collection<LocationGWT> locations) {
		HashMap<String, Location> newLocationsByUserID = new LinkedHashMap<String, Location>();

		for (LocationGWT locationGWT : locations) {
			Location location = Conversion.fromGWT(locationGWT);
			newLocationsByUserID
					.put(location.getBuilding() + "-" + location.getRoom(),
							location);
			model.saveLocation(location);
		}

		for (Location location : model.getLocations())
			if (newLocationsByUserID.get(location.getBuilding() + "-"
					+ location.getRoom()) == null)
				model.removeLocation(location);

		assert (model.getLocations().size() == locations.size());

		return getLocations();
	}
	
	@Override
	public ArrayList<CourseGWT> getCourses() {
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		availableCourses = new HashMap<String, Course>();
		for (Course course : model.getCourses()) {
			course.setLength(6);
			availableCourses.put(course.getDept() + course.getCatalogNum(),
					course);
			results.add(Conversion.toGWT(course));
		}
		return results;
	}

	@Override
	public void saveCourses(ArrayList<CourseGWT> courses) {
		HashMap<String, Course> newLocationsByUserID = new LinkedHashMap<String, Course>();

		for (CourseGWT courseGWT : courses) {
			Course course = Conversion.fromGWT(courseGWT);
			newLocationsByUserID.put(
					course.getDept() + "-" + course.getCatalogNum(), course);
			model.saveCourse(course);
		}

		for (Course course : model.getCourses())
			if (newLocationsByUserID.get(course.getDept() + "-"
					+ course.getCatalogNum()) == null)
				model.removeCourse(course);

		assert (model.getCourses().size() == courses.size());
	}

	@Override
	public Collection<CourseGWT> saveCourses(Collection<CourseGWT> courses) {
		HashMap<String, Course> newLocationsByUserID = new LinkedHashMap<String, Course>();

		for (CourseGWT courseGWT : courses) {
			Course crs = Conversion.fromGWT(courseGWT);
			newLocationsByUserID.put(
					courseGWT.getDept() + "-" + courseGWT.getCatalogNum(), crs);
			model.saveCourse(crs);
		}

		for (Course course : model.getCourses())
			if (newLocationsByUserID.get(course.getDept() + "-"
					+ course.getCatalogNum()) == null)
				model.removeCourse(course);

		assert (model.getCourses().size() == courses.size());

		return getCourses();
	}

	@Override
	public ArrayList<CourseGWT> getCourses2() {
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		int id = 1;
		for (Course course : model.getCourses())
			results.add(Conversion.toGWT(id++, course));
		return results;
	}

	private boolean hasPreferences(Instructor instructor) {
		int totalDesire = 0;
		for (Day day : instructor.getTimePreferences().keySet()) {
			LinkedHashMap<Time, TimePreference> dayPrefs = instructor
					.getTimePreferences().get(day);
			for (Time time : dayPrefs.keySet()) {
				TimePreference timePrefs = dayPrefs.get(time);
				totalDesire += timePrefs.getDesire();
			}
		}
		return totalDesire > 0;
	}

	@Override
	public void saveInstructor(InstructorGWT instructorGWT) {
		Instructor instructor = Conversion.fromGWT(instructorGWT);

		displayInstructorPrefs(instructor);

		model.saveInstructor(instructor);
	}

	@Override
	public ArrayList<ScheduleItemGWT> getSchedule() {
		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
		ScheduleItemGWT gwtItem;

		if (schedule == null) {
			schedule = new Schedule(model.getInstructors(),
					model.getLocations());
		}
		/*
		 * This throws exceptions! schedule =
		 * db.getScheduleDB().getSchedule(model.getScheduleID());
		 */
		scheduleItems = new HashMap<String, ScheduleItem>();

		for (ScheduleItem item : schedule.getItems()) {
			gwtItem = Conversion.toGWT(item, false);
			scheduleItems.put(gwtItem.getDept() + gwtItem.getCatalogNum()
					+ gwtItem.getSection(), item);
			gwtItems.add(gwtItem);
		}

		for (ScheduleItem item : schedule.getDirtyList()) {
			gwtItem = Conversion.toGWT(item, true);
			scheduleItems.put(gwtItem.getDept() + gwtItem.getCatalogNum()
					+ gwtItem.getSection(), item);
			gwtItems.add(gwtItem);
		}
		// System.out.println(model.exportToCSV(schedule));

		return gwtItems;
	}

	@Override
	public int copySchedule(int existingScheduleID, String scheduleName) {
		int newSchedule = model.copySchedule(existingScheduleID, scheduleName);
		openExistingSchedule(newSchedule);
		return newSchedule;
	}

	@Override
	public ArrayList<ScheduleItemGWT> removeScheduleItem(ScheduleItemGWT removed) {
		String schdItemKey = removed.getDept() + removed.getCatalogNum()
				+ removed.getSection();
		ScheduleItemGWT gwtItem;
		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();

		schedule.remove(scheduleItems.get(schdItemKey));
		scheduleItems = new HashMap<String, ScheduleItem>();

		for (ScheduleItem item : schedule.getItems()) {
			gwtItem = Conversion.toGWT(item, false);
			schdItemKey = gwtItem.getDept() + gwtItem.getCatalogNum()
					+ gwtItem.getSection();
			scheduleItems.put(schdItemKey, item);
			gwtItems.add(gwtItem);
		}
		for (ScheduleItem item : schedule.getDirtyList()) {
			gwtItem = Conversion.toGWT(item, true);
			schdItemKey = gwtItem.getCourseString() + gwtItem.getCatalogNum()
					+ gwtItem.getSection();
			scheduleItems.put(schdItemKey, item);
			gwtItems.add(gwtItem);
		}

		return gwtItems;
	}

	@Override
	public void saveSchedule() {
		model.saveSchedule(schedule);
	}

	@Override
	public int importFromCSV(String scheduleName, String value) {
		model.openNewSchedule(scheduleName);
		model.importFromCSV(value);
		return model.getScheduleID();
	}
}
