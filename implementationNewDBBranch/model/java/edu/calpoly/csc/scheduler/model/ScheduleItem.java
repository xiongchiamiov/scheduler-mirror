package edu.calpoly.csc.scheduler.model;

import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class ScheduleItem implements Identified {
	private final Model model;
	
	IDBScheduleItem underlying;
	
	private boolean scheduleLoaded;
	private Schedule schedule;

	private boolean courseLoaded;
	private Course course;
	
	private boolean locationLoaded;
	private Location location;
	
	private boolean instructorLoaded;
	private Instructor instructor;

	
	ScheduleItem(Model model, IDBScheduleItem underlying) {
		this.model = model;
		this.underlying = underlying;
	}

	
	// PERSISTENCE FUNCTIONS

	public void insert() throws NotFoundException {
		assert(scheduleLoaded);
		assert(courseLoaded);
		assert(locationLoaded);
		assert(instructorLoaded);
		
		model.itemCache.insert(this);
	}

	public void delete() {
		model.itemCache.delete(this);
	}

	public void update() throws NotFoundException {
		model.database.setScheduleItemCourse(underlying, course.underlyingCourse);
		model.database.setScheduleItemInstructor(underlying, instructor.underlyingInstructor);
		model.database.setScheduleItemLocation(underlying, location.underlyingLocation);
		model.itemCache.update(underlying);
	}
	
	public ScheduleItem createTransientCopy() throws NotFoundException {
		ScheduleItem result = new ScheduleItem(model, model.database.assembleScheduleItemCopy(underlying));
		result.setSchedule(getSchedule());
		result.setInstructor(getInstructor());
		result.setLocation(getLocation());
		result.setCourse(getCourse());
		return result;
	}
	
	

	// ENTITY ATTRIBUTES
	
	public int getID() { return underlying.getID(); }
	public int getSection() { return underlying.getSection(); }
	public void setSection(int section) { this.underlying.setSection(section); }
	public Set<Day> getDays() { return underlying.getDays(); }
	public void setDays(Set<Day> days) { underlying.setDays(days); }
	public int getStartHalfHour() { return underlying.getStartHalfHour(); }
	public void setStartHalfHour(int startHalfHour) { underlying.setStartHalfHour(startHalfHour); }
	public int getEndHalfHour() { return underlying.getEndHalfHour(); }
	public void setEndHalfHour(int endHalfHour) { underlying.setEndHalfHour(endHalfHour); }
	public boolean isPlaced() { return underlying.isPlaced(); }
	public void setIsPlaced(boolean placed) { underlying.setIsPlaced(placed); }
	public boolean isConflicted() { return underlying.isConflicted(); }
	public void setIsConflicted(boolean conflicted) { underlying.setIsConflicted(conflicted); }

	
	
	// ENTITY RELATIONS

	
	// Labs
	
	public Set<Integer> getLabIDs() { assert(false); return null; }


	// Schedule

	public Schedule getSchedule() throws NotFoundException {
		if (!scheduleLoaded) {
			assert(schedule == null);
			schedule = model.findScheduleByID(model.database.getScheduleItemSchedule(underlying).getID());
			scheduleLoaded = true;
		}
		return schedule;
	}

	public void setSchedule(Schedule newSchedule) {
		schedule = newSchedule;
		scheduleLoaded = true;
	}
	

	// Course

	public Course getCourse() throws NotFoundException {
		if (!courseLoaded) {
			if (course == null)
				throw new NotFoundException();
			course = model.findCourseByID(model.database.getScheduleItemCourse(underlying).getID());
			courseLoaded = true;
		}
		return course;
	}

	public void setCourse(Course newCourse) {
		course = newCourse;
		courseLoaded = true;
	}
	
	public boolean courseIsSet() { return courseLoaded; }
	

	// Course

	public Location getLocation() throws NotFoundException {
		if (!locationLoaded) {
			if (location == null)
				throw new NotFoundException();
			location = model.findLocationByID(model.database.getScheduleItemLocation(underlying).getID());
			locationLoaded = true;
		}
		return location;
	}

	public void setLocation(Location newLocation) {
		location = newLocation;
		locationLoaded = true;
	}

	public boolean locationIsSet() { return locationLoaded; }
	

	// Instructor

	public Instructor getInstructor() throws NotFoundException {
		if (!instructorLoaded) {
			if (instructor == null)
				throw new NotFoundException();
			instructor = model.findInstructorByID(model.database.getScheduleItemInstructor(underlying).getID());
			instructorLoaded = true;
		}
		return instructor;
	}

	public void setInstructor(Instructor newInstructor) {
		instructor = newInstructor;
		instructorLoaded = true;
	}

	public boolean instructorIsSet() { return instructorLoaded; }
	
}