package scheduler.db.coursedb;

/**
 * Thrown when a course isn't in the database. 
 *
 * @author Eric Liebowitz
 * @version 13apr10
 */
public class CourseNotInDatabaseException extends Exception
{
   /**
    * Calls the exception constructor.
    */
   public CourseNotInDatabaseException ()
   {
      super ();
   }
}
