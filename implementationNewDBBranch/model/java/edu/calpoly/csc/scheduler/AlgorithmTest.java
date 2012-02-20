//package edu.calpoly.csc.scheduler;
//
//import java.sql.Time;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import junit.framework.TestCase;
//import edu.calpoly.csc.scheduler.model.Course;
//import edu.calpoly.csc.scheduler.model.CoursePreference;
//import edu.calpoly.csc.scheduler.model.Day;
//import edu.calpoly.csc.scheduler.model.Instructor;
//import edu.calpoly.csc.scheduler.model.Model;
//import edu.calpoly.csc.scheduler.model.Schedule;
//
//public class AlgorithmTest extends TestCase {
//	
//	Model model;
////	Map<String, UserData> availableSchedules;
////	Schedule sched;
////	List<Course> courses;
////	List<Instructor> instructors;
//	
//	public AlgorithmTest() {
//		super();
//	}
//	
//	public static List<Course> generateCourseList() {
//		List<Course> courses = new ArrayList<Course>();
//		
//		Week weekMWF = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
//		Week weekTT = new Week(new Day[]{Day.TUE, Day.THU});
//		
//		Course lecture = new Course("Intro to Aerodynamics", "AERO", "101");
//		lecture.setEnrollment(30);
//		lecture.setLectureID(-1);
//		lecture.setDbid(1000);
//		lecture.setLength(6);
//		lecture.setNumOfSections(1);
//		lecture.setScu(4);
//		lecture.setWtu(4);
//		lecture.setType(Course.CourseType.LEC);
//		lecture.setDays(weekMWF);
//		courses.add(lecture);
//		
//		Course lab = new Course("Intro to Aerodynamics Lab", "AERO", "101");
//		lab.setEnrollment(30);
//		lab.setLectureID(1000);
//		lab.setLength(6);
//		lab.setNumOfSections(1);
//		lab.setScu(4);
//		lab.setWtu(4);
//		lab.setType(Course.CourseType.LAB);
//		lab.setTetheredToLecture(true);
//		lab.setDays(weekMWF);
//		courses.add(lab);
//		
//		Course lecture2 = new Course("Advanced Aerodynamics", "AERO", "305");
//		lecture2.setEnrollment(30);
//		lecture2.setLectureID(-1);
//		lecture2.setDbid(1001);
//		lecture2.setLength(6);
//		lecture2.setNumOfSections(1);
//		lecture2.setScu(4);
//		lecture2.setWtu(4);
//		lecture2.setType(Course.CourseType.LEC);
//		lecture2.setDays(weekTT);
//		courses.add(lecture2);
//		
//		Course lab2 = new Course("Advanced Aerodynamics Lab", "AERO", "305");
//		lab2.setEnrollment(30);
//		lab2.setLectureID(1001);
//		lab2.setLength(6);
//		lab2.setNumOfSections(1);
//		lab2.setScu(4);
//		lab2.setWtu(4);
//		lab2.setType(Course.CourseType.LAB);
//		lab2.setTetheredToLecture(false);
//		lab2.setDays(weekTT);
//		courses.add(lab2);
//		
//		Course multSectionLec = new Course("Spacecraft Design", "AERO", "405");
//		multSectionLec.setEnrollment(30);
//		multSectionLec.setLectureID(-1);
//		multSectionLec.setDbid(1002);
//		multSectionLec.setLength(6);
//		multSectionLec.setNumOfSections(2);
//		multSectionLec.setScu(4);
//		multSectionLec.setWtu(4);
//		multSectionLec.setType(Course.CourseType.LEC);
//		multSectionLec.setDays(weekMWF);
//		courses.add(multSectionLec);
//		
//		Course multSectionLab = new Course("Spacecraft Design", "AERO", "405");
//		multSectionLab.setEnrollment(30);
//		multSectionLab.setLectureID(1002);
//		multSectionLab.setLength(6);
//		multSectionLab.setNumOfSections(2);
//		multSectionLab.setScu(4);
//		multSectionLab.setWtu(4);
//		multSectionLab.setType(Course.CourseType.LAB);
//		multSectionLab.setTetheredToLecture(true);
//		multSectionLab.setDays(weekMWF);
//		courses.add(multSectionLab);
//		
//		/*Course lec3 = new Course("Leadership and Facilitation", "RPTA", "205");
//		lec3.setEnrollment(30);
//		lec3.setLectureID(-1);
//		lec3.setDbid(1000);
//		lec3.setLength(6);
//		lec3.setNumOfSections(1);
//		lec3.setScu(4);
//		lec3.setWtu(4);
//		lec3.setType(Course.CourseType.LEC);
//		lec3.setDays(weekTT);
//		courses.add(lec3);
//		
//		Course act1 = new Course("Leadership and Facilitation Act", "RPTA", "205");
//		act1.setEnrollment(30);
//		act1.setLectureID(1000);
//		//act1.setDbid();
//		act1.setLength(6);
//		act1.setNumOfSections(1);
//		act1.setScu(4);
//		act1.setWtu(4);
//		act1.setType(Course.CourseType.ACT);
//		act1.setTetheredToLecture(true);
//		act1.setDays(weekTT);
//		courses.add(act1);
//		
//		Course sem = new Course("Hospitality Seminar", "RPTA", "252");
//		sem.setEnrollment(30);
//		sem.setLectureID(-1);
//		sem.setDbid(1001);
//		sem.setLength(6);
//		sem.setNumOfSections(1);
//		sem.setScu(4);
//		sem.setWtu(4);
//		sem.setType(Course.CourseType.SEM);
//		sem.setTetheredToLecture(false);
//		sem.setDays(weekTT);
//		courses.add(sem);
//		*/
//		
//		return courses;
//	}
//	
//	public static List<Instructor> generateInstructorList(List<Course> courses) {
//		List<Instructor> instructors = new ArrayList<Instructor>();
//		
//		Instructor instructor = new Instructor();
//		instructor.setFirstName("Adam");
//		instructor.setLastName("Armstrong");
//		instructor.setCurWtu(0);
//		instructor.setDisability(false);
//		instructor.setMaxWtu(50);
//		instructor.setAvailability(new WeekAvail());
//		instructor.setUserID("adam");
//		
//		for(Course c : courses) {
//			instructor.addCoursePreference(new CoursePreference(c, 10));
//		}
//		
//		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> tps = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
//		LinkedHashMap<Integer, TimePreference> times = new LinkedHashMap<Integer, TimePreference>();
//		times.put(1000, new TimePreference(new Time(10, 0), 10));
//		times.put(1030, new TimePreference(new Time(10, 30), 10));
//		times.put(1100, new TimePreference(new Time(11, 0), 10));
//		times.put(1130, new TimePreference(new Time(11, 30), 10));
//		times.put(1200, new TimePreference(new Time(12, 0), 10));
//		times.put(1230, new TimePreference(new Time(12, 30), 10));
//		times.put(1300, new TimePreference(new Time(13, 0), 10));
//		times.put(1330, new TimePreference(new Time(13, 30), 10));
//		times.put(1400, new TimePreference(new Time(14, 0), 10));
//		times.put(1430, new TimePreference(new Time(14, 30), 10));
//		times.put(1500, new TimePreference(new Time(15, 0), 10));
//		times.put(1530, new TimePreference(new Time(15, 30), 10));
//		times.put(1600, new TimePreference(new Time(16, 0), 10));
//		times.put(1630, new TimePreference(new Time(16, 30), 10));
//		times.put(1700, new TimePreference(new Time(17, 0), 10));
//		times.put(1730, new TimePreference(new Time(17, 30), 10));
//		times.put(1800, new TimePreference(new Time(18, 0), 10));
//		times.put(1830, new TimePreference(new Time(18, 30), 10));
//		
//		tps.put(1, times);
//		//tps.put(2, times);
//		tps.put(3, times);
//		//tps.put(4, times);
//		tps.put(5, times);
//
//		instructor.setTimePreferences(tps);
//		
//		Instructor instructor2 = new Instructor();
//		instructor2.setFirstName("Evan");
//		instructor2.setLastName("Platypus-Ovadia");
//		instructor2.setCurWtu(0);
//		instructor2.setDisability(false);
//		instructor2.setMaxWtu(50);
//		instructor2.setAvailability(new WeekAvail());
//		instructor2.setUserID("evan");
//		
//		for(Course c : courses) {
//			if(c.getType() == CourseType.LEC || c.getType() == CourseType.SEM) {
//				
//			    if(c.getDbid() == 1000 || c.getDbid() == 1001)
//			        instructor2.addCoursePreference(new CoursePreference(c, 10));
//			    else
//				    instructor2.addCoursePreference(new CoursePreference(c, 0));
//			}
//		}
//		
//		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> tps2 = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
//		LinkedHashMap<Integer, TimePreference> times2 = new LinkedHashMap<Integer, TimePreference>();
//		times2.put(1800, new TimePreference(new Time(18, 0), 10));
//		times2.put(1830, new TimePreference(new Time(18, 30), 10));
//		times2.put(1900, new TimePreference(new Time(19, 0), 10));
//		times2.put(1930, new TimePreference(new Time(19, 30), 10));
//		times2.put(2000, new TimePreference(new Time(20, 0), 10));
//		times2.put(2030, new TimePreference(new Time(20, 30), 10));
//		
//		//tps2.put(1, times2);
//		tps2.put(2, times2);
//		//tps2.put(3, times2);
//		tps2.put(4, times2);
//		//tps2.put(5, times2);
//
//		instructor2.setTimePreferences(tps2);
//		
//		instructors.add(instructor);
//		instructors.add(instructor2);
//		
//		return instructors;
//	}
//	
//	public static void printAllScheduledCourseInfo(Schedule sched) {
//        System.out.println("Finished creating schedule");
//	}
//	
////	public static List<Course> generateCourseList() {
////		List<Course> courses = new ArrayList<Course>();
////		
////		Week weekMWF = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
////		Week weekTT = new Week(new Day[]{Day.TUE, Day.THU});
////		
////		Course lecture = new Course("Intro to Aerodynamics", "AERO", "101");
////		lecture.setEnrollment(30);
////		lecture.setLectureID(-1);
////		lecture.setDbid(1000);
////		lecture.setLength(6);
////		lecture.setNumOfSections(1);
////		lecture.setScu(4);
////		lecture.setWtu(4);
////		lecture.setType(Course.CourseType.LEC);
////		lecture.setDays(weekMWF);
////		courses.add(lecture);
////		
////		Course lab = new Course("Intro to Aerodynamics Lab", "AERO", "101");
////		lab.setEnrollment(30);
////		lab.setLectureID(1000);
////		lab.setLength(6);
////		lab.setNumOfSections(1);
////		lab.setScu(4);
////		lab.setWtu(4);
////		lab.setType(Course.CourseType.LAB);
////		lab.setTetheredToLecture(true);
////		lab.setDays(weekMWF);
////		courses.add(lab);
////		
////		Course lecture2 = new Course("Advanced Aerodynamics", "AERO", "305");
////		lecture2.setEnrollment(30);
////		lecture2.setLectureID(-1);
////		lecture2.setDbid(1001);
////		lecture2.setLength(6);
////		lecture2.setNumOfSections(1);
////		lecture2.setScu(4);
////		lecture2.setWtu(4);
////		lecture2.setType(Course.CourseType.LEC);
////		lecture2.setDays(weekTT);
////		courses.add(lecture2);
////		
////		Course lab2 = new Course("Advanced Aerodynamics Lab", "AERO", "305");
////		lab2.setEnrollment(30);
////		lab2.setLectureID(1001);
////		lab2.setLength(6);
////		lab2.setNumOfSections(1);
////		lab2.setScu(4);
////		lab2.setWtu(4);
////		lab2.setType(Course.CourseType.LAB);
////		lab2.setTetheredToLecture(false);
////		lab2.setDays(weekTT);
////		courses.add(lab2);
////		
////		return courses;
////	}
//	
////	public static List<Instructor> generateInstructorList(List<Course> courses) {
////		List<Instructor> instructors = new ArrayList<Instructor>();
////		
////		Instructor instructor = new Instructor();
////		instructor.setFirstName("Adam");
////		instructor.setLastName("Armstrong");
////		instructor.setCurWtu(0);
////		instructor.setDisability(false);
////		instructor.setMaxWtu(50);
////		instructor.setAvailability(new WeekAvail());
////		instructor.setUserID("adam");
////		
////		for(Course c : courses) {
////			instructor.addCoursePreference(new CoursePreference(c, 10));
////		}
////		
////		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> tps = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
////		LinkedHashMap<Integer, TimePreference> times = new LinkedHashMap<Integer, TimePreference>();
////		times.put(1000, new TimePreference(new Time(10, 0), 10));
////		times.put(1030, new TimePreference(new Time(10, 30), 10));
////		times.put(1100, new TimePreference(new Time(11, 0), 10));
////		times.put(1130, new TimePreference(new Time(11, 30), 10));
////		times.put(1200, new TimePreference(new Time(12, 0), 10));
////		times.put(1230, new TimePreference(new Time(12, 30), 10));
////		
////		tps.put(1, times);
////		//tps.put(2, times);
////		tps.put(3, times);
////		//tps.put(4, times);
////		tps.put(5, times);
////
////		instructor.setTimePreferences(tps);
////		
////		Instructor instructor2 = new Instructor();
////		instructor2.setFirstName("Evan");
////		instructor2.setLastName("Platypus-Ovadia");
////		instructor2.setCurWtu(0);
////		instructor2.setDisability(false);
////		instructor2.setMaxWtu(50);
////		instructor2.setAvailability(new WeekAvail());
////		instructor2.setUserID("evan");
////		
////		for(Course c : courses) {
////			if(c.getType() == CourseType.LEC) {
////			    if(c.getDbid() == 1001)
////			        instructor2.addCoursePreference(new CoursePreference(c, 10));
////			    else
////				    instructor2.addCoursePreference(new CoursePreference(c, 0));
////			}
////		}
////		
////		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> tps2 = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
////		LinkedHashMap<Integer, TimePreference> times2 = new LinkedHashMap<Integer, TimePreference>();
////		times2.put(1800, new TimePreference(new Time(18, 0), 10));
////		times2.put(1830, new TimePreference(new Time(18, 30), 10));
////		times2.put(1900, new TimePreference(new Time(19, 0), 10));
////		times2.put(1930, new TimePreference(new Time(19, 30), 10));
////		times2.put(2000, new TimePreference(new Time(20, 0), 10));
////		times2.put(2030, new TimePreference(new Time(20, 30), 10));
////		
////		tps2.put(1, times2);
////		tps2.put(2, times2);
////		tps2.put(3, times2);
////		tps2.put(4, times2);
////		tps2.put(5, times2);
////
////		instructor2.setTimePreferences(tps2);
////		
////		instructors.add(instructor);
////		instructors.add(instructor2);
////		
////		return instructors;
////	}
//	
////	public static void printAllScheduledCourseInfo(Schedule sched) {
////
////	}
//}