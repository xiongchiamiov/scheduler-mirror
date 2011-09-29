package scheduler.db.coursedb;

/**
 * Exception to be thrown when a null course database is passed to any
 * particular generating class/method. In particular, when the "getData()" 
 * method for a CDB returns null.
 *
 * @author Eric Liebowitz
 * @version 24feb10
 */
public class NullCourseDatabaseException extends Exception
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;


   /**
    * Calls the exception constructor.
    */
   protected NullCourseDatabaseException ()
   {
      super ();
   }
}
