package edu.calpoly.csc.scheduler.model.db;

import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;
import edu.calpoly.csc.scheduler.model.db.sdb.ScheduleDB;

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

   /** The current quarter id */
   private String       quarterID;

   /**
    * This constructor will create the SQLDB object.
    **/
   public Database()
   {
      sqldb = new SQLDB();
//      instructorDB = new InstructorDB(sqldb);
//      courseDB = new CourseDB(sqldb);
//      locationDB = new LocationDB(sqldb);
//      scheduleDB = new ScheduleDB(sqldb);
      scheduleID = -1;
      quarterID = "";
   }

   /**
    * This constructor will create the SQLDB object with the given quarter id
    * and schedule id.
    **/
   public Database(int scheduleID, String quarterID)
   {
      SQLDB sqldb = new SQLDB();
//      instructorDB = new InstructorDB(sqldb);
//      courseDB = new CourseDB(sqldb);
//      locationDB = new LocationDB(sqldb);
//      scheduleDB = new ScheduleDB(sqldb);
      this.scheduleID = scheduleID;
      this.quarterID = quarterID;
   }
   
   public void setSchedule(int scheduleID, String quarterID)
   {
      this.scheduleID = scheduleID;
      this.quarterID = quarterID;
   }

   /**
    * @return the instructorDB
    */
   public InstructorDB getInstructorDB()
   {
      return new InstructorDB(sqldb);
//      return new InstructorDB(sqldb, scheduleID, quarterID);
   }
   
   /**
    * @return the instructorDB
    */
   public InstructorDB getInstructorDB(int scheduleID, String quarterID)
   {
      return new InstructorDB(sqldb, scheduleID, quarterID);
   }

   /**
    * @return the courseDB
    */
   public CourseDB getCourseDB()
   {
      return new CourseDB(sqldb);
//      return new CourseDB(sqldb, scheduleID, quarterID);
   }
   
   /**
    * @return the courseDB
    */
   public CourseDB getCourseDB(int scheduleID, String quarterID)
   {
      return new CourseDB(sqldb, scheduleID, quarterID);
   }

   /**
    * @return the locationDB
    */
   public LocationDB getLocationDB()
   {
      return new LocationDB(sqldb);
//      return new LocationDB(sqldb, scheduleID, quarterID);
   }
   
   /**
    * @return the locationDB
    */
   public LocationDB getLocationDB(int scheduleID, String quarterID)
   {
      return new LocationDB(sqldb, scheduleID, quarterID);
   }

   /**
    * @return the scheduleDB
    */
   public ScheduleDB getScheduleDB()
   {
      return new ScheduleDB(sqldb);
//      return new ScheduleDB(sqldb, scheduleID, quarterID);
   }
   
   /**
    * @return the scheduleDB
    */
   public ScheduleDB getScheduleDB(int scheduleID, String quarterID)
   {
      return new ScheduleDB(sqldb, scheduleID, quarterID);
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

   /**
    * @return the quarterID
    */
   public String getQuarterID()
   {
      return quarterID;
   }

   /**
    * @param quarterID
    *           the quarterID to set
    */
   public void setQuarterID(String quarterID)
   {
      this.quarterID = quarterID;
   }
}
