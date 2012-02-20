//package edu.calpoly.csc.scheduler;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import edu.calpoly.csc.scheduler.model.Model;
//import edu.calpoly.csc.scheduler.model.db.Time;
//import edu.calpoly.csc.scheduler.model.db.cdb.Course;
//import edu.calpoly.csc.scheduler.model.db.idb.CoursePreference;
//import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
//import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
//import edu.calpoly.csc.scheduler.model.db.udb.UserData;
//import edu.calpoly.csc.scheduler.model.schedule.Day;
//import edu.calpoly.csc.scheduler.model.schedule.Schedule;
//import edu.calpoly.csc.scheduler.model.schedule.Week;
//import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;
//
//public class AlgorithmStressTest {
//
//	public static void main(String[] args) {
//		Model model = new Model("adam");
//		Map<String, UserData> availableSchedules = model.getSchedules();
//		for (String avail : availableSchedules.keySet())
//			System.out.println(avail);
//		assert(availableSchedules.containsKey("Stress Test Schedule"));
//		model.openExistingSchedule("Stress Test Schedule");
//		Schedule sched = model.getSchedule();
//		
//		System.err.println("Generating Courses");
//		
//		List<Course> courses = generateCourseList();
//
//		System.err.println("Generating Instructors");
//		
//		List<Instructor> instructors = generateInstructorList(courses);
//		
//		sched.setiSourceList(instructors);
//		
//		System.err.println("Done with course and instructor lists");
//		
//		System.err.println("Now generating schedule");
//		
//		long startTime = System.currentTimeMillis();
//		
//		sched.generate(courses);
//		
//		long endTime = System.currentTimeMillis();
//		
//		System.err.println("Schedule has been generated in: " + Long.toString((endTime - startTime) / 1000) + "s");
//	}
//	
//	public static List<Course> generateCourseList() {
//		List<Course> courses = new ArrayList<Course>();
//		
//		Week weekMWF = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
//		Week weekTT = new Week(new Day[]{Day.TUE, Day.THU});
//		
//		String dept = "AERO";
//		
//		String courseName = "MWF";
//		
//		//Loop to schedule classes on Mon, Wed, Fri
//		for(int i = 0; i < 200; i++) {
//			Course lecture = new Course(courseName + Integer.toString(i + 1), dept, Integer.toString(i + 1));
//		    lecture.setEnrollment(30);
//		    lecture.setLectureID(-1);
//		    lecture.setDbid(i + 1);
//		    lecture.setLength(6);
//		    lecture.setNumOfSections(1);
//		    lecture.setScu(4);
//		    lecture.setWtu(4);
//		    lecture.setType(Course.CourseType.LEC);
//		    lecture.setDays(weekMWF);
//		    courses.add(lecture);
//		}
//		
//		courseName = "TT";
//		
//		//Loop to schedule classes on Tues, Thurs
//		for(int i = 500; i < 1000; i++) {
//			Course lecture = new Course(courseName + Integer.toString(i + 1), dept, Integer.toString(i + 1));
//		    lecture.setEnrollment(30);
//		    lecture.setLectureID(-1);
//		    lecture.setDbid(i + 1);
//		    lecture.setLength(6);
//		    lecture.setNumOfSections(1);
//		    lecture.setScu(4);
//		    lecture.setWtu(4);
//		    lecture.setType(Course.CourseType.LEC);
//		    lecture.setDays(weekTT);
//		    courses.add(lecture);
//		}
//		
//		return courses;
//	}
//	
//	public static List<Instructor> generateInstructorList(List<Course> courses) {
//		List<Instructor> instructors = new ArrayList<Instructor>();
//		
//		//Make 500 instructors to teach the courses
//		for(int i = 0; i < 500; i++) {
//			Instructor instructor = new Instructor();
//		    instructor.setFirstName("IFN" + Integer.toString(i + 1));
//		    instructor.setLastName("ILN" + Integer.toString(i + 1));
//		    instructor.setCurWtu(0);
//		    instructor.setDisability(false);
//		    instructor.setMaxWtu(50);
//		    instructor.setAvailability(new WeekAvail());
//		    instructor.setUserID("IFN" + Integer.toString(i + 1));
//		
//		    for(Course c : courses) {
//			    instructor.addCoursePreference(new CoursePreference(c, 10));
//		    }
//		
//		    HashMap<Integer, LinkedHashMap<Integer, TimePreference>> tps = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
//		    LinkedHashMap<Integer, TimePreference> times = new LinkedHashMap<Integer, TimePreference>();
//		    times.put(1000, new TimePreference(new Time(10, 0), 10));
//		    times.put(1030, new TimePreference(new Time(10, 30), 10));
//		    times.put(1100, new TimePreference(new Time(11, 0), 10));
//		    times.put(1130, new TimePreference(new Time(11, 30), 10));
//		    times.put(1200, new TimePreference(new Time(12, 0), 10));
//		    times.put(1230, new TimePreference(new Time(12, 30), 10));
//		    times.put(1300, new TimePreference(new Time(13, 0), 10));
//		    times.put(1330, new TimePreference(new Time(13, 30), 10));
//		    times.put(1400, new TimePreference(new Time(14, 0), 10));
//		    times.put(1430, new TimePreference(new Time(14, 30), 10));
//		    times.put(1500, new TimePreference(new Time(15, 0), 10));
//		    times.put(1530, new TimePreference(new Time(15, 30), 10));
//		
//		    if(i % 2 == 0) {
//		        tps.put(1, times);
//		        tps.put(3, times);
//		        tps.put(5, times);
//		    }
//		    else {
//		        tps.put(2, times);
//		        tps.put(4, times);		    	
//		    }
//
//		    instructor.setTimePreferences(tps);
//		    
//		    instructors.add(instructor);
//		}
//				
//		return instructors;
//	}
//}
