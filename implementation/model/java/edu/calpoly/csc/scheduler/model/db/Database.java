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
	private SQLDB sqlDB;
	
   /** The instructor database. */
   private InstructorDB instructorDB;

   /** The course database. */
   private CourseDB     courseDB;

   /** The location database. */
   private LocationDB   locationDB;

   /** The schedule database. */
   private ScheduleDB   scheduleDB;

   /**
    * This constructor will create the SQLDB object.
    **/
   public Database()
   {
	   SQLDB sqldb = new SQLDB();
      instructorDB = new InstructorDB(sqldb);
      courseDB = new CourseDB(sqldb);
      locationDB = new LocationDB(sqldb);
      scheduleDB = new ScheduleDB(sqldb);
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
}
