package edu.calpoly.csc.scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;
import edu.calpoly.csc.scheduler.model.db.sdb.ScheduleDB;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

/**
 * This class holds all of the individual database objects. The view will
 * interact with this class to get the individual databases.
 * 
 * @author Tyler Holland
 **/

public class Database
{
   /** The SQLDB object to pass to other database objects */
   private SQLDB            sqldb;

   /** The instructor database. */
   private InstructorDB     instructorDB;

   /** The course database. */
   private CourseDB         courseDB;

   /** The location database. */
   private LocationDB       locationDB;

   /** The schedule database. */
   private ScheduleDB       scheduleDB;

   /** The current schedule id */
   private int              scheduleID;

   /** The current department */
   private String           dept;

   /** If we are setting up a new user */
   private boolean          newUser            = false;

   /** If we are copying data */
   private boolean          copying            = false;

   /** If we are copying data */
   private int              oldScheduleID      = -3;

   /** Example Chem Schedule template scheduleid */
   private static final int templateScheduleID = 1354;

   /**
    * STEP 1 This constructor will create the SQLDB object.
    **/
   public Database()
   {
      sqldb = new SQLDB();
      sqldb.open();
   }

   /**
    * STEP 2 Returns the department the user is in
    */
   public String getDept(String userid)
   {
      if (sqldb.doesUserExist(userid))
      {
         newUser = false;
         // If it exists, get dept
         this.dept = sqldb.getDeptByUserID(userid);
      }
      else
      {
         // Make a new user
         sqldb.makeNewUser(userid);
         // New dept will be userid
         newUser = true;
         this.dept = userid;
      }
      return dept;
   }

   /**
    * STEP 3 Returns the list of schedules for this department
    */
   public HashMap<String, Integer> getSchedules(String dept)
   {
      this.dept = dept;
      HashMap<String, Integer> schedules = new HashMap<String, Integer>();
      ResultSet rs = sqldb.getSchedulesByDept(dept);
      try
      {
         while (rs.next())
         {
            schedules.put(rs.getString("name"), rs.getInt("scheduleid"));
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      return schedules;
   }

   /**
    * Step 3.5 Copies a schedule from an existing one
    * 
    * @return scheduleid The copied schedules scheduleid
    */
   public int copySchedule(int oldscheduleid, String name)
   {
      this.oldScheduleID = oldscheduleid;
      copying = true;
      scheduleDB = new ScheduleDB(sqldb);
      Schedule old = scheduleDB.getSchedule(oldscheduleid);
      // Set new fields
      old.setId(-2);
      old.setName(name);
      scheduleDB.saveData(old);
      this.scheduleID = scheduleDB.getScheduleID(old);
      return this.scheduleID;
   }

   /**
    * STEP 4 Initialize databases with given schedule id
    */
   public void openDB(int scheduleid, String scheduleName)
   {
      System.out.println("ID: " + scheduleid + ", name: " + scheduleName);
      int realid = scheduleid;
      Schedule data = new Schedule();
      data.setDept(dept);
      data.setName(scheduleName);
      data.setScheduleId(realid);
      if (scheduleDB == null)
      {
         scheduleDB = new ScheduleDB(sqldb);
      }

      if (sqldb.doesScheduleIDExist(realid))
      {
         // Use this schedule
         System.out.println("Using existing schedule");
      }
      else
      {
         // Check schedule name inside dept
         if (sqldb.doesScheduleNameExist(scheduleName, dept))
         {
            // TODO: Change to throw error or something
            // Open existing schedule with that name
            System.err
                  .println("ERROR: Schedule name already exists, opening existing one");
            realid = sqldb.getScheduleIDByName(scheduleName, dept);
            scheduleDB.setScheduleID(realid);
         }
         else
         {
            // Create a new schedule with given name and dept
            scheduleDB.saveData(data);
            realid = scheduleDB.getScheduleID(data);
         }
      }
      instructorDB = new InstructorDB(sqldb, realid);
      courseDB = new CourseDB(sqldb, realid);
      locationDB = new LocationDB(sqldb, realid);
      this.scheduleID = realid;
      if (newUser)
      {
         System.out.println("Copying data from Example Chem Schedule");
         // Make temporary db's with scheduleid = Example Chem Schedule (1354)
         copyAllData(templateScheduleID);
         newUser = false;
      }
      else if (copying)
      {
         System.out.println("Copying data from scheduleid " + oldScheduleID);
         // Make temporary db's with scheduleid = whatever copying had
         copyAllData(oldScheduleID);
         copying = false;
      }
   }

   private void copyAllData(int oldScheduleID)
   {
      // Copy data into the new schedule
      InstructorDB tempInstructorDB = new InstructorDB(sqldb, oldScheduleID);
      CourseDB tempCourseDB = new CourseDB(sqldb, oldScheduleID);
      LocationDB tempLocationDB = new LocationDB(sqldb, oldScheduleID);

      // Copy data from temp dbs to real dbs
      // Copy instructors
      for (Instructor instructor : tempInstructorDB.getData())
      {
         instructorDB.saveData(instructor);
      }
      // Copy courses
      for (Course course : tempCourseDB.getData())
      {
         courseDB.saveData(course);
      }
      // Copy locations
      for (Location location : tempLocationDB.getData())
      {
         locationDB.saveData(location);
      }
      System.out.println("Done copying data");
   }

   /**
    * @return the instructorDB
    */
   public InstructorDB getInstructorDB()
   {
      return instructorDB;
   }

   /**
    * @return the courseDB
    */
   public CourseDB getCourseDB()
   {
      return courseDB;
   }

   /**
    * @return the locationDB
    */
   public LocationDB getLocationDB()
   {
      return locationDB;
   }

   /**
    * @return the scheduleDB
    */
   public ScheduleDB getScheduleDB()
   {
      return scheduleDB;
   }

   /**
    * @return the scheduleID
    */
   public int getScheduleID()
   {
      return scheduleID;
   }

   /**
    * @param scheduleID
    *           the scheduleID to set
    */
   public void setScheduleID(int scheduleID)
   {
      this.scheduleID = scheduleID;
   }
}
