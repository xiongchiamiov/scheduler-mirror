package scheduler.db.instructordb;

/**
 * Exception thrown when a given Instructor object is null.
 *
 * @author Eric Liebowitz
 * @version 21feb10
 */
public class NullInstructorException extends Exception
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;

   /**
    * Calls the exception constructor.
    */
   public NullInstructorException ()
   {
      super ();
   }
}
