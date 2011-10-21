package edu.calpoly.csc.scheduler.model.db;

import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;

/**
 * This class holds all of the individual database objects. The view will
 * interact with this class to get the individual databases.
 * 
 * @author Tyler Holland
 **/

public class Database
{
   /** The instructor database. */
   private InstructorDB instructorDB;

   /** The course database. */
   private CourseDB     courseDB;

   /** The location database. */
   private LocationDB   locationDB;

   // TODO: Add preferences DB?

   /**
    * This constructor will create the SQLDB object.
    **/
   public Database()
   {
      instructorDB = new InstructorDB();
      courseDB = new CourseDB();
      locationDB = new LocationDB();
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
}
