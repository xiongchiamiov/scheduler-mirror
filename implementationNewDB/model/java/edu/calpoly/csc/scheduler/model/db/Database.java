package edu.calpoly.csc.scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;
import edu.calpoly.csc.scheduler.model.db.sdb.ScheduleDB;
import edu.calpoly.csc.scheduler.model.db.udb.UserData;
import edu.calpoly.csc.scheduler.model.db.udb.UserDataDB;
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

   /** The userdata database. */
   private UserDataDB         userdataDB;

   /** The current schedule id */
   private int              scheduleID;

   /** The current userid */
   private String           userid;

   /** If we are setting up a new user */
   private boolean          newUser            = false;

   /** If we are copying data */
   private boolean          copying            = false;

   /** If we are copying data */
   private int              oldScheduleID      = -3;

   /** Example Chem Schedule template scheduleid */
   private static final int templateScheduleID = 1;

   /**
    * STEP 1 This constructor will create the SQLDB object.
    **/
   public Database(String userid)
   {
      sqldb = new SQLDB();
      sqldb.open();
      checkUser(userid);
      this.userid = userid;
   }

   /**
    * STEP 2 Checks to see if it is a new user, and adds to database if it is
    */
   private void checkUser(String userid)
   {
      // Create where clause for user to see if it exists
      LinkedHashMap<String, Object> wheres = new LinkedHashMap<String, Object>();
      wheres.put(UserDataDB.USERID, userid);
      if (sqldb.doesItExist(sqldb.executeSelect(UserDataDB.TABLENAME, wheres,
            wheres)))
      {
         newUser = false;
      }
      else
      {
         newUser = true;
      }
   }

   /**
    * STEP 3 Returns the list of schedules for this department
    */
   public HashMap<String, UserData> getSchedules()
   {
      return sqldb.getSchedulePermissions(userid);
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
      this.scheduleID = sqldb.getLastGeneratedKey();
      System.out.println("Just copied schedule from id: " + oldscheduleid
            + " to new id: " + this.scheduleID);
      // Insert new data in userdata
      userdataDB = new UserDataDB(sqldb, scheduleID);
      UserData entry = new UserData();
      entry.setPermission(UserData.ADMIN);
      entry.setScheduleId(scheduleID);
      entry.setScheduleName(name);
      entry.setUserId(userid);
      userdataDB.saveData(entry);
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
      data.setName(scheduleName);
      data.setScheduleId(realid);
      if (scheduleDB == null)
      {
         scheduleDB = new ScheduleDB(sqldb);
      }

      if (scheduleDB.exists(data))
      {
         // Use this schedule
         System.out.println("Using existing schedule");
         userdataDB = new UserDataDB(sqldb, realid);
      }
      else
      {
         // Check if user already has a schedule of that name
         LinkedHashMap<String, Object> wheres = new LinkedHashMap<String, Object>();
         wheres.put(UserDataDB.USERID, userid);
         wheres.put(UserDataDB.SCHEDULENAME, scheduleName);
         ResultSet rs = sqldb.executeSelect(UserDataDB.TABLENAME, null, wheres);
         if (sqldb.doesItExist(rs))
         {
            // TODO: Change to throw error or something
            // Open existing schedule with that name
            System.err
                  .println("ERROR: Schedule name already exists, opening existing one");
            try
            {
               realid = rs.getInt(DbData.SCHEDULEID);
            }
            catch (SQLException e)
            {
               e.printStackTrace();
            }
            scheduleDB.setScheduleID(realid);
            userdataDB = new UserDataDB(sqldb, realid);
         }
         else
         {
            // Create a new schedule with given name and dept
            scheduleDB.saveData(data);
            realid = scheduleDB.getScheduleID(data);
            userdataDB = new UserDataDB(sqldb, realid);
            UserData entry = new UserData();
            entry.setPermission(UserData.ADMIN);
            entry.setScheduleId(realid);
            entry.setScheduleName(scheduleName);
            entry.setUserId(userid);
            userdataDB.saveData(entry);
         }
      }
      this.scheduleID = realid;
      instructorDB = new InstructorDB(sqldb, realid);
      courseDB = new CourseDB(sqldb, realid);
      locationDB = new LocationDB(sqldb, realid);
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
         instructor.setDbid(-1);
         instructorDB.saveData(instructor);
      }
      // Copy courses
      for (Course course : tempCourseDB.getData())
      {
         course.setDbid(-1);
         courseDB.saveData(course);
      }
      // Copy locations
      for (Location location : tempLocationDB.getData())
      {
         location.setDbid(-1);
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
