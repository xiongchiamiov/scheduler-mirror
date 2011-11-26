package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.udb.UserData;
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
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private Model model;
	private Schedule schedule;
	private HashMap<String, Course> availableCourses;
	private HashMap<String, ScheduleItem> scheduleItems;

	@Override
	public void login(String username) {
		model = new Model(username);
	}

	@Override
	public Map<String, UserDataGWT> getScheduleNames() {
		Map<String, UserDataGWT> availableSchedules = new HashMap<String, UserDataGWT>();
		for (Entry<String, UserData> entry : model.getSchedules().entrySet()) {
			assert(entry.getValue() != null);
			availableSchedules.put(entry.getKey(), Conversion.toGWT(entry.getValue()));
		}
		return availableSchedules;
	}

	@Override
	public Integer openNewSchedule(String newScheduleName) {
		model.openNewSchedule(newScheduleName);
		return model.getScheduleID();
    }
    
    // Returns 0, null if its a guest
    // Returns 1, instructor if its an instructor
    // Returns 2, null if its a admin
	@Override
    public Pair<Integer, InstructorGWT> openExistingSchedule(int scheduleID) {
    	model.openExistingSchedule(scheduleID);
    	return new Pair<Integer, InstructorGWT>(2, null); // tyero, change this
    }

	@Override
    public void removeSchedule(String schedName) {
    	model.deleteSchedule(schedName);
    }

	@Override
	public ArrayList<InstructorGWT> getInstructors() throws IllegalArgumentException {
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		for (Instructor instructor : model.getInstructors())
			results.add(Conversion.toGWT(instructor));
		return results;
	}

	Map<Integer, Instructor> getInstructorsByID() {
		Map<Integer, Instructor> result = new HashMap<Integer, Instructor>();
		for (Instructor instructor : model.getInstructors())
			result.put(instructor.getDbid(), instructor);
		return result;
	}

	Map<Integer, Course> getCoursesByID() {
		Map<Integer, Course> result = new HashMap<Integer, Course>();
		for (Course course : model.getCourses())
			result.put(course.getDbid(), course);
		return result;
	}

	@Override
	public void saveInstructors(List<InstructorGWT> added, List<InstructorGWT> edited, List<InstructorGWT> removed) {
		assert(added != null);
		assert(edited != null);
		assert(removed != null);
		
		for (InstructorGWT addedInstructor : added) {
			assert(!edited.contains(addedInstructor));
			addedInstructor.setID(-1);
			model.saveInstructor(Conversion.fromGWT(addedInstructor, getCoursesByID()));
		}

		for (InstructorGWT editedInstructor : edited) {
			assert(!added.contains(editedInstructor));
			model.saveInstructor(Conversion.fromGWT(editedInstructor, getCoursesByID()));
		}
	
		for (InstructorGWT removedInstructorGWT : removed) {
			System.out.println("Removing instructor " + removedInstructorGWT.getLastName() + " id " + removedInstructorGWT.getID());
			Instructor removedInstructor = Conversion.fromGWT(removedInstructorGWT, getCoursesByID());
			model.removeInstructor(removedInstructor);
			assert(!model.getInstructors().contains(removedInstructor));
		}
	}

//	private void displayInstructorPrefs(Instructor instructor) {
//		System.out.println("Prefs for instructor " + instructor.getLastName());
//
//		for (Day day : instructor.getTimePreferences().keySet())
//			for (Time time : instructor.getTimePreferences().get(day).keySet())
//				System.out.println("Day "
//						+ day.getNum()
//						+ " time "
//						+ time.getHour()
//						+ ":"
//						+ time.getMinute()
//						+ " is "
//						+ instructor.getTimePreferences().get(day).get(time)
//								.getDesire());
//	}

	@Override
	public ArrayList<ScheduleItemGWT> generateSchedule() {
		assert (model != null);

		List<Course> coursesToGenerate = model.getCourses();

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

		List<ScheduleItem> scheduleItems = schedule
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
			List<CourseGWT> courses) {
		Course courseWithSections;

		assert (model != null);
		assert(availableCourses != null);
		scheduleItems = new HashMap<String, ScheduleItem>();

		List<Course> coursesToGenerate = new LinkedList<Course>();
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

	@Override
	public ScheduleItemList rescheduleCourse(ScheduleItemGWT scheduleItem,
			List<Integer> days, int startHour, boolean atHalfHour,
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
		for (Location location : model.getLocations())
			results.add(Conversion.toGWT(location));
		return results;
	}

	@Override
	public void saveInstructor(InstructorGWT instructorGWT) {
		Instructor instructor = Conversion.fromGWT(instructorGWT, getCoursesByID());
		model.saveInstructor(instructor);
	}

	@Override
	public ArrayList<ScheduleItemGWT> getSchedule() {
		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
		ScheduleItemGWT gwtItem;

		schedule = model.loadSchedule(model.getScheduleID());
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


	@Override
	public void saveLocations(List<LocationGWT> added, List<LocationGWT> edited, List<LocationGWT> removed) {
		for (LocationGWT addedLocation : added)
			model.saveLocation(Conversion.fromGWT(addedLocation));
		
		for (LocationGWT editedLocation : edited)
			model.saveLocation(Conversion.fromGWT(editedLocation));
		
		for (LocationGWT removedLocation : removed)
			model.removeLocation(Conversion.fromGWT(removedLocation));
	}

	@Override
	public ArrayList<CourseGWT> getCourses() throws IllegalArgumentException {
		availableCourses = new HashMap<String, Course>();
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		for (Course course : model.getCourses()) {
			availableCourses.put(course.getDept() + course.getCatalogNum(), course);
			results.add(Conversion.toGWT(course));
		}
		return results;
	}

	@Override
	public void saveCourses(List<CourseGWT> added, List<CourseGWT> edited, List<CourseGWT> removed) {
		for (CourseGWT addedCourse : added)
			model.saveCourse(Conversion.fromGWT(addedCourse));
		
		for (CourseGWT editedCourse : edited)
			model.saveCourse(Conversion.fromGWT(editedCourse));
		
		for (CourseGWT removedCourse : removed)
			model.removeCourse(Conversion.fromGWT(removedCourse));
	}
	
	@Override
	public int exportCSV(){
		if (schedule == null) {
			schedule = new Schedule(model.getInstructors(),
					model.getLocations());
		}
		
		/** TODO replace new Date with export to CSV String */
		//return CSVDownload.save(model.exportToCSV(schedule));
		return CSVDownload.save(model.exportToCSV(schedule));
	}
}
