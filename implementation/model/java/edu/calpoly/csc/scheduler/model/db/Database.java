package edu.calpoly.csc.scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
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
   private SQLDB        sqldb;

   /** The instructor database. */
   private InstructorDB instructorDB;

   /** The course database. */
   private CourseDB     courseDB;

   /** The location database. */
   private LocationDB   locationDB;

   /** The schedule database. */
   private ScheduleDB   scheduleDB;

   /** The current schedule id */
   private int          scheduleID;

   /** The current department */
   private String       dept;

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
      return sqldb.getDeptByUserID(userid);
   }

   /**
    * STEP 3 Returns the list of schedules for this department
    */
   public HashMap<Integer, String> getSchedules(String dept)
   {
      this.dept = dept;
      HashMap<Integer, String> schedules = new HashMap<Integer, String>();
      ResultSet rs = sqldb.getSchedulesByDept(dept);
      try
      {
         while (rs.next())
         {
            schedules.put(rs.getInt("scheduleid"), rs.getString("name"));
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
      int realid = scheduleid;
      Schedule temp = new Schedule();
      temp.setId(scheduleid);
      temp.setName(scheduleName);
      if (!(sqldb.doesScheduleExist(temp)))
      {
         System.out.println("Creating new schedule");
         scheduleDB = new ScheduleDB(sqldb, this.dept);
         realid = scheduleDB.createNewSchedule(scheduleName);
         System.out.println("New schedule id: " + realid);
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
