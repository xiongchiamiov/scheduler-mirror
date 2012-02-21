package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
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
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
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

	@Override
	public void login(String username) {
		model = new Model(username);
	}

	@Override
	public Map<String, UserDataGWT> getScheduleNames() {
		Map<String, UserDataGWT> availableSchedules = new HashMap<String, UserDataGWT>();
		
		for (Entry<String, UserData> entry : model.getSchedules().entrySet()) {
			assert (entry.getValue() != null);
			availableSchedules.put(entry.getKey(),
					Conversion.toGWT(entry.getValue()));
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
	public String openExistingSchedule(int scheduleID) {
		System.out.println("GreetingServiceImpl.openExistingSchedule(" + scheduleID + ")");
		model.openExistingSchedule(scheduleID);
		return model.getSchedule().getName();
	}

	@Override
	public void removeSchedule(String schedName) {
		model.deleteSchedule(schedName);
	}

	@Override
	public ArrayList<InstructorGWT> getInstructors()
			throws IllegalArgumentException {
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		for (Instructor instructor : model.getInstructors()) {
			System.out.println("Reading instructor, prefs:");
			for (Entry<Integer, Integer> entry : instructor.getCoursePreferences().entrySet())
				System.out.println("Course id " + entry.getKey() + " pref: " + entry.getValue());
			results.add(Conversion.toGWT(instructor));
		}
		return results;
	}

	Map<Integer, Instructor> getInstructorsByID() {
		Map<Integer, Instructor> result = new HashMap<Integer, Instructor>();
		for (Instructor instructor : model.getInstructors()) {
			System.out.println("Reading instructor, prefs: " + instructor.toString());
			for (Entry<Integer, Integer> entry : instructor.getCoursePreferences().entrySet())
				System.out.println("Course id " + entry.getKey() + " pref: " + entry.getValue());
			result.put(instructor.getDbid(), instructor);
		}
		return result;
	}

	Map<Integer, Course> getCoursesByID() {
		Map<Integer, Course> result = new HashMap<Integer, Course>();
		for (Course course : model.getCourses())
			result.put(course.getDbid(), course);
		return result;
	}

	// private void displayInstructorPrefs(Instructor instructor) {
	// System.out.println("Prefs for instructor " + instructor.getLastName());
	//
	// for (Day day : instructor.getTimePreferences().keySet())
	// for (Time time : instructor.getTimePreferences().get(day).keySet())
	// System.out.println("Day "
	// + day.getNum()
	// + " time "
	// + time.getHour()
	// + ":"
	// + time.getMinute()
	// + " is "
	// + instructor.getTimePreferences().get(day).get(time)
	// .getDesire());
	// }

	@Override
	public ArrayList<ScheduleItemGWT> generateSchedule(List<CourseGWT> courses, HashMap<String, ScheduleItemGWT> scheduleItems) {
		Course courseWithSections;
		Schedule schedule = model.getSchedule();
		Instructor ins;
		Location loc;
		
		assert (model != null);
		scheduleItems = new HashMap<String, ScheduleItemGWT>();

		List<Course> coursesToGenerate = new LinkedList<Course>();
		for (CourseGWT course : courses) {
			courseWithSections = Conversion.fromGWT(course);
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

//		List<Location> locToGenerate = new LinkedList<Location>();
//		
//		for (LocationGWT location: getLocations()) {
//			//if(instructor == null) 
//				//System.out.println("Instructor from gwt before conversion is null");
//
//			loc = Conversion.fromGWT(location);
//			//if(loc == null)
//				//System.out.println("Null instructor ins after gwt conversion");
//			locToGenerate.add(loc);
//		}
//		
//		schedule.setlSourceList(locToGenerate); 
//		
//		List<Instructor> insToGenerate = new LinkedList<Instructor>();
//		
//		for (InstructorGWT instructor: getInstructors()) {
//			if(instructor == null) 
//				System.out.println("Instructor from gwt before conversion is null");
//
//			ins = Conversion.fromGWT(instructor, getCoursesByID());
//			if(ins == null)
//				System.out.println("Null instructor ins after gwt conversion");
//			insToGenerate.add(ins);
//		}
//		
//		schedule.setiSourceList(insToGenerate); 
		
		schedule.generate(coursesToGenerate);

		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();

		for (ScheduleItem item : schedule.getItems()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			gwtItems.add(Conversion.toGWT(item, false));
//			scheduleItems.put(item.getCourse().getDept()
//					+ item.getCourse().getCatalogNum() + item.getSection(),
//					item);
		}
		
		for (ScheduleItem item : schedule.getDirtyList()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			gwtItems.add(Conversion.toGWT(item, true));
//			scheduleItems.put(item.getCourse().getDept()
//					+ item.getCourse().getCatalogNum() + item.getSection(),
//					item);
		}
		model.saveSchedule(schedule);

		return gwtItems;
	}

	@Override
	public ScheduleItemList rescheduleCourse(ScheduleItemGWT scheduleItem,
			List<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule, HashMap<String, ScheduleItemGWT> scheduleItems) {
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
		Schedule schedule = model.getSchedule();

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
			//moved = scheduleItems.get(schdItemKey);
			//schedule.removeConflictingItem(moved);
//			try {
//				schedule.move(moved, daysInWeek, startTime);
//			} catch (CouldNotBeScheduledException e) {
//				conflict = e.toString();
//				schedule.addConflictingItem(e.getSi());
//			}
		} else {
			course = Conversion.fromGWT(scheduleItem.getCourse());

			if (course.getScheduleDBId() == null) {
				course.setScheduleDBId(-1);
			}
			schedule.genItem(course, daysInWeek, startTime);
		}

		//scheduleItems = new HashMap<String, ScheduleItem>();

		for (ScheduleItem item : schedule.getItems()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			gwtItems.add(Conversion.toGWT(item, false));
//			scheduleItems.put(item.getCourse().getDept()
//					+ item.getCourse().getCatalogNum() + item.getSection(),
//					item);
		}
		
		for (ScheduleItem item : schedule.getDirtyList()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			
			gwtItems.add(Conversion.toGWT(item, true));
//			scheduleItems.put(item.getCourse().getDept()
//					+ item.getCourse().getCatalogNum() + item.getSection(),
//					item);

		}

		gwtItems.conflict = conflict;
		model.saveSchedule(schedule);
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
		Instructor instructor = Conversion.fromGWT(instructorGWT,
				getCoursesByID());

		System.out.println("Saving instructor, prefs:");
		for (Entry<Integer, Integer> entry : instructor.getCoursePreferences().entrySet())
			System.out.println("Course id " + entry.getKey() + " pref: " + entry.getValue());
		
		model.saveInstructor(instructor);
	}

	@Override
	public ArrayList<ScheduleItemGWT> getSchedule(HashMap<String, ScheduleItemGWT> scheduleItems) {
		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
		ScheduleItemGWT gwtItem;
		Schedule schedule = model.getSchedule();

		scheduleItems = new HashMap<String, ScheduleItemGWT>();

		for (ScheduleItem item : schedule.getItems()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			gwtItem = Conversion.toGWT(item, false);
//			scheduleItems.put(gwtItem.getDept() + gwtItem.getCatalogNum()
//					+ gwtItem.getSection(), item);
			gwtItems.add(gwtItem);
		}

		for (ScheduleItem item : schedule.getDirtyList()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			gwtItem = Conversion.toGWT(item, true);
//			scheduleItems.put(gwtItem.getDept() + gwtItem.getCatalogNum()
//					+ gwtItem.getSection(), item);
			gwtItems.add(gwtItem);
		}
		// System.out.println(model.exportToCSV(schedule));

		return gwtItems;
	}

	@Override
	public ArrayList<ScheduleItemGWT> removeScheduleItem(ScheduleItemGWT removed, HashMap<String, ScheduleItemGWT> scheduleItems) {
		String schdItemKey = removed.getDept() + removed.getCatalogNum()
				+ removed.getSection();
		ScheduleItemGWT gwtItem;
		ArrayList<ScheduleItemGWT> gwtItems = new ArrayList<ScheduleItemGWT>();
		Schedule schedule = model.getSchedule();

		System.out.println("before " + schedule.getItems().size()
				+ " removing " + scheduleItems.get(schdItemKey));
//		schedule.remove(scheduleItems.get(schdItemKey));
		System.out.println("after " + schedule.getItems().size());
		scheduleItems = new HashMap<String, ScheduleItemGWT>();

		for (ScheduleItem item : schedule.getItems()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			gwtItem = Conversion.toGWT(item, false);
			schdItemKey = gwtItem.getDept() + gwtItem.getCatalogNum()
					+ gwtItem.getSection();
//			scheduleItems.put(schdItemKey, item);
			gwtItems.add(gwtItem);
		}
		for (ScheduleItem item : schedule.getDirtyList()) {
			if (item.getCourse().getDbid() == null) {
				item.getCourse().setDbid(-1);
			}
			gwtItem = Conversion.toGWT(item, true);
			schdItemKey = gwtItem.getCourseString() + gwtItem.getCatalogNum()
					+ gwtItem.getSection();
//			scheduleItems.put(schdItemKey, item);
			gwtItems.add(gwtItem);
		}

		model.saveSchedule(schedule);

		return gwtItems;
	}

	@Override
	public ArrayList<CourseGWT> getCourses() throws IllegalArgumentException {
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		for (Course course : model.getCourses()) {
			System.out.println("returning course " + course.getCatalogNum()
					+ " type " + course.getType());
			results.add(Conversion.toGWT(course));
		}
		return results;
	}

	public void saveSchedule() {
		Schedule schedule = model.getSchedule();
		model.saveSchedule(schedule);
	}

	public void saveCurrentScheduleAs(String schedName) {
		model.saveCurrentScheduleAs(schedName);
	}

	@Override
	public int exportCSV() {
		Schedule schedule = model.getSchedule();
		if (schedule == null) {
			schedule = new Schedule(model.getInstructors(),
					model.getLocations());
		}

		/** TODO replace new Date with export to CSV String */
		// return CSVDownload.save(model.exportToCSV(schedule));
		return CSVDownload.save(model.exportToCSV());
	}

	@Override
	public CourseGWT addCourse(CourseGWT toAdd) {
		System.out.println("GreetingServiceImpl.addCourse()");
		toAdd.setID(-1);
		Course course = Conversion.fromGWT(toAdd);
		model.saveCourse(course);
		assert (course.getDbid() != -1);
		System.out.println("GreetingServiceImpl.addCourse result CourseID " + course.getDbid());
		return Conversion.toGWT(course);
	}

	@Override
	public void editCourse(CourseGWT toEdit) {
		System.out.println("GreetingServiceImpl.editCourse(CourseID " + toEdit.getID() + ")");
//		System.out.println("editCourse incoming gwt course " + toEdit.getCatalogNum() + " lectureid " + toEdit.getLectureID());
		Course course = Conversion.fromGWT(toEdit);
		assert (course.getDbid() != -1);
//		System.out.println("editCourse saving model course " + course.getCatalogNum() + " lectureid " + course.getLectureID());
		model.saveCourse(course);
	}

	@Override
	public void removeCourse(Integer toRemoveID) {
		System.out.println("GreetingServiceImpl.removeCourse(" + toRemoveID + ")");
		for (Course course : model.getCourses()) {
			System.out.println("Checking against course " + course + " id " + course.getDbid());
			if (course.getDbid().equals(toRemoveID)) {
				model.removeCourse(course);
				return;
			}
		}
		
		assert(false);
	}

	@Override
	public InstructorGWT addInstructor(InstructorGWT toAdd) {
		toAdd.setID(-1);
		Instructor instructor = Conversion.fromGWT(toAdd, getCoursesByID());

		System.out.println("Adding (saving) instructor, prefs:");
		for (Entry<Integer, Integer> entry : instructor.getCoursePreferences().entrySet())
			System.out.println("Course id " + entry.getKey() + " pref: " + entry.getValue());
		
		model.saveInstructor(instructor);
		assert (instructor.getDbid() != -1);
		return Conversion.toGWT(instructor);
	}

	@Override
	public void editInstructor(InstructorGWT toEdit) {
		Instructor instructor = Conversion.fromGWT(toEdit, getCoursesByID());
		assert (instructor.getDbid() != -1);
		model.saveInstructor(instructor);
	}

	@Override
	public void removeInstructor(Integer toRemoveID) {
		for (Instructor instructor : model.getInstructors()) {
			if (instructor.getDbid().equals(toRemoveID)) {
				model.removeInstructor(instructor);
				return;
			}
		}
		
		assert(false);
	}

	@Override
	public LocationGWT addLocation(LocationGWT toAdd) {
		toAdd.setID(-1);
		Location location = Conversion.fromGWT(toAdd);
		model.saveLocation(location);
		assert (location.getDbid() != -1);
		return Conversion.toGWT(location);
	}

	@Override
	public void editLocation(LocationGWT toEdit) {
		Location location = Conversion.fromGWT(toEdit);
		assert (location.getDbid() != -1);
		model.saveLocation(location);
	}

	@Override
	public void removeLocation(Integer toRemoveID) {
		for (Location location : model.getLocations()) {
			if (location.getDbid().equals(toRemoveID)) {
				model.removeLocation(location);
				return;
			}
		}
		
		assert(false);
	}

	@Override
	public Integer saveCurrentScheduleAsAndOpen(String scheduleName, boolean allowOverwrite) {
		// TODO: enforce allowOverwrite
		
		int newSchedule = model.copySchedule(model.getScheduleID(), scheduleName);
		openExistingSchedule(newSchedule);
		return newSchedule;
	}
}
