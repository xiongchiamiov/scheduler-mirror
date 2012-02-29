package edu.calpoly.csc.scheduler.model;

import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class ScheduleItem extends Identified {
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

		if (!underlying.isTransient())
			assert(!model.itemCache.inCache(underlying)); // make sure its not in the cache yet (how could it be, we're not even done with the constructor)
	}

	
	// PERSISTENCE FUNCTIONS

	public void insert() throws DatabaseException {
		assert(scheduleLoaded);
		assert(courseLoaded);
		assert(locationLoaded);
		assert(instructorLoaded);
		
		model.itemCache.insert(this);
	}

	public void delete() throws DatabaseException {
		model.itemCache.delete(this);
	}

	public void update() throws DatabaseException {
		model.database.setScheduleItemCourse(underlying, course.underlyingCourse);
		model.database.setScheduleItemInstructor(underlying, instructor.underlyingInstructor);
		model.database.setScheduleItemLocation(underlying, location.underlyingLocation);
		model.itemCache.update(this);
	}
	
	public ScheduleItem createTransientCopy() throws DatabaseException {
		ScheduleItem result = new ScheduleItem(model, model.database.assembleScheduleItemCopy(underlying));
		if (scheduleLoaded)
			result.setSchedule(getSchedule());
		if (instructorLoaded)
			result.setInstructor(getInstructor());
		if (locationLoaded)
			result.setLocation(getLocation());
		if (courseLoaded)
			result.setCourse(getCourse());
		return result;
	}
	
	

	// ENTITY ATTRIBUTES
	
	public Integer getID() { return underlying.getID(); }
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

	public Schedule getSchedule() throws DatabaseException {
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

	public Course getCourse() throws DatabaseException {
		if (!courseLoaded) {
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
	

	// Location

	public Location getLocation() throws DatabaseException {
		if (!locationLoaded) {
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

	public Instructor getInstructor() throws DatabaseException {
		if (!instructorLoaded) {
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
	
	/**
	    * Displays all this object's fields in a visually easy-to-read form.
	    */
	   public String toString ()
	   {
	      String r = "";
		try {
			r = (this.getCourse() + " - Section: " + this.getSection() + " - " +
			          this.getCourse().getType() + "\n" +
			          "Instructor:\t" + this.getInstructor() + "\n" +
			          "In:\t\t" + this.getLocation() + "\n" +
			          "On:\t\t" + this.getDays() + "\n" +
			          "Starts:\t\t" + this.getStartHalfHour() + "\n" +
			          "Ends:\t\t" + this.getEndHalfHour() + "\n");
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		//TODO Add Labs into printing
	      /*if (!this.getLabIDs().isEmpty())
	      {
	         r += this.getLabIDs().toString();
	      }*/
	      return r;
	   }
}
