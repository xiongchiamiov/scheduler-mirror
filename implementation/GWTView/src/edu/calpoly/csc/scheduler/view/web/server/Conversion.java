package edu.calpoly.csc.scheduler.view.web.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public abstract class Conversion {
	public static InstructorGWT toGWT(Instructor instructor) {
		InstructorGWT result = new InstructorGWT();
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
		
		HashMap<Course, Integer> sourceCoursePreferences = instructor.getCoursePreferences();
		HashMap<CourseGWT, Integer> coursePreferences = new LinkedHashMap<CourseGWT, Integer>();
		for (Course course : sourceCoursePreferences.keySet())
			coursePreferences.put(Conversion.toGWT(course), sourceCoursePreferences.get(course));
		result.setCoursePreferences(coursePreferences);
		
		Vector<ScheduleItem> sourceItemsTaught = instructor.getItemsTaught();
		Vector<ScheduleItemGWT> itemsTaught = new Vector<ScheduleItemGWT>();
		for (ScheduleItem item : sourceItemsTaught)
			itemsTaught.add(Conversion.toGWT(item));
		result.setItemsTaught(itemsTaught);
		
		result.setScheduleID(instructor.getScheduleId());
		return result;
	}
	
	public static Instructor fromGWT(InstructorGWT instructor) {
		instructor.verify();
		Instructor ins = new Instructor();
		ins.setScheduleId(instructor.getScheduleID());
		ins.setFirstName(instructor.getFirstName());
		ins.setLastName(instructor.getLastName());
		ins.setUserID(instructor.getUserID());
		ins.setMaxWtu(instructor.getMaxWtu());
		ins.setCurWtu(instructor.getCurWtu());
		ins.setOffice(new Location(instructor.getBuilding(), instructor.getRoomNumber()));
		ins.setFairness(instructor.getFairness());
		ins.setDisability(instructor.getDisabilities());
		ins.setGenerosity(instructor.getGenerosity());
		ins.setAvailability(new WeekAvail());
		ins.setCoursePreferences(new HashMap<Course,Integer>());
		ins.setTimePreferences(new HashMap<Day, LinkedHashMap<Time, TimePreference>>());
		ins.setItemsTaught(new Vector<ScheduleItem>());
		ins.setQuarterId("");
		ins.verify();
		
		return ins;
	}

	public static ScheduleItemGWT toGWT(ScheduleItem schdItem) {
		String instructor = (schdItem.getInstructor() == null ? "" : schdItem.getInstructor().getName());
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

		return new ScheduleItemGWT(instructor, courseDept, courseNum, section,
				dayNums, startTimeHour, startTimeMin, endTimeHour, endTimeMin,
				location);
	}

	public static CourseGWT toGWT(Course course) {
		CourseGWT newCourse = new CourseGWT();
		newCourse.setCatalogNum(course.getCatalogNum());
		newCourse.setCourseName(course.getName());
		newCourse.setDept(course.getDept());
		newCourse.setLab(null);
		newCourse.setDays(null);
		newCourse.setLength(course.getLength());
		newCourse.setLabPad(course.getLabPad());
		newCourse.setMaxEnroll(course.getEnrollment());
		newCourse.setNumSections(course.getNumOfSections());
		newCourse.setScu(course.getScu());
		newCourse.setType(course.getType().toString());
		newCourse.setWtu(course.getWtu());
		newCourse.setQuarterID(course.getQuarterId());
		//newCourse.setScheduleID(course.getScheduleId());
	    return newCourse;
	}

	public static LocationGWT toGWT(Location location) {
	    return new LocationGWT(location.getBuilding(), location.getRoom(),
                location.getMaxOccupancy(), location.getType(),
                location.isSmartRoom(), location.hasLaptopConnectivity(),
                location.isADACompliant(), location.hasOverhead());
	}
	
	public static Location fromGWT(LocationGWT location) {
		location.verify();
		
		Location loc = new Location();
		loc.setRoom(location.getRoom());
		loc.setAdaCompliant(location.isADACompliant());
		loc.setAvailability(new WeekAvail());
		loc.setBuilding(location.getBuilding());
		loc.setMaxOccupancy(location.getMaxOccupancy());
		loc.setProvidedEquipment(loc.new ProvidedEquipment());
		loc.setQuarterId(location.getQuarterID());
		loc.setRoom(location.getRoom());
		loc.setScheduleId(location.getScheduleID());
		loc.setType(location.getType());
		loc.verify();
		
		return loc;
	}
	
	public static Course fromGWT(CourseGWT course) {
		Course newCourse = new Course();
		newCourse.setName(course.getCourseName());
		newCourse.setCatalogNum(course.getCatalogNum());
		newCourse.setWtu(course.getWtu());
		newCourse.setScu(course.getScu());
		newCourse.setType(course.getType());
		newCourse.setEnrollment(course.getMaxEnroll());
		newCourse.setLab(null);
		newCourse.setLabPad(course.getLabPad());
		newCourse.setQuarterId(course.getQuarterID());
		newCourse.setScheduleId(course.getScheduleID());
		newCourse.setDept(course.getDept());
		newCourse.setLength(course.getLength());
		newCourse.setNumOfSections(course.getNumSections());
		newCourse.setDays(null);
		return newCourse;
	}
}
