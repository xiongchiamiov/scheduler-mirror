package edu.calpoly.csc.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.CoursePreference;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.udb.UserData;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;

public class AlgorithmTest {

	public static void main(String[] args) {
		Model model = new Model("adam");
		Map<String, UserData> availableSchedules = model.getSchedules();
		for (String avail : availableSchedules.keySet())
			System.out.println(avail);
		assert(availableSchedules.containsKey("Algorithm Test Schedule"));
		model.openExistingSchedule("Algorithm Test Schedule");
		Schedule sched = model.getSchedule();
		
		List<Course> courses = generateCourseList();
		List<Instructor> instructors = generateInstructorList(courses);
		
		sched.setiSourceList(instructors);
		
		sched.generate(courses);
		
		printAllScheduledCourseInfo(sched);
	}
	
	public static List<Course> generateCourseList() {
		List<Course> courses = new ArrayList<Course>();
		
		Course lecture = new Course("Intro to Aerodynamics", "AERO", "101");
		lecture.setEnrollment(30);
		lecture.setLectureID(-1);
		lecture.setDbid(1000);
		lecture.setLength(6);
		lecture.setNumOfSections(1);
		lecture.setScu(4);
		lecture.setWtu(4);
		lecture.setType(Course.CourseType.LEC);
		Week week = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
		lecture.setDays(week);
		courses.add(lecture);
		
		Course lab = new Course("Intro to Aerodynamics Lab", "AERO", "101");
		lab.setEnrollment(30);
		lab.setLectureID(1000);
		lab.setLength(6);
		lab.setNumOfSections(1);
		lab.setScu(4);
		lab.setWtu(4);
		lab.setType(Course.CourseType.LAB);
		lab.setTetheredToLecture(true);
		Week week2 = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
		lab.setDays(week2);
		courses.add(lab);
		
		return courses;
	}
	
	public static List<Instructor> generateInstructorList(List<Course> courses) {
		List<Instructor> instructors = new ArrayList<Instructor>();
		
		Instructor instructor = new Instructor();
		instructor.setFirstName("Adam");
		instructor.setLastName("Armstrong");
		instructor.setCurWtu(0);
		instructor.setDisability(false);
		instructor.setMaxWtu(50);
		instructor.setAvailability(new WeekAvail());
		instructor.setUserID("adam");
		
		for(Course c : courses) {
			instructor.addCoursePreference(new CoursePreference(c, 10));
		}
		
		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> tps = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
		LinkedHashMap<Integer, TimePreference> times = new LinkedHashMap<Integer, TimePreference>();
		times.put(600, new TimePreference(new Time(10, 0), 10));
		times.put(630, new TimePreference(new Time(10, 30), 10));
		times.put(660, new TimePreference(new Time(11, 0), 10));
		times.put(690, new TimePreference(new Time(11, 30), 10));
		times.put(720, new TimePreference(new Time(12, 0), 10));
		times.put(750, new TimePreference(new Time(12, 30), 10));
		
		tps.put(1, times);
		tps.put(2, times);
		tps.put(3, times);
		tps.put(4, times);
		tps.put(5, times);

		instructor.setTimePreferences(tps);
		
		instructors.add(instructor);
		
		return instructors;
	}
	
	public static void printAllScheduledCourseInfo(Schedule sched) {

	}
}
