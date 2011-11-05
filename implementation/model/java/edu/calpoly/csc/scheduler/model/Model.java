package edu.calpoly.csc.scheduler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;


/**
 * The top-level model. Views will be passed this object when they are 
 * instantiated. Any view interaction w/ underlying data will be done via
 * this class. Thus, the getters/setters here will determine how the view and
 * the model can interact. 
 * 
 * @author Eric Liebowitz
 * @version Oct 3, 2011
 */
public class Model implements Serializable
{
   public static final int serialVersionUID = 42;
   
   private Database db;
   private String dept;
   private Map<String, Integer> sched_map = new HashMap<String, Integer>();
   
   public Model (String userId)
   {
      db = new Database();
      this.dept = db.getDept(userId);
   }
   
   /**
    * Returns a list of schedules which a given user has access to.
    * 
    * @param userId Userid of the person who's asking for the schedules
    * 
    * @return Map, keyed by schedule ids which yields schedule names.
    * 
    * @see Database#getSchedules(String)
    */
   public Map<String, Integer> getSchedules ()
   {
      return (this.sched_map = db.getSchedules(this.dept));
   }
   
   public Database openExistingSchedule (String scheduleName)
   {
      int sid = this.sched_map.get(scheduleName);
      db.openDB(sid, scheduleName);
      
      return this.db;
   }
   
   public Database openExistingSchedule (Integer sid)
   {
      db.openDB(sid, "");
      return this.db;
   }

   public Database openNewSchedule (String scheduleName)
   {
      db.openDB(-1, scheduleName);
      return this.db;
   }

	public void saveInstructor(Instructor instructor) {
		db.getInstructorDB().saveData(instructor);
	}

	public Collection<Instructor> getInstructors() {
		return db.getInstructorDB().getData();
	}

	public Integer getScheduleID() {
		return db.getScheduleID();
	}

	public void removeInstructor(Instructor instructor) {
		db.getInstructorDB().removeData(instructor);
	}

	public ArrayList<Course> getCourses() {
		return db.getCourseDB().getData();
	}

	public Collection<Location> getLocations() {
		return db.getLocationDB().getData();
	}

	public void saveLocation(Location location) {
		db.getLocationDB().saveData(location);
	}

	public void removeLocation(Location location) {
		db.getLocationDB().removeData(location);
	}

	public void saveCourse(Course course) {
		db.getCourseDB().saveData(course);
	}

	public void removeCourse(Course course) {
		db.getCourseDB().removeData(course);
	}

	public Collection<ScheduleItem> generateSchedule() {
		Collection<Course> courses = getCourses();
		
		for (Course course : courses) {
			System.out.println("course length " + course.getLength() + " daylength " + course.getDayLength());
			assert(course.getLength() > 0);
			assert(course.getDayLength() > 0);
		}
		
		Schedule schedule = new Schedule();
		schedule.generate(new Vector<Course>(courses));
		return schedule.getItems();
	}
   
}
