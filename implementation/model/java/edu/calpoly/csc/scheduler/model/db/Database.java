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
    * STEP 4 Initialize databases with given schedule id
    */
   public void openDB(int scheduleid, String scheduleName)
   {
      System.out.println("ID: " + scheduleid + ", name: " + scheduleName);
      int realid = scheduleid;
      if (!(sqldb.doesScheduleIDExist(scheduleid)))
      {
         if (!(sqldb.doesScheduleNameExist(scheduleName)))
         {
            System.out.println("Creating new schedule");
            if (dept == null)
            {
               System.err.println("ERROR: DEPT IS NULL");
            }
            else
            {
               System.out.println("Using dept: " + dept);
            }
            scheduleDB = new ScheduleDB(sqldb, this.dept);
            realid = scheduleDB.createNewSchedule(scheduleName);
            scheduleDB.setScheduleID(realid);
            System.out.println("New schedule id: " + realid);
         }
         else
         {
            System.err
                  .println("ERROR: Schedule name already exists, opening existing one");
            realid = sqldb.getScheduleIDByName(scheduleName, dept);
         }
      }
      else
      {
         System.out.println("Using existing schedule");
         scheduleDB = new ScheduleDB(sqldb, realid, this.dept);
      }
      instructorDB = new InstructorDB(sqldb, realid);
      courseDB = new CourseDB(sqldb, realid);
      locationDB = new LocationDB(sqldb, realid);
      this.scheduleID = realid;
      if (newUser)
      {
         System.out.println("Copying data from Example Chem Schedule");
         // Copy data into the new schedule
         // Make temporary db's with scheduleid = Example Chem Schedule (1354)
         InstructorDB tempInstructorDB = new InstructorDB(sqldb,
               templateScheduleID);
         CourseDB tempCourseDB = new CourseDB(sqldb, templateScheduleID);
         LocationDB tempLocationDB = new LocationDB(sqldb, templateScheduleID);

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
         newUser = false;
      }
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
