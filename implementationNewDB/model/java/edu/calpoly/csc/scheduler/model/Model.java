package edu.calpoly.csc.scheduler.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.udb.UserData;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

/**
 * The top-level model. Views will be passed this object when they are
 * instantiated. Any view interaction w/ underlying data will be done via this
 * class. Thus, the getters/setters here will determine how the view and the
 * model can interact.
 * 
 * @author Eric Liebowitz
 * @version Oct 3, 2011
 */
public class Model implements Serializable
{
   public static final long serialVersionUID = 42;

   private Database db;
   private String userId;
   private Map<String, UserData> sched_map = new HashMap<String, UserData>();

   public Model (String userId)
   {
      this.userId = userId;
      this.db = new Database(userId);
   }

   /**
    * Returns a list of schedules which a given user has access to.
    * 
    * @return Map, keyed by schedule ids which yields schedule names.
    * 
    * @see Database#getSchedules(String)
    */
   public Map<String, UserData> getSchedules ()
   {
	   this.sched_map = db.getSchedules();
      return sched_map;
   }

   public Schedule loadSchedule (int sid)
   {
      return db.getScheduleDB().getSchedule(sid);
   }
   
   public void openExistingSchedule (String scheduleName)
   {
      int sid = this.sched_map.get(scheduleName).getScheduleDBId();
      System.out.println("Opening existing schedule by schedulename, id:" + sid);
      db.openDB(sid, scheduleName);
   }

   public void openExistingSchedule (Integer sid)
   {
	      System.out.println("Opening existing schedule by id, id:" + sid);
      db.openDB(sid, "");
   }

   public void openNewSchedule (String scheduleName)
   {
      db.openDB(-1, scheduleName);
   }

   public void saveInstructor (Instructor instructor)
   {
      db.getInstructorDB().saveData(instructor);
   }

   public Collection<Instructor> getInstructors ()
   {
      return db.getInstructorDB().getData();
   }

   public Integer getScheduleID ()
   {
      return db.getScheduleID();
   }

   public void removeInstructor (Instructor instructor)
   {
      db.getInstructorDB().removeData(instructor);
   }

   public ArrayList<Course> getCourses ()
   {
      return db.getCourseDB().getData();
   }

   public Collection<Location> getLocations ()
   {
      return db.getLocationDB().getData();
   }

   public void saveLocation (Location location)
   {
      db.getLocationDB().saveData(location);
   }

   public void removeLocation (Location location)
   {
      db.getLocationDB().removeData(location);
   }

   public void saveCourse (Course course)
   {
      db.getCourseDB().saveData(course);
   }

   public void removeCourse (Course course)
   {
      db.getCourseDB().removeData(course);
   }

   public int copySchedule (int existingScheduleID, String scheduleName)
   {
      return db.copySchedule(existingScheduleID, scheduleName);
   }

   public String exportToCSV (Schedule schedule)
   {
      try
      {
         return new CSVExporter().export(this, schedule);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         return null;
      }
   }

   public void saveSchedule (Schedule s)
   {
      db.getScheduleDB().saveData(s);
   }

   public void deleteSchedule (String s)
   {
	   System.out.println("Trying to remove schedule: " + s);
	   UserData userdata = sched_map.get(s);
      int sid = userdata.getScheduleDBId();
      Schedule sched = new Schedule();
      sched.setScheduleDBId(sid);
      sched.setDbid(sid);
      sched.setName(s);

      db.getScheduleDB().removeData(sched);
   }

   public void importFromCSV (String value)
   {
      try
      {
         new CSVImporter().read(this, value);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
